package cn.gzsendi.modules.workflow.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import cn.gzsendi.modules.framework.exception.GzsendiException;
import cn.gzsendi.modules.framework.model.PageResult;
import cn.gzsendi.modules.framework.model.RequestParams;
import cn.gzsendi.modules.workflow.enums.HandlerTypeEnum;
import cn.gzsendi.modules.workflow.enums.NodeStatusEnum;
import cn.gzsendi.modules.workflow.enums.NodeTypeEnum;
import cn.gzsendi.modules.workflow.enums.OrderStatusEnum;
import cn.gzsendi.modules.workflow.enums.TaskResultEnum;
import cn.gzsendi.modules.workflow.listener.CommonFlowTaskListener;
import cn.gzsendi.modules.workflow.mapper.WorkFlowAuditlogMapper;
import cn.gzsendi.modules.workflow.mapper.WorkFlowMapper;
import cn.gzsendi.modules.workflow.mapper.WorkFlowNodesMapper;
import cn.gzsendi.modules.workflow.mapper.WorkFlowRunHandlerMapper;
import cn.gzsendi.modules.workflow.mapper.WorkFlowRunNodesMapper;
import cn.gzsendi.modules.workflow.mapper.WorkOrderMapper;
import cn.gzsendi.modules.workflow.model.WorkFlow;
import cn.gzsendi.modules.workflow.model.WorkFlowAuditlog;
import cn.gzsendi.modules.workflow.model.WorkFlowNodes;
import cn.gzsendi.modules.workflow.model.WorkFlowRunHandler;
import cn.gzsendi.modules.workflow.model.WorkFlowRunNodes;
import cn.gzsendi.modules.workflow.model.WorkOrder;
import cn.gzsendi.modules.workflow.model.dto.ChangeApproveToOtherDto;
import cn.gzsendi.modules.workflow.service.IWorkFlowService;

@Service
public class WorkFlowServiceImpl implements IWorkFlowService{
	
	@Autowired
	private WorkFlowMapper workFlowMapper;
	
	@Autowired
	private WorkOrderMapper workOrderMapper;
	
	@Autowired
	private WorkFlowNodesMapper workFlowNodesMapper;
	
	@Autowired
	private WorkFlowRunNodesMapper workFlowRunNodesMapper;
	
	@Autowired
	private WorkFlowAuditlogMapper workFlowAuditlogMapper;
	
	@Autowired
	private WorkFlowRunHandlerMapper workFlowRunHandlerMapper;
	
	@Autowired
	private CommonFlowTaskListener commonFlowTaskListener;
	
	@Override
	public PageResult<WorkFlow> list(RequestParams<WorkFlow> params) {
		
		Page<WorkFlow> page = PageHelper.startPage(params.getPageNum(), params.getPageSize());
		List<WorkFlow> list = workFlowMapper.list(params.getData());
		
		return new PageResult<WorkFlow>(page.getTotal(), list);
		
	}
	
	/**根据主键查询*/
	public WorkFlow getById(Integer id){
		return workFlowMapper.getById(id);
	}

	@Override
	public int insert(WorkFlow workFlow) {
		return workFlowMapper.insert(workFlow);
	}

	@Override
	public int update(WorkFlow workFlow) {
		return workFlowMapper.update(workFlow);
	}
	
	@Override
	public int delete(List<Integer> ids) {
		return workFlowMapper.delete(ids);
	}

	/**启动流程*/
	@Transactional
	public void startWorkFlow(String userId,String userName,String orderId,String flowKey,Map<Integer,String> approveUserVariables){
		
		//页面传上来的审批人参数，用于替换handlerType为fromForm类型的处理人
		if(approveUserVariables ==null) approveUserVariables = new HashMap<Integer,String>();
		
		//0.work_flow_run_nodes表中如果有数据，说明此orderId已经启动了流程，直接抛异常
		int runNodeLength = workFlowRunNodesMapper.countWorkFlowRunNodes(orderId, flowKey);
		if(runNodeLength>0){
			throw new GzsendiException("流程已存在, orderId: {}", orderId);
		}
		
		//1.先通过flowKey将work_flow_nodes表中的结点流程树数据查询出来
		List<WorkFlowNodes> workFlowNodes = workFlowNodesMapper.queryWorkFlowNodes(flowKey);
		if(workFlowNodes.size() == 0) {
			throw new GzsendiException("未找到流程结点配置，flowKey is ", flowKey);
		}
		//2.将1步骤中查询出来的表记录，复制一份到work_flow_run_nodes表
		List<WorkFlowRunNodes> workFlowRunNodesList = new ArrayList<WorkFlowRunNodes>();
		//3.找出第一个结点或其子结点，将其更新为waiting或ready,
		//3.处理流程树第一个流程节点为ready状态，这样第一个环节的处理人就可以进行流程审批了
		WorkFlowRunNodes firstWorkFlowRunNode = null;//首环节结点
		Map<Integer,List<WorkFlowRunNodes>> parentIdToNodes = new HashMap<Integer,List<WorkFlowRunNodes>>();//以父结点id为key，list为value
		for(WorkFlowNodes woFlowNode : workFlowNodes){
			WorkFlowRunNodes workFlowRunNode = new WorkFlowRunNodes();
			BeanUtils.copyProperties(woFlowNode, workFlowRunNode);//先复制共同的字段
			workFlowRunNode.setId(null);//ID要清空
			workFlowRunNode.setRemark("");//备注清空，只有转派了操作后此字段有值，如changeApproveToOther[huangjc] 
			workFlowRunNode.setOrderId(orderId);
			workFlowRunNode.setFlowNodeId(woFlowNode.getId());
			//节点状态,先全部置为FUTURE
			workFlowRunNode.setNodeStatus(NodeStatusEnum.FUTURE.getValue());
			
			//同时借用循环查找出首环节结点，ParentNodeId为0表示流程上的主节点
			if(workFlowRunNode.getParentNodeId() == 0){
				if(firstWorkFlowRunNode == null) {
					firstWorkFlowRunNode = workFlowRunNode;
				}else{
					//取NodeOrder最小的节点为首结点
					if(workFlowRunNode.getNodeOrder() < firstWorkFlowRunNode.getNodeOrder()){
						firstWorkFlowRunNode = workFlowRunNode;
					}
				}
			}
			
			//处理从页面传上来的审批人数据，type为fromForm时需要从表单中获取审批人
			if(workFlowRunNode.getHandlerType().equals(HandlerTypeEnum.FROMFORM.getValue())){
				Integer flowNodeId = workFlowRunNode.getFlowNodeId();
				if(approveUserVariables.get(flowNodeId) == null) throw new GzsendiException("审批人配置了需要从表单中选择，但未找到传到后台的审批人参数数据.");
				workFlowRunNode.setHandler(approveUserVariables.get(flowNodeId) );//设置为表单上传过来的审批人
			}
			
			//处理parentIdToNodes的数据
			List<WorkFlowRunNodes> list = parentIdToNodes.get(workFlowRunNode.getParentNodeId());
			if(list == null) {
				list = new ArrayList<WorkFlowRunNodes>();
				parentIdToNodes.put(workFlowRunNode.getParentNodeId(), list);
			}
			list.add(workFlowRunNode);
			
			workFlowRunNodesList.add(workFlowRunNode);
		}
		
		//4.如果首节点为简单结点，直接更新为ready状态，否则是复杂结点，更新为waiting状态，同时查找所有的子结点进行相应的状态更新
		if(firstWorkFlowRunNode.getNodeType().equals(NodeTypeEnum.SIMPLE.getValue())){
			firstWorkFlowRunNode.setNodeStatus(NodeStatusEnum.READY.getValue());
		}else{
			setChildRunNodeStatus(firstWorkFlowRunNode,parentIdToNodes);//将子结点的状态处理成waiting或ready
		}
		
		//将流程实例的所有节点数据插入work_flow_run_nodes表
		workFlowRunNodesMapper.addWorkFlowRunNodes(workFlowRunNodesList);
		
		//4.插一条申请人提交审批的日志记录到work_flow_auditlog.
		WorkFlowAuditlog woFlowAuditlog = new WorkFlowAuditlog();
		woFlowAuditlog.setOrderId(orderId);
		woFlowAuditlog.setNodeName("申请人");//审批环节名称,重新提交环节名称为空，为空也表不是申请人的环节
		woFlowAuditlog.setHandler(userId);//当前用户账号
		woFlowAuditlog.setHandlerName(userName);//当前用户账号名称
		woFlowAuditlog.setAgree("1");//写死
		woFlowAuditlog.setAuditInfo("提交申请");//写死成提交申请
		woFlowAuditlog.setAuditTime(new Date());//申请时间
		workFlowAuditlogMapper.addWorkFlowAuditlog(woFlowAuditlog);//
		
		//5.更新工单表的当前环节结点名称信息
		WorkOrder workOrder = new WorkOrder();
		workOrder.setOrderId(orderId);
		workOrder.setOrderStatus(OrderStatusEnum.WAIT_FOR_VERIFY.getValue());
		workOrder.setCurrentNodeName(firstWorkFlowRunNode == null ? "": firstWorkFlowRunNode.getNodeName());
		workOrderMapper.updateCurrentNodeName(workOrder);
		
	}
	
    /**工单被打回后修改，然后重新修改提交时被调用*/
	@Transactional
    public void updateAndReSumit(String userId,String userName,String orderId){
		
		//1.先找到首环节的主流程上的结点，然后递归更新所有的子结点状态为future或waiting
		WorkFlowRunNodes workFlowRunNode = new WorkFlowRunNodes();
		workFlowRunNode.setOrderId(orderId);
		List<WorkFlowRunNodes> list = workFlowRunNodesMapper.getFirstMainRunNode(workFlowRunNode);
		if(list.size() == 0) {
			throw new GzsendiException("未找到首环节上的主流程节点记录,orderId : {}", orderId);
		}
    	
		//递归处理直接更新首环点及其子结点的状态为ready或waiting.
		WorkFlowRunNodes rootNode = list.get(0);
		updateNodeStatusReadyToDb(rootNode);
		
		//2.回调同步更新work_order表的当前状态
		WorkOrder workOrder = new WorkOrder();
		workOrder.setOrderId(orderId);
		workOrder.setOrderStatus(OrderStatusEnum.WAIT_FOR_VERIFY.getValue());
		workOrder.setCurrentNodeName(rootNode.getNodeName());
		workOrderMapper.updateCurrentNodeName(workOrder);
		
		//最后记录审批历史
    	//5.将审批记录记录加入work_flow_auditlog表
		WorkFlowAuditlog woFlowAuditlog = new WorkFlowAuditlog();
		woFlowAuditlog.setNodeName("申请人");//审批环节名称,重新提交环节名称为空，为空也表不是申请人的环节
		woFlowAuditlog.setOrderId(orderId);
		woFlowAuditlog.setHandler(userId);//当前用户账号
		woFlowAuditlog.setHandlerName(userName);//当前用户账号名称
		woFlowAuditlog.setAgree(String.valueOf(TaskResultEnum.EXAM_AND_APPROVE_PASS_TO_NEXT.getResult()));
		woFlowAuditlog.setAuditInfo("重新提交申请");
		woFlowAuditlog.setAuditTime(new Date());//审批时间
		workFlowAuditlogMapper.addWorkFlowAuditlog(woFlowAuditlog);//
		
    }
	
	/**将工单转换给其他人进行处理，本人也同时还能继续审批，代理人或本人审批完都算完成*/
	/**
	 * flowNodeId: 节点的Id
	 * orderId:订单Id
	 * otherUserId:转派的人的用户账号
	 * 
	 */
	@Transactional
	public void changeApproveToOther(ChangeApproveToOtherDto dto){
		
		if(dto.getFlowNodeId() == 0) throw new GzsendiException("flowNodeId Parameter is null.");
		Assert.notNull(dto.getOrderId(), "orderId Parameter is null.");
		Assert.notNull(dto.getOtherUserId(), "otherUserId Parameter is null.");
		
		//1.将数据库中的本节点查询出来先
		WorkFlowRunNodes wRunNode = workFlowRunNodesMapper.queryWorkFlowRunNodeByFlowNodeId(dto.getOrderId(), dto.getFlowNodeId());
		if(wRunNode == null) throw new GzsendiException("未找到运行中流程结点的数据，orderId:{}, flowNodeId: ", dto.getOrderId(),dto.getFlowNodeId());
		if(!wRunNode.getNodeStatus().equals(NodeStatusEnum.READY.getValue())){
			throw new GzsendiException("节点状态不是ready不能进行转派，orderId:{}, flowNodeId: ", dto.getOrderId(),dto.getFlowNodeId());
		}
		if(wRunNode.getRemark().startsWith("changeApproveToOther")){
			throw new GzsendiException("节点已经是转派的，不允许进行第二次转派，可以收回转派出重新转派，当前转派信息:{} ", wRunNode.getRemark());
		}
		
		//2.或签方式进行转派
		//转换原理：构造一个新的或签结点，然后再构造一个转换人员的审批结点，将审批人和转派代理人节点挂载到这个或签结点下面。取消转换为逆操作
		WorkFlowRunNodes orSignNode = new WorkFlowRunNodes();
		BeanUtils.copyProperties(wRunNode, orSignNode);
		WorkFlowRunNodes otherRunNode = new WorkFlowRunNodes();//转派人组成的节点
		BeanUtils.copyProperties(wRunNode, otherRunNode);
		orSignNode.setNodeStatus(NodeStatusEnum.WAITING.getValue());//复杂结点的waiting状态
		orSignNode.setHandler("");//或签结点的审批人要置为空
		orSignNode.setHandlerType("");//或签结点的审批人类型要置为空
		orSignNode.setNodeType(NodeTypeEnum.ORSIGN.getValue());//或签
		
		//本结点的父结点修改为或签结点的ID
		int nowSecond = (int)(System.currentTimeMillis()/1000);//获取到秒
		wRunNode.setParentNodeId(orSignNode.getFlowNodeId());
		wRunNode.setFlowNodeId(nowSecond);//人工构造一个flowNodeId,这里有点隐患，转派次数无限下去，可能会超int最大值，不过一般正常业务也不会多次转派，
		wRunNode.setRemark("changeApproveToOther["+dto.getOtherUserId()+"]");//备流字段记录转派人员的信息
		//转派人的父结点修改为或签结点的ID
		otherRunNode.setParentNodeId(orSignNode.getFlowNodeId());
		otherRunNode.setFlowNodeId(nowSecond + 1);//人工构造一个flowNodeId,这里有点隐患，转派次数无限下去，可能会超int最大值，不过一般正常业务也不会多次转派，
		otherRunNode.setHandler(dto.getOtherUserId());//转换人员userId
		
		workFlowRunNodesMapper.updateWorkFlowRunNodes(orSignNode);//或签节点
		List<WorkFlowRunNodes> workFlowRunNodes = new ArrayList<WorkFlowRunNodes>();
		workFlowRunNodes.add(wRunNode);//本节点
		workFlowRunNodes.add(otherRunNode);//转换人结点
		workFlowRunNodesMapper.addWorkFlowRunNodes(workFlowRunNodes);
		
		//注意：转换不用写审批历史，因为不是审批
		
	}
	
	/**取消将工单转派*/
	/**
	 * flowNodeId: 节点的Id
	 * orderId:订单Id
	 */
	@Transactional
	public void cancleChangeApproveToOther(ChangeApproveToOtherDto dto){
		
		if(dto.getFlowNodeId() == 0) throw new GzsendiException("flowNodeId Parameter is null.");
		Assert.notNull(dto.getOrderId(), "orderId Parameter is null.");
		
		//1.将数据库中的本节点查询出来先
		WorkFlowRunNodes wRunNode = workFlowRunNodesMapper.queryWorkFlowRunNodeByFlowNodeId(dto.getOrderId(), dto.getFlowNodeId());
		if(wRunNode == null) throw new GzsendiException("未找到运行中流程结点的数据，orderId:{}, flowNodeId: ", dto.getOrderId(),dto.getFlowNodeId());
		if(!wRunNode.getNodeStatus().equals(NodeStatusEnum.READY.getValue())){
			throw new GzsendiException("节点状态不是ready不能进行取消转派，orderId:{}, flowNodeId: ", dto.getOrderId(),dto.getFlowNodeId());
		}
		if(!wRunNode.getRemark().startsWith("changeApproveToOther")){
			throw new GzsendiException("节点未转派过，不允许进行取消转派操作,orderId:{}, flowNodeId: ", dto.getOrderId(),dto.getFlowNodeId());
		}
		
		//2.根据本节点找到父节点数据(转派时将普通审批结点更改成了orsign或签结点了)
		WorkFlowRunNodes parentRunNode = workFlowRunNodesMapper.queryWorkFlowRunNodeByFlowNodeId(wRunNode.getOrderId(), wRunNode.getParentNodeId());
		parentRunNode.setNodeStatus(NodeStatusEnum.READY.getValue());//复杂结点的waiting状态
		parentRunNode.setHandler(wRunNode.getHandler());//或签结点的审批人要置为空
		parentRunNode.setHandlerType(HandlerTypeEnum.FIXED.getValue());//审批人类型重新改回来fixed
		parentRunNode.setNodeType(NodeTypeEnum.SIMPLE.getValue());//或签
		parentRunNode.setRemark("");//备注清空
		workFlowRunNodesMapper.updateWorkFlowRunNodes(parentRunNode);//或签节点
		
		//3.将parentRunNode节点下的所有子节点删除，（转派的反向操作）,递归删除
		WorkFlowRunNodes query = new WorkFlowRunNodes();
		query.setOrderId(parentRunNode.getOrderId());
		query.setFlowNodeId(parentRunNode.getFlowNodeId());
		List<WorkFlowRunNodes> childs = workFlowRunNodesMapper.getChildRunNodes(query);
		if(childs != null){
    		for(WorkFlowRunNodes aRunNode : childs) {
    			deleteWorkFlowRunNodes(aRunNode);//调用递归删除子结点的方法
    		}
		}
		
	}
	
	/**审批流程，同意通过或拒绝不通过*/
	/**
	 * flowNodeId: 节点的Id
	 * orderId:订单Id
	 * agree:审批结果，1通过999不通过，
	 * comment：审批意见文字说明
	 */
	@Transactional
    public void examAndApprove(String userId,String userName,String orderId, int flowNodeId, String agree,String comment){
    	
    	//1.根据orderId获取到此工单流程的所有记录
    	List<WorkFlowRunNodes> workFlowRunNodesList = workFlowRunNodesMapper.queryWorkFlowRunNodes(orderId);
		if(workFlowRunNodesList.size() == 0) {
			throw new GzsendiException("未找到运行中流程结点的数据，orderId is {}", orderId);
		}
		
		//1.1审批结果如果是打回上一环节，直接调用打回上一环节的方法
		if(String.valueOf(TaskResultEnum.EXAM_AND_APPROVE_REJECT_TO_PRE.getResult()).equals(agree)){
			backToPreRunNode(userId, userName, orderId, flowNodeId, comment);
			return;
		}
		
		//1.2审批结果如果是不通过直接结束流程，调用直接结束流程的处理方法
		//TODO 待重构代码，目前以下代码也能正常使用，先不重构了。
		
		//遍历构造出从节点到父结点的hashMap以及从父结点找子结点的hashM
		Map<Integer,WorkFlowRunNodes> flowNodeIdToParent = new HashMap<Integer,WorkFlowRunNodes>();//根据节点flowNodeId查找到父结点的map
		Map<Integer,List<WorkFlowRunNodes>> flowNodeIdToChilds = new HashMap<Integer,List<WorkFlowRunNodes>>();//根据节点flowNodeId查找到子结点的map
		Map<Integer,WorkFlowRunNodes> flowNodeIdToWorkFlowRunNodes = new HashMap<Integer,WorkFlowRunNodes>();//根据节点flowNodeId映射自身对象
		for(WorkFlowRunNodes woFlowRunNode : workFlowRunNodesList) {
			flowNodeIdToWorkFlowRunNodes.put(woFlowRunNode.getFlowNodeId(), woFlowRunNode);
		}
		for(WorkFlowRunNodes woFlowRunNode : workFlowRunNodesList) {
			//如果能查询到父结点，放入根据节点flowNodeId查找到父结点的map
			if(flowNodeIdToWorkFlowRunNodes.get(woFlowRunNode.getParentNodeId()) != null ){
				flowNodeIdToParent.put(woFlowRunNode.getFlowNodeId(), flowNodeIdToWorkFlowRunNodes.get(woFlowRunNode.getParentNodeId()));
			}
			//根据节点flowNodeId查找到子结点的map
			List<WorkFlowRunNodes> list = flowNodeIdToChilds.get(woFlowRunNode.getParentNodeId());
			if(list == null) {
				list = new ArrayList<WorkFlowRunNodes>();
				flowNodeIdToChilds.put(woFlowRunNode.getParentNodeId(), list);
			}
			list.add(woFlowRunNode);
		}
    	
    	//2.更新当前处理的结点flowNodeId为complete状态
    	//这一步要递归遍历一直处理到父结点Id为0为止，当前结点为flowNodeId
		WorkFlowRunNodes workFlowRunNode = flowNodeIdToWorkFlowRunNodes.get(flowNodeId);
		if(workFlowRunNode == null){
			throw new GzsendiException("未找到运行中流程结点的数据，orderId is {},flowNodeId is {}", orderId, flowNodeId);
		}
		
		//当前处理的结点最后更新成正确的状态
		workFlowRunNode.setNodeStatus(NodeStatusEnum.COMPLETE.getValue());//更新结点为完成状态

		WorkFlowRunNodes tempNode = workFlowRunNode;
		while(tempNode.getParentNodeId() != 0){
			tempNode = flowNodeIdToParent.get(tempNode.getFlowNodeId());
			if(NodeTypeEnum.ORSIGN.getValue().endsWith(tempNode.getNodeType())){//或签，只要有一个子结点完成了任务，就直接更新成complete
				updateNodeStatusSkip(tempNode,flowNodeIdToChilds);//更新所有的子结点为skip或complete
			}else if(NodeTypeEnum.TOGETHERSIGN.getValue().equals(tempNode.getNodeType())){//会签，需要判断所有的子结点是否都完成了
				List<WorkFlowRunNodes> list = flowNodeIdToChilds.get(tempNode.getFlowNodeId());
				if(list!=null){
					
					boolean allComplete = true;
					for(int i=0; i<list.size();i++){
						WorkFlowRunNodes aWorkFlowRunNode = list.get(i);
						if(!aWorkFlowRunNode.getNodeStatus().equals(NodeStatusEnum.COMPLETE.getValue())){//有一个未完成，就说明还是需要是waiting状态
							allComplete = false;
							break;
						}
					}
					
					if(allComplete){
						tempNode.setNodeStatus(NodeStatusEnum.COMPLETE.getValue());
						//代码到这里说明或签或会签结点已经全部完成，更新成complete状态
						workFlowRunNodesMapper.updateWorkFlowRunNodeNodeStatus(tempNode);
					}
					
				}
				
			}
		}
		
		//最后将tempNode也处理一下，闭环
		if(NodeTypeEnum.ORSIGN.getValue().endsWith(tempNode.getNodeType())){//或签，只要有一个子结点完成了任务，就直接更新成complete
			updateNodeStatusSkip(tempNode,flowNodeIdToChilds);//更新所有的子结点为skip或complete
		}else if(NodeTypeEnum.TOGETHERSIGN.getValue().equals(tempNode.getFlowNodeId())){//会签，需要判断所有的子结点是否都完成了
			List<WorkFlowRunNodes> list = flowNodeIdToChilds.get(tempNode.getFlowNodeId());
			if(list!=null){
				
				boolean allComplete = true;
				for(int i=0; i<list.size();i++){
					WorkFlowRunNodes aWorkFlowRunNode = list.get(i);
					if(!aWorkFlowRunNode.getNodeStatus().equals(NodeStatusEnum.COMPLETE.getValue())){//有一个未完成，就说明还是需要是waiting状态
						allComplete = false;
						break;
					}
				}
				if(allComplete){
					tempNode.setNodeStatus(NodeStatusEnum.COMPLETE.getValue());
					//代码到这里说明或签或会签结点已经全部完成，更新成complete状态
					workFlowRunNodesMapper.updateWorkFlowRunNodeNodeStatus(tempNode);
				}

			}
			
		}
		
		//当前处理的结点最后更新成正确的状态，为什么把这一行代码放在这里，因上面的代码有可能会更新到这一条记录，为了最新更新这一行记录，放在这里执行保证正确性
		workFlowRunNode.setNodeStatus(NodeStatusEnum.COMPLETE.getValue());//更新结点为完成状态
		workFlowRunNodesMapper.updateWorkFlowRunNodeNodeStatus(workFlowRunNode);
		
		//当前处理节点的主结点（主流程上的结点）
		WorkFlowRunNodes curentMainNode = tempNode;
		WorkFlowRunNodes nextRunNode = null;//下一环节主结点
		
		//如果当前主环节结点变成完成状态，流转到下一个结点
		boolean endFlow = false;//流程是否结束
		
		if(String.valueOf(TaskResultEnum.EXAM_AND_APPROVE_REJECT_TO_CANCEL.getResult()).equals(agree)){
			endFlow = true;
			workFlowRunNodesMapper.deleteWorkFlowRunNodeNode(orderId);
		}else {
			
			if(NodeStatusEnum.COMPLETE.getValue().equals(curentMainNode.getNodeStatus())){
				
		    	//3.更新下一环节的结点为ready状态（如果没有下一环节了，更新整个工单为完结状态）
		    	//这一步还是会根据下一环节是simple还是其他复杂结点，来决定更新为ready还是waiting状态
				List<WorkFlowRunNodes> nodes = flowNodeIdToChilds.get(0); //查询出所有的主结点
				//获取当前结点的序号
				
				for(int i=0; i<nodes.size(); i++){
					WorkFlowRunNodes aNode = nodes.get(i);
					
					if(aNode.getNodeOrder()>curentMainNode.getNodeOrder()){
						if(nextRunNode == null) {
							nextRunNode = aNode;
						}else{
							if(aNode.getNodeOrder()<nextRunNode.getNodeOrder()){
								nextRunNode = aNode;
							}
						}
						
					}
				
				}
				
				if(nextRunNode == null){//找不到下一环节点，说明流程结束,不通过，也直接流程结束
					//流程结束，则将所有的work_flow_run_nodes表记录删除
					endFlow = true;
					workFlowRunNodesMapper.deleteWorkFlowRunNodeNode(orderId);
					
				}else{
					//更改下一环节的处理人为ready状态
					updateNodeStatusReady(nextRunNode,flowNodeIdToChilds);
				}
				
			}
			
		}
				
    	//4.更新work_flow_run_handler表，记录审批人列表（去重，比如一个人当作了多个环节处理人，只记录一次，避免查询我处理的列表时出现重复）
    	WorkFlowRunHandler wHandler = new WorkFlowRunHandler();
    	wHandler.setOrderId(orderId);
    	wHandler.setHandler(userId);
    	wHandler.setHandlerName(userName);
    	int handlerCount = workFlowRunHandlerMapper.countWorkFlowRunHandler(orderId, userId);
    	if(handlerCount == 0){
    		workFlowRunHandlerMapper.addWorkFlowRunHandler(wHandler);
    	}
		
    	//5.将审批记录记录加入work_flow_auditlog表
		WorkFlowAuditlog woFlowAuditlog = new WorkFlowAuditlog();
		woFlowAuditlog.setOrderId(orderId);
		woFlowAuditlog.setHandler(userId);//当前用户账号
		woFlowAuditlog.setHandlerName(userName);//当前用户账号名称
		woFlowAuditlog.setNodeName(workFlowRunNode.getNodeName());//记录当前处理人所在的环节名称
		//不通过处理
		if(String.valueOf(TaskResultEnum.EXAM_AND_APPROVE_REJECT_TO_CANCEL.getResult()).equals(agree)){
			woFlowAuditlog.setAgree(String.valueOf(TaskResultEnum.EXAM_AND_APPROVE_REJECT_TO_CANCEL.getResult()));
		}else if(String.valueOf(TaskResultEnum.EXAM_AND_APPROVE_REJECT_TO_MODIFICATION.getResult()).equals(agree)){
			woFlowAuditlog.setAgree(String.valueOf(TaskResultEnum.EXAM_AND_APPROVE_REJECT_TO_MODIFICATION.getResult()));
		}else if("1".equals(agree)){
			if(endFlow){
				woFlowAuditlog.setAgree(String.valueOf(TaskResultEnum.EXAM_AND_APPROVE_PASS_TO_END.getResult()));
			}else{
				woFlowAuditlog.setAgree(String.valueOf(TaskResultEnum.EXAM_AND_APPROVE_PASS_TO_NEXT.getResult()));
			}
		}else{
			woFlowAuditlog.setAgree(String.valueOf(TaskResultEnum.EXAM_AND_APPROVE_PASS_TO_END.getResult()));
		}
		woFlowAuditlog.setAuditInfo(comment);
		woFlowAuditlog.setAuditTime(new Date());//审批时间
		workFlowAuditlogMapper.addWorkFlowAuditlog(woFlowAuditlog);//
		
		
		//6.更新工单表的当前环节结点名称信息
		WorkOrder workOrder = new WorkOrder();
		workOrder.setOrderId(orderId);
		
		if(woFlowAuditlog.getAgree().equals(String.valueOf(TaskResultEnum.EXAM_AND_APPROVE_PASS_TO_END.getResult()))){
			//最后环节结束了，归档状态
			workOrder.setOrderStatus(OrderStatusEnum.FINISHED.getValue());//归档完成
		}else if(woFlowAuditlog.getAgree().equals(String.valueOf(TaskResultEnum.EXAM_AND_APPROVE_REJECT_TO_CANCEL.getResult()))){
			// EXAM_AND_APPROVE_REJECT_TO_CANCEL(999, "不通过，直接取消审批(撤单)")
			workOrder.setOrderStatus(OrderStatusEnum.CANCELED.getValue());
		}else if(woFlowAuditlog.getAgree().equals(String.valueOf(TaskResultEnum.EXAM_AND_APPROVE_REJECT_TO_MODIFICATION.getResult()))){
			// EXAM_AND_APPROVE_REJECT_TO_MODIFICATION(998, "不通过，下一步修改申请"),
			workOrder.setOrderStatus(OrderStatusEnum.WAIT_FOR_UPDATE.getValue());
		}else{
			//待审批
			workOrder.setOrderStatus(OrderStatusEnum.WAIT_FOR_VERIFY.getValue());
		}
		
		//7.如果当前的主节点还没有完成，直接更新成当前主节点名称
		if(!NodeStatusEnum.COMPLETE.getValue().equals(curentMainNode.getNodeStatus())){
			workOrder.setCurrentNodeName(curentMainNode.getNodeName());
		}else{
			workOrder.setCurrentNodeName(nextRunNode == null ? "": nextRunNode.getNodeName());
		}
		//8.如果被打回重新修改将CurrentNodeName变为空
		if(String.valueOf(TaskResultEnum.EXAM_AND_APPROVE_REJECT_TO_MODIFICATION.getResult()).equals(agree)){
			workOrder.setCurrentNodeName("");//置空
		}
		
		workOrderMapper.updateCurrentNodeName(workOrder);
		
		/******.......如果是打回待修改，则将work_flow_run_nodes里面该orderId相关的数据重置为future,表明所有审批要重新走 start*/
		if(OrderStatusEnum.WAIT_FOR_UPDATE.getValue().equals(workOrder.getOrderStatus())){
			updateAllNodeStatusFuture(orderId);
		}
		/******.......如果是打回待修改，则将work_flow_run_nodes里面该orderId相关的数据重置为future,表明所有审批要重新走 end*/
		
		//本环节处理后监听器任务执行,构造一些变量给监听器使用
		Map<String,Object> variables = new HashMap<String,Object>();
		variables.put("userId", userId);
		variables.put("orderId", orderId);
		variables.put("flowKey", curentMainNode.getFlowKey());
		variables.put("nodeStatus", curentMainNode.getNodeStatus());
		variables.put("orderStatus", workOrder.getOrderStatus());
		commonFlowTaskListener.notify(orderId, variables);
    	
    }
	
    /**工单打回到上一个结点,节点为ready的状态才能进行打回操作*/
    private void backToPreRunNode(String userId,String userName,String orderId,int flowNodeId,String comment){
		
		//1.先找到本结点
		WorkFlowRunNodes wNode = workFlowRunNodesMapper.queryWorkFlowRunNodeByFlowNodeId(orderId, flowNodeId);
		if(wNode == null) {
			throw new GzsendiException("未找到运行中流程结点的数据，orderId is {}, flowNodeId: {}", orderId, flowNodeId);
		}
		
		//2.递归查询父流点结点，直到查询到parentNodeId=0为止，此时找到了本结点所在的主流程的节点。
		WorkFlowRunNodes rootNode = wNode;
		while(rootNode.getParentNodeId() != 0){
			rootNode = workFlowRunNodesMapper.queryWorkFlowRunNodeByFlowNodeId(orderId, rootNode.getParentNodeId());
			if(rootNode == null) {
				throw new GzsendiException("尝试找到运行中主流程结点的数据失败，orderId is {}, flowNodeId: {}", orderId, flowNodeId);
			}
		}
		
		//3.将本环节中所有的结点和子结点更新为future状态（因为要退回上一步，所有本环节的状态全变成future）
		updateNodeStatusFutureToDb(rootNode);
		
		//4.尝试找本环节中的上一环节的主流程节点（parentNodeId为0）
		WorkFlowRunNodes preMainRunNode = workFlowRunNodesMapper.getPreMainRunNode(rootNode);
		//5.将上一环节的结点和子结点更新为waiting或ready状态
		if(preMainRunNode != null){
			updateNodeStatusReadyToDb(preMainRunNode);
		}
		
    	//5.更新work_flow_run_handler表，记录审批人列表（去重，比如一个人当作了多个环节处理人，只记录一次，避免查询我处理的列表时出现重复）
    	WorkFlowRunHandler wHandler = new WorkFlowRunHandler();
    	wHandler.setOrderId(orderId);
    	wHandler.setHandler(userId);
    	wHandler.setHandlerName(userName);
    	int handlerCount = workFlowRunHandlerMapper.countWorkFlowRunHandler(orderId, userId);
    	if(handlerCount == 0){
    		workFlowRunHandlerMapper.addWorkFlowRunHandler(wHandler);
    	}
		
		//6.更新工单表状态
		if(preMainRunNode == null){
			//已经打回到申请人那里了,则将工单状态变为待修改
			WorkOrder workOrder = new WorkOrder();
			workOrder.setOrderId(orderId);
			workOrder.setOrderStatus(OrderStatusEnum.WAIT_FOR_UPDATE.getValue());
			workOrder.setCurrentNodeName("");//置空
			workOrderMapper.updateCurrentNodeName(workOrder);
		}else{
			WorkOrder workOrder = new WorkOrder();
			workOrder.setOrderId(orderId);
			workOrder.setOrderStatus(OrderStatusEnum.WAIT_FOR_VERIFY.getValue());
			workOrder.setCurrentNodeName(preMainRunNode.getNodeName());
			workOrderMapper.updateCurrentNodeName(workOrder);
		}
		
		//最后记录审批历史
    	//7.将审批记录记录加入work_flow_auditlog表
		WorkFlowAuditlog woFlowAuditlog = new WorkFlowAuditlog();
		woFlowAuditlog.setOrderId(orderId);
		woFlowAuditlog.setHandler(userId);//当前用户账号
		woFlowAuditlog.setHandlerName(userName);//当前用户账号名称
		woFlowAuditlog.setAgree(String.valueOf(TaskResultEnum.EXAM_AND_APPROVE_REJECT_TO_PRE.getResult()));
		woFlowAuditlog.setAuditInfo(comment == null ? "退回到上一环节" : comment);
		woFlowAuditlog.setAuditTime(new Date());//审批时间
		woFlowAuditlog.setNodeName(wNode.getNodeName());//环节名称
		workFlowAuditlogMapper.addWorkFlowAuditlog(woFlowAuditlog);//
    	
    }
	
	//打回修改时调用此方法，将所有运行节点的状态修改为future
	private void updateAllNodeStatusFuture(String orderId){
		workFlowRunNodesMapper.updateAllNodeStatusFuture(orderId);
	}
	
	//将复杂节点及子结点更新状态为ready或waiting 节点为simple将更新为ready,节点为复杂的更新为wating
	private void setChildRunNodeStatus(WorkFlowRunNodes firstWorkFlowRunNode,Map<Integer,List<WorkFlowRunNodes>> parentIdToNodes){
		firstWorkFlowRunNode.setNodeStatus(NodeStatusEnum.WAITING.getValue());
		//查找出所有的子结点
		List<WorkFlowRunNodes> list = parentIdToNodes.get(firstWorkFlowRunNode.getFlowNodeId());
		if(list != null){
			
			for(WorkFlowRunNodes workFlowRunNode : list){
				if(workFlowRunNode.getNodeType().equals(NodeTypeEnum.SIMPLE.getValue())){
					workFlowRunNode.setNodeStatus(NodeStatusEnum.READY.getValue());
				}else{
					setChildRunNodeStatus(workFlowRunNode, parentIdToNodes);
				}
			}
			
		}
	}
	
    //递归删除本节点及所有的子结点
    private void deleteWorkFlowRunNodes(WorkFlowRunNodes rootNode){
    	if(NodeTypeEnum.SIMPLE.getValue().equals(rootNode.getNodeType())){
    		rootNode.setNodeStatus(NodeStatusEnum.READY.getValue());
    		workFlowRunNodesMapper.deleteWorkFlowRunNodeByFlowNodeId(rootNode.getOrderId(), rootNode.getFlowNodeId());
    	}else {
    		
    		//复杂结点，递归查询并更新状态
    		rootNode.setNodeStatus(NodeStatusEnum.WAITING.getValue());
    		workFlowRunNodesMapper.deleteWorkFlowRunNodeByFlowNodeId(rootNode.getOrderId(), rootNode.getFlowNodeId());
    		
    		WorkFlowRunNodes query = new WorkFlowRunNodes();
    		query.setOrderId(rootNode.getOrderId());
    		query.setFlowNodeId(rootNode.getFlowNodeId());
    		List<WorkFlowRunNodes> childs = workFlowRunNodesMapper.getChildRunNodes(query);
    		if(childs.size()>0){
    			for(WorkFlowRunNodes aRunNode : childs) {
    				deleteWorkFlowRunNodes(aRunNode);
        		}
    		}
    		
    	}
    }
	
    //递归更新所有的子结点都为Ready状态或wating状态
    private void updateNodeStatusReady(WorkFlowRunNodes aWorkFlowRunNode,Map<Integer,List<WorkFlowRunNodes>> flowNodeIdToChilds){
    	
    	if(NodeTypeEnum.SIMPLE.getValue().equals(aWorkFlowRunNode.getNodeType())){
    		aWorkFlowRunNode.setNodeStatus(NodeStatusEnum.READY.getValue());
    		workFlowRunNodesMapper.updateWorkFlowRunNodeNodeStatus(aWorkFlowRunNode);
    	}else {
    		aWorkFlowRunNode.setNodeStatus(NodeStatusEnum.WAITING.getValue());
    		workFlowRunNodesMapper.updateWorkFlowRunNodeNodeStatus(aWorkFlowRunNode);
    		List<WorkFlowRunNodes> childs = flowNodeIdToChilds.get(aWorkFlowRunNode.getFlowNodeId());
    		if(childs != null){
        		for(WorkFlowRunNodes aRunNode : childs) {
        			updateNodeStatusReady(aRunNode,flowNodeIdToChilds);
        		}
    		}
    	}
    	
    }
  
    //递归更新所有的子结点都为Ready状态或wating状态(直接查询库递归更新的方法)
    private void updateNodeStatusReadyToDb(WorkFlowRunNodes rootNode){
    	if(NodeTypeEnum.SIMPLE.getValue().equals(rootNode.getNodeType())){
    		rootNode.setNodeStatus(NodeStatusEnum.READY.getValue());
    		workFlowRunNodesMapper.updateWorkFlowRunNodeNodeStatus(rootNode);
    	}else {
    		
    		//复杂结点，递归查询并更新状态
    		rootNode.setNodeStatus(NodeStatusEnum.WAITING.getValue());
    		workFlowRunNodesMapper.updateWorkFlowRunNodeNodeStatus(rootNode);
    		
    		WorkFlowRunNodes query = new WorkFlowRunNodes();
    		query.setOrderId(rootNode.getOrderId());
    		query.setFlowNodeId(rootNode.getFlowNodeId());
    		List<WorkFlowRunNodes> childs = workFlowRunNodesMapper.getChildRunNodes(query);
    		if(childs.size()>0){
    			for(WorkFlowRunNodes aRunNode : childs) {
    				updateNodeStatusReadyToDb(aRunNode);
        		}
    		}
    		
    	}
    }
    
    //递归更新所有的子结点都为futrue状态(直接查询库递归更新的方法)
    private void updateNodeStatusFutureToDb(WorkFlowRunNodes rootNode){
    	if(NodeTypeEnum.SIMPLE.getValue().equals(rootNode.getNodeType())){
    		rootNode.setNodeStatus(NodeStatusEnum.FUTURE.getValue());
    		workFlowRunNodesMapper.updateWorkFlowRunNodeNodeStatus(rootNode);
    	}else {
    		
    		//复杂结点，递归查询并更新状态
    		rootNode.setNodeStatus(NodeStatusEnum.FUTURE.getValue());
    		workFlowRunNodesMapper.updateWorkFlowRunNodeNodeStatus(rootNode);
    		
    		WorkFlowRunNodes query = new WorkFlowRunNodes();
    		query.setOrderId(rootNode.getOrderId());
    		query.setFlowNodeId(rootNode.getFlowNodeId());
    		List<WorkFlowRunNodes> childs = workFlowRunNodesMapper.getChildRunNodes(query);
    		if(childs.size()>0){
    			for(WorkFlowRunNodes aRunNode : childs) {
    				updateNodeStatusReadyToDb(aRunNode);
        		}
    		}
    		
    	}
    }
	
    //递归更新所有的子结点都为Skip状态或complete状态
    private void updateNodeStatusSkip(WorkFlowRunNodes aWorkFlowRunNode,Map<Integer,List<WorkFlowRunNodes>> flowNodeIdToChilds){
    	
    	if(NodeTypeEnum.SIMPLE.getValue().equals(aWorkFlowRunNode.getNodeType())){
    		aWorkFlowRunNode.setNodeStatus(NodeStatusEnum.SKIP.getValue());
    		workFlowRunNodesMapper.updateWorkFlowRunNodeNodeStatus(aWorkFlowRunNode);
    	}else {
    		aWorkFlowRunNode.setNodeStatus(NodeStatusEnum.COMPLETE.getValue());
    		workFlowRunNodesMapper.updateWorkFlowRunNodeNodeStatus(aWorkFlowRunNode);
    		List<WorkFlowRunNodes> childs = flowNodeIdToChilds.get(aWorkFlowRunNode.getFlowNodeId());
    		if(childs != null){
        		for(WorkFlowRunNodes aRunNode : childs) {
        			updateNodeStatusSkip(aRunNode,flowNodeIdToChilds);
        		}
    		}
    	}
    }
	
}
