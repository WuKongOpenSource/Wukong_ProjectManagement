package com.kakarote.work.common.project;

import com.kakarote.work.entity.PO.ProjectField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @author ZZW
 */
@Data
@ApiModel(value = "ProjectFieldModelBO对象", description = "自定义字段新增定义类")
public class ProjectFieldModelBO implements Serializable {

    @ApiModelProperty(value = "标签名")
    private String name;

    @NotNull
    @ApiModelProperty(value = "数据")
    private List<ProjectField> data;
}
