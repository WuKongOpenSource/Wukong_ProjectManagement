package com.kakarote.work.entity.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * <p>
 * 事项完成情况统计VO
 * </p>
 *
 * @author baijc
 * @since 2022-09-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ProjectTaskCountVO对象", description="事项完成情况统计VO")
public class ProjectTaskCountVO {

    @ApiModelProperty(value = "项目名称")
    private String projectName;

    @ApiModelProperty(value = "事项总数量")
    private Integer totalNumber;

    @ApiModelProperty(value = "每日已完成")
    private List<ProjectTaskNumVO> projectTaskNumVOEnd;

    @ApiModelProperty(value = "未开始的事件")
    private List<ProjectTaskNumVO> projectTaskNumVOsNoStart;

    @ApiModelProperty(value = "每日进行中的数量统计")
    private List<ProjectTaskNumVO> projectTaskNumVOsRun;

}
