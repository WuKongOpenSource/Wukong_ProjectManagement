package com.kakarote.work.entity.PO;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 项目公告表
 * </p>
 *
 * @author zhangzhiwei
 * @since 2022-09-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_project_announcement")
@ApiModel(value="ProjectAnnouncement对象", description="项目公告表")
public class ProjectAnnouncement implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "project_announcement_id", type = IdType.ASSIGN_ID)
    private Long projectAnnouncementId;

    @ApiModelProperty(value = "项目ID")
    private Long projectId;

    @ApiModelProperty(value = "公告内容")
    private String content;

    @ApiModelProperty(value = "类型")
    private Integer type;

    @ApiModelProperty(value = "创建人ID")
    @TableField(fill = FieldFill.INSERT)
    private Long createUserId;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;

    @ApiModelProperty(value = "批次 比如附件批次")
    private String batchId;


}
