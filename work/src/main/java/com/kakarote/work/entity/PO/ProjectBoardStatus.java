package com.kakarote.work.entity.PO;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 项目管理 项目看板状态
 * </p>
 *
 * @author guole
 * @since 2022-09-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_project_board_status")
@ApiModel(value = "ProjectBoardStatus对象", description = "项目管理 项目看板状态")
public class ProjectBoardStatus implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId(value = "project_board_status_id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "项目看板状态id")
    private Long projectBoardStatusId;

    @ApiModelProperty(value = "看板id")
    private Long boardId;

    @ApiModelProperty(value = "状态id")
    private Long statusId;

    @ApiModelProperty(value = "排序")
    private Integer sorting;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty(value = "修改时间")
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
    @ApiModelProperty(value = "修改人ID")
    private Long updateUserId;

    @ApiModelProperty(value = "创建人ID")
    @TableField(fill = FieldFill.INSERT)
    private Long createUserId;

}
