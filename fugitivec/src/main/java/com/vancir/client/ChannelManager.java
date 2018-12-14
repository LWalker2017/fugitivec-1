package com.vancir.client;

import lombok.Getter;

import com.vancir.client.FabricManager;

import org.hyperledger.fabric.sdk.Channel;


@Getter
public class ChannelManager {
    String name;
    Channel channel;
    FabricManager fabricManager;

    public ChannelManager(String name, Channel channel, FabricManager fabricManager) {
        this.name = name;
        this.channel = channel;
        this.fabricManager = fabricManager;
    }
}