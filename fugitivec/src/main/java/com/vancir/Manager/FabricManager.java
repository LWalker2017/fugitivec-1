package com.vancir.client;

import lombok.Getter;
import java.lang.reflect.InvocationTargetException;

import com.vancir.user.AppUser;
import com.vancir.client.ChannelManager;

import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;

@Getter 
public class FabricManager {

    private HFClient hfclient;

    public FabricManager(AppUser user) throws CryptoException, InvalidArgumentException, IllegalAccessException, InstantiationException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException {
        CryptoSuite cryptoSuite = CryptoSuite.Factory.getCryptoSuite();
        hfclient = HFClient.createNewInstance();
        hfclient.setCryptoSuite(cryptoSuite);
        hfclient.setUserContext(user);
    }

    public ChannelManager createChannelManager(String channelName) throws InvalidArgumentException {
        Channel channel = hfclient.newChannel(channelName);
        ChannelManager client = new ChannelManager(channelName, channel, this);
        return client;
    }



}