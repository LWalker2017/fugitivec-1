package com.vancir.network;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;

import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.ChannelConfiguration;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.Orderer;

import com.vancir.config.Config;
import com.vancir.user.AppUser;
import com.vancir.manager.FabricManager;
import com.vancir.utilities.util;

public class CreateChannel {

    private static Logger logger = Logger.getLogger(CreateChannel.class); 

    private static String PROJ_ROOT;
    public static void main(String[] args) {
        try {
            PROJ_ROOT = util.getProjectRoot();

            AppUser org1Admin = util.getOrgAdmin(Config.ADMIN, Config.ORG1_MSP,
                PROJ_ROOT + Config.ORG1_ADMIN_PK, PROJ_ROOT + Config.ORG1_ADMIN_CERT);

            AppUser org2Admin = util.getOrgAdmin(Config.ADMIN, Config.ORG2_MSP,
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

            Collection<Peer> peers = mychannel.getPeers();
            Iterator peerIter = peers.iterator();
            while (peerIter.hasNext()) {
                Peer nextPeer = (Peer) peerIter.next();
                logger.info(nextPeer.getName() + " at " + nextPeer.getUrl());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    


    
}