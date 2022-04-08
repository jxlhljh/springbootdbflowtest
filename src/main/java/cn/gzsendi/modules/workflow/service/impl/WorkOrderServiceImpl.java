package cn.gzsendi.modules.workflow.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import cn.gzsendi.modules.framework.exception.GzsendiException;
import cn.gzsendi.modules.framework.model.PageResult;
import cn.gzsendi.modules.framework.model.RequestParams;
import cn.gzsendi.modules.workflow.enums.OrderStatusEnum;
import cn.gzsendi.modules.workflow.mapper.WorkOrderMapper;
import cn.gzsendi.modules.workflow.model.WorkOrder;
import cn.gzsendi.modules.workflow.model.dto.MyDealWorkOrderDto;
import cn.gzsendi.modules.workflow.model.dto.WorkOrderQueryDto;
import cn.gzsendi.modules.workflow.service.IWorkFlowService;
import cn.gzsendi.modules.workflow.service.IWorkOrderService;

@Service
public class WorkOrderServiceImpl implements IWorkOrderService{
	
	@Autowired
	private WorkOrderMapper workOrderMapper;
	
	@Autowired
	private IWorkFlowService workFlowService;

	public PageResult<WorkOrder> list(RequestParams<WorkOrder> params) {
		
        Page<WorkOrder> page = PageHelper.startPage(params.getPageNum(), params.getPageSize());
        List<WorkOrder> list = workOrderMapper.list(params.getData());
        
		return new PageResult<WorkOrder>(page.getTotal(), list);
	}

	public WorkOrder queryById(Integer id) {
		return workOrderMapper.queryById(id);
	}
	
	/**根据orderId查询*/
	public WorkOrder queryByOrderId(String orderId){
		return workOrderMapper.queryByOrderId(orderId);
	}
	
	@Transactional
	public int addWorkOrder(WorkOrder workOrder) {
		
		String flowKey = workOrder.getFlowKey();
		String orderId = workOrder.getOrderId();
		
		//保存工单表单
		workOrderMapper.insert(workOrder);
		
		//1.启动流程
		workFlowService.startWorkFlow(workOrder.getApplicant(),workOrder.getApplicant(),orderId,flowKey);
		
		return 0;
		
	}

	/**修改并重新提交审批*/
	@Transactional
	public int updateAndReSumit(WorkOrder dto){
		
		//0.查询出数据库中的记录
		WorkOrder orderToUpdate = workOrderMapper.queryByOrderId(dto.getOrderId());
		if(orderToUpdate == null){
			throw new GzsendiException("未找到工单，orderId : " ,dto.getOrderId());
		}
		orderToUpdate.setFormData(dto.getFormData());//将formData更新
		orderToUpdate.setOrderStatus(OrderStatusEnum.WAIT_FOR_VERIFY.getValue());//订单状态重新改为审批状态
		
		//1.更新工单信息
		workOrderMapper.update(orderToUpdate);
		
		//2.将首环节结点的状态改成Ready或Waiting(simple结点改为ready，复杂结点改为waiting)，因为打回前已经把所有的状态重置为future了
		workFlowService.updateAndReSumit(orderToUpdate.getApplicant(), orderToUpdate.getApplicant(), orderToUpdate.getOrderId());
		
		return 0;
	}
	
	public int update(WorkOrder workOrder) {
		return workOrderMapper.update(workOrder);
	}

	public int delete(List<Integer> ids) {
		return workOrderMapper.delete(ids);
	}
	
	/**列表工单查询，我处理的*/
	public PageResult<MyDealWorkOrderDto> listMyDeal(RequestParams<WorkOrderQueryDto> params){
		
        Page<MyDealWorkOrderDto> page = PageHelper.startPage(params.getPageNum(), params.getPageSize());
        List<MyDealWorkOrderDto> list = workOrderMapper.listMyDeal(params.getData().getUserId());
        
		return new PageResult<MyDealWorkOrderDto>(page.getTotal(), list);
		
	}
	
	/**我申请的但被打回需要重新修改提交的的工单列表*/
	public PageResult<WorkOrder> listMyUpdateAndReSumit(RequestParams<WorkOrder> params){
		
		Page<WorkOrder> page = PageHelper.startPage(params.getPageNum(), params.getPageSize());
		List<WorkOrder> list = workOrderMapper.listMyUpdateAndReSumit(params.getData().getApplicant());
		return new PageResult<WorkOrder>(page.getTotal(), list);
		
	}

	/**listMyApply我申请的工单查询*/
	public PageResult<WorkOrder> listMyApply(RequestParams<WorkOrderQueryDto> params){
        Page<WorkOrder> page = PageHelper.startPage(params.getPageNum(), params.getPageSize());
        List<WorkOrder> list = workOrderMapper.listMyApply(params.getData().getUserId());
		return new PageResult<WorkOrder>(page.getTotal(), list);
	}
	
	/**我已处理的的工单查询*/
	public PageResult<WorkOrder> listMyExamAndApprove(RequestParams<WorkOrderQueryDto> params){
        Page<WorkOrder> page = PageHelper.startPage(params.getPageNum(), params.getPageSize());
        List<WorkOrder> list = workOrderMapper.listMyExamAndApprove(params.getData().getUserId());
		return new PageResult<WorkOrder>(page.getTotal(), list);
	}
	
}



