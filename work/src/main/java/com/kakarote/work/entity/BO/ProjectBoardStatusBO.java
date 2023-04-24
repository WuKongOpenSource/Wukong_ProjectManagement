package com.kakarote.work.entity.BO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 项目管理 项目看板状态
 * </p>
 *
 * @author guole
 * @since 2022-09-22
 */
@Data
public class ProjectBoardStatusBO implements Serializable {

    private static final long serialVersionUID = 1L;


    @ApiModelProperty(value = "状态id")
    private Long statusId;

    @ApiModelProperty(value = "排序")
    private Integer sorting;


}
