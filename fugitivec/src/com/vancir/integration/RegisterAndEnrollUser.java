package com.vancir.integration;

import org.apache.log4j.Logger;

import com.vancir.manager.CAManager;
import com.vancir.user.AppUser;
import com.vancir.utilities.Config;
import com.vancir.utilities.Util;

public class RegisterAndEnrollUser {

    private static Logger logger = Logger.getLogger(RegisterAndEnrollUser.class); 

    public static void main(String[] args) {
        try {
            String PROJ_ROOT = Util.getProjectRoot();
            CAManager caManager = new CAManager(Config.CA_ORG1_URL, null);

            // enroll admin to Org1MSP
            AppUser org1Admin = Util.getOrgUser(Config.ADMIN, Config.ORG1_MSP);
            caManager.setAdminUser(org1Admin);
            org1Admin = caManager.enrollAdminUser(Config.ADMIN, Config.ADMINPW);

            // enroll user to Org1MSP
            // FIXME: when register a new user, chaincode will throw a authentication failure error. 
            // fabric-ca-client register -d --id.name user123 --id.affiliation org1
            String userName = "user"+System.currentTimeMillis();
            AppUser org1User = Util.getOrgAdmin(userName, Config.ORG1_MSP, PROJ_ROOT + Config.ORG1_ADMIN_PK, PROJ_ROOT + Config.ORG1_ADMIN_CERT);
            String org1UserSecret = caManager.registerAndEnrollUser(org1User);
            
            logger.info("Org1 User already regsiter and enrolled. Name: " + userName + " Password: " + org1UserSecret);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}