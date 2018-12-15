package com.vancir.chaincode;

import org.apache.log4j.Logger;
import java.util.Arrays;

import org.hyperledger.fabric.shim.ChaincodeBase;
import org.hyperledger.fabric.shim.ChaincodeStub;

public abstract class AbstractChaincode extends ChaincodeBase {
    private static Logger logger = Logger.getLogger(AbstractChaincode.class); 

    public static final String FUNCTION_ADD = "add";
    public static final String FUNCTION_INVOKE = "invoke";
    public static final String FUNCTION_QUERY = "query";

    protected abstract String handleAdd(ChaincodeStub stub, String[] args);
    protected abstract String handleQuery(ChaincodeStub stub, String[] args);
    protected abstract String handleInvoke(ChaincodeStub stub, String[] args);

    public String run(ChaincodeStub stub, String function, String[] args) {
        logger.info("Greeting from run(): function -> " + function + " | arg -> " + Arrays.toString(args));
        String result = null;

        switch (function) {
            case FUNCTION_ADD:
                result = handleAdd(stub, args);
                break;
            case FUNCTION_INVOKE:
                result = handleInvoke(stub, args);
                break;
            case FUNCTION_QUERY:
                result = handleQuery(stub, args);
                break;
            default:
                logger.warn("Invalid function");
                break;
        }
        return result;
    }

    public String query(ChaincodeStub stub, String function, String[] args) {
        return handleQuery(stub, args);
    }
}