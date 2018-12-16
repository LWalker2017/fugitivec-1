package test.java;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.assertEquals;

import com.vancir.utilities.Config;
import com.vancir.utilities.Util;
import com.vancir.user.AppUser;

public class TestCreateChannel {

    String PROJ_ROOT;
    AppUser org1Admin;
    AppUser org2Admin;

    @Before
    public void setup() throws Exception {
        PROJ_ROOT = Util.getProjectRoot(); 
        this.org1Admin = Util.getOrgAdmin(Config.ADMIN, Config.ORG1_MSP,
            PROJ_ROOT + Config.ORG1_ADMIN_PK, PROJ_ROOT + Config.ORG1_ADMIN_CERT);
        this.org2Admin = Util.getOrgAdmin(Config.ADMIN, Config.ORG2_MSP,
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