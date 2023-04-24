package com.kakarote.work.controller;


import com.alibaba.fastjson.JSONObject;
import com.kakarote.common.ApiExplain;
import com.kakarote.common.result.Result;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.work.entity.PO.AdminRole;
import com.kakarote.work.service.IProjectRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 角色表 前端控制器
 * </p>
 *
 * @author zhangzhiwei
 * @since 2020-04-27
 */
@RestController
@RequestMapping("/projectRole")
@Api(tags = "角色模块")
public class ProjectRoleController {
    @Autowired
    private IProjectRoleService projectRoleService;

    @PostMapping("/auth")
    @ApiOperation("角色权限")
    public Result<JSONObject> auth() {
        JSONObject object = projectRoleService.auth(UserUtil.getUserId());
        return Result.ok(object);
    }

    @PostMapping(value = "/queryProjectRoleByTypes")
    @ApiExplain("根据项目类型查询项目管理角色")
    public Result<List<AdminRole>> queryProjectRoleByTypes(@RequestParam("types") List<Integer> types) {
        List<AdminRole> adminRoles = projectRoleService.queryProjectRoleByTypes(types);
        return Result.ok(adminRoles);

    }

    @PostMapping("/getRoleByType")
    @ApiOperation("通过角色类型查询角色")
    public Result<List<AdminRole>> getRoleByType() {
        List<AdminRole> roleByType = projectRoleService.getRoleByType();
        return Result.ok(roleByType);
    }

    @PostMapping("/addProjectRole")
    @ApiOperation("添加自定义项目角色和权限")
    public Result addProjectRole(@RequestBody AdminRole adminRole) {
        return Result.ok(projectRoleService.addProjectRole(adminRole));
    }

    @PostMapping("/updateRoleMenu")
    @ApiOperation("保存角色菜单关系")
    public Result updateRoleMenu(@RequestBody AdminRole adminRole) {
        projectRoleService.updateRoleMenu(adminRole);
        return Result.ok();
    }

    @PostMapping("/delete")
    @ApiOperation("删除角色")
    public Result delete(@RequestParam("roleId") Long roleId) {
        projectRoleService.delete(roleId);
        return Result.ok();
    }

    @RequestMapping("/getMenuListByType")
    @ApiOperation("根据类型查询菜单")
    public Result<JSONObject> getMenuListByType() {
        JSONObject byType = projectRoleService.getMenuListByType();
        return Result.ok(byType);
    }


}

