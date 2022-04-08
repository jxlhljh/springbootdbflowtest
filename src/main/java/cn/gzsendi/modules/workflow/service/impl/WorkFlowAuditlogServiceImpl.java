package cn.gzsendi.modules.workflow.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import cn.gzsendi.modules.workflow.mapper.WorkFlowAuditlogMapper;
import cn.gzsendi.modules.workflow.model.WorkFlowAuditlog;
import cn.gzsendi.modules.workflow.service.IWorkFlowAuditlogService;

@Service
public class WorkFlowAuditlogServiceImpl implements IWorkFlowAuditlogService{
	
	@Autowired
	private WorkFlowAuditlogMapper workFlowAuditlogMapper;
	
	@Override
	public List<WorkFlowAuditlog> listWorkFlowAuditlogByOrderId(String orderId) {
		
		Assert.notNull(orderId, "orderId is not allowed null.");
		return workFlowAuditlogMapper.listWorkFlowAuditlogByOrderId(orderId);
		
	}

}
