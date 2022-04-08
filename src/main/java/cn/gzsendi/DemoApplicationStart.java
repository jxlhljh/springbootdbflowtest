package cn.gzsendi;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 测试URL：先从普通人员页面开始测试即可,即：http://localhost:8080/flowtest/main.html?userId=lix
 * 
 * 
 * 普通人员：http://localhost:8080/flowtest/main.html?userId=lix
 * 项目经理：http://localhost:8080/flowtest/main.html?userId=zhaoyl
 * 部门经理：http://localhost:8080/flowtest/main.html?userId=liujh
 * 部门经理：http://localhost:8080/flowtest/main.html?userId=linjh
 * 部门经理：http://localhost:8080/flowtest/main.html?userId=huangjc
 * 总经理：http://localhost:8080/flowtest/main.html?userId=xuwei
 * 
 * 》》》》》》》》》》》》数据库结构说明：----》》》》》》》》》》》》
 * work_flow：流程定义表（模板）
 * work_flow_nodes： 流程节点表（模板）
 * work_flow_run_nodes：流程节点数据表（实例数据，从work_flow_nodes复制出来的，新审批此表有记录，审批结束此表记录删除）
 * work_flow_auditlog：审批历史
 * work_flow_form_field：流程表单设计（动态存取每种表单有哪些字段）
 * work_flow_run_handler：存储我审批过的人员，用于与工单表关联查询，查询出我审批过的工单
 * work_order：工单表，每发起一个流程，新加一条工单记录
 * work_flow_run_success_send：抄送人员列表（目前只设计了表，未实现相关代码，很简单，只要在流程结束时将要抄送的人员往这个表插数据即可。）
 * 
 * @author jxlhl
 */
@SpringBootApplication
@MapperScan(basePackages = "cn.gzsendi.**.mapper")
public class DemoApplicationStart {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplicationStart.class, args);
	}

}
