package cn.gzsendi.modules.workflow.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import cn.gzsendi.modules.workflow.model.WorkFlowFormField;

/**   
 * @Description: work_flow_from_field
 * @author liujh
 * @date 2022-04-06 13:54:03
 * @version V1.0   
 */
public interface WorkFlowFormFieldMapper{
	
	/**列表查询*/
	public List<WorkFlowFormField> list(WorkFlowFormField workFlowFormField);
	
	/**根据主键查询*/
	public WorkFlowFormField queryById(Integer id);
	
	/**新增*/
	public int insert(WorkFlowFormField workFlowFormField);
	
	/**修改*/
	public int update(WorkFlowFormField workFlowFormField);
	
	/**删除*/
	public int delete(@Param("ids") List<Integer> ids);
	
	
}


