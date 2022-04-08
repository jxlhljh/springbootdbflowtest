package cn.gzsendi.modules.workflow.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import cn.gzsendi.modules.workflow.model.WorkFlowRunNodes;

public interface WorkFlowRunNodesMapper {

	/**根据orderId和flowKey查询work_flow_run_nodes中是否有记录*/
	public int countWorkFlowRunNodes(@Param("orderId") String orderId,@Param("flowKey") String flowKey);
	
	/**添加多个workFlowRunNodes结点*/
	public void addWorkFlowRunNodes(@Param("workFlowRunNodes") List<WorkFlowRunNodes> workFlowRunNodes);
	
	/**根据orderId查询出所有的work_flow_run_nodes记录*/
	public List<WorkFlowRunNodes> queryWorkFlowRunNodes(@Param("orderId") String orderId);
	
	/**根据orderId查询出当前工单正在审批中的节点记录，用于查询谁正在处理此工单（流转到哪些人手上了）*/
	public List<WorkFlowRunNodes> queryWorkFlowRunNodesReadyHandler(@Param("orderId") String orderId);
	
	/**根据orderId和flowNodeId进行更新nodeStatus*/
	public void updateWorkFlowRunNodeNodeStatus(WorkFlowRunNodes workFlowRunNode);
	
	/**根据orderId进行更新nodeStatus为future,审批被打回重新修改时被调用*/
	public void updateAllNodeStatusFuture(@Param("orderId") String orderId);
	
	/**根据orderId删除所有的work_flow_run_nodes记录*/
	public void deleteWorkFlowRunNodeNode(@Param("orderId") String orderId);
	
	/**根据orderId获取首环节的主流程结点记录*/
	public List<WorkFlowRunNodes> getFirstMainRunNode(WorkFlowRunNodes workFlowRunNode);
	
	/**根据orderId获取首环节的流程结点下的子节点记录*/
	public List<WorkFlowRunNodes> getChildRunNodes(WorkFlowRunNodes workFlowRunNode);
	
}
