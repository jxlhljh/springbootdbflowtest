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
import cn.gzsendi.modules.workflow.model.WorkFlow;
import cn.gzsendi.modules.workflow.service.IWorkFlowService;

/**
 * 流程定义控制器
 * @author jxlhl
 */
@RestController
@RequestMapping("/workFlow")
public class WorkFlowController {
	
	@Autowired
	private IWorkFlowService iWorkFlowService;
	
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	public Result<Object> list(@RequestBody RequestParams<WorkFlow> params) throws Exception {
		
		PageResult<WorkFlow> list = iWorkFlowService.list(params);
		
		return Result.ok(list);
		
	}
	
	@RequestMapping(value = "/getById", method = RequestMethod.POST)
	public Result<Object> getById(@RequestBody RequestParams<WorkFlow> params) throws Exception {
		
		WorkFlow workFlow = params.getData();
		Assert.notNull(workFlow, "data parameter is not allowed null.");
		Assert.notNull(workFlow.getId(), "Id parameter is not allowed null.");
		
		WorkFlow result = iWorkFlowService.getById(workFlow.getId());
		
		return Result.ok(result);
		
	}
	
	@RequestMapping(value = "/insert", method = RequestMethod.POST)
	public Result<Object> insert(@RequestBody RequestParams<WorkFlow> params) throws Exception {
		
		WorkFlow WorkFlow = params.getData();
		iWorkFlowService.insert(WorkFlow);
		
		return Result.ok();
		
	}

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public Result<Object> update(@RequestBody RequestParams<WorkFlow> params) throws Exception {
		
		WorkFlow WorkFlow = params.getData();
		Assert.notNull(WorkFlow.getId(), "id is not allowed null.");
		
		iWorkFlowService.update(WorkFlow);
		
		return Result.ok();
	}
	
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public Result<Object> delete(@RequestBody RequestParams<Map<String,Object>> params) throws Exception {
		
		Map<String,Object> jsonObject = params.getData();
		Assert.notNull(jsonObject, "data parameter can not be null.");
		List<Integer> idList = JsonUtil.getList(jsonObject, "ids", Integer.class);
		Assert.notNull(idList, "ids parameter can not be null.");
		iWorkFlowService.delete(idList);
		
		return Result.ok();
	}
	
}
