package com.kakarote.work.entity.BO;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 项目表
 * </p>
 *
 * @author bai
 * @since 2022-09-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "Project对象", description = "项目表")
public class ProjectVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "项目ID")
    @TableId(value = "project_id", type = IdType.ASSIGN_ID)
    private Long projectId;

    @ApiModelProperty(value = "项目名字")
    private String name;

    @ApiModelProperty(value = "状态 1启用 3归档 2 删除")
    private Integer status;

    @ApiModelProperty(value = "状态 1普通 2敏捷")
    private Integer type;

    @ApiModelProperty(value = "1 最高 2较高 3普通 4较低 5最低")
    private Integer level;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty(value = "创建人ID")
    @TableField(fill = FieldFill.INSERT)
    private Long createUserId;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "是否所有人可见 1 是 0 否")
    private Integer isOpen;

    @ApiModelProperty(value = "公开项目成员角色id")
    private Long ownerRole;

    @ApiModelProperty(value = "归档时间")
    private LocalDateTime archiveTime;

    @ApiModelProperty(value = "删除时间")
    private LocalDateTime deleteTime;

    @ApiModelProperty(value = "图标")
    private String icon;

    @ApiModelProperty(value = "批次ID")
    private String batchId;

    @ApiModelProperty(value = "方案ID")
    private Long schemeId;


    @ApiModelProperty(value = "项目成员")
    private String ownerUserIds;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;

    @ApiModelProperty(value = "关联项目的部门")
    private String ownerDeptIds;

    @ApiModelProperty(value = "修改人ID")
    private Long updateUserId;

    @ApiModelProperty(value = "开始时间")
    private String startTime;

    @ApiModelProperty(value = "结束时间")
    private String stopTime;

    @ApiModelProperty(value = "项目成员列表")
    @TableField(exist = false)
    private List<ProjectOwnerRoleSimpleBO> WorkOwnerRoleList;

    @ApiModelProperty(value = "项目完成度")
    @TableField(exist = false)
    private Double theProgress;

}
