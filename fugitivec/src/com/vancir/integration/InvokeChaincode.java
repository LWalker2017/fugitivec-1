package com.vancir.integration;

import java.util.Map;
import java.util.HashMap;
import static java.nio.charset.StandardCharsets.UTF_8;

import com.vancir.manager.CAManager;
import com.vancir.manager.ChannelManager;
import com.vancir.manager.FabricManager;
import com.vancir.user.AppUser;
import com.vancir.utilities.Config;
import com.vancir.utilities.Util;

import org.apache.log4j.Logger;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.EventHub;
import org.hyperledger.fabric.sdk.Orderer;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.TransactionProposalRequest;
public class InvokeChaincode {
    
    private static Logger logger = Logger.getLogger(InvokeChaincode.class); 

    private static final byte[] EXPECTED_EVENT_DATA = "!".getBytes(UTF_8);
    private static final String EXPECTED_EVENT_NAME = "event";

    public static void main(String[] args) {
        try {
            AppUser org1Admin = Util.getOrgUser(Config.ADMIN, Config.ORG1_MSP);
            CAManager caManager = new CAManager(Config.CA_ORG1_URL, null);
            caManager.setAdminUser(org1Admin);
            org1Admin = caManager.enrollUser(org1Admin, Config.ADMINPW);

            FabricManager fabricManager = new FabricManager(org1Admin);
            ChannelManager channelManager = fabricManager.createChannelManager(Config.CHANNEL_NAME);
            Channel channel = channelManager.getChannel();
            Peer peer = fabricManager.getHfclient().newPeer(Config.PEER0_ORG1_NAME, Config.PEER0_ORG1_URL);
            EventHub eventHub = fabricManager.getHfclient().newEventHub(Config.EVENTHUB_NAME, Config.EVENTHUB_URL);
            Orderer orderer = fabricManager.getHfclient().newOrderer(Config.ORDERER_NAME, Config.ORDERER_URL);

            channel.addPeer(peer);
            channel.addEventHub(eventHub);
            channel.addOrderer(orderer);
            channel.initialize();

            TransactionProposalRequest request = fabricManager.getHfclient().newTransactionProposalRequest();
            ChaincodeID chaincodeID = ChaincodeID.newBuilder().setName(Config.CHAINCODE_NAME).build();
            
            request.setChaincodeID(chaincodeID);   
            request.setFcn("init");
            // init <string ID> <string name> <string sex> <int age> <bool isFleeing> <String description>
            String[] arguments = { "11111111", "Alice", "Female", "20", "true", "Alice is not fugitive" };
            request.setArgs(arguments);
            request.setProposalWaitTime(1000);

            Map<String, byte[]> transMap = new HashMap<>();
            transMap.put("HyperLedgerFabric", "TransactionProposalRequest:JavaSDK".getBytes(UTF_8));
            transMap.put("method", "TransactionProposalRequest".getBytes(UTF_8));
            transMap.put("result", ":)".getBytes(UTF_8));
            transMap.put(EXPECTED_EVENT_NAME, EXPECTED_EVENT_DATA);
            request.setTransientMap(transMap);

            Thread.sleep(10000);
            String[] testAddArgs = { "22222222", "Peter", "Male", "19", "false", "Perter is a good boy" };
            channelManager.invokeChaincode(Config.CHAINCODE_NAME, "add", testAddArgs);

            Thread.sleep(10000);
            String[] testDeleteArgs = { "Alice" };
            channelManager.invokeChaincode(Config.CHAINCODE_NAME, "delete", testDeleteArgs);

            Thread.sleep(10000);
            String[] testQueryArgs = { "Peter" };
            channelManager.invokeChaincode(Config.CHAINCODE_NAME, "query", testQueryArgs);

            Thread.sleep(10000);
            String[] testUpdateArgs = { "Peter", "Peter is not a good boy" };
            channelManager.invokeChaincode(Config.CHAINCODE_NAME, "update", testUpdateArgs);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}