package cn.gzsendi.modules.workflow.model;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

/**   
 * @Description: work_order
 * @author liujh
 * @date 2022-04-03 16:44:42
 * @version V1.0   
 */
public class WorkOrder implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	/**id*/
	private java.lang.Integer id;
	
	//"工单号")
	private java.lang.String orderId;
	
	//"流程定义key")
	private java.lang.String flowKey;
	
	//"工单所属主体的ID")
	private java.lang.Integer subjectId;
	
	//"工单所属主体的类型")
	private java.lang.String subjectType;
	
	//"申请人(用户ID)")
	private java.lang.String applicant;
	
	//"申请时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date applicationTime;
	
	//"工单摘要/审批摘要")
	private java.lang.String orderSummary;
	
	//"工单状态")
	private java.lang.String orderStatus;
	
	//"提工单原因")
	private java.lang.String reason;
	
	//"工单可变字段数据,json文本存储")
	private java.lang.String formData;
	
	//"当前流程节点名称")
	private java.lang.String currentNodeName;
	
	//"备注")
	private java.lang.String remark;
	
	//"创建人员")
	private java.lang.String createUser;
	
	//"创建时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createTime;
	
	//"更新人员")
	private java.lang.String updateUser;
	
	//"更新时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date updateTime;

	public java.lang.Integer getId() {
		return id;
	}

	public void setId(java.lang.Integer id) {
		this.id = id;
	}

	public java.lang.String getOrderId() {
		return orderId;
	}

	public void setOrderId(java.lang.String orderId) {
		this.orderId = orderId;
	}

	public java.lang.String getFlowKey() {
		return flowKey;
	}

	public void setFlowKey(java.lang.String flowKey) {
		this.flowKey = flowKey;
	}

	public java.lang.Integer getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(java.lang.Integer subjectId) {
		this.subjectId = subjectId;
	}

	public java.lang.String getSubjectType() {
		return subjectType;
	}

	public void setSubjectType(java.lang.String subjectType) {
		this.subjectType = subjectType;
	}

	public java.lang.String getApplicant() {
		return applicant;
	}

	public void setApplicant(java.lang.String applicant) {
		this.applicant = applicant;
	}

	public Date getApplicationTime() {
		return applicationTime;
	}

	public void setApplicationTime(Date applicationTime) {
		this.applicationTime = applicationTime;
	}

	public java.lang.String getOrderSummary() {
		return orderSummary;
	}

	public void setOrderSummary(java.lang.String orderSummary) {
		this.orderSummary = orderSummary;
	}

	public java.lang.String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(java.lang.String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public java.lang.String getReason() {
		return reason;
	}

	public void setReason(java.lang.String reason) {
		this.reason = reason;
	}

	public java.lang.String getFormData() {
		return formData;
	}

	public void setFormData(java.lang.String formData) {
		this.formData = formData;
	}

	public java.lang.String getCurrentNodeName() {
		return currentNodeName;
	}

	public void setCurrentNodeName(java.lang.String currentNodeName) {
		this.currentNodeName = currentNodeName;
	}

	public java.lang.String getRemark() {
		return remark;
	}

	public void setRemark(java.lang.String remark) {
		this.remark = remark;
	}

	public java.lang.String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(java.lang.String createUser) {
		this.createUser = createUser;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public java.lang.String getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(java.lang.String updateUser) {
		this.updateUser = updateUser;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	
	
}
