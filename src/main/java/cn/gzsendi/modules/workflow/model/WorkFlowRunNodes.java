package cn.gzsendi.modules.workflow.model;

import java.io.Serializable;

/**
 * 工作流程实例，运行时节点
 * @author jxlhl
 */
public class WorkFlowRunNodes implements Serializable{
	
	private static final long serialVersionUID = 6990321204908413551L;

	/**id*/
	private java.lang.Integer id;
	
	/**工单号*/
	private String orderId;

	/**流程节点Id*/
	private int flowNodeId;	
	
	/**流程定义key*/
	private String flowKey;	
	
	/**流程节点名称*/
	private String nodeName;
	
	/**流程节点类型，simple简单结点，togethersign会签，orsign或签，conditionsign条件结点*/
	private String nodeType;
	
	/**父节点Id，为0的为主要流程节点*/
	private int parentNodeId;

	/**节点顺序，从1开始，中间不能间隔，只有父节点Id为0的才有可能值不为0.*/
	private int nodeOrder;
	
	/**节点状态：ready已准备好处理中，complete完成,waiting等待（复杂结点才有）,skip略过不处理的，future未走到的*/
	private String nodeStatus;
	
	/**处理人,处理人类型为固定时，此字段有值*/
	private String handler;
	
	/**处理人类型，fixed固定，fromForm从表单选择，mapTo根据发起人计算出审批人*/
	private String handlerType;
	
	/**备注*/
	private String remark;

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

	public int getFlowNodeId() {
		return flowNodeId;
	}

	public void setFlowNodeId(int flowNodeId) {
		this.flowNodeId = flowNodeId;
	}

	public String getFlowKey() {
		return flowKey;
	}

	public void setFlowKey(String flowKey) {
		this.flowKey = flowKey;
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public String getNodeType() {
		return nodeType;
	}

	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}

	public int getParentNodeId() {
		return parentNodeId;
	}

	public void setParentNodeId(int parentNodeId) {
		this.parentNodeId = parentNodeId;
	}

	public int getNodeOrder() {
		return nodeOrder;
	}

	public void setNodeOrder(int nodeOrder) {
		this.nodeOrder = nodeOrder;
	}

	public String getNodeStatus() {
		return nodeStatus;
	}

	public void setNodeStatus(String nodeStatus) {
		this.nodeStatus = nodeStatus;
	}

	public String getHandler() {
		return handler;
	}

	public void setHandler(String handler) {
		this.handler = handler;
	}

	public String getHandlerType() {
		return handlerType;
	}

	public void setHandlerType(String handlerType) {
		this.handlerType = handlerType;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}
