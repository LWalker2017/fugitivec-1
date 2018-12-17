package fugitivec;

import java.util.List;
// import log4j.Logger;

import org.hyperledger.fabric.shim.ChaincodeBase;
import org.hyperledger.fabric.shim.ChaincodeStub;

public class Chaincode extends ChaincodeBase {
    // private static Logger logger = Logger.getLogger(Chaincode.class); 

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
            if (!function.equals(FUNCTION_INIT)) {
                return newErrorResponse("Function other than init is not supported.");
            }

            List<String> args = stub.getParameters();

            if (args.size() != 4) {
                return newErrorResponse("Incorrect number of arguments. Expecting 4 Syntax: init <key1> <msg1> <key2> <msg2>");
            }

            String FirstKey = KEY_PREFIX + args.get(0);
            String FirstMsg = args.get(1);
            String SecondKey = KEY_PREFIX + args.get(2);
            String SecondMsg = args.get(3);
            
            stub.putStringState(FirstKey, FirstMsg);
            stub.putStringState(SecondKey, SecondMsg);

            return newSuccessResponse("inited with " + FirstKey + " " + FirstMsg + " " + SecondKey + " " + SecondMsg);
        } catch (Exception e) {
            return newErrorResponse(e);
        }
    }

    @Override
    public Response invoke(ChaincodeStub stub) {
        try {
            String function = stub.getFunction();
            List<String> params = stub.getParameters();
            Response response;

            if (function.equals(FUNCTION_ADD)) {
                response = add(stub, params);
            } else if (function.equals(FUNCTION_DELETE)) {
                response = delete(stub, params);
            } else if (function.equals(FUNCTION_QUERY)) {
                response = query(stub, params);
            } else if (function.equals(FUNCTION_UPDATE)) {
                response = update(stub, params);
            } else {
                return newErrorResponse(String.format("Invalid function to invoke. Expecting one of"  
                + "[%s, %s, %s, %s]", FUNCTION_ADD, FUNCTION_DELETE, FUNCTION_QUERY, FUNCTION_UPDATE));
            }
            return response;
        } catch(Exception e) {
            return newErrorResponse(e);
        }
    }

    private Response add(ChaincodeStub stub, List<String> args) {
        if (args.size() != 2) {
            return newErrorResponse("Incorrect number of arguments. Expecting 2. Syntax: add <key> <message>");
        } 
        
        String logKey = KEY_PREFIX + args.get(0);
        String logMsg = args.get(1);
        stub.putStringState(logKey, logMsg);

        return newSuccessResponse("Added " + logKey + " " + logMsg);
    }

    private Response delete(ChaincodeStub stub, List<String> args) {
        if (args.size() != 1) {
            return newErrorResponse("Incorrect number of arguments. Expecting 1. Syntax: delete <key>");
        }
        String logKey = KEY_PREFIX + args.get(0);
        stub.delState(logKey);
        return newSuccessResponse();
    }
    
    private Response query(ChaincodeStub stub, List<String> args) {
        if (args.size() != 1) {
            return newErrorResponse("Incorrect number of arguments. Expecting name of the person to query. Syntax: query <key>");
        }

        String logKey = KEY_PREFIX + args.get(0);
        String value = stub.getStringState(logKey);
        if (value == null) {
            return newErrorResponse(String.format("Error: query for %s is null", logKey));
        }

        return newSuccessResponse(String.format("Query Response: Name: %s, Info: %s ", logKey, value));
    }

    private Response update(ChaincodeStub stub, List<String> args) {
        if (args.size() != 2) {
            return newErrorResponse("Incorrect number of arguments. Expecting 2 Syntax: update <key> <newMsg>");
        }
        String logKey = KEY_PREFIX + args.get(0);
        String afterMsg = args.get(1);
        String beforeMsg = stub.getStringState(logKey);

        stub.putStringState(logKey, afterMsg);
        
        return newSuccessResponse(String.format("Key: %s. Before updated, the message is %s After  updated, the message is %s.", logKey, beforeMsg, afterMsg));
    }
}
