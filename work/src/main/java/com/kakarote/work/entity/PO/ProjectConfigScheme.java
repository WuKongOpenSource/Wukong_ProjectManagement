package com.kakarote.work.entity.PO;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 项目配置方案表
 * </p>
 *
 * @author bai
 * @since 2022-09-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("wk_project_config_scheme")
@ApiModel(value = "ProjectConfigScheme对象", description = "项目配置方案表")
public class ProjectConfigScheme implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @ApiModelProperty(value = "方案名称")
    private String name;

    @ApiModelProperty(value = "描述")
    private String describes;

    @ApiModelProperty(value = "1 系统 2 自定义")
    private Integer sysType;

    @ApiModelProperty(value = "协作类型 1 敏捷开发 2经典项目开发")
    private Integer collaborationType;

    @ApiModelProperty(value = "关联项目数目")
    private Long associatedNum;

    @ApiModelProperty(value = "创建人ID")
    @TableField(fill = FieldFill.INSERT)
    private Long createUserId;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty(value = "修改人ID")
    private Long updateUserId;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "对应主键batchId")
    private String batchId;


}
