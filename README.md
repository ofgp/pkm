### PKM（Private Key Manager）
-------------
独立的私钥存储管理服务
>对外提供公私钥对生产以及签名服务

>使用方只接触公钥,私钥独立加密存储与外界隔绝

>服务访问控制，只有授权接入方及IP白名单可访问

#### 服务启动
#{pwd}为服务启动密码，首次启动时自行决定，后续重启采用相同密码方可访问过去的运行数据
```
./service start --password=#{pwd}
./service run --password=#{pwd}
./service stop
```
#### 创建service
#{port}替换成服务实际端口，#{pwd}为服务启动密码
```
./service cs #{port} #{pwd}
```
该命令输出样例如下：
serviceId和privateKey提供给调用方，后续接口调用需要使用
```
serviceId: bfab0fd1-0522-419e-a351-6dc1b3ccc357
privateKey: A899F28317CCD8E978BB163B4FC18C35717072D58E22720E2C81DFE710EBFE34
```
#### service访问控制
锁定service，禁止其访问
```
./service lock #port #pwd #serviceId
```
解锁service，允许其访问
```
./service unlock #port #pwd #serviceId
```
查看service信息
```
./service list #port #pwd #serviceId
./service list #port #pwd all
```
限制service的访问ip
```
./service addIp #port #pwd #serviceId #Ip
./service delIp #port #pwd #serviceId #Ip
```

#### 接口文档
服务地址/swagger-ui.html，例如：http://127.0.0.1/swagger-ui.html