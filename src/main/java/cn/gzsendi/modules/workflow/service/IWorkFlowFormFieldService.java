package cn.gzsendi.modules.workflow.service;

import java.util.List;

import cn.gzsendi.modules.framework.model.PageResult;
import cn.gzsendi.modules.framework.model.RequestParams;
import cn.gzsendi.modules.workflow.model.WorkFlowFormField;

public interface IWorkFlowFormFieldService {

	/**列表查询*/
	public PageResult<WorkFlowFormField> list(RequestParams<WorkFlowFormField> params);
	
	/**根据主键查询*/
	public WorkFlowFormField queryById(Integer id);
	
	/**新增*/
	public int insert(WorkFlowFormField workFlowFormField);
	
	/**修改*/
	public int update(WorkFlowFormField workFlowFormField);
	
	/**删除*/
	public int delete(List<Integer> ids);
	
}
