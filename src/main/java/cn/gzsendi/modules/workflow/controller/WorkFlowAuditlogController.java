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
import cn.gzsendi.modules.workflow.model.WorkFlowAuditlog;
import cn.gzsendi.modules.workflow.model.dto.WorkOrderQueryDto;
import cn.gzsendi.modules.workflow.service.IWorkFlowAuditlogService;

/**
 * 审批历史控制器
 * @author jxlhl
 *
 */
@RestController
@RequestMapping("/workFlowAuditlog")
public class WorkFlowAuditlogController {
	
	@Autowired
	private IWorkFlowAuditlogService iWorkFlowAuditlogService;
	
	/**
	 * 根据工单号查询此工单的审批历史
	 * @param params
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/listWorkFlowAuditlogByOrderId", method = RequestMethod.POST)
	public Result<Object> list(@RequestBody RequestParams<WorkOrderQueryDto> params) throws Exception {
		WorkOrderQueryDto dto = params.getData();
		Assert.notNull(dto,"data Parameter is null.");
		String orderId = dto.getOrderId();
		Assert.notNull(orderId,"orderId Parameter is null.");
		List<WorkFlowAuditlog> list = iWorkFlowAuditlogService.listWorkFlowAuditlogByOrderId(orderId);
		return Result.ok(list);
	}


}
