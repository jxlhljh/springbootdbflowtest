package cn.gzsendi.modules.workflow.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import cn.gzsendi.modules.framework.model.RequestParams;
import cn.gzsendi.modules.framework.model.Result;
import cn.gzsendi.modules.workflow.model.WorkFlowRunNodes;
import cn.gzsendi.modules.workflow.model.dto.WorkOrderQueryDto;
import cn.gzsendi.modules.workflow.service.IWorkFlowRunNodesService;

@RestController
@RequestMapping("/WorkFlowRunNodes")
public class WorkFlowRunNodesController {
	
	@Autowired
	private IWorkFlowRunNodesService iWorkFlowRunNodesService;
	
	/**
	 * 根据orderId查询出当前工单正在审批中的节点记录，用于查询谁正在处理此工单（流转到哪些人手上了）
	 * @param params
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/queryWorkFlowRunNodesReadyHandler", method = RequestMethod.POST)
	public Result<Object> queryWorkFlowRunNodesReadyHandler(@RequestBody RequestParams<WorkOrderQueryDto> params) throws Exception {
		
		WorkOrderQueryDto dto = params.getData();
		Assert.notNull(dto, "data parameter is not allowed null.");
		Assert.notNull(dto.getOrderId(), "orderId parameter is not allowed null.");
		List<WorkFlowRunNodes> list = iWorkFlowRunNodesService.queryWorkFlowRunNodesReadyHandler(dto.getOrderId());
		return Result.ok(list);
	}

}
