package com.kakarote.work.constant;

import com.kakarote.common.result.ResultCode;
import com.kakarote.work.common.project.Const;

/**
 * @author BAIJC
 * 项目管理响应错误代码枚举类
 * 8000 -
 */

public enum ProjectCodeEnum implements ResultCode {

    //项目管理模块管理
    REQUIRED_OPTIONS_CANNOT_BE_HIDDEN(8022, "必填选项不能隐藏！"),
    PROJECT_FIELD_NUM_ERROR(8024, "每个模块最多存在"+ Const.QUERY_MAX_SIZE +"个字段"),
    SYSTEM_RELATED_FIELDS_CANNOT_BE_HIDDEN(8026, "系统关联字段不能隐藏！"),
    THE_FIELD_NAME_OF_THE_FORM_CANNOT_BE_REPEATED(8026,"自定义表单字段名称不能重复"),
    THE_FIELD_NUM_RESTRICT_ERROR(8027,"自定义表单限制的数值格式错误"),
    THE_FIELD_DETAIL_TABLE_FORMAT_ERROR(8028,"请设置表格内的具体字段！"),
    PROJECT_NUMBER_SETTING_NO_NUMBER_INCREASE(8230, "编号规则设置缺少数字类型规则，修改后重试！"),
    PROJECT_NUMBER_SETTING_NUMBER_INCREASE_MUST_BT_ZERO(8231, "数字类型类型编号规则递增数必须大于0，修改后重试！"),
    PROJECT_TAG_FIELD_REPETITION_ERROR(8232, "标签类型字段只允许有一个"),
    PROJECT_SERIAL_NUMBER_FIELD_LENGTH_ERROR(8233, "自定义编号类型字段不能超过200字符"),
    PROJECT_SERIAL_NUMBER_DATA_EXIST_ERROR(8234, "自定义编号字段重复"),
    PROJECT_EVENT_STATUS_REPETITION_ERROR(8235, "不能添加重复的状态"),
    PROJECT__STATUS_SYS_ERROR(8235, "不能删除系统状态"),
    PROJECT_EVENT_STATUS_BAND_ERROR(8236, "不能删除已绑定的状态"),
    PROJECT_EVENT_STATUS_INIT_ERROR(8237, "不能设置未启用的状态"),
    PROJECT_UNION_PROJECT_ADMIN_ERROR(8238, "至少指定一名管理员！"),
    PROJECT_ROLE_NAME_EXIST_ERROR(8239, "角色名称已存在"),
    PROJECT_DEFAULT_ROLE_CANNOT_BE_DELETED(8240, "默认角色不能删除!"),
    PROJECT_CREATE_NAME_NULL_ERROR(8241,"项目名称不存在"),
    PROJECT_TASK_DELETE_ERROR(8242,"任务已被删除"),
    PROJECT_EXIST_ERROR(8243,"项目不存在"),
    ;
    ProjectCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private int code;
    private String msg;

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }
}
