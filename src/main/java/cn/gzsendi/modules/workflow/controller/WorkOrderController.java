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

import cn.gzsendi.modules.framework.exception.GzsendiException;
import cn.gzsendi.modules.framework.model.PageResult;
import cn.gzsendi.modules.framework.model.RequestParams;
import cn.gzsendi.modules.framework.model.Result;
import cn.gzsendi.modules.framework.utils.JsonUtil;
import cn.gzsendi.modules.workflow.model.WorkOrder;
import cn.gzsendi.modules.workflow.model.dto.ApproveUserFormDto;
import cn.gzsendi.modules.workflow.model.dto.ChangeApproveToOtherDto;
import cn.gzsendi.modules.workflow.model.dto.MyDealWorkOrderDto;
import cn.gzsendi.modules.workflow.model.dto.WorkOrderApproveDto;
import cn.gzsendi.modules.workflow.model.dto.WorkOrderQueryDto;
import cn.gzsendi.modules.workflow.service.IWorkFlowService;
import cn.gzsendi.modules.workflow.service.IWorkOrderService;

@RestController
@RequestMapping("/workOrder")
public class WorkOrderController {
	
	@Autowired
	private IWorkOrderService iWorkOrderService;
	
	@Autowired
	private IWorkFlowService workFlowService;
	
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	public Result<Object> list(@RequestBody RequestParams<WorkOrder> params) throws Exception {
		
		PageResult<WorkOrder> list = iWorkOrderService.list(params);
		
		return Result.ok(list);
	}
	
	//根据id查询", httpMethod = HttpMethodContstans.POST)
	@RequestMapping(value = "/queryById", method = RequestMethod.POST)
	public Result<Object> queryById(@RequestBody RequestParams<Map<String,Object>> params) throws Exception {
		
		WorkOrder workOrder = iWorkOrderService.queryById(JsonUtil.getInteger(params.getData(), "id"));
		
		return Result.ok(workOrder);
	}
	
	//根据orderId查询", httpMethod = HttpMethodContstans.POST)
	@RequestMapping(value = "/queryByOrderId", method = RequestMethod.POST)
	public Result<Object> queryByOrderId(@RequestBody RequestParams<WorkOrderQueryDto> params) throws Exception {
		
		WorkOrderQueryDto dto = params.getData();
		Assert.notNull(dto,"data Parameter is null.");
		String orderId = dto.getOrderId();
		Assert.notNull(orderId,"orderId Parameter is null.");
		WorkOrder workOrder = iWorkOrderService.queryByOrderId(orderId);
		
		return Result.ok(workOrder);
	}
	
	//增加工单", httpMethod = HttpMethodContstans.POST)
	@RequestMapping(value = "/addWorkOrder", method = RequestMethod.POST)
	public Result<Object> addWorkOrder(@RequestBody RequestParams<Map<String,Object>> params) throws Exception {
		
		String jsonStr = URLDecoder.decode(JsonUtil.toJSONString(params.getData()),StandardCharsets.UTF_8.name());
		Map<String,Object> dataParam = JsonUtil.castToObject(jsonStr);
		String flowKey = JsonUtil.getString(dataParam, "flowKey");//流程Key
		String userId = JsonUtil.getString(dataParam, "userId");//用户
		String formJsonStr = JsonUtil.toJSONString(dataParam.get("formJson"));//动态表单数据
		
		Assert.notNull(flowKey, "flowKey is null.");
		Assert.notNull(userId, "userId is null.");
		Assert.notNull(formJsonStr, "formJsonStr is null.");
		
		//表单上传上来的审批人列表，以流程的flowNodeId为key,审批人用户账号为value,用于替换流程配置中的fromForm的值
		List<ApproveUserFormDto> approveUserList =  JsonUtil.getList(dataParam, "approveUserList", ApproveUserFormDto.class);
		Map<Integer,String> approveUserVariables = new HashMap<Integer, String>();
		if(approveUserList.size()>0){
			for(ApproveUserFormDto dto : approveUserList){
				approveUserVariables.put(dto.getFlowNodeId(), dto.getUserId());
			}
		}
		
		String orderId = "GD"+System.currentTimeMillis();//用毫秒数模拟，正式环境改成工单ID生成服务调用，idGeneratorService.generateId(IdTypeEnum.USER_ID);
		
		//新加表单并启动流程
		WorkOrder workOrder = iWorkOrderService.addWorkOrder(userId, orderId, flowKey, 1, "1", formJsonStr,approveUserVariables);
		
		return Result.ok(workOrder);
		
	}

	//更新", httpMethod = HttpMethodContstans.POST)
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public Result<Object> update(@RequestBody RequestParams<WorkOrder> params) throws Exception {
		
		Assert.notNull(params.getData().getId(), "id is not allowed null.");
		
		iWorkOrderService.update(params.getData());
		
		return Result.ok();
	}
	
	//删除", httpMethod = HttpMethodContstans.POST)
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public Result<Object> delete(@RequestBody RequestParams<Map<String,Object>> params) throws Exception {
		
		List<Integer> idList = JsonUtil.getList(params.getData(), "ids", Integer.class);
		
		iWorkOrderService.delete(idList);
		
		return Result.ok();
	}
	
	//工单转派给其他人员处理
	//changeApproveToOther
	//WorkFlowRunNodes的状态为ready是才可以转派
	@RequestMapping(value = "/changeApproveToOther", method = RequestMethod.POST)
	public Result<Object> changeApproveToOther(@RequestBody RequestParams<ChangeApproveToOtherDto> params) throws Exception {
		
		Assert.notNull(params.getData(), "data Parameter is null.");
	
		ChangeApproveToOtherDto dto = params.getData();

		//进行转派处理
		workFlowService.changeApproveToOther(dto);
		
		return Result.ok();
		
	}
	
	//工单取消转派
	//changeApproveToOther
	@RequestMapping(value = "/cancleChangeApproveToOther", method = RequestMethod.POST)
	public Result<Object> cancleChangeApproveToOther(@RequestBody RequestParams<ChangeApproveToOtherDto> params) throws Exception {
		
		Assert.notNull(params.getData(), "data Parameter is null.");
		ChangeApproveToOtherDto dto = params.getData();

		//进行取消转派处理
		workFlowService.cancleChangeApproveToOther(dto);
		
		return Result.ok();
		
	}
	
	
	
	//工单被打回后修改，然后重新修改提交时被调用
	//updateAndReSumit
	@RequestMapping(value = "/updateAndReSumit", method = RequestMethod.POST)
	public Result<Object> updateAndReSumit(@RequestBody RequestParams<Map<String,Object>> params) throws Exception {
		
		Assert.notNull(params.getData(), "data Parameter is null.");
	
		String jsonStr = URLDecoder.decode(JsonUtil.toJSONString(params.getData()),StandardCharsets.UTF_8.name());
		Map<String,Object> dataParam = JsonUtil.castToObject(jsonStr);
		String orderId = JsonUtil.getString(dataParam, "orderId");//orderId
		String userId = JsonUtil.getString(dataParam, "userId");//用户
		String formJsonStr = JsonUtil.toJSONString(dataParam.get("formJson"));//动态表单数据
		
		Assert.notNull(orderId, "orderId Parameter is null.");
		Assert.notNull(userId, "userId Parameter is null.");
		Assert.notNull(formJsonStr, "formJsonStr Parameter is null.");
		
		WorkOrder dto = new WorkOrder();
		dto.setApplicant(userId);
		dto.setOrderId(orderId);
		dto.setFormData(formJsonStr);
		iWorkOrderService.updateAndReSumit(dto);
		
		return Result.ok();
		
	}
	
	//工单审核
	//examAndApprove
	@RequestMapping(value = "/examAndApprove", method = RequestMethod.POST)
	public Result<Object> examAndApprove(@RequestBody RequestParams<WorkOrderApproveDto> params) throws Exception {
	
		WorkOrderApproveDto dto = params.getData();
		Assert.notNull(dto,"data Parameter is null.");
		
		String userId = dto.getUserId();
		String orderId = dto.getOrderId();
		int flowNodeId = dto.getFlowNodeId();
		if(flowNodeId == 0) {
			throw new GzsendiException("flowNodeId Parameter is null.");
		}
		String agree = dto.getAgree();
		String comment = dto.getComment() == null ? "" : dto.getComment();
		Assert.notNull(dto,"userId Parameter is null.");
		Assert.notNull(dto,"orderId Parameter is null.");
		Assert.notNull(dto,"agree Parameter is null.");
		
		//调用service进行审批操作
		workFlowService.examAndApprove(userId, userId, orderId, flowNodeId, agree, comment);
		
		return Result.ok();
		
	}
	
	
	//待我处理的工单列表
	@RequestMapping(value = "/listMyDeal", method = RequestMethod.POST)
	public Result<Object> listMyDeal(@RequestBody RequestParams<WorkOrderQueryDto> params) throws Exception {
		
		WorkOrderQueryDto dto = params.getData();
		Assert.notNull(dto,"data Parameter is null.");
		//正式环境改成读取当前用户，这里采用从request.parameter中取当前用户
		String userId = dto.getUserId();
		Assert.notNull(userId,"userId Parameter is null.");
		
		//列出待我处理的工单
		PageResult<MyDealWorkOrderDto> list = iWorkOrderService.listMyDeal(params);
		
		return Result.ok(list);
	}
	
	//我申请的但被打回需要重新修改提交的的工单列表
	@RequestMapping(value = "/listMyUpdateAndReSumit", method = RequestMethod.POST)
	public Result<Object> listMyUpdateAndReSumit(@RequestBody RequestParams<WorkOrder> params) throws Exception {
		
		WorkOrder dto = params.getData();
		Assert.notNull(dto,"data Parameter is null.");
		//正式环境改成读取当前用户，这里采用从读取申请人进和使用
		String userId = dto.getApplicant();
		Assert.notNull(userId,"userId Parameter is null.");
		
		//列出我申请的但被打回需要重新修改提交的的工单列表
		PageResult<WorkOrder> list = iWorkOrderService.listMyUpdateAndReSumit(params);
		
		return Result.ok(list);
	}
	
	//我申请的的工单列表
	@RequestMapping(value = "/listMyApply", method = RequestMethod.POST)
	public Result<Object> listMyApply(@RequestBody RequestParams<WorkOrderQueryDto> params) throws Exception {
		
		WorkOrderQueryDto dto = params.getData();
		Assert.notNull(dto,"data Parameter is null.");
		//正式环境改成读取当前用户，这里采用从request.parameter中取当前用户
		String userId = dto.getUserId();
		Assert.notNull(userId,"userId Parameter is null.");
		
		//列出待我处理的工单
		PageResult<WorkOrder> list = iWorkOrderService.listMyApply(params);
		
		return Result.ok(list);
	}
	
	//我已处理的的工单列表
	@RequestMapping(value = "/listMyExamAndApprove", method = RequestMethod.POST)
	public Result<Object> listMyExamAndApprove(@RequestBody RequestParams<WorkOrderQueryDto> params) throws Exception {
		
		WorkOrderQueryDto dto = params.getData();
		Assert.notNull(dto,"data Parameter is null.");
		//正式环境改成读取当前用户，这里采用从request.parameter中取当前用户
		String userId = dto.getUserId();
		Assert.notNull(userId,"userId Parameter is null.");
		
		//列出已处理的的工单列表
		PageResult<WorkOrder> list = iWorkOrderService.listMyExamAndApprove(params);
		
		return Result.ok(list);
	}
	
}

