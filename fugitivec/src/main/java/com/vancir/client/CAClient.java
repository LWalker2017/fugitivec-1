package com.vancir.client;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.Properties;
import java.lang.Exception;
import java.lang.IllegalAccessException;
import java.lang.InstantiationException;
import java.lang.reflect.InvocationTargetException;
import java.lang.ClassNotFoundException;
import org.apache.log4j.Logger;

import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;
import org.hyperledger.fabric_ca.sdk.exception.EnrollmentException;
// import org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric_ca.sdk.exception.RegistrationException;


import java.net.MalformedURLException;
import com.vancir.config.Config;
import com.vancir.user.AppUser;

/**
 * CAClient
 */
@Getter
@Setter
@ToString
public class CAClient {
    private static Logger logger = Logger.getLogger(CAClient.class); 

    String caUrl;
    Properties caClientProperties;
    HFCAClient caClient;
    AppUser adminUser;

    /**
     * Constructor 
     * 
     * @param caUrl                 The fabric-ca server url
     * @param caClientProperties    The fabric-ca client properties. Can be null
     * @throws Exception
     */
    public CAClient(String caUrl, Properties caClientProperties) throws IllegalAccessException, MalformedURLException, InstantiationException, ClassNotFoundException, CryptoException, InvalidArgumentException, NoSuchMethodException, InvocationTargetException {
        this.caUrl = caUrl;
        this.caClientProperties = caClientProperties;     

        CryptoSuite cryptoSuite = CryptoSuite.Factory.getCryptoSuite();
        caClient = HFCAClient.createNewInstance(caUrl, caClientProperties);
        caClient.setCryptoSuite(cryptoSuite);
    }
    /**
     * Register user
     * 
     * @param registrar
     * @return
     * @throws Exception
     */
    public String registerUser(AppUser registrar) throws Exception, RegistrationException, InvalidArgumentException {
        RegistrationRequest rr = new RegistrationRequest(registrar.getName(), registrar.getAffiliation());
        String enrollmentSecret = caClient.register(rr, registrar);

        logger.info("CA -" + caUrl + " Registered User - " + registrar.getName());
        return enrollmentSecret;
    }    
    /**
     * Enroll user
     * 
     * @param user
     * @param secret
     * @return
     * @throws Exception
     */
    public AppUser enrollUser(AppUser user, String secret) throws Exception {
        Enrollment enrollment = caClient.enroll(user.getName(), secret);
        user.setEnrollment(enrollment);

        logger.info("CA -" + caUrl + "Enrolled User - " + user.getName());
        return user;
    }
    /**
     * Register and enroll user
     * 
     * @param registrar
     * @return
     * @throws Exception
     */
    public AppUser registerAndEnrollUser(AppUser registrar) throws Exception, EnrollmentException {
        String eSecret = registerUser(registrar);
        AppUser user = enrollUser(registrar, eSecret);
        return user;
    }
}