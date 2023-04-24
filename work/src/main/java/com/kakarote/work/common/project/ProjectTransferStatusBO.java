package com.kakarote.work.common.project;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "事件状态迁移", description = "事件状态迁移")
public class ProjectTransferStatusBO {
    @ApiModelProperty(value = "事件id")
    private Long eventId;
    @ApiModelProperty(value = "源状态列表")
    private List<Long> sourceStatus;
    @ApiModelProperty(value = "迁移状态列表")
    private List<Long> transferStatus;
}
