package com.example.wallet.service;

import com.example.wallet.CommonConstants;
import com.example.wallet.WalletUpdateStatus;
import com.example.wallet.enums.TransactionStatus;
import com.example.wallet.model.Transaction;
import com.example.wallet.repository.TransactionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class TransactionService implements UserDetailsService {
    @Autowired
    RestTemplate restTemplate;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    TransactionRepository transactionRepository;

    public String initiateTransaction(String username, String receiver, String message, Double amount) throws JsonProcessingException {

        Transaction transaction = Transaction.builder().sender(username).receiver(receiver)
                .message(message).amount(amount).transactionId(UUID.randomUUID().toString())
                .transactionStatus(TransactionStatus.PENDING).build();
        transactionRepository.save(transaction);

        // publish the event after initiating the Transaction
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("sender", username);
        jsonObject.put("receiver", receiver);
        jsonObject.put("amount", amount);
        jsonObject.put("transactionId", transaction.getTransactionId());

        kafkaTemplate.send(CommonConstants.TRANSACTION_CREATION_TOPIC, objectMapper.writeValueAsString(jsonObject));

        return transaction.getTransactionId();
    }

    @KafkaListener(topics = CommonConstants.WALLET_UPDATED_TOPIC, groupId = "WalletSuccessGroup")
    @Transactional
    public void updateTransaction(String msg) throws ParseException, JsonProcessingException {

        JSONObject data = (JSONObject) new JSONParser().parse(msg);

        String sender = (String) data.get("sender");
        String receiver = (String) data.get("receiver");
        Double amount = (Double) data.get("amount");
        String transactionId = (String) data.get("transactionId");
        WalletUpdateStatus walletUpdateStatus = WalletUpdateStatus.valueOf((String) data.get("walletUpdateStatus"));

        JSONObject senderObj = (JSONObject) getUserFromUserService(sender);
        String senderEmail = (String) senderObj.get("email");
        String receiverEmail = null;

        if(walletUpdateStatus.toString().equals(WalletUpdateStatus.SUCCESS.toString())){
            JSONObject receiverObj = (JSONObject) getUserFromUserService(receiver);
            receiverEmail = (String) receiverObj.get("email");
            transactionRepository.updateTransaction(transactionId, TransactionStatus.SUCCESS);
        } else {
            transactionRepository.updateTransaction(transactionId, TransactionStatus.FAILED);
        }

        // its not good habit to use constants here , it should be used in config file or constant files
        String senderMsg = "Hi, Your Transaction with id "+transactionId+" got "+walletUpdateStatus;
        JSONObject senderEmailObj = new JSONObject();
        senderEmailObj.put("email",senderEmail);
        senderEmailObj.put("msg",senderMsg);


        kafkaTemplate.send(CommonConstants.TRANSACTION_COMPLETION_TOPIC, objectMapper.writeValueAsString(senderEmailObj));

        if(walletUpdateStatus == WalletUpdateStatus.SUCCESS){
            String receiverMsg = "Hi, You have received Rs:"+amount+" from "+sender+ " in your wallet Linked with Phone Number "+receiver;
            JSONObject receiverEmailObj = new JSONObject();
            receiverEmailObj.put("email", receiverEmail);
            receiverEmailObj.put("msg",receiverMsg);

            kafkaTemplate.send(CommonConstants.TRANSACTION_COMPLETION_TOPIC, objectMapper.writeValueAsString(receiverEmailObj));
        }

    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        JSONObject requestedUser = getUserFromUserService(username);
        List<GrantedAuthority> authorities;
        List<LinkedHashMap<String, String>> requestedAuthorities = (List<LinkedHashMap<String, String>>) requestedUser.get("authorities");
        authorities = requestedAuthorities.stream().map(x-> x.get("authority")).map(x-> new SimpleGrantedAuthority(x))
                .collect(Collectors.toList());
        return new User((String) requestedUser.get("username"),(String) requestedUser.get("password"),authorities);
    }
    private JSONObject getUserFromUserService(String username){
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBasicAuth("txnService","txn123" );
        HttpEntity request = new HttpEntity<>(httpHeaders);
        return restTemplate.exchange("http://localhost:6001/admin/user/"+username, HttpMethod.GET, request, JSONObject.class).getBody();
    }
}
