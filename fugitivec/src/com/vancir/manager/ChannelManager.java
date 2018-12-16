package com.vancir.manager;

import lombok.Getter;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import static java.nio.charset.StandardCharsets.UTF_8;

import org.apache.log4j.Logger;

import org.hyperledger.fabric.sdk.ChaincodeEndorsementPolicy;
import org.hyperledger.fabric.sdk.BlockEvent.TransactionEvent;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.TransactionRequest.Type;
import org.hyperledger.fabric.sdk.InstantiateProposalRequest;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.exception.ChaincodeEndorsementPolicyParseException;

import com.vancir.manager.FabricManager;

@Getter
public class ChannelManager {
    String name;
    Channel channel;
    FabricManager fabricManager;

    private static Logger logger = Logger.getLogger(ChannelManager.class); 

    public ChannelManager(String name, Channel channel, FabricManager fabricManager) {
        this.name = name;
        this.channel = channel;
        this.fabricManager = fabricManager;
    }

    public Collection<ProposalResponse> instantiateChaincode(String chaincodeName, String chaincodeVersion, String chaincodePath, 
            String langType, String funcName, String[] funcArgs, String policyPath) 
            throws InvalidArgumentException, ProposalException, ChaincodeEndorsementPolicyParseException, IOException {
        
        InstantiateProposalRequest request = fabricManager.getHfclient().newInstantiationProposalRequest();
        request.setProposalWaitTime(180000);
        ChaincodeID.Builder chaincodeIDBuilder = ChaincodeID.newBuilder().setName(chaincodeName)
                                                                        .setVersion(chaincodeVersion)
                                                                        .setPath(chaincodePath);

        ChaincodeID chaincodeID = chaincodeIDBuilder.build();
        request.setChaincodeID(chaincodeID);

        if (langType.equals(Type.JAVA.toString()))
            request.setChaincodeLanguage(Type.JAVA);
        else if (langType.equals(Type.NODE.toString())) {
            request.setChaincodeLanguage(Type.NODE);
        } else if (langType.equals(Type.GO_LANG.toString())) {
            request.setChaincodeLanguage(Type.GO_LANG);
        } 
        
        request.setFcn(funcName);
        request.setArgs(funcArgs);

        Map<String, byte[]> transMap = new HashMap<>();
		transMap.put("HyperLedgerFabric", "InstantiateProposalRequest:JavaSDK".getBytes(UTF_8));
		transMap.put("method", "InstantiateProposalRequest".getBytes(UTF_8));
		request.setTransientMap(transMap);

        if (policyPath != null) {
			ChaincodeEndorsementPolicy chaincodeEndorsementPolicy = new ChaincodeEndorsementPolicy();
			chaincodeEndorsementPolicy.fromYamlFile(new File(policyPath));
			request.setChaincodeEndorsementPolicy(chaincodeEndorsementPolicy);
        }

        Collection<ProposalResponse> responses = channel.sendInstantiationProposal(request);
		CompletableFuture<TransactionEvent> cf = channel.sendTransaction(responses);
        
        logger.info(cf.toString());
        return responses;
    }

}