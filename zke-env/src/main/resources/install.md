# 版本
1. zookeeper选择3.5.4
2. jdk选择 1.8

# 集群模式
##配置ZK_HOME,执行命令：
```text
vim ~/.bash_profile
export ZK_HOME=/Users/shifeifei/Software/zk-3.5.4/zookeeper-3.5.4-beta
export PATH=$ZK_HOME/bin:$PATH
source .bash_profile
```

##配置文件 zoo.cfg
初次使用zk时，需要将 %ZK_HOME%/conf 目录下的 zoo_sample.cfg 文件重命名为 zoo.cfg，并按照如下格式配置：
```text
tickTime=2000
dataDir=/Users/shifeifei/Software/zk-3.5.4/zk-data/
ataLogDir=/Users/shifeifei/Software/zk-3.5.4/zk-log/
clientPort=2181
initLimit=5
syncLimit=2
server.1=ip1.2888.3888
server.2=ip2.2888.3888
server.3=ip3.2888.3888
```

# 说明
1. 在集群模式下，集群中的每台机器都要感知到其他机器，则配置：
```text
server.id=host:2888:3888
```
其中 server id 标识机器的序号，在每台机器上，都需要在我们的数据目录，即 dataDir 指向的目录中创建 myid 文件
，该文件只有一行内容，就是 server.id 中的 id 值，它的范围是 1到255。

# 单机模式
只在一台机器上需要修改 zoo.cfg 文件即可，如下：
```text
tickTime=2000
dataDir=/Users/shifeifei/Software/zk-3.5.4/zk-data/
ataLogDir=/Users/shifeifei/Software/zk-3.5.4/zk-log/
clientPort=2181
initLimit=5
syncLimit=2
server.1=ip1.2888.3888
```

# 伪集群模式
利用一台真实的物理机模仿集群，只需要修改 zoo.cfg 文件
```text
tickTime=2000
dataDir=/Users/shifeifei/Software/zk-3.5.4/zk-data/
ataLogDir=/Users/shifeifei/Software/zk-3.5.4/zk-log/
clientPort=2181
initLimit=5
syncLimit=2
server.1=ip1.2888.3888
server.1=ip1.2889.3889
server.1=ip1.2890.3890
```

# 运行服务
在 %ZK_HOME%/bin 目录下有
1. zkServer.sh start 启动服务；zkServer.sh stop 停止服务
2. zkCleanup.sh 清除zk历史数据；zkCli.sh 客户端
