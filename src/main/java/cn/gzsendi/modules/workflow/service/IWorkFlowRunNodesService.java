package cn.gzsendi.modules.workflow.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import cn.gzsendi.modules.workflow.model.WorkFlowRunNodes;

public interface IWorkFlowRunNodesService {
	
	/**根据orderId查询出当前工单正在审批中的节点记录，用于查询谁正在处理此工单（流转到哪些人手上了）*/
	public List<WorkFlowRunNodes> queryWorkFlowRunNodesReadyHandler(@Param("orderId") String orderId);

}
