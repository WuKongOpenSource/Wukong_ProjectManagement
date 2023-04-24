package com.kakarote.work.common.project;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 字段排序表
 * </p>
 *
 * @author bai
 * @since 2022-09-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="ProjectFieldSortBO对象", description="字段排序表")
public class ProjectFieldSortQueryBO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "项目id")
    private Long projectId;

    @ApiModelProperty(value = "模块类型")
    private Integer moduleType;


}
