package cn.gzsendi.modules.workflow.model;


import java.io.Serializable;

/**   
 * @Description: work_flow_from_field
 * @author liujh
 * @date 2022-04-06 13:54:03
 * @version V1.0   
 */
public class WorkFlowFormField implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	/**id*/
	private java.lang.Integer id;
	
	//"流程定义key")
	private java.lang.String flowKey;
	
	//"字段名称")
	private java.lang.String fieldName;
	
	//"字段中文")
	private java.lang.String fieldCname;
	
	//"字段类型")
	private java.lang.String fieldType;
	
	//默认值
	private String defaultValue;
	
	//其他信息内容
	private String otherInfo;
	
	//"序号")
	private java.lang.Integer fieldOrder;

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getOtherInfo() {
		return otherInfo;
	}

	public void setOtherInfo(String otherInfo) {
		this.otherInfo = otherInfo;
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

	public java.lang.String getFieldName() {
		return fieldName;
	}

	public void setFieldName(java.lang.String fieldName) {
		this.fieldName = fieldName;
	}

	public java.lang.String getFieldCname() {
		return fieldCname;
	}

	public void setFieldCname(java.lang.String fieldCname) {
		this.fieldCname = fieldCname;
	}

	public java.lang.String getFieldType() {
		return fieldType;
	}

	public void setFieldType(java.lang.String fieldType) {
		this.fieldType = fieldType;
	}

	public java.lang.Integer getFieldOrder() {
		return fieldOrder;
	}

	public void setFieldOrder(java.lang.Integer fieldOrder) {
		this.fieldOrder = fieldOrder;
	}
	
	
}
