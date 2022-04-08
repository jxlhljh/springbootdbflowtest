package cn.gzsendi.modules.workflow.enums;

import cn.gzsendi.modules.framework.exception.GzsendiException;

/**
 * 工单状态枚举
 */
public enum OrderStatusEnum {

    /**
     * 待审批
     */
    WAIT_FOR_VERIFY("待审批", "1"),

    /**
     * 待处理
     */
    WAIT_FOR_HANDLE("待处理", "2"),

    /**
     * 已归档
     */
    FINISHED("已归档", "3"),

    /**
     * 待修改
     */
    WAIT_FOR_UPDATE("待修改", "4"),

    /**
     * 已作废
     */
    INVALIDED("已作废", "5"),

    /**
     * 已撤单
     */
    CANCELED("已撤单", "6"),
    ;

    private final String name;
    private final String value;

    OrderStatusEnum(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public static OrderStatusEnum getOrderStatusEnum(String value) {

        OrderStatusEnum[] orderStatusEnums = OrderStatusEnum.values();
        for (int i = 0; i < orderStatusEnums.length; i++) {
            OrderStatusEnum orderStatusEnum = orderStatusEnums[i];
            if (value.equals(orderStatusEnum.value)) {
                return orderStatusEnum;
            }
        }
        throw new GzsendiException("请传入正确的工单状态value值");
    }


}
