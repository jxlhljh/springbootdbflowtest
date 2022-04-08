package cn.gzsendi.modules.workflow.listener;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import cn.gzsendi.modules.framework.utils.JsonUtil;
import cn.gzsendi.modules.workflow.enums.OrderStatusEnum;

@Component
public class MyDelegateTaskListener implements DelegateTaskListener{
	
	protected final Logger logger = LoggerFactory.getLogger(MyDelegateTaskListener.class);
	
	/**
	 * 检验当前任务监听器实现类是否需要执行
	 */
    public boolean validate(String orderId,Map<String,Object> variables) {
    	
    	//流程结束时进行监听器的执行
    	String orderStatus = variables.get("orderStatus").toString();
    	if(OrderStatusEnum.FINISHED.getValue().equals(orderStatus)){
    		return true;
    	}
    	
        return false;
    }

	@Override
	public void doTaskNotify(String orderId, Map<String, Object> variables) {
		
		logger.info("----------这里写自己想要实现的代码，做后续的自定义业务逻辑-----------");
		logger.info(JsonUtil.toJSONString(variables));
		
	}

}
