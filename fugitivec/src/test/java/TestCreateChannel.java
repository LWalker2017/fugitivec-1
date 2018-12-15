package com.vancir.network;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.assertEquals;

import com.vancir.config.Config;
import com.vancir.network.CreateChannel;
import com.vancir.user.AppUser;
import com.vancir.client.FabricManager;


public class TestCreateChannel {

    String PROJ_ROOT;
    AppUser org1Admin;
    AppUser org2Admin;

    @Before
    public void setup() throws Exception {
        PROJ_ROOT = CreateChannel.init(); 
        this.org1Admin = CreateChannel.getOrgAdmin(Config.ADMIN, Config.ORG1_MSP,
            PROJ_ROOT + Config.ORG1_ADMIN_PK, PROJ_ROOT + Config.ORG1_ADMIN_CERT);
        this.org2Admin = CreateChannel.getOrgAdmin(Config.ADMIN, Config.ORG2_MSP,
            PROJ_ROOT + Config.ORG2_ADMIN_PK, PROJ_ROOT + Config.ORG2_ADMIN_CERT);

        System.out.println("[*] Setup org1Admin and org2Admin");
    }


    @Test
    public void TestGetOrgAdmin() {
        assertEquals(org1Admin.getName(), "admin");
        assertEquals(org1Admin.getMspId(), "Org1MSP");

        assertEquals(org2Admin.getName(), "admin");
        assertEquals(org2Admin.getMspId(), "Org2MSP");

        System.out.println("[+] TestGetOrgAdmin passed");
    }
}