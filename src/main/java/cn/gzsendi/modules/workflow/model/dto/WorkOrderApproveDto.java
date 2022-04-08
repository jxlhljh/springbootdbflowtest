package cn.gzsendi.modules.workflow.model.dto;

import java.io.Serializable;

/**
 * 工单审核表单信息Dto
 * @author jxlhl
 */
public class WorkOrderApproveDto implements Serializable{

	private static final long serialVersionUID = -3087975491004343845L;
	
	private String userId;//当前用户Id
	
	private String orderId;//工单Id
	
	private int flowNodeId;//节点Id
	
	private String agree;//是否通过审核
	
	private String comment;//审批意见

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

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

	public String getAgree() {
		return agree;
	}

	public void setAgree(String agree) {
		this.agree = agree;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

}
