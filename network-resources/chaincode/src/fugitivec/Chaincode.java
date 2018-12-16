package fugitivec;

import java.util.List;
import org.apache.logging.log4j.Logger;

import org.hyperledger.fabric.shim.ChaincodeBase;
import org.hyperledger.fabric.shim.ChaincodeStub;

public class Chaincode extends ChaincodeBase {
    private static Logger logger = Logger.getLogger(Chaincode.class); 

    public static final String FUNCTION_INIT = "init";
    public static final String FUNCTION_ADD = "add";
    public static final String FUNCTION_DELETE = "delete";
    public static final String FUNCTION_QUERY = "query";
    public static final String FUNCTION_UPDATE = "update";

    public static final String CHAINCODE_ID = "Fujian";
    public static final String KEY_PREFIX = CHAINCODE_ID + "-CFL-"; // "CFL" means "China Fugitive Ledger"

    public static void main(String[] args) {
        new Chaincode().start(args);
    }

    public String getChaincodeID() {
        return CHAINCODE_ID;
    }

    @Override
    public Response init(ChaincodeStub stub) {
        try {
            String function = stub.getFunction();
            if (function != FUNCTION_INIT) {
                return newErrorResponse("Function other than init is not supported.");
            }
            List<String> args = stub.getParameters();

            if (args.size() != 4) {
                return newErrorResponse("Incorrect number of arguments. Expecting 4\nSyntax: init <key1> <msg1> <key2> <msg2>");
            }
            String FirstKey = KEY_PREFIX + args.get(0);
            String FirstMsg = args.get(1);
            String SecondKey = KEY_PREFIX + args.get(2);
            String SecondMsg = args.get(3);

            stub.putStringState(FirstKey, FirstMsg);
            stub.putStringState(SecondKey, SecondMsg);

            return newSuccessResponse();
        } catch (Exception e) {
            return newErrorResponse(e);
        }

    }

    @Override
    public Response invoke(ChaincodeStub stub) {
        try {
            String function = stub.getFunction();
            List<String> params = stub.getParameters();

            switch (function) {
                case FUNCTION_ADD:
                    add(stub, params);
                    break;
                case FUNCTION_DELETE:
                    delete(stub, params);
                    break;
                case FUNCTION_QUERY:
                    query(stub, params);
                    break;
                case FUNCTION_UPDATE:
                    update(stub, params);
                    break;
                default:
                    return newErrorResponse(String.format("Invalid function to invoke.\nExpecting one of"  
                        + "[%s, %s, %s, %s]", FUNCTION_ADD, FUNCTION_DELETE, FUNCTION_QUERY, FUNCTION_UPDATE));
            }
        } catch(Exception e) {
            return newErrorResponse(e);
        }

        return newSuccessResponse();
    }

    private Response add(ChaincodeStub stub, List<String> args) {
        if (args.size() != 2) {
            return newErrorResponse("Incorrect number of arguments. Expecting 2.\nSyntax: add <key> <message>");
        } 
        String logKey = KEY_PREFIX + args.get(0);
        String logMsg = args.get(1);
        stub.putStringState(logKey, logMsg);
        return newSuccessResponse();
    }

    private Response delete(ChaincodeStub stub, List<String> args) {
        if (args.size() != 1) {
            return newErrorResponse("Incorrect number of arguments. Expecting 1.\nSyntax: delete <key>");
        }
        String logKey = KEY_PREFIX + args.get(0);
        stub.delState(logKey);
        return newSuccessResponse();
    }
    
    private Response query(ChaincodeStub stub, List<String> args) {
        if (args.size() != 1) {
            return newErrorResponse("Incorrect number of arguments. Expecting name of the person to query.\nSyntax: query <key>");
        }
        String logKey = KEY_PREFIX + args.get(0);
        String value = stub.getStringState(logKey);
        if (value == null) {
            return newErrorResponse(String.format("Error: query for %s is null", logKey));
        }
        logger.info(String.format("Query Response:\nName: %s, Info: %s\n", logKey, value));
        return newSuccessResponse(value);
    }

    private Response update(ChaincodeStub stub, List<String> args) {
        if (args.size() != 2) {
            return newErrorResponse("Incorrect number of arguments. Expecting 2\nSyntax: update <key> <newMsg>");
        }
        String logKey = KEY_PREFIX + args.get(0);
        String afterMsg = args.get(1);
        String beforeMsg = stub.getStringState(logKey);

        stub.putStringState(logKey, afterMsg);
        logger.info(String.format("Before updated, the message of %s: %s", logKey, beforeMsg));
        logger.info(String.format("After  updated, the message of %s: %s", logKey, afterMsg));
        
        return newSuccessResponse("updated successfully");
    }
}