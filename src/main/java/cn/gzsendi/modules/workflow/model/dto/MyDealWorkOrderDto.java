package cn.gzsendi.modules.workflow.model.dto;

import java.io.Serializable;

import cn.gzsendi.modules.workflow.model.WorkOrder;

/**
 * 待我处理的工单列表页面用的DTO
 * @author jxlhl
 */
public class MyDealWorkOrderDto extends WorkOrder implements Serializable{

	private static final long serialVersionUID = 1649018318049001643L;
	
	private int flowNodeId;//流程节点Id

	public int getFlowNodeId() {
		return flowNodeId;
	}

	public void setFlowNodeId(int flowNodeId) {
		this.flowNodeId = flowNodeId;
	}

}
