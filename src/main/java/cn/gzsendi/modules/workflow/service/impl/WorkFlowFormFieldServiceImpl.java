package cn.gzsendi.modules.workflow.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.gzsendi.modules.framework.model.PageResult;
import cn.gzsendi.modules.framework.model.RequestParams;
import cn.gzsendi.modules.workflow.mapper.WorkFlowFormFieldMapper;
import cn.gzsendi.modules.workflow.model.WorkFlowFormField;
import cn.gzsendi.modules.workflow.service.IWorkFlowFormFieldService;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

@Service
public class WorkFlowFormFieldServiceImpl implements IWorkFlowFormFieldService{
	
	@Autowired
	private WorkFlowFormFieldMapper workFlowFormFieldMapper;

	public PageResult<WorkFlowFormField> list(RequestParams<WorkFlowFormField> params) {
		
        Page<WorkFlowFormField> page = PageHelper.startPage(params.getPageNum(), params.getPageSize());
        List<WorkFlowFormField> list = workFlowFormFieldMapper.list(params.getData());
        
		return new PageResult<WorkFlowFormField>(page.getTotal(), list);
	}

	public WorkFlowFormField queryById(Integer id) {
		return workFlowFormFieldMapper.queryById(id);
	}

	public int insert(WorkFlowFormField workFlowFormField) {
		return workFlowFormFieldMapper.insert(workFlowFormField);
	}

	public int update(WorkFlowFormField workFlowFormField) {
		return workFlowFormFieldMapper.update(workFlowFormField);
	}

	public int delete(List<Integer> ids) {
		return workFlowFormFieldMapper.delete(ids);
	}

}



