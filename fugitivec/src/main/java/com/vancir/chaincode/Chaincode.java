package com.vancir.chaincode;

import org.apache.log4j.Logger;

import org.hyperledger.fabric.shim.ChaincodeStub;

import com.vancir.chaincode.AbstractChaincode;
/**
 * Chaincode
 */
public abstract class Chaincode extends AbstractChaincode {

    private static Logger logger = Logger.getLogger(Chaincode.class); 

    public static final String CHAINCODE_ID = "ChinaFugitiveLedger";
    
    public static final String KEY_PREFIX = CHAINCODE_ID + "-CFL-";

    public static void main(String[] args) {
        // new Chaincode().start(args);
    }   

    public String getChaincodeID() {
      return CHAINCODE_ID;
    }

    @Override
    public String handleAdd(ChaincodeStub stub, String[] args) {
        String result = null;
        logger.info("Start handling init function");

        String logKey = args[0];
        String logMsg = args[1];
        stub.putState(KEY_PREFIX + logKey, logMsg.getBytes());

        result = logMsg;
        return result;
    }

    @Override
    public String handleQuery(ChaincodeStub stub, String[] args) {
        StringBuilder sb = new StringBuilder();
        try {
            for (String key : args) {
                String logKey = KEY_PREFIX + key;
                byte[] valueArray = stub.getState(logKey);
                String value = new String(valueArray, "UTF-8");
                logger.info("Query: For key: " + logKey + " value is " + value);
                sb.append(value + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sb.toString().trim();
    }

    @Override
    public String handleInvoke(ChaincodeStub stub, String[] args) {
        String result = null;
        // TODO: 

        result = "SUCCESSED";
        return result;
    }
}