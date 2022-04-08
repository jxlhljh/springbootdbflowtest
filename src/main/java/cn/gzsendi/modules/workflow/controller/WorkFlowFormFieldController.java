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
import cn.gzsendi.modules.workflow.model.WorkFlowFormField;
import cn.gzsendi.modules.workflow.service.IWorkFlowFormFieldService;

@RestController
@RequestMapping("/workFlowFormField")
public class WorkFlowFormFieldController {
	
	@Autowired
	private IWorkFlowFormFieldService iWorkFlowFormFieldService;
	
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	public Result<Object> list(@RequestBody RequestParams<WorkFlowFormField> params) throws Exception {
		
		WorkFlowFormField workFlowField = params.getData();
		Assert.notNull(workFlowField, "data parameter is not allowed null.");
		Assert.notNull(workFlowField.getFlowKey(), "flowKey parameter is not allowed null.");
		
		PageResult<WorkFlowFormField> list = iWorkFlowFormFieldService.list(params);
		
		return Result.ok(list);
	}
	
	@RequestMapping(value = "/queryById", method = RequestMethod.POST)
	public Result<Object> queryById(@RequestBody RequestParams<Map<String,Object>> params) throws Exception {
		
		WorkFlowFormField workFlowFormField = iWorkFlowFormFieldService.queryById(JsonUtil.getInteger(params.getData(), "id"));
		
		return Result.ok(workFlowFormField);
	}
	
	@RequestMapping(value = "/insert", method = RequestMethod.POST)
	public Result<Object> insert(@RequestBody RequestParams<WorkFlowFormField> params) throws Exception {
		
		iWorkFlowFormFieldService.insert(params.getData());
		
		return Result.ok();
	}

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public Result<Object> update(@RequestBody RequestParams<WorkFlowFormField> params) throws Exception {
		
		Assert.notNull(params.getData().getId(), "id is not allowed null.");
		
		iWorkFlowFormFieldService.update(params.getData());
		
		return Result.ok();
	}
	
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public Result<Object> delete(@RequestBody RequestParams<Map<String,Object>> params) throws Exception {
		
		List<Integer> idList = JsonUtil.getList(params.getData(), "ids", Integer.class);
		
		iWorkFlowFormFieldService.delete(idList);
		
		return Result.ok();
	}
	
}

