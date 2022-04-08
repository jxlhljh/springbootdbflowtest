package cn.gzsendi.modules.workflow.service;

import java.util.List;

import cn.gzsendi.modules.workflow.model.WorkFlowAuditlog;

public interface IWorkFlowAuditlogService {
	
	/**查询审批历史记录*/
	public List<WorkFlowAuditlog> listWorkFlowAuditlogByOrderId(String orderId);

}
