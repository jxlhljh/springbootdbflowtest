package cn.gzsendi.modules.workflow.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import cn.gzsendi.modules.workflow.mapper.WorkFlowRunNodesMapper;
import cn.gzsendi.modules.workflow.model.WorkFlowRunNodes;
import cn.gzsendi.modules.workflow.service.IWorkFlowRunNodesService;

@Service
public class WorkFlowRunNodesServiceImpl implements IWorkFlowRunNodesService{
	
	@Autowired
	private WorkFlowRunNodesMapper workFlowRunNodesMapper;

	@Override
	public List<WorkFlowRunNodes> queryWorkFlowRunNodesReadyHandler(String orderId) {
		Assert.notNull(orderId, "orderId is not allowed null.");
		return workFlowRunNodesMapper.queryWorkFlowRunNodesReadyHandler(orderId);
	}

}
