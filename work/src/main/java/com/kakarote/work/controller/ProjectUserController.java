package com.kakarote.work.controller;


import com.alibaba.fastjson.JSONObject;

import com.kakarote.common.result.Result;

import com.kakarote.work.common.admin.AdminProjectRole;
import com.kakarote.work.common.admin.AdminProjectRoleBO;
import com.kakarote.work.common.project.ProjectOwnerRoleBO;
import com.kakarote.work.common.admin.AdminEditProjectRoleBO;
import com.kakarote.work.entity.BO.ProjectRoleQueryBO;
import com.kakarote.work.entity.VO.ProjectRolesGroupVO;
import com.kakarote.work.service.IProjectUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 项目成员表 前端控制器
 * </p>
 *
 * @author bai
 * @since 2022-10-27
 */
@RestController
@RequestMapping("/projectUser")
@Api(tags = "项目成员管理")
public class ProjectUserController {

    @Autowired
    private IProjectUserService projectUserService;

    @PostMapping("/relatedProjectUser")
    @ApiOperation("添加项目成员")
    public Result relatedProjectUser(@RequestBody List<AdminProjectRoleBO> adminProjectRoleBOS) {
        projectUserService.relatedProjectUser(adminProjectRoleBOS);
        return Result.ok();
    }

    @PostMapping("/editProjectUser")
    @ApiOperation("编辑项目成员")
    public Result editProjectUser(@RequestBody AdminEditProjectRoleBO adminEditProjectRoleBO) {
        projectUserService.editProjectUser(adminEditProjectRoleBO);
        return Result.ok();
    }

    @PostMapping("/getAllRoleMenu/{projectId}/{userId}")
    @ApiOperation("查看项目角色权限")
    public Result<List<Long>> getAllRoleMenu(@PathVariable("projectId") Long projectId, @PathVariable("userId") Long userId) {
        return Result.ok(projectUserService.getAllRoleMenu(projectId, userId));
    }

    @PostMapping("/getProjectRoles/{projectId}")
    @ApiOperation("查询项目关联的角色列表")
    public Result<List<ProjectRolesGroupVO>> getProjectRoles(@PathVariable("projectId") Long projectId) {
        return Result.ok(projectUserService.getProjectRoles(projectId));
    }

    @PostMapping("/deleteProjectRoles")
    @ApiOperation("移除项目角色")
    public Result deleteProjectRoles(@RequestBody AdminProjectRole adminProjectRole) {
        projectUserService.deleteProjectRoles(adminProjectRole);
        return Result.ok();
    }

    @PostMapping("/queryProjectUser")
    @ApiOperation("查询项目下所有员工和角色")
    public Result<List<ProjectOwnerRoleBO>> queryProjectUser(@RequestBody ProjectRoleQueryBO projectRoleQueryBO) {
        return Result.ok(projectUserService.queryProjectUser(projectRoleQueryBO));
    }

    @PostMapping("/projectAuth/{projectId}")
    @ApiOperation("项目管理角色权限")
    public Result<JSONObject> projectAuth(@PathVariable("projectId") Long projectId) {

        JSONObject object = projectUserService.getProjectAuth(projectId);
        return Result.ok(object);
    }

    @PostMapping("/projectAuthList")
    @ApiOperation("项目管理角色权限")
    public Result<List<JSONObject>> projectAuthList(@RequestParam("projectIds") List<Long> projectIds) {

        List<JSONObject> object = projectUserService.projectAuthList(projectIds);
        return Result.ok(object);
    }
}

