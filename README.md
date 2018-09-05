我们使用Spring Cloud Config的动机除了通常所说的微服务数量太多，方便配置的统一管理外。另一个重要原因是，application.properties（或application.yml）中配置项太多，在不同环境(dev、alpha、beta、www）下配置时，调整并核对application.properties时常看得眼花缭乱，而许多配置项对测试和运维来说其实并不关心。所以决定用Spring Cloud Config对配置管理过程做一次调整。

Spring Cloud Config Server
------------------------------

首先部署Spring Cloud Config Server，注意以下几项：

1. 配置从Git下载。
2. 使用Spring Security对服务进行保护，需要用户名和密码才可下载配置信息。
3. 不要配置加密信息，即Config Server不处理配置信息的加解密。（Config Server不处理加解密并非配置信息不加密存储）

Spring Cloud Config Client
------------------------------

