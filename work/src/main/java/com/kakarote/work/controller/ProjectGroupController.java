package com.kakarote.work.controller;



import com.kakarote.common.result.Result;
import com.kakarote.work.entity.PO.ProjectGroup;
import com.kakarote.work.service.IProjectGroupService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 项目分组表 前端控制器
 * </p>
 *
 * @author bai
 * @since 2022-09-09
 */
@RestController
@RequestMapping("/projectGroup")
@Api(tags = "项目分组new")
public class ProjectGroupController {

    @Autowired
    private IProjectGroupService projectGroupService;

    @PostMapping("/addGroup")
    @ApiOperation("添加项目分组")
    public Result addGroup(@RequestBody ProjectGroup projectGroup) {
        projectGroupService.addGroup(projectGroup);
        return Result.ok();
    }

    @PostMapping("/updateGroup")
    @ApiOperation("更新项目分组")
    public Result updateGroup(@RequestBody ProjectGroup projectGroup) {
        projectGroupService.updateGroup(projectGroup);
        return Result.ok();
    }

    @PostMapping("/updateGroupBatch")
    @ApiOperation("批量更新项目分组")
    public Result updateGroupBatch(@RequestBody List<ProjectGroup> projectGroups) {
        projectGroupService.updateGroupBatch(projectGroups);
        return Result.ok();
    }

    @PostMapping("/searchGroupList")
    @ApiOperation("查询分组")
    public Result searchGroupList() {
        return Result.ok(projectGroupService.searchGroupList());
    }

    @PostMapping("/removeGroupById")
    @ApiOperation("移除分组")
    public Result removeGroupById(@RequestParam Long groupId) {
        projectGroupService.removeGroupById(groupId);
        return Result.ok();
    }
}

