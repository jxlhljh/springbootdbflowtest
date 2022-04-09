package cn.gzsendi.modules.workflow.service;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import cn.gzsendi.modules.framework.model.PageResult;
import cn.gzsendi.modules.framework.model.RequestParams;
import cn.gzsendi.modules.workflow.model.WorkFlow;
import cn.gzsendi.modules.workflow.model.dto.ChangeApproveToOtherDto;

public interface IWorkFlowService {
	
	/**列表查询*/
	public PageResult<WorkFlow> list(RequestParams<WorkFlow> params);
	
	/**根据主键查询*/
	public WorkFlow getById(Integer id);
	
	/**新增*/
	public int insert(WorkFlow workFlow);
	
	/**修改*/
	public int update(WorkFlow workFlow);
	
	/**删除*/
	public int delete(@Param("ids") List<Integer> ids);
	
	/**启动流程*/
	public void startWorkFlow(String userId,String userName,String orderId,String flowKey,Map<Integer,String> approveUserVariables);
	
	/**审批流程，同意通过或拒绝不通过*/
    public void examAndApprove(String userId,String userName,String orderId, int flowNodeId, String agree,String comment);
    
    /**工单被打回后修改，然后重新修改提交时被调用*/
    public void updateAndReSumit(String userId,String userName,String orderId);
    
	/**将工单转换给其他人进行处理，本人也同时还能继续审批，代理人或本人审批完都算完成*/
	public void changeApproveToOther(ChangeApproveToOtherDto dto);
	
	/**取消将工单转派*/
	public void cancleChangeApproveToOther(ChangeApproveToOtherDto dto);
    
}
