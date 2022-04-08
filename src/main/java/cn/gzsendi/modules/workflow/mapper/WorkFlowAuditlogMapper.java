package cn.gzsendi.modules.workflow.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import cn.gzsendi.modules.workflow.model.WorkFlowAuditlog;

public interface WorkFlowAuditlogMapper {
	
	/**新增一条审批意见记录*/
	public int addWorkFlowAuditlog(WorkFlowAuditlog workFlowAuditlog);
	
	/**查询审批历史记录*/
	public List<WorkFlowAuditlog> listWorkFlowAuditlogByOrderId(@Param("orderId") String orderId);

}
