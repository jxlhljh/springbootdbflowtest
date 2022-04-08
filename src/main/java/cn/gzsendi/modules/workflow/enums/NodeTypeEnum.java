package cn.gzsendi.modules.workflow.enums;

import cn.gzsendi.modules.framework.exception.GzsendiException;

/**
 * 节点类型枚举
 */
public enum NodeTypeEnum {

    /**
     * 简单结点
     */
	SIMPLE("简单结点", "simple"),
	
    /**
     * 会签结点
     */
	TOGETHERSIGN("会签结点", "togethersign"),
	
    /**
     * 或签结点
     */
	ORSIGN("或签结点", "orsign"),
    ;

    private final String name;
    private final String value;

    NodeTypeEnum(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public static NodeTypeEnum getOrderStatusEnum(String value) {

        NodeTypeEnum[] orderStatusEnums = NodeTypeEnum.values();
        for (int i = 0; i < orderStatusEnums.length; i++) {
            NodeTypeEnum orderStatusEnum = orderStatusEnums[i];
            if (value.equals(orderStatusEnum.value)) {
                return orderStatusEnum;
            }
        }
        throw new GzsendiException("请传入正确的工单状态value值");
    }


}
