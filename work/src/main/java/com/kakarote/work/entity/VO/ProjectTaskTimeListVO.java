package com.kakarote.work.entity.VO;

import com.kakarote.work.entity.PO.ProjectTaskTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * <p>
 * 项目事项工时列表VO
 * </p>
 *
 * @author zhangyongjie
 * @since 2022-09-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "ProjectTaskTimeListVO对象", description = "项目事项工时列表VO")
public class ProjectTaskTimeListVO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "项目事项主键ID")
    private Long taskId;

    @ApiModelProperty(value = "项目事项名称")
    private String name;
    @ApiModelProperty(value = "项目事项类型")
    private Integer type;
    @ApiModelProperty(value = "总计")
    private Integer totalTime;

    @ApiModelProperty(value = "所属迭代ID")
    private Long belongIterationId;

//    @ApiModelProperty(value = "统计集合")
//    private List<Map<String, Object>> actualHourList;
@ApiModelProperty(value = "工时列表")
private List<ProjectTaskTime> taskTimeVOS;
}
