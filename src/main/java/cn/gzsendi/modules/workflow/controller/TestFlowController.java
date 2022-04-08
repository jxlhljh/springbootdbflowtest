package cn.gzsendi.modules.workflow.controller;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import cn.gzsendi.modules.framework.exception.GzsendiException;
import cn.gzsendi.modules.framework.model.RequestParams;
import cn.gzsendi.modules.framework.model.Result;
import cn.gzsendi.modules.framework.utils.JsonUtil;
import cn.gzsendi.modules.workflow.enums.OrderStatusEnum;
import cn.gzsendi.modules.workflow.mapper.WorkFlowMapper;
import cn.gzsendi.modules.workflow.model.WorkFlow;
import cn.gzsendi.modules.workflow.model.WorkOrder;
import cn.gzsendi.modules.workflow.service.IWorkOrderService;

@RestController
@RequestMapping("/testFlow")
public class TestFlowController {
	
	@Autowired
	private WorkFlowMapper workFlowMapper;
	
	@Autowired
	private IWorkOrderService iWorkOrderService;
	
	//@ApiOperation(value = "增加工单测试", httpMethod = HttpMethodContstans.POST)
	@RequestMapping(value = "/addWorkOrderTest", method = RequestMethod.POST)
	public Result<Object> addWorkOrderTest(@RequestBody RequestParams<Map<String,Object>> params) throws Exception {
		
		String jsonStr = URLDecoder.decode(JsonUtil.toJSONString(params.getData()),StandardCharsets.UTF_8.name());
		Map<String,Object> dataParam = JsonUtil.castToObject(jsonStr);
		String flowKey = JsonUtil.getString(dataParam, "flowKey");//流程Key
		String userId = JsonUtil.getString(dataParam, "userId");//用户
		String formJsonStr = JsonUtil.toJSONString(dataParam.get("formJson"));//动态表单数据
		
		Assert.notNull(flowKey, "flowKey is null.");
		Assert.notNull(userId, "userId is null.");
		Assert.notNull(formJsonStr, "formJsonStr is null.");
		
		//根据flowKey去数据库查询,数据库中不存在则报错出去
		WorkFlow flowQuery = new WorkFlow();
		flowQuery.setFlowKey(flowKey);
		List<WorkFlow> flowList = workFlowMapper.list(flowQuery);
		if(flowList.size() == 0) {
			throw new GzsendiException("不存在的flowKey: {}",flowKey);
		}
		
		String orderId = "GD"+System.currentTimeMillis();//用毫秒数模拟，正式环境改成工单ID生成服务调用，idGeneratorService.generateId(IdTypeEnum.USER_ID);
		
		/*****提交申请,测试，正式环境根据情况调整**********************************************************/
		//String userId = "admin";//申请人
		WorkOrder workOrder = new WorkOrder();
		workOrder.setOrderId(orderId);//orderId,工单ID
		workOrder.setFlowKey(flowKey);//调休申请流程的flowKey
		workOrder.setSubjectId(1);//工单所属主体的ID.关联其他表时，存其他表的记录的ID
		workOrder.setSubjectType("1");//工单所属主体的类型，关联其他表时，存其他表的类型
		workOrder.setApplicant(userId);//申请人登陆账号
		workOrder.setApplicationTime(new Date());//申请时间
		workOrder.setOrderSummary("{\"title\":\""+flowList.get(0).getFlowName()+"\",\"createTime\":\""+new Date()+"\",\"userId\":\""+userId+"\"}");
		workOrder.setOrderStatus(OrderStatusEnum.WAIT_FOR_VERIFY.getValue());//工单状态\
		workOrder.setReason("");//提单原因
		workOrder.setFormData(formJsonStr);//工单可变字段数据,json文本存储
		workOrder.setRemark("");//remark
		workOrder.setCurrentNodeName("");//当前流程节点名称
		workOrder.setCreateUser(userId);
		workOrder.setCreateTime(new Date());
		workOrder.setUpdateUser(userId);
		workOrder.setUpdateTime(new Date());
		
		//新加表单并启动流程
		iWorkOrderService.addWorkOrder(workOrder);
		
		return Result.ok(workOrder);
	}

}
