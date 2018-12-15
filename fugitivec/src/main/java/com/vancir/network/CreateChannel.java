package com.vancir.network;

import org.apache.log4j.Logger;

import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.ChannelConfiguration;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.Orderer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;
import javax.xml.bind.DatatypeConverter;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

import com.vancir.config.Config;
import com.vancir.user.AppUser;
import com.vancir.client.CAManager;
import com.vancir.client.FabricManager;

public class CreateChannel {

    private static Logger logger = Logger.getLogger(CreateChannel.class); 

    private static String PROJ_ROOT;
    public static void main(String[] args) {
        try {
            init();

            AppUser org1Admin = getOrgAdmin(Config.ADMIN, Config.ORG1_MSP,
                PROJ_ROOT + Config.ORG1_ADMIN_PK, PROJ_ROOT + Config.ORG1_ADMIN_CERT);

            AppUser org2Admin = getOrgAdmin(Config.ADMIN, Config.ORG2_MSP,
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
    
    public static String init() {
        // get project root path
        String workingDir = System.getProperty("user.dir");
        int rootIndex = workingDir.indexOf("fugitivec");
        PROJ_ROOT = workingDir.substring(0, rootIndex) + "fugitivec" + File.separator;
        return PROJ_ROOT;
    }


    /**
     * Get organization admin user
     * 
     * @param adminName                 Admin user name 
     * @param msp                       MSP(Membership service provider) name
     * @param pkFolderPath              The folder path of private key
     * @param certFolderPath            The folder path of certificate
     * @return                          A AppUser instance for the specified organization admin user
     */
    public static AppUser getOrgAdmin(String adminName, String msp, 
            String pkFolderPath, String certFolderPath) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        AppUser orgAdmin = new AppUser();
        Enrollment enrollOrgAdmin = getEnrollFromPath(pkFolderPath, certFolderPath);
        orgAdmin.setEnrollment(enrollOrgAdmin);
        orgAdmin.setMspId(msp);
        orgAdmin.setName(adminName);

        return orgAdmin;
    }
    /**
     * Get enrollment from the specified 
     * keystore and admincerts folder path
     * 
     * @param pkFolderPath              The folder path of private key
     * @param certFolderPath            The folder path of certificate
     * @return                          A Enrollment instance 
     */
    public static Enrollment getEnrollFromPath(String pkFolderPath, String certFolderPath) throws IOException, FileNotFoundException, NoSuchAlgorithmException, InvalidKeySpecException {
        File pkFolder = new File(pkFolderPath);
        File[] pkFiles = pkFolder.listFiles();
        String pkName = pkFiles[0].getName();

        File certFolder = new File(certFolderPath);
        File[] certFile = certFolder.listFiles();
        String certName = certFile[0].getName();

        PrivateKey key = null;
        String cert = null;
        InputStream isKey = null;
        BufferedReader brKey = null;
        StringBuilder keyBuilder = new StringBuilder();

        try {
            isKey = new FileInputStream(pkFolderPath + File.separator + pkName);
            brKey = new BufferedReader(new InputStreamReader(isKey));
            
            for (String line = brKey.readLine(); line != null; line = brKey.readLine()) {
                if (line.indexOf("PRIVATE") == -1) {
                    keyBuilder.append(line);
                }
            }

            cert = new String(Files.readAllBytes(Paths.get(certFolderPath, certName)));
            
            byte[] encodedKey = DatatypeConverter.parseBase64Binary(keyBuilder.toString());
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encodedKey);
            KeyFactory kf = KeyFactory.getInstance("EC");
            key = kf.generatePrivate(keySpec);

        } finally {
            isKey.close();
            brKey.close();
        }

        Enrollment enrollOrgAdmin = new CAEnrollment(key, cert);
        return enrollOrgAdmin;
    }


    
}