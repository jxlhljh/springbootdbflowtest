/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 50731
 Source Host           : localhost:3306
 Source Schema         : flowtest

 Target Server Type    : MySQL
 Target Server Version : 50731
 File Encoding         : 65001

 Date: 08/04/2022 12:00:48
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for work_flow
-- ----------------------------
DROP TABLE IF EXISTS `work_flow`;
CREATE TABLE `work_flow`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `flow_key` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '流程定义key',
  `flow_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '流程名称',
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of work_flow
-- ----------------------------
INSERT INTO `work_flow` VALUES (1, 'exchange', '调休申请', '调休申请', '2022-04-02 21:48:03', '2022-04-02 21:48:10');
INSERT INTO `work_flow` VALUES (4, 'billing', '开票申请', '开票申请', '2022-04-06 13:22:14', '2022-04-07 13:13:14');
INSERT INTO `work_flow` VALUES (6, 'leave', '请假(或签)', '请假(或签)', '2022-04-07 22:18:38', '2022-04-07 22:18:38');

-- ----------------------------
-- Table structure for work_flow_auditlog
-- ----------------------------
DROP TABLE IF EXISTS `work_flow_auditlog`;
CREATE TABLE `work_flow_auditlog`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '工单号',
  `handler` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '审批人用户名',
  `handler_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '审批人名称',
  `agree` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '是否审批通过，1通过,999不通过',
  `audit_info` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '审批意见',
  `audit_time` datetime(0) NULL DEFAULT NULL COMMENT '审批时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for work_flow_form_field
-- ----------------------------
DROP TABLE IF EXISTS `work_flow_form_field`;
CREATE TABLE `work_flow_form_field`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `flow_key` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '流程定义key',
  `field_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '字段名称',
  `field_cname` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '字段中文',
  `field_type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '字段类型',
  `field_order` int(11) NULL DEFAULT NULL COMMENT '序号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of work_flow_form_field
-- ----------------------------
INSERT INTO `work_flow_form_field` VALUES (8, 'test1', 'fieldName2', '中文2', 'text', 1);
INSERT INTO `work_flow_form_field` VALUES (9, 'exchange', 'dayStr', '调休天数', 'text', 3);
INSERT INTO `work_flow_form_field` VALUES (10, 'billing', 'contractName', '合同名称', 'text', 1);
INSERT INTO `work_flow_form_field` VALUES (11, 'billing', 'price', '开票金额', 'text', 2);
INSERT INTO `work_flow_form_field` VALUES (12, 'exchange', 'dateStart', '调休开始时间', 'text', 2);
INSERT INTO `work_flow_form_field` VALUES (13, 'exchange', 'reason', '调休原因', 'text', 4);
INSERT INTO `work_flow_form_field` VALUES (14, 'leave', '请假天数', 'dayStr', 'text', 1);

-- ----------------------------
-- Table structure for work_flow_nodes
-- ----------------------------
DROP TABLE IF EXISTS `work_flow_nodes`;
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
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of work_flow_nodes
-- ----------------------------
INSERT INTO `work_flow_nodes` VALUES (1, 'exchange', '项目经理审批', 'simple', 0, 1, 'zhaoyl', 'fixed', '项目经理审批', 1);
INSERT INTO `work_flow_nodes` VALUES (2, 'exchange', '部门经理审批（会签）', 'togethersign', 0, 2, '', '', '部门经理审批（会签）', 2);
INSERT INTO `work_flow_nodes` VALUES (3, 'exchange', '部门经理审批（会签）', 'simple', 2, 0, 'liujh', 'fixed', '部门经理审批（会签）', 3);
INSERT INTO `work_flow_nodes` VALUES (4, 'exchange', '部门经理审批（会签）', 'simple', 2, 0, 'linjh', 'fixed', '部门经理审批（会签）', 4);
INSERT INTO `work_flow_nodes` VALUES (5, 'exchange', '部门经理审批（会签）', 'simple', 2, 0, 'huangjc', 'fixed', '部门经理审批（会签）', 5);
INSERT INTO `work_flow_nodes` VALUES (6, 'exchange', '总经理审批', 'simple', 0, 3, 'xuw', 'fixed', '总经理审批', 6);
INSERT INTO `work_flow_nodes` VALUES (9, 'billing', '总经理审批', 'simple', 0, 2, 'xuw', 'fixed', '总经理审批', 2);
INSERT INTO `work_flow_nodes` VALUES (10, 'billing', '部门经理审批', 'simple', 0, 1, 'liujh', 'fixed', '部门经理审批', 1);
INSERT INTO `work_flow_nodes` VALUES (11, 'leave', '项目经理审批', 'simple', 0, 1, 'zhaoyl', 'fixed', '项目经理审批', 1);
INSERT INTO `work_flow_nodes` VALUES (12, 'leave', '部门经理审批（或签）', 'orsign', 0, 2, '', '', '部门经理审批（或签）', 2);
INSERT INTO `work_flow_nodes` VALUES (13, 'leave', '部门经理审批（或签）', 'simple', 12, 0, 'liujh', 'fixed', '部门经理审批（或签）', 3);
INSERT INTO `work_flow_nodes` VALUES (14, 'leave', '部门经理审批（或签）', 'simple', 12, 0, 'huangjc', 'fixed', '部门经理审批（或签）', 6);
INSERT INTO `work_flow_nodes` VALUES (15, 'leave', '部门经理审批（或签）', 'simple', 12, 0, 'linjh', 'fixed', '部门经理审批（或签）', 7);

-- ----------------------------
-- Table structure for work_flow_run_handler
-- ----------------------------
DROP TABLE IF EXISTS `work_flow_run_handler`;
CREATE TABLE `work_flow_run_handler`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '工单号',
  `handler` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '审批人用户名',
  `handler_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '审批人名称',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for work_flow_run_nodes
-- ----------------------------
DROP TABLE IF EXISTS `work_flow_run_nodes`;
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
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for work_flow_run_success_send
-- ----------------------------
DROP TABLE IF EXISTS `work_flow_run_success_send`;
CREATE TABLE `work_flow_run_success_send`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '工单号',
  `sender` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '抄送人用户名',
  `sender_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '抄送人名称',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for work_order
-- ----------------------------
DROP TABLE IF EXISTS `work_order`;
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
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
