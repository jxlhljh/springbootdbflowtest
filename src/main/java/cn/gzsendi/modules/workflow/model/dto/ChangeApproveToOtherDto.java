package cn.gzsendi.modules.workflow.model.dto;

import java.io.Serializable;

/**
 * 转派流程用的DTO
 * @author jxlhl
 */
public class ChangeApproveToOtherDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3126190211820796721L;
	
	private String orderId;//工单ID
	private int flowNodeId;//运行时流程节点的flowNodeId
	private String userId;//当前处理人员
	private String otherUserId;//被转派的人员账号
	
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public int getFlowNodeId() {
		return flowNodeId;
	}
	public void setFlowNodeId(int flowNodeId) {
		this.flowNodeId = flowNodeId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getOtherUserId() {
		return otherUserId;
	}
	public void setOtherUserId(String otherUserId) {
		this.otherUserId = otherUserId;
	}
	
	

}
