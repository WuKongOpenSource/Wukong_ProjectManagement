package com.kakarote.work.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

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
@ApiModel(value="ProjectAnnouncementBO对象", description="项目公告表")
public class ProjectAnnouncementBO {

    @ApiModelProperty(value = "项目公告ID")
    private Long projectAnnouncementId;

    @ApiModelProperty(value = "项目ID")
    private Long projectId;

    @ApiModelProperty(value = "公告内容")
    private String content;

    @ApiModelProperty(value = "类型")
    private Integer type;

    @ApiModelProperty(value = "创建人ID")
    private Long createUserId;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "批次 比如附件批次")
    private String batchId;


}
