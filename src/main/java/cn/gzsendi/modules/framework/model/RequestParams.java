package cn.gzsendi.modules.framework.model;


public class RequestParams<T> {


	private Integer pageNum = 1;
	private Integer pageSize = 10;

	private T data;

	public Integer getPageNum() {
		return pageNum==0?1:pageNum;
	}

	public void setPageNum(Integer pageNum) {
		this.pageNum = pageNum;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}


}
