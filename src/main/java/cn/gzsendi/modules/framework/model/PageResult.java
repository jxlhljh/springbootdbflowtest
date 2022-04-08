package cn.gzsendi.modules.framework.model;

import java.util.List;

public class PageResult<T> {
	
	private long total;
	private List<T> list;
	
	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}

	public PageResult(long total,List<T> list){
		this.total = total;
		this.list = list;
	}

}
