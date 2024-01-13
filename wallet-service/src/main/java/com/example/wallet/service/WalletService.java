package com.example.wallet.service;

import com.example.wallet.CommonConstants;
import com.example.wallet.UserIdentifier;
import com.example.wallet.WalletUpdateStatus;
import com.example.wallet.model.Wallet;
import com.example.wallet.repository.WalletRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class WalletService {

    private static Logger logger = LoggerFactory.getLogger(WalletService.class);
    @Autowired
    WalletRepository walletRepository;

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    ObjectMapper objectMapper;

    @KafkaListener(topics = CommonConstants.USER_CREATION_TOPIC, groupId = "walletConsumerGroup")
    public void createWallet(String msg) throws ParseException {

        JSONObject data = (JSONObject) new JSONParser().parse(msg);

        Long userId = (Long) data.get(CommonConstants.USER_CREATION_TOPIC_USERID);
        String phoneNumber = (String) data.get(CommonConstants.USER_CREATION_TOPIC_PHONE_NUMBER);
        String identifierKey = (String) data.get(CommonConstants.USER_CREATION_TOPIC_IDENTIFIER_KEY);
        String identifierValue = (String) data.get(CommonConstants.USER_CREATION_TOPIC_IDENTIFIER_VALUE);

        Wallet wallet = Wallet.builder().userId(userId).phoneNumber(phoneNumber)
                .userIdentifier(UserIdentifier.valueOf(identifierKey)).balance(10.00)
                .identifierValue(identifierValue).build();
        walletRepository.save(wallet);
    }

    @KafkaListener(topics = CommonConstants.TRANSACTION_CREATION_TOPIC, groupId = "WalletConsumerAfterTransactionCreation")
    @Transactional
    public void updateWalletForTransaction(String msg) throws ParseException, JsonProcessingException {

        JSONObject data = (JSONObject) new JSONParser().parse(msg);

        String sender = (String) data.get("sender");
        String receiver = (String) data.get("receiver");
        Double amount = (Double) data.get("amount");
        String transactionId = (String) data.get("transactionId");

        logger.info("Validating the Sender's wallet balance: sender - {}, receiver - {}, amount - {}, transactionId - {}", sender, receiver, amount, transactionId);
        System.out.println(sender+" " + receiver+" "+amount+" "+transactionId);
//        Check if Sender and Receiver Accounts are not Blocked/Expired
        Wallet senderWallet = walletRepository.findByPhoneNumber(sender);
        Wallet receiverWallet = walletRepository.findByPhoneNumber(receiver);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("sender",sender);
        jsonObject.put("receiver",receiver);
        jsonObject.put("amount",amount);
        jsonObject.put("transactionId",transactionId);

        if(senderWallet != null && receiverWallet != null && senderWallet.getBalance() >= amount){
            jsonObject.put("walletUpdateStatus", WalletUpdateStatus.SUCCESS);
            walletRepository.updateWallet(0 - amount, sender);
            walletRepository.updateWallet(amount, receiver);
        } else {
            jsonObject.put("walletUpdateStatus", WalletUpdateStatus.FAILED);
        }
        kafkaTemplate.send(CommonConstants.WALLET_UPDATED_TOPIC, objectMapper.writeValueAsString(jsonObject));
    }
}
