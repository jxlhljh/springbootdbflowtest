package cn.gzsendi.modules.workflow.listener;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import cn.gzsendi.modules.framework.utils.AppCtxHolder;
import cn.gzsendi.modules.framework.utils.JsonUtil;

@Component
public class CommonFlowTaskListener {
	
	protected final Logger logger = LoggerFactory.getLogger(CommonFlowTaskListener.class);
	
	public void notify(String orderId, Map<String,Object> variables) {
		
        if (logger.isInfoEnabled()) {
            String classSimpleName = this.getClass().getSimpleName();
            logger.info("=========================" + classSimpleName + "==========================");
            logger.info("业务key/工单ID: {}", orderId);
            logger.info("流程实例变量: {}", JsonUtil.toJSONString(variables));
            logger.info("=========================" + classSimpleName + "==========================");
        }
		
		 //因为当前类不处于IOC的管理中，所以通过AppCtxHolder获取代理执行监听器列表，再根据校验结果执行它们
        Map<String, DelegateTaskListener> dtlMap = AppCtxHolder.getBeanOfType(DelegateTaskListener.class);
        for (Map.Entry<String, DelegateTaskListener> entry : dtlMap.entrySet()) {
            String delegateTaskListenerBeanName = entry.getKey();
            DelegateTaskListener delegateTaskListener = entry.getValue();

            if (delegateTaskListener.validate(orderId, variables)) {
                if (logger.isInfoEnabled()) {
                    logger.info("({})通过校验,将执行其业务方法", delegateTaskListenerBeanName);
                }
                delegateTaskListener.doTaskNotify(orderId,variables);
            }
        }
	}

}
