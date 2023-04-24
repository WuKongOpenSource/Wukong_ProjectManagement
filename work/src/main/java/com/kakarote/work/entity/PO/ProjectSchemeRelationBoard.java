package com.kakarote.work.entity.PO;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@TableName("wk_project_scheme_relation_board")
@ApiModel(value="ProjectSchemeRelationBoard对象", description="项目配置关联和看板关系表")
public class ProjectSchemeRelationBoard {

    private Long id;
    @ApiModelProperty(value = "项目配置关联")
    private Long schemeRelationId;
    @ApiModelProperty(value = "看板名称")
    private String boardName;
    private Integer sorting;

    @ApiModelProperty(value = "创建人ID")
    @TableField(fill = FieldFill.INSERT)
    private Long createUserId;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty(value = "修改人ID")
    private Long updateUserId;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
    @ApiModelProperty(value = "项目id")
    private Long projectId;
}
