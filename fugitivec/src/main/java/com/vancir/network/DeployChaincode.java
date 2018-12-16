package com.vancir.network;

import org.apache.log4j.Logger;

import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.ChannelConfiguration;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.Orderer;

import com.vancir.config.Config;
import com.vancir.user.AppUser;
import com.vancir.manager.CAManager;
import com.vancir.manager.FabricManager;
import com.vancir.utilities.util;

public class DeployChaincode {

    private static Logger logger = Logger.getLogger(DeployChaincode.class); 

    private static String PROJ_ROOT;

    public static void main(String[] args) {
        try {
            PROJ_ROOT = util.getProjectRoot();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}