package com.kakarote.work.entity.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

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

@ApiModel(value="ProjectBoard对象", description="项目管理 看板信息")
public class ProjectBoardVO implements Serializable {

    private static final long serialVersionUID=1L;

    private Long projectBoardId;

    @ApiModelProperty(value = "看板名称")
    private String boardName;

    @ApiModelProperty(value = "排序 从小到大")
    private Integer sorting;

    @ApiModelProperty(value = "事件id")
    private Long eventId;

    @ApiModelProperty(value = "绑定状态列表")
    public List<ProjectBoardStatusVO> statusList;

}
