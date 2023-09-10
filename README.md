# no-templates

文章链接：[使用JDK类绕过TemplatesImpl黑名单](https://mp.weixin.qq.com/s?__biz=MzkzOTQzOTE1NQ==&mid=2247483845&idx=1&sn=3fd32ced17f517edf63a7cfea7ce7a6a&chksm=c2f1a499f5862d8f3390acf08b35a039019e3d56e053dfec41be30b0d32fcd8af5073c38f545#rd)

这是一个靶场，用于测试`TemplatesImpl`被拉黑的情况：
- spring-boot-env 一个包含了前端的测试靶场
- gadget-generator 生成各种测试`payload`
- evil-ldap-server 用于绕过黑名单的`ldap`服务端

