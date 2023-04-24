package com.kakarote.work.entity.BO;

import com.kakarote.common.result.PageEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author JiaS
 * @date 2020/11/6
 */
@Data
@ApiModel("项目状态查询参数")
public class ProjectStatusQueryBO extends PageEntity {


    @ApiModelProperty("搜索状态名称")
    private String search;


}
