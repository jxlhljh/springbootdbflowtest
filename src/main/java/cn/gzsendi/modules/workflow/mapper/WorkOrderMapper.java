package cn.gzsendi.modules.workflow.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import cn.gzsendi.modules.workflow.model.WorkOrder;
import cn.gzsendi.modules.workflow.model.dto.MyDealWorkOrderDto;

/**   
 * @Description: work_order
 * @author liujh
 * @date 2022-04-03 16:44:42
 * @version V1.0   
 */
public interface WorkOrderMapper{
	
	/**列表查询*/
	public List<WorkOrder> list(WorkOrder workOrder);
	
	/**待我处理的工单查询*/
	public List<MyDealWorkOrderDto> listMyDeal(@Param("userId") String userId);
	
	/**我申请的但被打回需要重新修改提交的的工单列表*/
	public List<WorkOrder> listMyUpdateAndReSumit(@Param("userId") String userId);
	
	/**listMyApply我申请的工单查询*/
	public List<WorkOrder> listMyApply(@Param("userId") String userId);
	
	/**我已处理的的工单查询*/
	public List<WorkOrder> listMyExamAndApprove(@Param("userId") String userId);
	
	/**根据主键查询*/
	public WorkOrder queryById(Integer id);
	
	public WorkOrder queryByOrderId(@Param("orderId") String orderId);
	
	/**新增*/
	public int insert(WorkOrder workOrder);
	
	/**修改*/
	public int update(WorkOrder workOrder);
	
	public int updateCurrentNodeName(WorkOrder workOrder);
	
	/**删除*/
	public int delete(@Param("ids") List<Integer> ids);
	
	
}


