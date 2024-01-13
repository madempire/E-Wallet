package com.example.wallet;

// these things which are there in utils not specific to any one module it's common for all modules
public class CommonConstants {

  //  Used when new user created
  public static final String USER_CREATION_TOPIC = "user_created";
  //  Used when new transaction Initiated
  public static final String TRANSACTION_CREATION_TOPIC = "transaction_created";
  //  Used when new transaction Completed
  public static final String TRANSACTION_COMPLETION_TOPIC = "transaction_completed";

  //  Used when new Wallet Updated
  public static final String WALLET_UPDATED_TOPIC = "wallet_updated";
  public static final String USER_CREATION_TOPIC_USERID = "userId";
  public static final String USER_CREATION_TOPIC_PHONE_NUMBER = "phoneNumber";
  public static final String USER_CREATION_TOPIC_IDENTIFIER_KEY = "identifier";
  public static final String USER_CREATION_TOPIC_IDENTIFIER_VALUE = "identifierValue";

}
