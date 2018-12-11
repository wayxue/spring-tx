---
title: spring 事物
---
1. 数据库事物的基本特性  
&nbsp;&nbsp;**A** :原子性（atomicity）：事物中各项操作，要么全部执行，要么全部不执行。  
&nbsp;&nbsp;**C** :一致性（consistency）：事物结束后系统状态是一致的。  
&nbsp;&nbsp;**I** :隔离性（isolation）：并发执行的事物彼此无法看到对方的中间状态。  
&nbsp;&nbsp;**D** :持久性（durability）：事物完成后所做的改动都会被持久化，即使发生灾难性的失败。  
&nbsp;&nbsp;在高并发的情况下，要完全保证其ACID特性是非常困难的，除非把所有的事物串行化执行，但带来的负面影响将是性能大打折扣。数据库中存在四种隔离级别，可基于业务进行选择。  

<table>
    <tr>
        <th>隔离级别</th>
        <th>脏读(dirty read)</th>
        <th>不可重复读(non repeatable read)</th>
        <th>幻读(phantom read)</th>
    </tr>
    <tr>
        <th>读未提交(read uncommitted)</th>
        <th>maybe</th>
        <th>maybe</th>
        <th>maybe</th>
    </tr>
    <tr>
        <th>读已提交(read committed)</th>
        <th>n</th>
        <th>maybe</th>
        <th>maybe</th>
    </tr>
    <tr>
        <th>可重复读(repeatable read)</th>
        <th>n</th>
        <th>n</th>
        <th>maybe</th>
    </tr>
	<tr>
        <th>串行化(serializable)</th>
        <th>n</th>
        <th>n</th>
        <th>n</th>
    </tr>
</table>
&nbsp;&nbsp;脏读:一个事物读取到另一个事物未提交的更新数据  
&nbsp;&nbsp;不可重复读：在同一事物中，多次读取同一数据返回的结果有所不同，换句话说，后续读取可以读取到另一事物的已提交更新数据。相反，“可重复读”在同一事物中多次读取数据时，能够保证所读数据一样，也就是后续读取不能读取到另一事物已提交的更新数据。  
&nbsp;&nbsp;幻读：查询表中一条数据如果不存在就插入一条，并发的时候却发现，里面居然有两条相同的数据。这就是幻读的问题。  
&nbsp;&nbsp;for example  
&nbsp;&nbsp;数据库默认隔离级别：  
&nbsp;&nbsp;&nbsp;oracle 中默认级别是 read committed  
&nbsp;&nbsp;&nbsp;mysql 中默认级别是 repeatable read，每个查询语句默认都是一个独立的事物。  
&nbsp;&nbsp;&nbsp;mysql 查看默认级别的命令：SELECT @@tx_isolation 或者 SELECT @@transaction_isolation(用于高版本)  
2. spring 事物  
&nbsp;&nbsp;spring 事物是在数据库事物的基础上进行了封装扩展，其主要特性如下：  
&nbsp;&nbsp;&nbsp;a. 支持原有数据库事物的隔离级别  
&nbsp;&nbsp;&nbsp;b. 加入了事物传播的概念，提供多个事物的合并或者隔离的功能  
&nbsp;&nbsp;&nbsp;c. 提供声明式事物，让业务代码与事物隔离，事物变得更易用  
&nbsp;&nbsp;2.1 spring 事物相关api说明  
&nbsp;&nbsp;&nbsp;spring 提供了三个接口来使用事物。  

&nbsp;&nbsp;&nbsp; - TransactionDefinition  
&nbsp;&nbsp;&nbsp;&nbsp;事物的定义，提供五种隔离级别（数据库隔离级别4个 + 1个默认），7个传播行为  
![](\image\spring-tx\TransactionDefinition.png)  
&nbsp;&nbsp;&nbsp; - PlatformTransactionManager  
&nbsp;&nbsp;&nbsp;&nbsp;事物管理，提供事物提交、回滚  
![](\image\spring-tx\PlatformTransactionManager.png)  
&nbsp;&nbsp;&nbsp; - TransactionStatus  
&nbsp;&nbsp;&nbsp;&nbsp;事物运行时状态，提供保存点、回滚（setRollBackOnly）  
![](\image\spring-tx\TransactionStatus.png)   
&nbsp;&nbsp;&nbsp;spring 事物有两种：编程式事物、生命式事物  
&nbsp;&nbsp;2.2 spring 编程式事物  
&nbsp;&nbsp;&nbsp;主要是基于TransactionTemplate 实现  
&nbsp;&nbsp;2.3 spring 声明式事物  
&nbsp;&nbsp;&nbsp;声明式事物有两种，一种是在xml中配置使用事物的类名、方法名，比较麻烦，另一种是注解的方式（@Transactional）。  

    <?xml version="1.0" encoding="UTF-8"?>
	<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xmlns:tx="http://www.springframework.org/schema/tx"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="
        http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/tx
        http://www.springframework.org/schema/tx/spring-tx.xsd
        http://www.springframework.org/schema/context
        http://www.springframework.org/schema/context/spring-context.xsd
        http://www.springframework.org/schema/aop
        http://www.springframework.org/schema/aop/spring-aop.xsd">
        
        <context:annotation-config></context:annotation-config>
        
        <context:component-scan base-package="com.yitaqi.service.**"/>
	<!--         // 暴露代理对象 默认false -->
	<!--         <aop:aspectj-autoproxy expose-proxy="true"></aop:aspectj-autoproxy> -->
        
        <bean id="dataSource" class="org.springframework.jdbc.datasource.DriverManagerDataSource">
        	<constructor-arg name="url" 
        		value="jdbc:mysql://localhost:3306/txtest?useUnicode=true&amp;characterEncoding=UTF-8&amp;serverTimezone=UTC&amp;useSSL=false"/>
        	<constructor-arg name="username" value="root"/>
        	<constructor-arg name="password" value="123"/>
        </bean>
        
        <bean id="txManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
	        <property name="dataSource" ref="dataSource"/>
	    </bean>
	    
	    <bean class="org.springframework.jdbc.core.JdbcTemplate">
	        <property name="dataSource" ref="dataSource"/>
	    </bean>
	    
	    <tx:annotation-driven transaction-manager="txManager"></tx:annotation-driven>
	</beans>
&nbsp;&nbsp;2.4 spring 事物传播机制  
<table>
    <tr>
        <th>类别</th>
        <th>事物传播类型</th>
        <th>说明</th>
    </tr>
    <tr>
        <th rowspan="3">支持当前事物</th>
        <th>PROPAGATION_REQUIRED（必须的）</th>
        <th>如果当前没有事物，就新建一个事物，如果已经存在一个事物，加入到这个事物中。这个是默认的</th>
    </tr>
    <tr>
        <th>PROPAGATION_SUPPORTS（支持）</th>
        <th>支持当前事物，如果当前没有事物，就以非事物方式执行</th>
    </tr>
    <tr>
        <th>PROPAGATION_MANDATORY（强制）</th>
        <th>使用当前的事物，如果当前没有事物，就抛出异常</th>
    </tr>
	<tr>
        <th rowspan="3">不支持当前事物</th>
        <th>PROPAGATION_REQUIRES_NEW（隔离）</th>
        <th>新建事物，如果当前存在事物，就把当前事物挂起</th>
    </tr>
	<tr>
        <th>PROPAGATION_NOT_SUPPORTED（不支持）</th>
        <th>以非事物方式执行操作，如果当前存在事物，就把当前事物挂起</th>
    </tr>
	<tr>
        <th>PROPAGATION_NEVER（强制非事物）</th>
        <th>以非事物方式执行，如果当前存在事物，则抛出异常</th>
    </tr>
	<tr>
        <th>套事物</th>
        <th>PROPAGATION_NESTED（嵌套事物）</th>
        <th>如果当前存在事物，则在嵌套事物内执行。如果当前没有事物，则执行与PROPAGATION_REQUIRED类似的操作</th>
    </tr>
</table>
&nbsp;&nbsp;&nbsp;常用的事物传播机制：  
&nbsp;&nbsp;&nbsp;PROPAGATION_REQUIRED:这是默认的传播机制  
&nbsp;&nbsp;&nbsp;PROPAGATION_NOT_SUPPORTED:可以用于发送提示消息，站内信、短信、邮件提示等。不属于并且不影响主体业务逻辑，即使发送失败也不应该对主体业务逻辑回滚。  
&nbsp;&nbsp;&nbsp;PROPAGATION_REQUIRES_NEW:总是新启一个事物，这个传播机制使用于不受父方法影响的操作，比如某些场景下要记录业务日志，用于异步反查，那么不管主体业务逻辑是否完成，日志都需要记录下来，不能因为主体业务逻辑报错而丢失日志。  
&nbsp;&nbsp;**spring 声明式事物使用动态代理来实现**