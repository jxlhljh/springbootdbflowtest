package cn.gzsendi.modules.workflow.enums;

import cn.gzsendi.modules.framework.exception.GzsendiException;

/**
 * 审批人类型枚举
 */
public enum HandlerTypeEnum {
	
    /**
     * 固定
     */
	FIXED("固定", "fixed"),
	
	
	//ready已准备好处理中，complete,waiting等待（复杂结点才有）,skip略过不处理的，future未走到的

    /**
     * 从表单选择
     */
	FROMFORM("从表单选择", "fromForm"),

    /**
     * 根据发起人计算出审批人
     */
    MAPTO("根据发起人计算", "mapTo"),
    ;

    private final String name;
    private final String value;

    HandlerTypeEnum(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public static HandlerTypeEnum getOrderStatusEnum(String value) {

        HandlerTypeEnum[] orderStatusEnums = HandlerTypeEnum.values();
        for (int i = 0; i < orderStatusEnums.length; i++) {
            HandlerTypeEnum orderStatusEnum = orderStatusEnums[i];
            if (value.equals(orderStatusEnum.value)) {
                return orderStatusEnum;
            }
        }
        throw new GzsendiException("请传入正确的审批人类型value值");
    }


}
