# 12306-ticket

#### 介绍
12306抢票软件，基于Jfinal-3.7 jfinal-undertow-1.6开发。支持自动打码，全自动下单！仅做学习参考，目前已经实现了全自动抢票




## Features
- [x] 单日期查询
- [x] CDN轮查
- [x] 自动打码下单
- [x] 用户状态恢复
- [ ] 电话语音通知
- [ ] 邮件通知
- [ ] 动态代理IP

## 使用
 需要maven，和jdk1.8(理论上1.7也支持，没测试过。)

## 版本
- 19-04-02
    - 基本功能开发完成


### CDN轮查
![CDN轮查图片](https://images.gitee.com/uploads/images/2019/0402/192316_0901e6b8_955082.png)

### 下单成功
![下单成功图片](https://images.gitee.com/uploads/images/2019/0402/192156_e72faf78_955082.png)


### 关于防封
目前查询和登录操作是分开的，查询是不依赖用户是否登录，放在 A 云 T 云容易被限制 ip，建议在其它网络环境下运行

## License

[Apache License.](https://github.com/pjialin/py12306/blob/master/LICENSE)