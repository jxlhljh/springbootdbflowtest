package cn.gzsendi.modules.workflow.listener;

import java.util.Map;

/**
 * 代理任务监听器,可以通过实现自己的DelegateTaskListener进行监听器扩展
 */
public interface DelegateTaskListener {
	
    /**
     * 检验当前任务监听器实现类是否需要执行
     * @param orderId           工单号
     * @param variables         变量Map,有哪些变量可以使用见：WorkFlowServiceImpl.java#examAndApprove方法的最后
     */
    default boolean validate(String orderId,Map<String,Object> variables) {

        //校验默认不通过，只有通过校验了才会执行这个接口的其他的方法
        //项目启动时会统一初始化所有的DelegateTaskListener的实现类并声明成Bean，然后在CommonFlowTaskListener全部获取出来，
        //再通过此方法(validate)的当前流程所属工单(orderId)、当前的任务事件(taskDefinitionKey)、当前的任务事件(taskEventName)等条件
        //校验这个代理任务监听器是否对应当前流程所属工单，再选择执行这个接口的其他的方法
        return false;
    }

    /**
     * 执行（代理任务监听器）的通知逻辑，这里可以做一些如发送邮件，写其他相关业务逻辑表等的操作
     */
    void doTaskNotify(String orderId,Map<String,Object> variables);
}
