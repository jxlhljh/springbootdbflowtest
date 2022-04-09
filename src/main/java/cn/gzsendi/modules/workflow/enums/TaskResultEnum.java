package cn.gzsendi.modules.workflow.enums;

import cn.gzsendi.modules.framework.exception.GzsendiException;

/**
 * 通用流程的任务节点的处理结果的枚举
 */
public enum TaskResultEnum {

    /**
     * 审批节点的处理结果的含义
     * result > 0  ：通过，交给下一个人再审
     * result == 0 ：通过，流程结束，直接归档
     * result == -999 ：不通过，直接取消审批(撤单)
     * result < 0 && result != -999 ：不通过，下一步修改申请
     */
    EXAM_AND_APPROVE_PASS_TO_NEXT(1, "通过，交给下一个人再审"),
    EXAM_AND_APPROVE_PASS_TO_END(0, "通过，流程结束，直接归档"),
    EXAM_AND_APPROVE_REJECT_TO_PRE(997, "不通过，下一步打回到上一个环节"),
    EXAM_AND_APPROVE_REJECT_TO_MODIFICATION(998, "不通过，下一步修改申请"),
    EXAM_AND_APPROVE_REJECT_TO_CANCEL(999, "不通过，直接取消审批(撤单)")
    ;

    private final int result;
    private final String resultDesc;

    TaskResultEnum(int result, String resultDesc) {
        this.result = result;
        this.resultDesc = resultDesc;
    }

    public int getResult() {
        return result;
    }

    public String getResultDesc() {
        return resultDesc;
    }
    
    public static TaskResultEnum getTaskResultEnum(String result) {

    	TaskResultEnum[] taskResultEnums = TaskResultEnum.values();
        for (int i = 0; i < taskResultEnums.length; i++) {
        	TaskResultEnum taskResultEnum = taskResultEnums[i];
            if (result.equals(taskResultEnum.result)) {
                return taskResultEnum;
            }
        }
        throw new GzsendiException("请传入正确的处理结果result值");
    }

}
