# 客户管理系统

### 项目简介
本系统运用原生JSP和Servelt写成，只用了jdbc来连接数据库。该系统支持登陆，会有过滤器防止未登陆直接访问客户信息页面。进入客户信息页面之后，可以看到客户的信息，并支持批量删除。只能单选进行修改。可以新增客户信息，新增客户信息时，会判断姓名是是否填写（必填），简单判断出生日期格式是否正确，手机号可以喧阗，填了会判断位数是否正确。查询功能支持模糊查询，可单个或多个条件（姓名，性别，出生日期）来进行查询。

### 运行环境
1. Mysql5
2. Tomcat8.5

### 运行说明
1. 下载或 git clone 代码
2. 数据库新建一个名字叫”intern“的 table
3. 导入数据库初始数据 intern.sql
4. 导入项目 Intern
5. 部署到Tomcat
6. 默认账号admin  密码admin
