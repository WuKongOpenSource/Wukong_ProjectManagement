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
 * 项目收藏表
 * </p>
 *
 * @author bai
 * @since 2022-09-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("wk_project_collect")
@ApiModel(value="ProjectCollect对象", description="项目收藏表")
public class ProjectCollect implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "项目收藏id")
    @TableId(value = "collect_id", type = IdType.ASSIGN_ID)
    private Long collectId;

    @ApiModelProperty(value = "项目id")
    private Long projectId;

    @ApiModelProperty(value = "类型 1项目 2任务")
    private Long type;

    @ApiModelProperty(value = "用户id")
    private Long userId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty(value = "创建人ID")
    @TableField(fill = FieldFill.INSERT)
    private Long createUserId;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "修改人ID")
    private Long updateUserId;


}
