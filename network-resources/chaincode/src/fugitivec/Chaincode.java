package fugitivec;

import java.util.List;
// import log4j.Logger;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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

    ObjectMapper mapper = new ObjectMapper();

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

            if (args.size() != 6) {
                return newErrorResponse("Incorrect number of arguments. Expecting 4 Syntax: init <string ID> <string name> <string sex> <int age> <bool isFleeing> <String description>");
            }

            String personID = args.get(0);
            String personLogID = KEY_PREFIX + args.get(0);
            String personName = args.get(1);
            String personSex = args.get(2);
            int personAge = Integer.parseInt(args.get(3));
            Boolean personIsFleeing = Boolean.parseBoolean(args.get(4));
            String personDesc = args.get(5);

            Person person = new Person(personID, personName, personSex, personAge, personIsFleeing, personDesc);
            try {
                // convert object to json 
                String personString = mapper.writeValueAsString(person);
                stub.putStringState(personLogID, personString);
                return newSuccessResponse("inited with " + personString);

            } catch (JsonGenerationException e) {
                return newErrorResponse("Json generation failed");
            } catch (JsonMappingException e) {
                return newErrorResponse("Json mapping failed");
            }
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
            return newErrorResponse("Incorrect number of arguments. Expecting 2. Syntax: add <string ID> <string name> <string sex> <int age> <bool isFleeing> <String description>");
        } 
        
        String personID = args.get(0);
        String personLogID = KEY_PREFIX + args.get(0);
        String personName = args.get(1);
        String personSex = args.get(2);
        int personAge = Integer.parseInt(args.get(3));
        Boolean personIsFleeing = Boolean.parseBoolean(args.get(4));
        String personDesc = args.get(5);

        Person person = new Person(personID, personName, personSex, personAge, personIsFleeing, personDesc);
        try {
            // convert object to json 
            String personString = mapper.writeValueAsString(person);
            stub.putStringState(personLogID, personString);
            return newSuccessResponse("added with " + personString);
        
        } catch (JsonGenerationException e) {
            return newErrorResponse("Json generation failed");
        } catch (JsonMappingException e) {
            return newErrorResponse("Json mapping failed");
        } catch (JsonProcessingException e) {
            return newErrorResponse("Json processing failed"); 
        }
    }

    private Response delete(ChaincodeStub stub, List<String> args) {
        if (args.size() != 1) {
            return newErrorResponse("Incorrect number of arguments. Expecting 1. Syntax: delete <string ID>");
        }
        String personLogID = KEY_PREFIX + args.get(0);
        stub.delState(personLogID);
        return newSuccessResponse("Deleted " + personLogID);
    }
    
    private Response query(ChaincodeStub stub, List<String> args) {
        if (args.size() != 1) {
            return newErrorResponse("Incorrect number of arguments. Expecting ID of the person to query. Syntax: query <string ID>");
        }

        String personLogID = KEY_PREFIX + args.get(0);
        String personString = stub.getStringState(personLogID);
        if (personString == null) {
            return newErrorResponse(String.format("Error: query for %s is null", personLogID));
        }
        try {
            Person person = mapper.readValue(personString, Person.class);
            return newSuccessResponse(String.format("Query Response: Name: %s, Sex: %s, Age: %d, isFleeing: %b, Description: %s", 
                                                    person.getName(), person.getSex(), person.getAge(), person.getIsFleeing(), person.getDesc()));
        } catch (Exception e) {
            return newErrorResponse("failed to convert person from string into object");
        }
    }
    /**
     * 
     * @param stub
     * @param args
     * @return
     */
    private Response update(ChaincodeStub stub, List<String> args) {
        if (args.size() != 2) {
            return newErrorResponse("Incorrect number of arguments. Expecting 2 Syntax: update <string ID> <string description>");
        }
        String personLogID = KEY_PREFIX + args.get(0);
        String personAfterDesc = args.get(1);
        String personString = stub.getStringState(personLogID);

        try {
            Person person = mapper.readValue(personString, Person.class);

            String personBeforeDesc = person.getDesc();
            person.setDesc(personAfterDesc);

            String personUpdatedString = mapper.writeValueAsString(person);
            stub.putStringState(personLogID, personUpdatedString);

            return newSuccessResponse(String.format("Key: %s. Before updated, the message is %s After updated, the message is %s.", 
                                                        personLogID, personBeforeDesc, stub.getStringState(personLogID)));

        } catch (Exception e) {
            return newErrorResponse("failed to convert person from string into object");
        }
    }
}


/**
 * Person 
 * @param id
 * @param name          
 * @param sex
 * @param age
 * @param isFleeing     is still in fleeing
 * @param desc          person description
 */ 
class Person {
    String id;
    String name;
    String sex;
    int age;
    Boolean isFleeing; 
    String desc;

    public Person(String id, String name, String sex, int age, Boolean isFlee, String desc) {
        this.id = id;
        this.name = name;
        this.sex = sex;
        this.age = age;
        this.isFleeing = isFlee;
        this.desc = desc;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return this.name;
    } 
    public String getSex() {
        return this.sex;
    }
    public int getAge() {
        return this.age;
    }
    public Boolean getIsFleeing() {
        return this.isFleeing;
    }
    public String getDesc() {
        return this.desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }


}