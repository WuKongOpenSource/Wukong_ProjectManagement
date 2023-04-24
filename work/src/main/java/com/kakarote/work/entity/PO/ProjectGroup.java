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
 * 项目分组表
 * </p>
 *
 * @author bai
 * @since 2022-09-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("wk_project_group")
@ApiModel(value="ProjectGroup对象", description="项目分组表")
public class ProjectGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "项目分组ID")
    @TableId(value = "group_id", type = IdType.ASSIGN_ID)
    private Long groupId;

    @ApiModelProperty(value = "分组名称")
    private String name;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "创建人ID")
    @TableField(fill = FieldFill.INSERT)
    private Long createUserId;

    @ApiModelProperty(value = "新建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "修改人ID")
    private Long updateUserId;

    @ApiModelProperty(value = "分组类型(1:全部分组，2:未分组，3:自定义分组)")
    private Integer type;

    @ApiModelProperty(value = "关联项目数量")
    @TableField(exist = false)
    private Integer num;


}
