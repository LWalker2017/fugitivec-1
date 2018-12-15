# Fagitive Consortium

## Introduction

a fugitive ledger based on hyperledger fabric java sdk 

## Quick Start

1. clone the repository to the local

``` bash
$ git clone https://github.com/Vancir/fugitivec.git
$ cd fugitivec
```

2. build the network resources

``` bash
$ cd build-network
$ chmod +x *.sh
$ ./init.sh
$ ./generate
```

3. start the blockchain network 

``` bash
$ cd build-network
$ ./start.sh
# $ ./stop.sh         stop the blockchain network
# $ ./teardown.sh     stop the blockchain network and remove docker images
```

4. build the blockchain java client

``` bash
$ cd fugitivec
$ mvn install
```

these command will create a `target` folder which contains our client.

``` bash
$ ls
archive-tmp  fugitivec-1.0-SNAPSHOT.jar                        generated-sources  maven-status
classes      fugitivec-1.0-SNAPSHOT-jar-with-dependencies.jar  maven-archiver
```

5. run the client 

``` bash
$ cd fugitivec/target
$ java -cp fugitivec-1.0-SNAPSHOT-jar-with-dependencies.jar com.vancir.network.CreateChannel
2018-12-15 00:15:00 WARN  Config:127 - Failed to load any configuration from: config.properties. Using toolkit defaults
2018-12-15 00:15:01 INFO  Channel:770 - Channel{id: 3, name: mychannel} joining Peer{ id: 5, name: peer0.org1.vancir.com, channelName: null, url: grpc://localhost:7051}.
2018-12-15 00:15:02 INFO  Channel:802 - Peer Peer{ id: 5, name: peer0.org1.vancir.com, channelName: mychannel, url: grpc://localhost:7051} joined into channel Channel{id: 3, name: mychannel}
2018-12-15 00:15:02 INFO  Channel:770 - Channel{id: 3, name: mychannel} joining Peer{ id: 6, name: peer1.org1.vancir.com, channelName: null, url: grpc://localhost:7056}.
2018-12-15 00:15:02 INFO  Channel:802 - Peer Peer{ id: 6, name: peer1.org1.vancir.com, channelName: mychannel, url: grpc://localhost:7056} joined into channel Channel{id: 3, name: mychannel}
2018-12-15 00:15:03 INFO  Channel:1147 - Channel Channel{id: 3, name: mychannel} eventThread started shutdown: false  thread: null
2018-12-15 00:15:03 INFO  Channel:770 - Channel{id: 3, name: mychannel} joining Peer{ id: 7, name: peer0.org2.vancir.com, channelName: null, url: grpc://localhost:8051}.
2018-12-15 00:15:03 WARN  PeerEventServiceClient:230 - PeerEventServiceClient{id: 16, channel: mychannel, peerName: peer0.org2.vancir.com, url: grpc://localhost:8051} PeerEventServiceClient{id: 16, channel: mychannel, peerName:peer0.org2.vancir.com, url: grpc://localhost:8051} attempts 0 Status returned failure code 404 (NOT_FOUND) during peer service event registration
2018-12-15 00:15:03 INFO  Channel:802 - Peer Peer{ id: 7, name: peer0.org2.vancir.com, channelName: mychannel, url: grpc://localhost:8051} joined into channel Channel{id: 3, name: mychannel}
2018-12-15 00:15:03 INFO  Channel:770 - Channel{id: 3, name: mychannel} joining Peer{ id: 8, name: peer1.org2.vancir.com, channelName: null, url: grpc://localhost:8056}.
2018-12-15 00:15:03 WARN  PeerEventServiceClient:230 - PeerEventServiceClient{id: 19, channel: mychannel, peerName: peer1.org2.vancir.com, url: grpc://localhost:8056} PeerEventServiceClient{id: 19, channel: mychannel, peerName:peer1.org2.vancir.com, url: grpc://localhost:8056} attempts 0 Status returned failure code 404 (NOT_FOUND) during peer service event registration
2018-12-15 00:15:03 INFO  Channel:802 - Peer Peer{ id: 8, name: peer1.org2.vancir.com, channelName: mychannel, url: grpc://localhost:8056} joined into channel Channel{id: 3, name: mychannel}
2018-12-15 00:15:03 INFO  CreateChannel:88 - peer1.org2.vancir.com at grpc://localhost:8056
2018-12-15 00:15:03 INFO  CreateChannel:88 - peer0.org2.vancir.com at grpc://localhost:8051
2018-12-15 00:15:03 INFO  CreateChannel:88 - peer0.org1.vancir.com at grpc://localhost:7051
2018-12-15 00:15:03 INFO  CreateChannel:88 - peer1.org1.vancir.com at grpc://localhost:7056
```