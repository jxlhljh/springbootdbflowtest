package cn.gzsendi.modules.workflow.model.dto;

import java.io.Serializable;

public class WorkOrderQueryDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7181769961637520390L;

	private String userId;//
	
	private String orderId;//工单Id

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
}
