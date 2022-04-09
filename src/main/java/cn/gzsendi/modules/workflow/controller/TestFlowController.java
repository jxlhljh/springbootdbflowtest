package cn.gzsendi.modules.workflow.controller;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import cn.gzsendi.modules.framework.model.RequestParams;
import cn.gzsendi.modules.framework.model.Result;
import cn.gzsendi.modules.framework.utils.JsonUtil;
import cn.gzsendi.modules.workflow.model.WorkOrder;
import cn.gzsendi.modules.workflow.model.dto.ApproveUserFormDto;
import cn.gzsendi.modules.workflow.service.IWorkOrderService;

@RestController
@RequestMapping("/testFlow")
public class TestFlowController {
	
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
		
		//表单上传上来的审批人列表，以流程的flowNodeId为key,审批人用户账号为value,用于替换流程配置中的fromForm的值
		@SuppressWarnings("rawtypes")
		List<Map> approveUserList =  JsonUtil.getList(dataParam, "approveUserList", Map.class);
		Map<Integer,String> approveUserVariables = new HashMap<Integer, String>();
		if(approveUserList.size()>0){
			for(@SuppressWarnings("rawtypes") Map aMap : approveUserList){
				ApproveUserFormDto dto = JsonUtil.castToObject(JsonUtil.toJSONString(aMap), ApproveUserFormDto.class);
				approveUserVariables.put(dto.getFlowNodeId(), dto.getUserId());
			}
		}
		
		Assert.notNull(flowKey, "flowKey is null.");
		Assert.notNull(userId, "userId is null.");
		Assert.notNull(formJsonStr, "formJsonStr is null.");
		
		String orderId = "GD"+System.currentTimeMillis();//用毫秒数模拟，正式环境改成工单ID生成服务调用，idGeneratorService.generateId(IdTypeEnum.USER_ID);
		
		//新加表单并启动流程
		WorkOrder workOrder = iWorkOrderService.addWorkOrder(userId, orderId, flowKey, 1, "1", formJsonStr, approveUserVariables);
		
		return Result.ok(workOrder);
	}

}
