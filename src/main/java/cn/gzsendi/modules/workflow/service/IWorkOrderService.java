package cn.gzsendi.modules.workflow.service;

import java.util.List;
import java.util.Map;

import cn.gzsendi.modules.framework.model.PageResult;
import cn.gzsendi.modules.framework.model.RequestParams;
import cn.gzsendi.modules.workflow.model.WorkOrder;
import cn.gzsendi.modules.workflow.model.dto.MyDealWorkOrderDto;
import cn.gzsendi.modules.workflow.model.dto.WorkOrderQueryDto;

public interface IWorkOrderService {

	/**列表查询*/
	public PageResult<WorkOrder> list(RequestParams<WorkOrder> params);
	
	/**根据主键查询*/
	public WorkOrder queryById(Integer id);
	
	/**根据orderId查询*/
	public WorkOrder queryByOrderId(String orderId);
	
	/**新增工单并启动流程*/
	public int addWorkOrder(WorkOrder workOrder);
	
	/**新增工单并启动流程*/
	//approveUserVariables);页面传上来的审批人列表
	public WorkOrder addWorkOrder(String userId,String orderId,String flowKey,int subjectId,String subjectType,String formJsonStr,Map<Integer,String> approveUserVariables);
	
	/**修改*/
	public int update(WorkOrder workOrder);
	
	/**删除*/
	public int delete(List<Integer> ids);
	
	/**修改并重新提交审批*/
	public int updateAndReSumit(WorkOrder workOrder);
	
	/**列表查询，我处理的*/
	public PageResult<MyDealWorkOrderDto> listMyDeal(RequestParams<WorkOrderQueryDto> params);
	
	/**我申请的但被打回需要重新修改提交的的工单列表*/
	public PageResult<WorkOrder> listMyUpdateAndReSumit(RequestParams<WorkOrder> params);
	
	/**listMyApply我申请的工单查询*/
	public PageResult<WorkOrder> listMyApply(RequestParams<WorkOrderQueryDto> params);
	
	/**我已处理的的工单查询*/
	public PageResult<WorkOrder> listMyExamAndApprove(RequestParams<WorkOrderQueryDto> params);	
	
	
}
