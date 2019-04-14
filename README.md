# 12306-ticket

#### 介绍
12306抢票软件，基于Jfinal-3.7 jfinal-undertow-1.6开发。支持自动打码，全自动下单！仅做学习参考，目前已经实现了全自动抢票




## Features
- [x] 单日期查询（后续添加多日期查询）
- [x] CDN轮查
- [x] 自动打码下单
- [x] 用户状态恢复
- [x] 电话语音通知
- [x] 邮件通知
- [ ] 动态代理IP
- [x] 动态秘钥，自动获取(https://kyfw.12306.cn/otn/HttpZF/GetJS)
- [ ] 多日期查询

## 使用
 需要maven，和jdk1.8(理论上1.7也支持，没测试过。)

## 版本
- 19-04-02
    - 基本功能开发完成
- 19-04-03
    - 优化CDN检测流程
    - 增加指定车次
    - 优化下单流程
    - 增加电话通知
- 19-04-04
    - 增加获取动态秘钥


### CDN检测 （目前是通过请求12306的js进行测试是否可用）
![CDN轮查图片](https://images.gitee.com/uploads/images/2019/0402/192641_eabd71e1_955082.jpeg)


### CDN轮查
![CDN轮查图片](https://images.gitee.com/uploads/images/2019/0402/192316_0901e6b8_955082.png)

### 下单成功
![下单成功图片](https://images.gitee.com/uploads/images/2019/0403/225923_f7c4b74f_955082.jpeg)


### 关于防封
放在 A 云 T 云容易被限制 ip，建议在其它网络环境下运行

## License

[Apache License.](https://www.apache.org/licenses/LICENSE-2.0)