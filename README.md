@[TOC](基于数据库mysql实现工作流介绍及代码实现（附源码）)
<hr style=" border:solid; width:100px; height:1px;" color=#000000 size=1">

#  一、工作流设计思路学习
虽然有开源的activiti、flowable等工作流可现成使用，但一直有个想法想自己通过数据库实现一套自己的工作流，直到看到了这篇文章 [老大要我开发一个简单的工作流引擎](https://blog.csdn.net/java_mindmap/article/details/115804747) 觉得很符合，借空闲时间将此文的一些内容梳理并通过数据库实现了一遍（有部分思路及内容抄自上面的链接文章），详计如下：

`首先设计了以下的角色，后面的内容都是基于以下角色来的`
```
普通人员：lix
项目经理：zhaoyl
部门经理：liujh
部门经理：linjh
部门经理：huangjc
总经理：xuw
```
## 1.顺序流
`先实现最简单的顺序流`
比如请假流程如：申请人发起申请->项目经理审批->部门经理审批->总经理审批->结束
![在这里插入图片描述](https://img-blog.csdnimg.cn/a85a2e9823674be197cf14d2369a064c.png)
## 2.会签的支持
`比如上面的部门经理审批，可能有三个部门经理，需要三个部门经理都同意才算通过，设计如下`
![在这里插入图片描述](https://img-blog.csdnimg.cn/e5adeafe131f494399146b794785c2ab.png)
说明：
>1.把节点分为两大类：简单节点(上图中长方形，黄色长方形和绿色长方形的都是)和复杂节点(上图中圆形)
>2.绿色的部分为主流程，即如项目经理->部门经理会签->总经理审批
>3.每个简单节点里都有且仅有有一个审批人
>4.复杂节点包含若干个子节点
>5.会签节点: 会签节点激活后，所有的子节点都可以审批，当所有的子节点都审批完毕后，会签节点完成
>6.`绿色`主流程结点找不到下一个节点时，说明流程结束。

`节点状态设计以下:`
```
ready: 可以进行审批操作的简单节点是ready状态。
complete: 已经审批完成的节点状态。 
future: 现在还没有走到的节点状态。
waiting: 只有复杂节点有该状态，表示在等待子节点审批
```
上面的完整的设计走一遍流程的状态变化如下：
1. `项目经理审批完成，流转到部门经理时，会签的复杂结点为waiting状态，三个子结点为ready状态，总经理审批结点未开始，因此为future状态`
![在这里插入图片描述](https://img-blog.csdnimg.cn/8bc1b91c11c648a182cc999c9640d209.png)

2. `部门经理有2个子结点审批完成时，会签的复杂结点仍为waiting状态，三个子结点有2个完completed状态，一个为ready状态，总经理审批结点未开始，因此仍为future状态`
![在这里插入图片描述](https://img-blog.csdnimg.cn/589c0beba1d24cc79d958513c615148b.png)
3. `部门经理全部审批完成时，会签的复杂结点改完complete状态，三个子结点，流转到总经理审批结点，因此变成ready状态`
![在这里插入图片描述](https://img-blog.csdnimg.cn/89f1c75e916840be99e37c9c3934235f.png)
4.`总经理审批完成后，绿色主流程结点找不到下一个结点了，说明流程结束。`

## 2.或签的支持
`或签：即比如上面的部门经理审批，可能有三个部门经理，任意一个个部门经理同意，就算部门经理节点审批通过，设计和会签一样，只是节点状态有点不一样`
![在这里插入图片描述](https://img-blog.csdnimg.cn/bd33f5d3856f4cfda3c282a1fdf9cd9e.png)
补充说明：
>1.其他的和会签的说明一样
>2.或签节点: 或签节点激活后，所有的子节点都可以审批，当任意的或签子节点都审批完毕后，或签节点就算完成
>3.同样，`绿色`主流程结点找不到下一个节点时，说明流程结束。
>4.`或签结点的子结点的状态，加入新状态 skip`
>5.当或签结点的子节点状态有一个为`complete`时，其它兄弟节点及其子节点的状态被置为`skip`(因为或签只要一个完成就可)

上面的走一遍流程的状态变化如下：
1.` 流转到会签结点时：`
![在这里插入图片描述](https://img-blog.csdnimg.cn/868416da9e33468eba56d9dfa1ed310a.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA5Yaw5LmL5p2N,size_19,color_FFFFFF,t_70,g_se,x_16)
2.` 任意一个部门经理审批完成，本主结点完成`
![在这里插入图片描述](https://img-blog.csdnimg.cn/1b00804cb0db4a70b29e85488c85f681.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA5Yaw5LmL5p2N,size_19,color_FFFFFF,t_70,g_se,x_16)
3.` 总经理审批完成后，绿色主流程结点找不到下一个结点了，流程结束。`

## 3.嵌套的支持
`比如会签节点里有个或签节点，或签里面又有会签，基于无限的树状结构已经算是支持`
![在这里插入图片描述](https://img-blog.csdnimg.cn/809c7fefa0364699a1cdcee913f36dad.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA5Yaw5LmL5p2N,size_19,color_FFFFFF,t_70,g_se,x_16)
## 4.条件结点
`条件节点类似或签结点，也是满足其中的一个结点审批完成就整个结点审批完成，比如`
![在这里插入图片描述](https://img-blog.csdnimg.cn/33ce65d58469438ea77ffacb886cbca1.png)
`请假天数小于3天，部门经理审批，然后流程结束`
`请假天数大于3天，总经理审批，然后流程结束`
如上，如果部门经理一定需要审批，可以这样：
![在这里插入图片描述](https://img-blog.csdnimg.cn/4e308107cb21441fbc9bae9a4c75240b.png)
`条件结点，如果满足请假大于3天，则需要执行总经理审批，否则，条件结点就直接结束，条件结点结束后，由于找不到下一个主流程上的结点，因为流程结束。`
## 5.审批人的处理
`审批人需要支持配置：可以流程设计时写死，也可能可以从表单中选择下一个审批人，还有根据发起人不同选择不同的审批人。`
因此设计了处理人有三种类型：
`FIXED("固定", "fixed"),`
`FROMFORM("从表单选择", "fromForm"),`
`MAPTO("根据发起人计算", "mapTo"),`

## 6.驳回上一主节点的支持
`需要支持驳回，考虑如下：`
>1.将当前正常执行审批的主流程结点及子结点全部变回为future状态
>2.反向找到上一个主流程节点，将上一个主流程节点的流程状态改成`waiting或ready`即可

详细说明如下：
1. 比如现在到了总经理审批环节，认为流程有问题，当前状态如下：
![在这里插入图片描述](https://img-blog.csdnimg.cn/e4e1849135d142879526d5ac228bf909.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA5Yaw5LmL5p2N,size_19,color_FFFFFF,t_70,g_se,x_16)
2. 总经理进行驳回后，总经理节点的状态为future,上一环节的部门经理审批变成`waiting或ready`：
![在这里插入图片描述](https://img-blog.csdnimg.cn/e48f7a2ef3f1416f9c20d5dbf4d8ed8b.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA5Yaw5LmL5p2N,size_19,color_FFFFFF,t_70,g_se,x_16)
## 6.驳回到发起人重新修改
`与驳回到上一结点的重复操作，不断递归驳回即可，直到驳回到发起人为止`

## 7.转派的支持
`有时需要将本处理的任务转派给别人进行处理，这个需求，通过动态构造或签结点即可，
即将本来的结点修改成或签结点，然后将原来的结点，加上代理人构成的节点，挂到或签结点下`
1.`比如项目经理审批环节，项目经理1希望将任务转派给项目经理2处理，转派之前如下:`
![在这里插入图片描述](https://img-blog.csdnimg.cn/7e6141786a1b4a3e9021fb29f96ace31.png)
2. `转派之后:`
![在这里插入图片描述](https://img-blog.csdnimg.cn/567ce96c074c485c83c425af7d7a0841.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA5Yaw5LmL5p2N,size_15,color_FFFFFF,t_70,g_se,x_16)
3. `转派人审批完成后，流转到部门经理审批环节，完成转派:`
![在这里插入图片描述](https://img-blog.csdnimg.cn/72c063daa95f482c945867526278e3d5.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA5Yaw5LmL5p2N,size_15,color_FFFFFF,t_70,g_se,x_16)
`另外，可以进行多次转派，比如项目经理2还可以转派给项目经理3：`
![在这里插入图片描述](https://img-blog.csdnimg.cn/6a643bea98aa4186bf9b5664120a38de.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA5Yaw5LmL5p2N,size_18,color_FFFFFF,t_70,g_se,x_16)
`特别注意：` `转派操作的节点需要是ready状态，不能是其他状态，因为只有ready状态的结点才流转到了当前的操作用户`

## 8.取消转派
`转派后悔了，需要进行转派的取消，即转派的反操作，恢复即可，如：`
1. 取消转派前
![在这里插入图片描述](https://img-blog.csdnimg.cn/647abf31334c49c282ba5166e06d6582.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA5Yaw5LmL5p2N,size_15,color_FFFFFF,t_70,g_se,x_16)
2. 取消转派后
![在这里插入图片描述](https://img-blog.csdnimg.cn/7e6141786a1b4a3e9021fb29f96ace31.png)
`特别注意：` `取消转派操作的节点需要是ready状态，不能是其他状态，因为只有ready状态的结点才流转到了当前的操作用户`
## 9.抄送的支持
`抄送的支持较简单，只要在流程结束时将工单和抄送人员关联起来即可`

#  二、数据库设计说明（mysql）
基于以上的设计思路，我做了以下的数据库设计建表。
```c
work_flow
work_flow_nodes
work_flow_run_nodes
work_flow_run_handler
work_flow_auditlog
work_flow_form_field
work_flow_run_success_send
work_order
```
## 1  `work_flow(工作流程定义表)`
```c
DROP TABLE IF EXISTS `work_flow`;
CREATE TABLE `work_flow`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `flow_key` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '流程定义key',
  `flow_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '流程名称',
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;
```
数据示例:
```c
mysql> select * from work_flow;
+----+-------------------+--------------------------------+--------------------------------+---------------------+---------------------+
| id | flow_key          | flow_name                      | remark                         | create_time         | update_time         |
+----+-------------------+--------------------------------+--------------------------------+---------------------+---------------------+
|  1 | exchange          | 调休申请                       | 调休申请                       | 2022-04-02 21:48:03 | 2022-04-02 21:48:10 |
|  4 | billing           | 开票申请                       | 开票申请                       | 2022-04-06 13:22:14 | 2022-04-07 13:13:14 |
|  6 | leave             | 请假(或签)                     | 请假(或签)                     | 2022-04-07 22:18:38 | 2022-04-07 22:18:38 |
|  7 | selectAtFlowStart | 发起申请时选择审批人           | 发起申请时选择审批人           | 2022-04-09 11:53:43 | 2022-04-09 12:00:43 |
+----+-------------------+--------------------------------+--------------------------------+---------------------+---------------------+
4 rows in set (0.00 sec)
```
## 2  `work_flow_nodes(工作流程节点定义表)`
记录流程下有哪些任务节点，一条work_flow记录会有多个work_flow_nodes
```c
CREATE TABLE `work_flow_nodes`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `flow_key` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '流程定义key',
  `node_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '流程节点名称',
  `node_type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'simple简单结点，togethersign会签，orsign或签，conditionsign条件结点',
  `parent_node_id` int(11) NULL DEFAULT 0 COMMENT '父节点Id，为0的为主要流程节点',
  `node_order` int(11) NULL DEFAULT 0 COMMENT '节点顺序，从1开始，中间不能间隔，只有父节点Id为0的才有可能值不为0.',
  `handler` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '处理人,处理人类型为固定时，此字段有值',
  `handler_type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '处理人类型，fixed固定，fromForm从表单选择，mapTo根据发起人计算出审批人',
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `page_order` int(11) NULL DEFAULT 0 COMMENT '页面排序号，用于页面显示排序',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 19 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;
```
记录示例：
![在这里插入图片描述](https://img-blog.csdnimg.cn/e357918050354b4087560e973f4cfa24.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA5Yaw5LmL5p2N,size_20,color_FFFFFF,t_70,g_se,x_16)
## 3  `work_flow_run_nodes(运行时的工作流程节点数据表)`
流程启动时，程序通过启动流程，将work_flow_nodes表中的记录复制一份到work_flow_run_nodes表，然后后续的工作流转通过修改work_flow_run_nodes表的各节点的状态来进行流转，比如改成waiting状态，ready状态，complete状态等。
```c
CREATE TABLE `work_flow_run_nodes`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '工单号',
  `flow_node_id` int(11) NULL DEFAULT NULL COMMENT '流程节点Id',
  `flow_key` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '流程定义key',
  `node_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '流程节点名称',
  `node_type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'simple简单结点，togethersign会签，orsign或签，conditionsign条件结点',
  `parent_node_id` int(11) NULL DEFAULT NULL COMMENT '父节点Id，为0的为主要流程节点',
  `node_order` int(11) NULL DEFAULT NULL COMMENT '节点顺序，从1开始，中间不能间隔，只有父节点Id为0的才有可能值不为0.',
  `node_status` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'ready已准备好处理中，complete完成,waiting等待（复杂结点才有）,skip略过不处理的，future未走到的',
  `handler` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '此字段有值',
  `handler_type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '处理人类型，fixed固定，fromForm从表单选择，mapTo根据发起人计算出审批人',
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 51 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;
```
数据示例：
![在这里插入图片描述](https://img-blog.csdnimg.cn/4c42dc4cc8e149949e66ba60b0ec293a.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA5Yaw5LmL5p2N,size_20,color_FFFFFF,t_70,g_se,x_16)
## 4  `work_flow_run_handler(工单审批人记录表)`
此表记录工单被哪些人员审批过，会去重处理，用于查询`我审批过的工单`，比如工单id如GD1649412699736的工单，有部门经理审批和总经理审批环节，比如作了转派操作，部门经理liujh将部门经理的审批任务转派给了总经理审批，那最后总经理除了审批部门经理的节点外，还会审批总经理节点，因为会产生两次审批，为了在查询`我审批过的工单`时方便查询，设计此表进行去重处理，在界面上查询时就可以直接查了。
```c
CREATE TABLE `work_flow_run_handler`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '工单号',
  `handler` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '审批人用户名',
  `handler_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '审批人名称',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;
```
数据示例：
```c
mysql> select * from work_flow_run_handler;
+----+-----------------+---------+--------------+
| id | order_id        | handler | handler_name |
+----+-----------------+---------+--------------+
|  1 | GD1649412699736 | zhaoyl  | zhaoyl       |
|  2 | GD1649412699736 | huangjc | huangjc      |
|  3 | GD1649413115897 | huangjc | huangjc      |
|  4 | GD1649413115897 | xuw     | xuw          |
|  5 | GD1649471877865 | zhaoyl  | zhaoyl       |
|  6 | GD1649471877865 | liujh   | liujh        |
|  7 | GD1649413213278 | liujh   | liujh        |
|  8 | GD1649413213278 | xuw     | xuw          |
|  9 | GD1649480981695 | liujh   | liujh        |
| 10 | GD1649480981695 | huangjc | huangjc      |
| 11 | GD1649480981695 | xuw     | xuw          |
+----+-----------------+---------+--------------+
11 rows in set (0.00 sec)
```
## 5  `work_flow_auditlog(审批历史表)`
此表用于查询工单的所有审批历史记录，审批人每执行一次审批，记录一条记录在审批历史记录表。
```c
CREATE TABLE `work_flow_auditlog`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '工单号',
  `node_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '环节主流程节点名称',
  `handler` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '审批人用户名',
  `handler_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '审批人名称',
  `agree` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '是否审批通过，1通过,999不通过',
  `audit_info` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '审批意见',
  `audit_time` datetime(0) NULL DEFAULT NULL COMMENT '审批时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 19 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;
```
数据示例：
![在这里插入图片描述](https://img-blog.csdnimg.cn/733df1c5c5484550812318faf392ffbd.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA5Yaw5LmL5p2N,size_20,color_FFFFFF,t_70,g_se,x_16)
## 6.`work_flow_form_field工单动态表单表`
每个流程可能使用的字段，展示的数据不一样，通过此表记录每种流程有哪些字段信息。
```c
CREATE TABLE `work_flow_form_field`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `flow_key` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '流程定义key',
  `field_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '字段名称',
  `field_cname` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '字段中文',
  `field_type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '字段类型,text,select',
  `default_value` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '默认值',
  `other_info` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '补充信息，如下拉列表静态数据配置，其他信息等',
  `field_order` int(11) NULL DEFAULT NULL COMMENT '序号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;
```
![在这里插入图片描述](https://img-blog.csdnimg.cn/d46e47089f304cbc98b12d3d6d7dd451.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA5Yaw5LmL5p2N,size_20,color_FFFFFF,t_70,g_se,x_16)
## 7.`work_order工单表`
每发一个流程，就会在work_order表中产生一条记录
```c
CREATE TABLE `work_order`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '工单号',
  `flow_key` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '流程定义key',
  `subject_id` int(11) NULL DEFAULT NULL COMMENT '工单所属主体的ID',
  `subject_type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '工单所属主体的类型',
  `applicant` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '申请人(用户ID)',
  `application_time` datetime(0) NULL DEFAULT NULL COMMENT '申请时间',
  `order_summary` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '工单摘要/审批摘要',
  `order_status` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '工单状态',
  `reason` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '提工单原因',
  `form_data` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '工单可变字段数据,json文本存储',
  `current_node_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '当前流程节点名称',
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `create_user` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人员',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_user` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新人员',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `order_id`(`order_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;
```
![在这里插入图片描述](https://img-blog.csdnimg.cn/0392a55c63754c7d99c729c9d7de3a74.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA5Yaw5LmL5p2N,size_20,color_FFFFFF,t_70,g_se,x_16)
## 8.`work_flow_run_success_send审批结束抄送人员记录表`
如果要实现抄送，设计了这个抄送人员表，可以在流程结束后，将抄送人员插入此记录，同时与work_order表关联查询，可获取`抄送给我的工单列表`
```c
CREATE TABLE `work_flow_run_success_send`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '工单号',
  `sender` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '抄送人用户名',
  `sender_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '抄送人名称',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;
```

#  三、实现过程部分代码说明
设计了数据库之后，通过springboot+jquery+html实现以上的基于数据库的工作流程，前端代码不太会写样式，只是通过jquery做了下流转的代码测试，正式环境可由前端工程师调试完善。
## 1.controller层
```c
WorkFlowAuditlogController：审批历史控制类
WorkFlowController：工作流程定义控制类
WorkFlowFormFieldController：动态工作流表单控制器
WorkFlowNodesController：工作流程节点定义控制类
WorkFlowRunNodesController：工作流程节点动态数据控制类
WorkOrderController：像启动流程，审批，转派，取消转派，退回等在这个接口中。
```
## 2.service层
```c
IWorkOrderService：核心的类之1，主要的工作流转的封装在这个类实现1
IWorkFlowService：核心的类之2，主要的工作流转的封装在这个类实现2
其他Service类基本上负责增删改查
```
## 3.enums
定义了一些枚举辅助进行工作流转，如：
`HandlerTypeEnum:`审批人类型枚举类
```c
	FIXED("固定", "fixed"),
	FROMFORM("从表单选择", "fromForm"),
    MAPTO("根据发起人计算", "mapTo"),
```
`NodeStatusEnum:`节点状态枚举
```c
	READY("已准备", "ready"),
    COMPLETE("已完成", "complete"),
    WAITING("等待中", "waiting"),
    SKIP("已略过", "skip"),
    FUTURE("未走到", "future"),
```
`NodeTypeEnum:`节点类型枚举
```c
	SIMPLE("简单结点", "simple"),
	TOGETHERSIGN("会签结点", "togethersign"),
	ORSIGN("或签结点", "orsign"),
```
`OrderStatusEnum`:工单状态枚举
```c
    WAIT_FOR_VERIFY("待审批", "1"),
    WAIT_FOR_HANDLE("待处理", "2"),
    FINISHED("已归档", "3"),
    WAIT_FOR_UPDATE("待修改", "4"),
    INVALIDED("已作废", "5"),
    CANCELED("已撤单", "6"),
```
`TaskResultEnum`:审批结果的枚举
```c
    EXAM_AND_APPROVE_PASS_TO_NEXT(1, "通过，交给下一个人再审"),
    EXAM_AND_APPROVE_PASS_TO_END(0, "通过，流程结束，直接归档"),
    EXAM_AND_APPROVE_REJECT_TO_PRE(997, "不通过，下一步打回到上一个环节"),
    EXAM_AND_APPROVE_REJECT_TO_MODIFICATION(998, "不通过，下一步修改申请"),
    EXAM_AND_APPROVE_REJECT_TO_CANCEL(999, "不通过，直接取消审批(撤单)")
```
## 4.listener
`cn.gzsendi.modules.workflow.listener.CommonFlowTaskListener`
`cn.gzsendi.modules.workflow.listener.DelegateTaskListener`:代理任务监听器接口,可以通过实现自己的DelegateTaskListener进行监听器扩展
>流程审批结束后，将调用CommonFlowTaskListener的notify方法，此方法再通过spring上下文查找所有的DelegateTaskListener，比如cn.gzsendi.modules.workflow.listener.MyDelegateTaskListener，然后在满足validate方法返回true的情况下，执行一些自定义的业务代码，可以靠这些监听器实现来做一些如审批通过后的通知，如短信，邮件，钉钉，微信通知等操作。

`如以下为一个实现的listener示例测试:`
```c
package cn.gzsendi.modules.workflow.listener;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import cn.gzsendi.modules.framework.utils.JsonUtil;
import cn.gzsendi.modules.workflow.enums.OrderStatusEnum;

@Component
public class MyDelegateTaskListener implements DelegateTaskListener{
	
	protected final Logger logger = LoggerFactory.getLogger(MyDelegateTaskListener.class);
	
	/**
	 * 检验当前任务监听器实现类是否需要执行
	 */
    public boolean validate(String orderId,Map<String,Object> variables) {
    	//流程结束时进行监听器的执行
    	String orderStatus = variables.get("orderStatus").toString();
    	if(OrderStatusEnum.FINISHED.getValue().equals(orderStatus)){
    		return true;
    	}
        return false;
    }

	@Override
	public void doTaskNotify(String orderId, Map<String, Object> variables) {
		logger.info("----------这里写自己想要实现的代码，做后续的自定义业务逻辑-----------");
		logger.info(JsonUtil.toJSONString(variables));
	}
}
```
#  四、一些测试效果截图或gif图片
## 1.主页面
![在这里插入图片描述](https://img-blog.csdnimg.cn/8421400068c845c282571a4346da08e7.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA5Yaw5LmL5p2N,size_20,color_FFFFFF,t_70,g_se,x_16)
## 2.流程设计
![在这里插入图片描述](https://img-blog.csdnimg.cn/daaee94f85ed4ff99da8567ee62ba2fb.gif)
## 3.流程表单设计
![在这里插入图片描述](https://img-blog.csdnimg.cn/1e8e9eb21bd54aa4a7a12b5618a84992.gif)
## 4.查看工单审批的详情
![在这里插入图片描述](https://img-blog.csdnimg.cn/b441149738504744952555e34ec4029c.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA5Yaw5LmL5p2N,size_20,color_FFFFFF,t_70,g_se,x_16)
## 5.发起流程
![在这里插入图片描述](https://img-blog.csdnimg.cn/a9f08139d4e94d89ac5def11871b5716.gif)
## 6.审批人审核
![在这里插入图片描述](https://img-blog.csdnimg.cn/2537e436c2364117a02bf9b2c033522f.gif)
## 7.转派效果
![在这里插入图片描述](https://img-blog.csdnimg.cn/d503b3ac67ed442b93243c5392164574.gif)
#  五、附代码下载
```
github: https://github.com/jxlhljh/springbootdbflowtest.git
gitee : https://gitee.com/jxlhljh/springbootdbflowtest.git
```
