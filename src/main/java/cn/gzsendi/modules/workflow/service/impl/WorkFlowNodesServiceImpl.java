package cn.gzsendi.modules.workflow.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.gzsendi.modules.framework.model.PageResult;
import cn.gzsendi.modules.framework.model.RequestParams;
import cn.gzsendi.modules.workflow.mapper.WorkFlowNodesMapper;
import cn.gzsendi.modules.workflow.model.WorkFlowNodes;
import cn.gzsendi.modules.workflow.service.IWorkFlowNodesService;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

@Service
public class WorkFlowNodesServiceImpl implements IWorkFlowNodesService{
	
	@Autowired
	private WorkFlowNodesMapper workFlowNodesMapper;

	public PageResult<WorkFlowNodes> list(RequestParams<WorkFlowNodes> params) {
		
        Page<WorkFlowNodes> page = PageHelper.startPage(params.getPageNum(), params.getPageSize());
        List<WorkFlowNodes> list = workFlowNodesMapper.list(params.getData());
        
		return new PageResult<WorkFlowNodes>(page.getTotal(), list);
	}

	public WorkFlowNodes queryById(Integer id) {
		return workFlowNodesMapper.queryById(id);
	}

	public int insert(WorkFlowNodes workFlowNodes) {
		return workFlowNodesMapper.insert(workFlowNodes);
	}

	public int update(WorkFlowNodes workFlowNodes) {
		return workFlowNodesMapper.update(workFlowNodes);
	}

	public int delete(List<Integer> ids) {
		return workFlowNodesMapper.delete(ids);
	}

}



