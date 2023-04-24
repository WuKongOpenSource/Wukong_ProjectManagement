package com.kakarote.work.entity.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 项目事项工时VO
 * </p>
 *
 * @author zhangyongjie
 * @since 2022-09-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "ProjectTaskTimeVO对象", description = "项目事项工时VO")
public class ProjectTaskTimeVO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "项目事项主键ID")
    private Long taskId;

    @ApiModelProperty(value = "预估工时")
    private Integer actualHour;

    @ApiModelProperty(value = "已登记工时")
    private Integer registeredHours;

}
