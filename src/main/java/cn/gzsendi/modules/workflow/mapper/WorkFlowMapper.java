package cn.gzsendi.modules.workflow.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import cn.gzsendi.modules.workflow.model.WorkFlow;

public interface WorkFlowMapper {
	
	/**列表查询*/
	public List<WorkFlow> list(WorkFlow workFlow);
	
	/**根据主键查询*/
	public WorkFlow getById(Integer id);
	
	/**新增*/
	public int insert(WorkFlow workFlow);
	
	/**修改*/
	public int update(WorkFlow workFlow);
	
	/**删除*/
	public int delete(@Param("ids") List<Integer> ids);

}
