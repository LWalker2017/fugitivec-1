package com.vancir.manager;

import lombok.Getter;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.lang.reflect.InvocationTargetException;

import com.vancir.user.AppUser;
import com.vancir.manager.ChannelManager;

import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.InstallProposalRequest;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.TransactionRequest.Type;

@Getter 
public class FabricManager {

    private HFClient hfclient;

    public FabricManager(AppUser user) throws CryptoException, InvalidArgumentException, IllegalAccessException, 
            InstantiationException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException {
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
    /**
     * 
     * @param chaincodeName
     * @param chaincodeVersion
     * @param chaincodePath
     * @param chaincodeSourceLocation
     * @param langType
     * @param peers
     * @return
     * @throws InvalidArgumentException
     * @throws IOException
     * @throws ProposalException
     */
    public Collection<ProposalResponse> deployChaincode(String chaincodeName, String chaincodeVersion, 
            String chaincodeSourceLocation, Type langType, Collection<Peer> peers) 
            throws InvalidArgumentException, IOException, ProposalException {

        InstallProposalRequest request = hfclient.newInstallProposalRequest();
        ChaincodeID.Builder chaincodeIDBuilder = ChaincodeID.newBuilder().setName(chaincodeName)
                                                                        .setVersion(chaincodeVersion);
                                                                        // .setPath(chaincodePath);
                                                                        // java chaincode don't need to set chaincodePath
        ChaincodeID chaincodeID = chaincodeIDBuilder.build();

        request.setChaincodeID(chaincodeID);
        request.setChaincodeLanguage(langType);
        request.setUserContext(hfclient.getUserContext());
        request.setChaincodeSourceLocation(new File(chaincodeSourceLocation));
        request.setChaincodeVersion(chaincodeVersion);

        Collection<ProposalResponse> responses = hfclient.sendInstallProposal(request, peers);
        return responses;
    }
}