package cn.gzsendi.modules.workflow.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import cn.gzsendi.modules.framework.model.PageResult;
import cn.gzsendi.modules.framework.model.RequestParams;
import cn.gzsendi.modules.framework.model.Result;
import cn.gzsendi.modules.framework.utils.JsonUtil;
import cn.gzsendi.modules.workflow.model.WorkFlowNodes;
import cn.gzsendi.modules.workflow.service.IWorkFlowNodesService;

@RestController
@RequestMapping("/workFlowNodes")
public class WorkFlowNodesController {
	
	@Autowired
	private IWorkFlowNodesService iWorkFlowNodesService;
	
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	public Result<Object> list(@RequestBody RequestParams<WorkFlowNodes> params) throws Exception {
		
		WorkFlowNodes workFlowNode = params.getData();
		Assert.notNull(workFlowNode, "data parameter is not allowed null.");
		Assert.notNull(workFlowNode.getFlowKey(), "flowKey parameter is not allowed null.");
		
		PageResult<WorkFlowNodes> list = iWorkFlowNodesService.list(params);
		
		return Result.ok(list);
	}
	
	@RequestMapping(value = "/queryById", method = RequestMethod.POST)
	public Result<Object> queryById(@RequestBody RequestParams<Map<String,Object>> params) throws Exception {
		
		WorkFlowNodes workFlowNodes = iWorkFlowNodesService.queryById(JsonUtil.getInteger(params.getData(), "id"));
		
		return Result.ok(workFlowNodes);
	}
	
	@RequestMapping(value = "/insert", method = RequestMethod.POST)
	public Result<Object> insert(@RequestBody RequestParams<WorkFlowNodes> params) throws Exception {
		
		iWorkFlowNodesService.insert(params.getData());
		
		return Result.ok();
	}

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public Result<Object> update(@RequestBody RequestParams<WorkFlowNodes> params) throws Exception {
		
		Assert.notNull(params.getData().getId(), "id is not allowed null.");
		
		iWorkFlowNodesService.update(params.getData());
		
		return Result.ok();
	}
	
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public Result<Object> delete(@RequestBody RequestParams<Map<String,Object>> params) throws Exception {
		
		List<Integer> idList = JsonUtil.getList(params.getData(), "ids", Integer.class);
		
		iWorkFlowNodesService.delete(idList);
		
		return Result.ok();
	}
	
}

