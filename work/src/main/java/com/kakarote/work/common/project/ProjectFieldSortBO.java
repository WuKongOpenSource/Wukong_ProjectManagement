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
public class ProjectFieldSortBO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "项目ID")
    private Long projectId;

    @ApiModelProperty(value = "字段名称")
    private String fieldName;

    @ApiModelProperty(value = "字段中文名称")
    private String name;


    @ApiModelProperty(value = "字段排序")
    private Integer sort;

    @ApiModelProperty(value = "宽度")
    private Integer width;

    @ApiModelProperty(value = "是否隐藏 0、不隐藏 1、隐藏")
    private Integer isHide;

    @ApiModelProperty(value = "模块类型")
    private Integer moduleType;

}
