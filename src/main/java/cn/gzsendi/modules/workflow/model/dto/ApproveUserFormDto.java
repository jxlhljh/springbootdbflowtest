package cn.gzsendi.modules.workflow.model.dto;

import java.io.Serializable;

/**
 * 界面上选择的审批人模型对象
 * @author jxlhl
 */
public class ApproveUserFormDto implements Serializable{
	
	private static final long serialVersionUID = 7481319098256581868L;
	private int flowNodeId;
	private String userId;
	
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

}
