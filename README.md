# 复盘软工鉴权相关发展历程
## 1. **无身份时期**

在互联网的早期阶段，用户在网络上的活动是完全匿名的。此时，身份验证的概念几乎不存在，网络请求主要依赖于 IP 地址来识别用户。这种方式缺乏安全性，无法有效防止伪造身份或滥用。

## 2. **匿名邮件身份时期**

随着网络技术的发展，用户开始使用电子邮件作为身份凭证。虽然电子邮件提供了一种基本的身份识别方式，但仍然是基于离线点对点的认证，缺乏公共基础设施支持。这一时期的身份验证仍然相对薄弱，无法满足日益增长的安全需求。

## 3. **普通身份认证时期（Web时代）**

进入 Web 时代后，越来越多的网站开始提供注册和登录功能。用户可以创建账号并设置密码，这一阶段标志着身份验证的初步发展。尽管用户可以通过上传身份证等信息进行实名认证，但这仍然是一个相对简单的过程，主要依赖于账号和密码组合。

- **挑战**: 用户需要记住多个账号密码，导致管理困难，并且容易受到网络攻击。

## 4. **统一身份认证时期（SSO/OAuth2.0）**

随着互联网应用数量的激增，统一身份认证（Single Sign-On, SSO）开始流行。用户只需一次登录即可访问多个服务，这大大简化了用户体验。OAuth2.0 的出现为这一过程提供了标准化的解决方案，使得不同应用之间可以安全地共享用户信息。

- **影响**: OAuth2.0 的普及使得社交媒体平台（如微博、Facebook 等）能够成为用户身份的中心，推动了 Web2.0 时代的发展。

## 5. **现代身份验证与授权**

在当前阶段，身份验证和授权不仅仅是技术问题，还涉及法律合规、用户隐私保护等复杂因素。随着人工智能和区块链等新技术的发展，身份验证机制变得更加智能化和安全化。

- **趋势**:
    - **无口令认证**: 越来越多的系统开始探索无口令认证方法，以降低被盗用的风险。
    - **生物特征识别**: 使用指纹、面部识别等生物特征进行身份验证逐渐成为主流。
    - **行为分析**: 基于用户行为模式进行动态身份验证，以提高安全性。

## 当前使用方案：
> 本次使用的 选择的登录鉴权方式为Oauth2 ，选择使用微服务 构建授权中心，通过jwt token
实现 access token -->refresh token----> openId （java语言实现）
> 服务端采用nginx配置证书 （rsa）实现+ssl
> 高并发或者用户量大的情况下可以使用 多个微服务 通过网关进行负载均衡
> 如果用户信息相对较多可以缓存进入redis 和其他kv中间件，使用redis布隆过滤器优化缓存
> 避免缓存击穿。


一、登录前环节:

客户端请求: 用户通过用户名/邮箱和密码（或其他身份验证方式）发起登录请求。 这个请求应该使用 HTTPS 进行加密传输，防止信息在传输过程中被窃取。

输入验证: 服务器端在接收请求后，首先要进行输入验证。 这包括：

数据类型验证: 确保用户名/邮箱和密码是正确的类型。
长度验证: 检查用户名/邮箱和密码的长度是否符合要求。
格式验证: 检查用户名/邮箱是否符合规范（例如邮箱地址格式）。
恶意代码检测: 检查输入中是否存在恶意代码（例如SQL注入攻击的尝试）。
验证码验证 (可选): 为了防止暴力破解攻击，可以加入验证码验证，要求用户输入验证码以证明其是人类。 验证码应该具有良好的用户体验，避免过于复杂或难以识别。

二、登录过程:

身份验证: 服务器端根据用户提供的用户名/邮箱和密码，进行身份验证。 这通常涉及到以下步骤：

密码哈希比较: 服务器端不会直接存储用户的明文密码，而是存储密码的哈希值（例如使用 bcrypt 或 Argon2 算法）。 服务器端将用户提供的密码进行哈希运算，并将结果与数据库中存储的哈希值进行比较。 如果两者匹配，则验证成功。
多因素身份验证 (MFA): 为了增强安全性，采用多因素身份验证，例如结合密码、短信验证码、或身份验证应用等。
会话管理: 验证成功后，服务器端会生成一个会话令牌（session token）或 JWT (JSON Web Token)，并将它发送给客户端。 客户端需要在后续请求中携带这个令牌，以便服务器端识别用户的身份。 会话令牌应该具有过期时间，以防止长时间未使用的会话被滥用。 考虑使用安全的会话存储机制，例如数据库或内存缓存。

权限校验: 根据用户的身份，服务器端会进行权限校验，确定用户是否有权访问请求的资源。

三、登录后环节:

会话维护: 客户端需要在后续请求中携带会话令牌token(JWT)。 服务器端需要验证令牌的有效性，并根据令牌信息获取用户信息。

会话超时: 如果用户长时间没有活动，服务器端应该自动注销用户的会话，以提高安全性。

注销: 用户可以主动注销登录，这将使服务器端销毁用户的会话。

安全审计: 记录登录尝试（成功和失败）、用户IP地址、登录时间等信息，以便进行安全审计和追踪。

## 平衡性能和可扩展性？
- 使用缓存: 缓存常用的用户信息和会话信息，减少对数据库的访问次数。
- 异步处理: 将一些非关键操作（例如安全审计）异步处理，避免阻塞主流程。
- 水平扩展: 使用负载均衡器将请求分发到多个服务器，提高系统的并发处理能力。
- 数据库优化: 选择合适的数据库，并进行数据库优化，例如添加索引、优化查询语句。
- **微服务架构**: 将登录模块独立成一个微服务，方便扩展和维护。

## tip: 关于验证的便捷性，如何使得其他地方也能使用？

OAuth 2.0 或 OpenID Connect: 使用标准的授权协议，方便与其他系统集成。
API 网关: 使用 API 网关统一管理 API 请求，方便其他系统调用登录 API。
统一身份认证平台: 构建一个统一的身份认证平台，为多个系统提供身份验证服务。


## tip :注意安全可能性？

使用 HTTPS: 所有与服务器端的通信都应该使用 HTTPS 进行加密。(服务器Nginx配置rsa证书+ssl加密)
输入验证（双端校验：前端校验+后端校验）: 严格进行输入验证，防止各种攻击，例如 SQL 注入、跨站脚本攻击 (XSS)。
密码安全: 使用安全的密码哈希算法，并定期更新密码哈希算法。
防止暴力破解: 限制登录尝试次数，并使用验证码或其他手段防止暴力破解攻击。
定期安全审计: 定期对系统进行安全审计，发现并修复安全漏洞。
安全编码规范: 遵循安全编码规范，避免常见的安全漏洞。


# 下面以我之前为 上家公司搭建的rpa后端微服务为例 演示鉴权部分：

打开 oauth2 的颁发accesstoken 的接口
http://127.0.0.1:8760/api/doc.html#/oauth-%E8%AE%A4%E8%AF%81%E6%9C%8D%E5%8A%A1/%E7%99%BB%E5%BD%95%E6%8E%A5%E5%8F%A3/loginUsingPOST

图示 1文档 2测试  点击测试发送rest请求
![1.png](pic%2F1.png)

使用测试账号报文

{
"account": "euphy",
"code": "",
"grantType": "password",
"key": "",
"password": "123456",
"refreshToken": "",
"tenant": "MDAwMA=="
}

![2.png](pic%2F2.png)





得到返回如下：

{
"code": 0,
"data": {
"token": "eyJ0eXAiOiJKc29uV2ViVG9rZW4iLCJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoiZXVwaHkiLCJ0b2tlbl90eXBlIjoidG9rZW4iLCJ1c2VyaWQiOiIxNDEzMzMyOTEyNDQyNDQxNzI4IiwiYWNjb3VudCI6ImV1cGh5IiwiZXhwIjoxNjQ3MjUzODI3LCJuYmYiOjE2NDcyMjUwMjd9.nNVtA_GndIsDKArqwzNxKq_uQ24gx6_naAsG3hHVEn8",
"tokenType": "token",
"refreshToken": "eyJ0eXAiOiJKc29uV2ViVG9rZW4iLCJhbGciOiJIUzI1NiJ9.eyJ0b2tlbl90eXBlIjoicmVmcmVzaF90b2tlbiIsInVzZXJpZCI6IjE0MTMzMzI5MTI0NDI0NDE3MjgiLCJleHAiOjE2NDczMTE0MjcsIm5iZiI6MTY0NzIyNTAyN30.ZGsL3OjgYncMC-D3p0KISiYppYK8YQg8u6Z3rfiIVfE",
"name": "euphy",
"account": "euphy",
"avatar": "",
"workDescribe": "",
"userId": "1413332912442441728",
"expire": 28800,
"expiration": "2022-03-14 18:30:27"
},
"msg": "ok",
"path": null,
"extra": null,
"timestamp": 1647225027426,
"isError": false,
"isSuccess": true
}

上面两个token ，token就是 accesstoken ,另一个是refreshtoken 用来做静默刷新的
Oauth2协议是saas系统标配 。



获取到令牌之后可以直接在下面的获取菜单接口，当中尝试使用一下
将
![3.png](pic%2F3.png)

观察请求头的报文：
发现只需填充token即可 token 格式为 Bearer +空格 +accesstoken
请求头另外两个参数是应用的base64编码 和 租户的base64编码
我已经在swagger默认配置当中填充了 ，测试环境不需要修改
![4.png](pic%2F4.png)


curl -X GET -H  "Accept:*/*" -H  "Authorization:Basic dGFpc2hhbl91aTp0YWlzaGFuX3VpX3NlY3JldA==" -H  "tenant:MDAwMA==" -H  "token:Bearer eyJ0eXAiOiJKc29uV2ViVG9rZW4iLCJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoiZXVwaHkiLCJ0b2tlbl90eXBlIjoidG9rZW4iLCJ1c2VyaWQiOiIxNDEzMzMyOTEyNDQyNDQxNzI4IiwiYWNjb3VudCI6ImV1cGh5IiwiZXhwIjoxNjQ3MjU0NTE4LCJuYmYiOjE2NDcyMjU3MTh9.V4-TAVjr73gQ1qhk8dXaNFtDuhVdfnxFkc0yaykfhaU" -H  "Request-Origion:Knife4j" -H  "Content-Type:application/x-www-form-urlencoded" "http://127.0.0.1:8760/api/oauth/menu/admin/router"



4: rpc 调用演示
下面演示基础的类 rpc远程过程调用 实现方式feign-http
Eg:
Source：订单服务
在订单服务当中,通过远程过程调用来 调取demo4u 项目的接口
Target：demo4u 项目
RestTemplateController--> /get 方法

![5.png](pic%2F5.png)







5:nacos地址： 用户名密码  nacos/nacos
http://10.60.44.116:8848/nacos/

上面的配置文件均已经配置好，如果不了解具体配置文件作用请不要改 不然启动会报错


![6.png](pic%2F6.png)



入门6:
本次吸取了之前luna 和签约项目/财小神 中台的问题
迎合近期需求 添加了服务监控功能 （已经集成好） 启动tsmonitor


![7.png](pic%2F7.png)

![8.png](pic%2F8.png)

点击和访问应用墙：


![9.png](pic%2F9.png)

点击选取一个服务：

可以看到内部监控的详细信息：



7.最后是用来跑批的分布式任务
Xxl-jobs 吸取了之前给ats7调研用，在上次文档的基础上将oracle数据库支持换为了原生的mysql数据库支持


用户名密码： taishan: taishan


## 关于服务器nginx以及证书配置
### 1.nginx相关优化配置 
可以通过优化cpu绑定参数进行优化，或者通过使用openresty当中的lua脚本
进行性能的进一步优化
![10.jpg](pic%2F10.jpg)
### 2.openssl
![11.jpg](pic%2F11.jpg)
nginx内置了openssl库，这个c语言的源码我看过目前也是比较成熟的业界库。
如果是当前这个课题，我认为日常开发当中 自己通过服务器构建证书环境也是可能的场景之一。
比方通过hash散列算法 将我们的明文缩短之后使用公钥进行
签名，然后在发送给对方。
我在filecoin相关项目当中 配合python端和go端 协同开发的过程中，使用过类似的方案。
public key ---> 发送给受信方 加密返回的数据 密文
private key----> 1 加签 
                 2 解密

java 项目可以使用 Hutool，或者是 bouncycastle 目前我使用p10（pkcs10、pkcs7）较多
