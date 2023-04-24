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
 * 项目管理 看板信息
 * </p>
 *
 * @author guole
 * @since 2022-09-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_project_board")
@ApiModel(value="ProjectBoard对象", description="项目管理 看板信息")
public class ProjectBoard implements Serializable {

    private static final long serialVersionUID=1L;
    @TableId(value = "project_board_id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "状态id")
    private Long projectBoardId;

    @ApiModelProperty(value = "看板名称")
    private String boardName;

    @ApiModelProperty(value = "排序 从小到大")
    private Long sorting;

    @ApiModelProperty(value = "事件id")
    private Long eventId;

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
