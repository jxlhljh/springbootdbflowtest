package cn.gzsendi.modules.framework.constants;

public interface RespCodeConstant {
	
	public static int NOAUTHRIZED = 401; //无权限
	public static int TIMEOUT = 503; //超时服务响应
	public static int BACK_ERVICE_DOWN = 999; //后端服务降级响应 
    public static final Integer INTERNAL_SERVER_ERROR_500 = 500;//服务响应错误
    public static final Integer OK_200 = 200;//响应成功
    
	
}
