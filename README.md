# 近期更新：

支持2021年的第二学期啦！

换了UI，修改了图标（然而渲染图还没换）。

修复了之前没有发现的内存泄漏隐患。~~（虽说没人用还是要修的~~

增加了debug模块，出问题了可以一键清除出错的事件了。



# 前言：

由于各种原因，华师一直都没有一个优雅的课表展示方式，往往需要牺牲手机壁纸充当课程表提醒。受益于网协开发的课表转换，利用系统的日历来提醒上课非常舒服。又是由于各种原因，这个服务停止了维护，就尝试性地写了一个算是回馈社区啦！尝试了基于多种实现，包括python+flask，纯python脚本，Java和kotlin，最后个人觉得kotlin+Android是一种不错的实现方式。~~也算是实现serverless了~~

应用仍处于测试阶段，大学城（南海）和石牌校区都可用，按照提示来就行啦。欢迎使用和报告bug~
![wS09II.jpg](https://s1.ax1x.com/2020/09/02/wS09II.jpg)

# 导入后效果：

## Android端：

[upl-image-preview url=https://static.0xffff.one/assets/files/2020-09-06/1599403828-233852-8dc827e315414621baff1d776c66e957.jpeg]

## iOS端：

[upl-image-preview url=https://static.0xffff.one/assets/files/2020-09-06/1599404066-694958-421e44d1f820cc1d42d62354138d592.jpeg]

# 开源地址：

https://github.com/chanfan1999/SCNU_ICAL_Android

# 下载地址：

[Github Release](https://github.com/chanfan1999/SCNU_ICAL_Android/releases)

或者

[腾讯微云](https://share.weiyun.com/kFOMPhTp)

# 不定期增加iPhone支持：

还没回宿舍，暂时不可用~~

~~偶尔发现校园网的神奇之处，iOS用户在校园网环境下可以登陆下面网址进行在线获取。~~

~~http://10.243.129.97:8080/login~~

# 已知Issue:

暂无

~~小米手机出现课程消失的现象~~【新版本修复】

~~学校sso认证系统强制要求修改密码，否则无法使用SSO服务中心(包括教务系统)的功能，该bug暂时无法修复~
[upl-image-preview url=https://static.0xffff.one/assets/files/2020-09-02/1599063453-303445-image.png]~~


~~分享到电脑功能不正常，qq提示无效的文件。~~【beta版本已经修复】

~~多次在线登录获取会导致重复导入日历。~~【beta版本已经修复，增加防止重复写入日历的判断，文件已存在的情况下无需输入密码点击登录按钮(在线获取模式)或者浮动按钮(本地获取模式)即可打开分享界面】

# 神神叨叨：

项目需要维护，希望有好兄弟可以一起接手，特别期待师弟师妹们哈。没技术可以学，可以问，最怕就是不去尝试。我觉得这是一个非常好的练手机会。**有意向或者认识的师弟师妹有意向维护的可以PM我哈~~~~~**
项目中你可以学到

1. 各语言网络模块的功能，基本掌握python的requests库，kotlin/Java的retrofit，或者基本的flask服务端功能（甚至包括少量Linux维护知识）
2. 熟悉不同语言的I/O读写功能（生成ics文件）
3. 了解爬虫的基本思路
4. 了解ical文件的基本语法格式
5. 或者是一些html知识
