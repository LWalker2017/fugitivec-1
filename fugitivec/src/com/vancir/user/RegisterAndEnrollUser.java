package com.vancir.user;

import org.apache.log4j.Logger;

import com.vancir.manager.CAManager;
import com.vancir.user.AppUser;
import com.vancir.utilities.Config;
import com.vancir.utilities.Util;

public class RegisterAndEnrollUser {

    private static Logger logger = Logger.getLogger(RegisterAndEnrollUser.class); 

    public static void main(String[] args) {
        try {
            String userName = "user"+System.currentTimeMillis();
            CAManager caManager = new CAManager(Config.CA_ORG1_URL, null);
            AppUser org1User = Util.getOrgUser(userName, Config.ORG1_MSP);

            caManager.setAdminUser(org1User);
            String org1UserSecret = caManager.registerAndEnrollUser(org1User);
            logger.info("Org1 User already regsiter and enrolled.\nName: " + userName + " Password: " + org1UserSecret);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}