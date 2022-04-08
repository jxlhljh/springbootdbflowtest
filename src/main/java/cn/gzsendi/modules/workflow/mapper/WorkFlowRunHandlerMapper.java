package cn.gzsendi.modules.workflow.mapper;

import org.apache.ibatis.annotations.Param;

import cn.gzsendi.modules.workflow.model.WorkFlowRunHandler;

public interface WorkFlowRunHandlerMapper {
	
	/**添加WorkFlowRunHandler*/
	public void addWorkFlowRunHandler(WorkFlowRunHandler workFlowRunHandler);
	
	/**根据orderId及handler查询是否已有记录*/
	public int countWorkFlowRunHandler(@Param("orderId") String orderId, @Param("handler") String handler);

}
