我们使用Spring Cloud Config的动机除了通常所说的微服务数量太多，方便配置的统一管理外。另一个重要原因是，application.properties（或application.yml）中配置项太多，在不同环境(dev、alpha、beta、www）下配置时，调整并核对application.properties时常看得眼花缭乱，而许多配置项运维时并不关心。所以决定用Spring Cloud Config对配置管理过程做一次调整。

这里包含了三个项目，用于大致演示Spring Cloud Config的配置方法。

- spring-cloud-config-server：配置服务器。
- blog-server：演示对称加密的配置方式。
- account-server：演示非对称加密的配置方式。

服务职责说明
------------------------------

### Config Server

Spring Cloud Config Server负责从Git服务器下载配置并分发给Config Client，注意以下几项：

1. Config Server不处理配置内容的加解密，需将spring.cloud.server.encrypt.enabled设为false；
2. 使用Spring Security对配置内容的下载请求做权限控制。

### Git Repository

配置内容存储在Git仓库，生产环境的敏感信息须加密后存储，加密内容前使用{cipher}前缀。

获取加密内容使用Spring Cloud Cli工具，使用类似下面的命令：

```
~ spring encrypt securityContent --key passkey
2c65b421138fd5c69f29560f9d972ab022b0d7e764482d565d3c5e3cdfc0155c
```

多个环境时，除生产环境使用保护分支master外，其他开发和测试环境都使用一个分支，可以是dev分支。

### Config Client

在具体服务中集成Config Client处理配置，在生产环境中，Config Client需要配置密钥，以便对下载配置中的加密信息进行解密。(需要JCE完整版)

安全性说明
------------------------------

1. Config Server简单集成Spring Security后使用单一的用户名密码，也就是每个服务的维护成员都可以看到其他所有服务的配置内容。这个感觉没有问题，Git中的敏感信息会被加密，未加密内容被其他团队成员看到应当问题不大。
2. 生产环境的敏感信息使用对称加密，主要基于以下两个方面的考虑：
- 使用非对称加密稍嫌麻烦，不同服务下配置密钥需要花更多精力。
- 服务团队使用密钥加密配置内容后提交到Git，并在Config Client配置密钥，并不需要将密钥交给其他团队，使用非对称加密也没有太大意义。
- 不同服务可以使用不同密钥，即使密钥泄露影响的也只是一两个服务，必要时更换即可。

Spring Cloud Cli安装
------------------------------

1. 下载Spring Boot Cli，下载地址：https://repo.spring.io/release/org/springframework/boot/spring-boot-cli/2.0.4.RELEASE/spring-boot-cli-2.0.4.RELEASE-bin.zip
2. 解压后，将spring-2.0.4.RELEASE/bin加入环境变量。
3. 安装Spring Cloud Cli插件，安装命令：spring install org.springframework.cloud:spring-cloud-cli:2.0.0.RELEASE

JCE策略文件替换
------------------------------

http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html

下载后，解压文件，把local_policy.jar,US_export_policy.jar拷贝并覆盖到$JAVA_HOME/jre/lib/security。

非对称加密处理说明
------------------------------

使用非对称加密对敏感数据进行加密时，比较繁琐，大致步骤如下：

1. 使用下面的命令生成密钥对

```
ssh-keygen -t rsa -f ./config_rsa
```

2. 创建证书请求

```
openssl req -new -key config_rsa -out config_cert.csr
```

3. 生成证书

```
openssl x509 -req -days 3650 -in config_cert.csr -signkey config_rsa -out config_cert.crt
```

4. 导出P12文件

```
openssl pkcs12 -export -out config.p12 -inkey config_rsa -in config_cert.crt -name democonfig
```

5. 使用JAVA的keytool将P12转为KeyStore文件

```
keytool -importkeystore -deststorepass storepass -destkeypass keypass -destkeystore config.jks -srckeystore config.p12 -srcstoretype PKCS12 -srcstorepass abc123 -alias democonfig
```

6. 将config.jks文件配置到Config Client中，配置方式如下：

```
encrypt:
  key-store:
    location: file:///root/demo/config.jks
    password: storepass
    alias: democonfig
    secret: keypass
```
