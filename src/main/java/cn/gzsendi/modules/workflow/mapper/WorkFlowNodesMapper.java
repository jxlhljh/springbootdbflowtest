package cn.gzsendi.modules.workflow.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import cn.gzsendi.modules.workflow.model.WorkFlowNodes;

/**   
 * @Description: work_flow_nodes
 * @author liujh
 * @date 2022-04-07 10:22:41
 * @version V1.0   
 */
public interface WorkFlowNodesMapper{
	
	/**列表查询*/
	public List<WorkFlowNodes> list(WorkFlowNodes workFlowNodes);
	
	/**根据主键查询*/
	public WorkFlowNodes queryById(Integer id);
	
	/**新增*/
	public int insert(WorkFlowNodes workFlowNodes);
	
	/**修改*/
	public int update(WorkFlowNodes workFlowNodes);
	
	/**删除*/
	public int delete(@Param("ids") List<Integer> ids);
	
	/**根据flowKey查询work_flow_nodes记录列表并返回*/
	public List<WorkFlowNodes> queryWorkFlowNodes(@Param("flowKey") String flowKey);
	
	
}


