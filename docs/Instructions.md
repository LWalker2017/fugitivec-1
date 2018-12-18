# Instructions 

Follow these steps to setup the environment and run the repo

1. [Setup the network](#1-setup-the-network)

### 1. Setup the network

生成密钥证书

``` bash
cryptogen generate --config=./crypto-config.yaml --output="../network-resources/crypto-config"
```

显示详细帮助信息`cryptogen --help-long`


生成Orderer的创世区块

``` bash
export FABRIC_CFG_PATH=$PWD
configtxgen -profile TwoOrgsOrdererGenesis -outputBlock  ../network-resources/channel-artifacts/genesis.block
```

> Note: 这里需要将configtx.yaml的`Profiles`标签整个剪切到文件末尾. 并且需要预先创建好一个`channel-artifacts/genesis.block`文件以便写入


生成channel交易配置 - channel.tx

``` bash
export CHANNEL_NAME=mychannel

# this file contains the definitions for our sample channel
configtxgen -profile TwoOrgsChannel -outputCreateChannelTx ../network-resources/channel-artifacts/channel.tx -channelID $CHANNEL_NAME
```

定义`Org1 & Org2`的锚节点(Anchor peer)

``` bash
configtxgen -profile TwoOrgsChannel -outputAnchorPeersUpdate ../network-resources/channel-artifacts/Org1MSPanchors.tx -channelID $CHANNEL_NAME -asOrg Org1MSP

configtxgen -profile TwoOrgsChannel -outputAnchorPeersUpdate ../network-resources/channel-artifacts/Org2MSPanchors.tx -channelID $CHANNEL_NAME -asOrg Org2MSP
```

启动网络

``` bash
docker-compose -f docker-compose.yml up -d
```

如果想实时查看区块链网络的日志, 可以不使用`-d`选项


安装和实例化chaincode

``` bash
$ docker exec -it chaincode bash
# CORE_PEER_ADDRESS=peer0.org1.vancir.com:7051 CORE_CHAINCODE_ID_NAME=mycc java -jar chaincode.jar
CORE_PEER_ADDRESS=peer0.org1.vancir.com:7052 CORE_CHAINCODE_ID_NAME=mycc java -jar chaincode.jar
```

``` bash
$ java -cp fugitivec-1.0-SNAPSHOT-jar-with-dependencies.jar com.vancir.network.CreateChannel
$ docker exec -it cli bash
# install chaincode 
peer chaincode install -n mycc -v 1.0 -l java -p /opt/gopath/src/github.com/chaincode
# instantiate
peer chaincode instantiate -o orderer.vancir.com:7050 --cafile /opt/gopath/src/github.com/hyperledger/fabric/peer/crypto/ordererOrganizations/vancir.com/orderers/orderer.vancir.com/msp/tlscacerts/tlsca.vancir.com-cert.pem -C mychannel -n mycc -v 1.0 -c '{"Args":["init", "Alice", "Alice is fugitive", "Bob", "Bob is not fugitive"]}' -P "OR ('Org1MSP.member','Org2MSP.member')"
# add, delete, query, update 
peer chaincode invoke -n mycc -c '{"Args":["add", "Peter", "Peter is a good boy"]}' -C mychannel
peer chaincode invoke -n mycc -c '{"Args":["delete", "Bob"]}' -C mychannel
peer chaincode invoke -n mycc -c '{"Args":["query", "Alice"]}' -C mychannel
peer chaincode invoke -n mycc -c '{"Args":["update", "Alice", "Alice is not fugitive"]}' -C mychannel
```


Some command can be helpful
``` bash
# use wget to submit post request
wget --post-data "{\"id\":\"user1234\",\"type\":\"client\",\"affiliation\":\"org1\",\"attrs\":[]}" http://localhost:7054/api/v1/register
wget --post-data "{\"id\":\"user555\",\"type\":\"client\",\"affiliation\":\"org1\",\"attrs\":[]}" http://localhost:7054/register
# register a user to org1
fabric-ca-client register -d --id.name user001 --id.affiliation org1
```