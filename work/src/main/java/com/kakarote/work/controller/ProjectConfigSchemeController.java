package com.kakarote.work.controller;



import com.kakarote.common.result.Result;
import com.kakarote.work.entity.PO.ProjectConfigScheme;
import com.kakarote.work.entity.PO.ProjectEvent;
import com.kakarote.work.service.IProjectConfigSchemeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 项目配置方案表 前端控制器
 * </p>
 *
 * @author bai
 * @since 2022-09-19
 */
@RestController
@RequestMapping("/projectConfigScheme")
@Api(tags = "项目配置new")
public class ProjectConfigSchemeController {

    @Autowired
    IProjectConfigSchemeService projectConfigSchemeService;

    @PostMapping("/init")
    @ApiOperation("初始化项目配置(未完成)")
    public Result init(@RequestBody ProjectConfigScheme projectConfigScheme) {
        projectConfigSchemeService.add(projectConfigScheme);
        return Result.ok();
    }


    @PostMapping("/add")
    @ApiOperation("新曾项目配置")
    public Result add(@RequestBody ProjectConfigScheme projectConfigScheme) {
        projectConfigSchemeService.add(projectConfigScheme);
        return Result.ok();
    }

    @PostMapping("/update")
    @ApiOperation("更新项目配置")
    public Result update(@RequestBody ProjectConfigScheme projectConfigScheme) {
        projectConfigSchemeService.update(projectConfigScheme);
        return Result.ok();
    }

    @PostMapping("/delete")
    @ApiOperation("删除项目")
    public Result delete(@RequestBody Long schemeId) {
        projectConfigSchemeService.delete(schemeId);
        return Result.ok();
    }

    @PostMapping("/queryBySchemeId")
    @ApiOperation("根据配置方案ID查询事件列表")
    public Result<List<ProjectEvent>> queryBySchemeId(@RequestBody Long schemeId) {
        projectConfigSchemeService.queryBySchemeId(schemeId);
        return Result.ok();
    }
}

