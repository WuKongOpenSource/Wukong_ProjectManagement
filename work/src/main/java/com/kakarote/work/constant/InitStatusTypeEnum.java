package com.kakarote.work.constant;

import java.util.Objects;

/**
 * @author zyh
 * 初始化状态
 */

public enum InitStatusTypeEnum {
    //缺陷类型
    NONE(1,"未开始"),
    RUNNING(2,"进行中"),
    END(3,"已完成"),
    ;

    private InitStatusTypeEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    private Integer type;
    private String desc;

    public Integer getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }

    public static InitStatusTypeEnum enumByType(Integer type) {
        for (InitStatusTypeEnum initStatusTypeEnum : values()) {
            if (Objects.equals(type, initStatusTypeEnum.getType())) {
                return initStatusTypeEnum;
            }
        }
        return null;
    }

    public static InitStatusTypeEnum enumByDesc(String desc) {
        for (InitStatusTypeEnum initStatusTypeEnum : values()) {
            if (Objects.equals(desc, initStatusTypeEnum.getDesc())) {
                return initStatusTypeEnum;
            }
        }
        return null;
    }

}
