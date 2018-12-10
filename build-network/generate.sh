#!/bin/bash
#
set -e

export FABRIC_CFG_PATH=$PWD
export CHANNEL_NAME=mychannel


echo
echo "##########################################################"
echo "##### Generate certificates using cryptogen tool #########"
echo "##########################################################"
cryptogen generate --config=./crypto-config.yaml --output="../network-resources/crypto-config"


echo "##########################################################"
echo "#########  Generating Orderer Genesis block ##############"
echo "##########################################################"
configtxgen -profile TwoOrgsOrdererGenesis -outputBlock  ../network-resources/channel-artifacts/genesis.block


echo
echo "#################################################################"
echo "### Generating channel configuration transaction 'channel.tx' ###"
echo "#################################################################"
configtxgen -profile TwoOrgsChannel -outputCreateChannelTx ../network-resources/channel-artifacts/channel.tx -channelID $CHANNEL_NAME



echo
echo "#################################################################"
echo "##### Generating anchor peer update for Org1MSP and Org2MSP #####"
echo "#################################################################"

configtxgen -profile TwoOrgsChannel -outputAnchorPeersUpdate ../network-resources/channel-artifacts/Org1MSPanchors.tx -channelID $CHANNEL_NAME -asOrg Org1MSP
configtxgen -profile TwoOrgsChannel -outputAnchorPeersUpdate ../network-resources/channel-artifacts/Org2MSPanchors.tx -channelID $CHANNEL_NAME -asOrg Org2MSP
