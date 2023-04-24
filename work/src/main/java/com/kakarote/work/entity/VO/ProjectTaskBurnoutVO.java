package com.kakarote.work.entity.VO;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

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
@ApiModel(value = "ProjectTaskBurnoutVO", description = "迭代燃尽图")
public class ProjectTaskBurnoutVO {

    @ApiModelProperty(value = "总剩余工时")
    private Integer surplus;

    @ApiModelProperty(value = "总预估工时")
    private Integer forecastTime;

    @ApiModelProperty(value = "时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date updateTime;

    @ApiModelProperty(value = "当前登记总工时")
    private Integer actualHour;

}
