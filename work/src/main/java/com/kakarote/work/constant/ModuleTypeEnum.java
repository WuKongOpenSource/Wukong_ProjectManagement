package com.kakarote.work.constant;

import java.util.Objects;

/**
 * @author zyh
 * 缺陷类型
 */

public enum ModuleTypeEnum {
    //缺陷类型
    ALL(0,"全部事项"),
    ITERATION(1,"迭代"),
    NEED(2,"需求"),
    TASK(3,"任务"),
    DEFECT(4,"缺陷"),
    ;

    private ModuleTypeEnum(Integer type, String desc) {
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

    public static ModuleTypeEnum enumByType(Integer type) {
        for (ModuleTypeEnum moduleTypeEnum : values()) {
            if (Objects.equals(type, moduleTypeEnum.getType())) {
                return moduleTypeEnum;
            }
        }
        return null;
    }

    public static ModuleTypeEnum enumByDesc(String desc) {
        for (ModuleTypeEnum moduleTypeEnum : values()) {
            if (Objects.equals(desc, moduleTypeEnum.getDesc())) {
                return moduleTypeEnum;
            }
        }
        return null;
    }

}
