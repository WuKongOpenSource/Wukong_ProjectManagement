package com.kakarote.work.entity.VO;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 任务表
 * </p>
 *
 * @author bai
 * @since 2022-09-08
 */
@Data
@ApiModel(value = "ProjectTaskVO对象", description = "任务VO")
public class ProjectTaskVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "任务id")
    private Long taskId;

    @ApiModelProperty(value = "任务名称")
    private String name;

    @ApiModelProperty(value = "0 带规划 1迭代 2需求 3任务 4缺陷 5工时")
    private Integer type;

    @ApiModelProperty(value = "事件状态")
    private Long status;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "开始时间")
    private Date startTime;

    @ApiModelProperty(value = "结束时间")
    private Date stopTime;

    @ApiModelProperty(value = "优先级 从大到小 3高 2中 1低 0无")
    private Integer priority;

    @ApiModelProperty(value = "看板状态名称")
    private String boardStatusName;

    @ApiModelProperty(value = "序号")
    private Integer num;

    @ApiModelProperty(value = "负责人ID")
    private Long mainUserId;

    @ApiModelProperty(value = "负责人img")
    @TableField(exist = false)
    private String mainUserImg;

    @ApiModelProperty(value = "负责人名字")
    @TableField(exist = false)
    private String mainUserName;
}
