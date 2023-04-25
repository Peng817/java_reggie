# java_reggie
springboot项目第一个实战练习

## 技术栈
### build部分：
mysql [数据库];
druid [连接池];
jdbc [连接池接口];

mybatis [对象关系映射技术,ORM];

lombok [实体类的方法自动构建工具]；

springboot/spring [框架：构建起映射层，服务层，控制层的后端模式]

### 部署部分：
git [版本控制工具];

linux [系统，架设于服务器];

### 优化部分:
redis [nosql、缓存技术];

Nginx [静态页面服务器，前端服务器]

### 思想部分：
MVC模式:model,view,controller.一种软件架构的模型;

前后端分离：代码层面的分离有前端使用独立出java代码的js，html，css
而后端负责实现mvc的controller交互；项目层面的分离，本项目中将前端进程
和后端进程分别在两台虚拟机服务器上进行部署，前端使用反向代理来访问后端

读写分离：本项目在两台服务器上部署了主从数据库

缓存技术：部署Redis数据库用于服务器运行的缓存库