package cn.gzsendi.modules.workflow.enums;

import cn.gzsendi.modules.framework.exception.GzsendiException;

/**
 * 节点状态枚举
 */
public enum NodeStatusEnum {

    /**
     * READY
     */
	READY("已准备", "ready"),
	
	
	//ready已准备好处理中，complete,waiting等待（复杂结点才有）,skip略过不处理的，future未走到的

    /**
     * 已完成
     */
    COMPLETE("已完成", "complete"),

    /**
     * 等待中
     */
    WAITING("等待中", "waiting"),

    /**
     * 已略过
     */
    SKIP("已略过", "skip"),

    /**
     * 未走到
     */
    FUTURE("未走到", "future"),
    ;

    private final String name;
    private final String value;

    NodeStatusEnum(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public static NodeStatusEnum getOrderStatusEnum(String value) {

        NodeStatusEnum[] orderStatusEnums = NodeStatusEnum.values();
        for (int i = 0; i < orderStatusEnums.length; i++) {
            NodeStatusEnum orderStatusEnum = orderStatusEnums[i];
            if (value.equals(orderStatusEnum.value)) {
                return orderStatusEnum;
            }
        }
        throw new GzsendiException("请传入正确的工单状态value值");
    }


}
