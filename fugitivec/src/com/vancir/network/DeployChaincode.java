package com.vancir.network;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.log4j.Logger;

import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.ChannelConfiguration;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.Orderer;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.TransactionRequest.Type;


import com.vancir.user.AppUser;
import com.vancir.manager.ChannelManager;
import com.vancir.manager.FabricManager;
import com.vancir.utilities.Util;
import com.vancir.utilities.Config;

public class DeployChaincode {

    private static Logger logger = Logger.getLogger(DeployChaincode.class); 

    private static String PROJ_ROOT;

    public static void main(String[] args) {
        try {
            PROJ_ROOT = Util.getProjectRoot();

            AppUser org1Admin = Util.getOrgAdmin(Config.ADMIN, Config.ORG1_MSP,
                PROJ_ROOT + Config.ORG1_ADMIN_PK, PROJ_ROOT + Config.ORG1_ADMIN_CERT);

            AppUser org2Admin = Util.getOrgAdmin(Config.ADMIN, Config.ORG2_MSP,
                PROJ_ROOT + Config.ORG2_ADMIN_PK, PROJ_ROOT + Config.ORG2_ADMIN_CERT);

            FabricManager fabricManager = new FabricManager(org1Admin);

            Orderer orderer = fabricManager.getHfclient().newOrderer(Config.ORDERER_NAME, Config.ORDERER_URL);
            
            // create channel
            ChannelConfiguration  channelConfiguration = new ChannelConfiguration(new File(PROJ_ROOT + Config.CHANNEL_TX_PATH)); 
            byte[] channelConfigurationSignatures = fabricManager.getHfclient().getChannelConfigurationSignature(channelConfiguration, org1Admin);
            Channel mychannel = fabricManager.getHfclient().newChannel(Config.CHANNEL_NAME, orderer, 
                                    channelConfiguration, channelConfigurationSignatures);

            // create peer
            Peer peer0_org1 = fabricManager.getHfclient().newPeer(Config.PEER0_ORG1_NAME, Config.PEER0_ORG1_URL);
            Peer peer1_org1 = fabricManager.getHfclient().newPeer(Config.PEER1_ORG1_NAME, Config.PEER1_ORG1_URL);
            Peer peer0_org2 = fabricManager.getHfclient().newPeer(Config.PEER0_ORG2_NAME, Config.PEER0_ORG2_URL);
            Peer peer1_org2 = fabricManager.getHfclient().newPeer(Config.PEER1_ORG2_NAME, Config.PEER1_ORG2_URL);

            mychannel.joinPeer(peer0_org1);
            mychannel.joinPeer(peer1_org1);
            mychannel.addOrderer(orderer);
            mychannel.initialize();

            fabricManager.getHfclient().setUserContext(org2Admin);
            mychannel = fabricManager.getHfclient().getChannel(Config.CHANNEL_NAME);
            mychannel.joinPeer(peer0_org2);
            mychannel.joinPeer(peer1_org2);

            List<Peer> org1Peers = new ArrayList<Peer>();
            org1Peers.add(peer0_org1);
            org1Peers.add(peer1_org1);
            List<Peer> org2Peers = new ArrayList<Peer>();
            org2Peers.add(peer0_org2);
            org2Peers.add(peer1_org2);

            // start to deploy chaincode in org1
            fabricManager.getHfclient().setUserContext(org1Admin);
            Collection<ProposalResponse> response = fabricManager.deployChaincode(Config.CHAINCODE_NAME, Config.CHAINCODE_VERSION,
                                                        Config.CHAINCODE_PATH, Config.CHAINCODE_SOURCE,       
                                                        Type.JAVA, org1Peers);

            for (ProposalResponse res : response) {
                logger.info(Config.CHAINCODE_NAME + " - Chaincode deployment " + res.getStatus());
            }

            // start to deploy chaincode in org2
            fabricManager.getHfclient().setUserContext(org2Admin);
            response = fabricManager.deployChaincode(Config.CHAINCODE_NAME, Config.CHAINCODE_VERSION, 
                                                        Config.CHAINCODE_PATH, Config.CHAINCODE_SOURCE,
                                                        Type.JAVA, org2Peers);

            for (ProposalResponse res : response) {
                logger.info(Config.CHAINCODE_NAME + " - Chaincode deployment " + res.getStatus());
            }

            // start to instantiate chaincode
            ChannelManager channelManager = new ChannelManager(mychannel.getName(), mychannel, fabricManager);
            String[] arguments = { "Alice", "Alice is fugitive", "Bob", "Bob is not fugitive" };

            // FIXME: Chaincode instantiation FAILURE
            response = channelManager.instantiateChaincode(Config.CHAINCODE_NAME, Config.CHAINCODE_VERSION, PROJ_ROOT + "network-resources/" + Config.CHAINCODE_SOURCE,
                                                        Type.JAVA.toString(), "init", arguments, null);
            for (ProposalResponse res : response) {
                logger.info(Config.CHAINCODE_NAME + " - Chaincode instantiation " + res.getStatus());
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}