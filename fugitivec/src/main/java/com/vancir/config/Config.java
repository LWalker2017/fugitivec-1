package com.vancir.config;

import java.io.File;

public class Config {
	
	public static final String ORG1 = "org1";
	public static final String ORG1_MSP = "Org1MSP";
	public static final String ORG2 = "org2";
	public static final String ORG2_MSP = "Org2MSP";

	public static final String ADMIN = "admin";
	public static final String ADMINPW = "adminpw";

	public static final String PROJ_ROOT = "/home/vancir/Documents/code/fugitivec";

	public static final String ORG1_ADMIN_BASE = PROJ_ROOT // ".." + File.separator + ".." + File.separator 
												+ "network-resources" + File.separator
												+ "crypto-config" + File.separator 
												+ "peerOrganizations" + File.separator 
												+ "org1.vancir.com" + File.separator 
												+ "users" + File.separator 
												+ "Admin@org1.vancir.com" + File.separator 
												+ "msp";
	public static final String ORG1_ADMIN_PK = ORG1_ADMIN_BASE + File.separator + "keystore";
	public static final String ORG1_ADMIN_CERT = ORG1_ADMIN_BASE + File.separator + "admincerts";
	public static final String ORG2_ADMIN_BASE = PROJ_ROOT // ".." + File.separator + ".." + File.separator 
												+ "network-resources" + File.separator
												+ "crypto-config" + File.separator 
												+ "peerOrganizations" + File.separator 
                                                + "org2.vancir.com" + File.separator 
                                                + "users" + File.separator 
												+ "Admin@org2.vancir.com" + File.separator 
												+ "msp";
	public static final String ORG2_ADMIN_PK = ORG2_ADMIN_BASE + File.separator + "keystore";
	public static final String ORG2_ADMIN_CERT = ORG2_ADMIN_BASE + File.separator + "admincerts";

    public static final String CA_ORG1_URL = "http://localhost:7054";
	public static final String CA_ORG2_URL = "http://localhost:8054";
	
	public static final String ORDERER_NAME = "orderer.vancir.com";
	public static final String ORDERER_URL = "grpc://localhost:7050";
	
	public static final String CHANNEL_NAME = "mychannel";
	public static final String CHANNEL_TX_PATH = PROJ_ROOT // ".." + File.separator + ".." + File.separator
												+ "network-resources" + File.separator
												+ "channel-artifacts" + File.separator
												+ "channel.tx";
	
	public static final String PEER0_ORG1_NAME = "peer0.org1.vancir.com";
	public static final String PEER0_ORG1_URL = "grpc://localhost:7051";
	
	public static final String PEER1_ORG1_NAME = "peer1.org1.vancir.com";
	public static final String PEER1_ORG1_URL = "grpc://localhost:7056";
	
    public static final String PEER0_ORG2_NAME = "peer0.org2.vancir.com";
	public static final String PEER0_ORG2_URL = "grpc://localhost:8051";
	
	public static final String PEER1_ORG2_NAME = "peer1.org2.vancir.com";
	public static final String PEER1_ORG2_URL = "grpc://localhost:8056";
	

}