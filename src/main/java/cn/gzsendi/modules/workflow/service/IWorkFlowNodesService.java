package cn.gzsendi.modules.workflow.service;

import java.util.List;

import cn.gzsendi.modules.framework.model.PageResult;
import cn.gzsendi.modules.framework.model.RequestParams;
import cn.gzsendi.modules.workflow.model.WorkFlowNodes;

public interface IWorkFlowNodesService {

	/**列表查询*/
	public PageResult<WorkFlowNodes> list(RequestParams<WorkFlowNodes> params);
	
	/**根据主键查询*/
	public WorkFlowNodes queryById(Integer id);
	
	/**新增*/
	public int insert(WorkFlowNodes workFlowNodes);
	
	/**修改*/
	public int update(WorkFlowNodes workFlowNodes);
	
	/**删除*/
	public int delete(List<Integer> ids);
	
}
