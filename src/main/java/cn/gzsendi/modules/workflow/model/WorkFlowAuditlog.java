package cn.gzsendi.modules.workflow.model;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class WorkFlowAuditlog implements Serializable{
	private static final long serialVersionUID = 1710643682229173216L;
	
	/**id*/
	private java.lang.Integer id;
	
	/**工单号*/
	private String orderId;
	
	/**审批环节名称*/
	private String nodeName;

	/**处理人,处理人类型为固定时，此字段有值*/
	private String handler;
	
	/**处理人,审批人名称*/
	private String handlerName;
	
	/**是否审批通过，1通过,2不通过*/
	private String agree;
	
	/**审批意见*/
	private String auditInfo;
	
	/**审批时间*/
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date auditTime;

	public java.lang.Integer getId() {
		return id;
	}

	public void setId(java.lang.Integer id) {
		this.id = id;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getHandler() {
		return handler;
	}

	public void setHandler(String handler) {
		this.handler = handler;
	}

	public String getHandlerName() {
		return handlerName;
	}

	public void setHandlerName(String handlerName) {
		this.handlerName = handlerName;
	}

	public String getAgree() {
		return agree;
	}

	public void setAgree(String agree) {
		this.agree = agree;
	}

	public String getAuditInfo() {
		return auditInfo;
	}

	public void setAuditInfo(String auditInfo) {
		this.auditInfo = auditInfo;
	}

	public Date getAuditTime() {
		return auditTime;
	}

	public void setAuditTime(Date auditTime) {
		this.auditTime = auditTime;
	}


	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	
}
