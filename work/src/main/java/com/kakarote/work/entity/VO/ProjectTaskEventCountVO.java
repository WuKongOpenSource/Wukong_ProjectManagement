package com.kakarote.work.entity.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 迭代下事件分布图
 * </p>
 *
 * @author baijc
 * @since 2022-09-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "ProjectTaskEventCountVO对象", description = "迭代下事件分布图")
public class ProjectTaskEventCountVO {

    @ApiModelProperty(value = "需求未开始数量")
    private Integer demandNoStartNum;

    @ApiModelProperty(value = "需求进行中数量")
    private Integer demandRunNum;

    @ApiModelProperty(value = "需求已完成数量")
    private Integer demandEndNum;

    @ApiModelProperty(value = "任务未开始数量")
    private Integer taskNoStartNum;

    @ApiModelProperty(value = "任务进行中数量")
    private Integer taskRunNum;

    @ApiModelProperty(value = "任务已完成数量")
    private Integer taskEndNum;

    @ApiModelProperty(value = "缺陷未开始数量")
    private Integer defectsNoStartNum;

    @ApiModelProperty(value = "缺陷进行中数量")
    private Integer defectsRunNum;

    @ApiModelProperty(value = "缺陷已完成数量")
    private Integer defectsEndNum;

}
