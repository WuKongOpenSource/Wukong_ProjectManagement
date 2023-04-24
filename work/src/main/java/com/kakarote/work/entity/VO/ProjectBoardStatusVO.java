package com.kakarote.work.entity.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 项目管理 项目看板状态
 * </p>
 *
 * @author guole
 * @since 2022-09-22
 */
@Data
@ApiModel(value = "ProjectBoardStatus对象", description = "项目管理 项目看板状态")
public class ProjectBoardStatusVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "项目看板状态id")
    private Long projectBoardStatusId;

    @ApiModelProperty(value = "状态名称")
    private String statusName;

    @ApiModelProperty(value = "看板id")
    private Long boardId;

    @ApiModelProperty(value = "状态id")
    private Long statusId;

    @ApiModelProperty(value = "排序")
    private Integer sorting;
    @ApiModelProperty(value = "状态类型 1 未完成 2进行中 3已完成")
    private Integer statusType;
    private List<ProjectTaskVO> taskVOList;

}
