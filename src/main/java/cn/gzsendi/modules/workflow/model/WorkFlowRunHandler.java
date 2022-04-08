package cn.gzsendi.modules.workflow.model;

import java.io.Serializable;

/**
 * 流程处理人记录
 * @author jxlhl
 */
public class WorkFlowRunHandler implements Serializable{

	private static final long serialVersionUID = -3976670422225664915L;
	
	/**id*/
	private java.lang.Integer id;

	/**工单号*/
	private String orderId;
	
	/**处理人,处理人类型为固定时，此字段有值*/
	private String handler;
	
	/**处理人,审批人名称*/
	private String handlerName;

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
	
	
}
