package com.kakarote.work.constant;

import java.util.Objects;

/**
 * @author zyh
 * 分组类型
 */

public enum GroupTypeEnum {
    //分组类型
    ALL(1,"全部项目"),
    NONE(2,"未分组"),
    CUSTOM(3,"自定义"),
    ;

    private GroupTypeEnum(Integer type, String desc) {
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

    public static GroupTypeEnum enumByType(Integer type) {
        for (GroupTypeEnum moduleTypeEnum : values()) {
            if (Objects.equals(type, moduleTypeEnum.getType())) {
                return moduleTypeEnum;
            }
        }
        return null;
    }

    public static GroupTypeEnum enumByDesc(String desc) {
        for (GroupTypeEnum moduleTypeEnum : values()) {
            if (Objects.equals(desc, moduleTypeEnum.getDesc())) {
                return moduleTypeEnum;
            }
        }
        return null;
    }

}
