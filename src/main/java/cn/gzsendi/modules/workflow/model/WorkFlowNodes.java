package cn.gzsendi.modules.workflow.model;


import java.io.Serializable;
/**   
 * @Description: work_flow_nodes
 * @author liujh
 * @date 2022-04-07 10:22:41
 * @version V1.0   
 */
public class WorkFlowNodes implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	/**id*/
	private java.lang.Integer id;
	
	//"流程定义key")
	private java.lang.String flowKey;
	
	//"流程节点名称")
	private java.lang.String nodeName;
	
	//"simple简单结点，togethersign会签，orsign或签，conditionsign条件结点")
	private java.lang.String nodeType;
	
	//"父节点Id，为0的为主要流程节点")
	private java.lang.Integer parentNodeId;
	
	//"节点顺序，从1开始，中间不能间隔，只有父节点Id为0的才有可能值不为0.")
	private java.lang.Integer nodeOrder;
	
	//"处理人,处理人类型为固定时，此字段有值")
	private java.lang.String handler;
	
	//"处理人类型，fixed固定，fromForm从表单选择，mapTo根据发起人计算出审批人")
	private java.lang.String handlerType;
	
	//"备注")
	private java.lang.String remark;
	
	//页面排序号
	private int pageOrder;

	public int getPageOrder() {
		return pageOrder;
	}

	public void setPageOrder(int pageOrder) {
		this.pageOrder = pageOrder;
	}

	public java.lang.Integer getId() {
		return id;
	}

	public void setId(java.lang.Integer id) {
		this.id = id;
	}

	public java.lang.String getFlowKey() {
		return flowKey;
	}

	public void setFlowKey(java.lang.String flowKey) {
		this.flowKey = flowKey;
	}

	public java.lang.String getNodeName() {
		return nodeName;
	}

	public void setNodeName(java.lang.String nodeName) {
		this.nodeName = nodeName;
	}

	public java.lang.String getNodeType() {
		return nodeType;
	}

	public void setNodeType(java.lang.String nodeType) {
		this.nodeType = nodeType;
	}

	public java.lang.Integer getParentNodeId() {
		return parentNodeId;
	}

	public void setParentNodeId(java.lang.Integer parentNodeId) {
		this.parentNodeId = parentNodeId;
	}

	public java.lang.Integer getNodeOrder() {
		return nodeOrder;
	}

	public void setNodeOrder(java.lang.Integer nodeOrder) {
		this.nodeOrder = nodeOrder;
	}

	public java.lang.String getHandler() {
		return handler;
	}

	public void setHandler(java.lang.String handler) {
		this.handler = handler;
	}

	public java.lang.String getHandlerType() {
		return handlerType;
	}

	public void setHandlerType(java.lang.String handlerType) {
		this.handlerType = handlerType;
	}

	public java.lang.String getRemark() {
		return remark;
	}

	public void setRemark(java.lang.String remark) {
		this.remark = remark;
	}
	
	
}
