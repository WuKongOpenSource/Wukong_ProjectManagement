package com.kakarote.work.entity.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 项目管理 项目看板状态
 * </p>
 *
 * @author guole
 * @since 2022-09-22
 */
@Data
@ApiModel(value = "ProjectRolesGroupVO对象", description = "项目管理 查看角色关联成员数目列表")
public class ProjectRolesGroupVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "角色ID")
    private Long roleId;

    @ApiModelProperty(value = "关联成员数目")
    private Long num;

    @ApiModelProperty(value = "角色名称")
    private String roleName;

    @ApiModelProperty(value = "创建时间")
    private String createTime;

    @ApiModelProperty(value = "当前用户权限")
    private List<Long> rules;
}
