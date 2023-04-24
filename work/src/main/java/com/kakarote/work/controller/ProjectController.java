package com.kakarote.work.controller;


import cn.hutool.core.bean.BeanUtil;

import com.kakarote.common.result.BasePage;
import com.kakarote.common.result.Result;


import com.kakarote.work.common.project.ProjectOwnerRoleBO;
import com.kakarote.work.entity.BO.ProjectGanttQueryBO;
import com.kakarote.work.entity.BO.ProjectQueryBO;
import com.kakarote.work.entity.BO.ProjectVo;
import com.kakarote.work.entity.PO.Project;
import com.kakarote.work.entity.VO.ProjectGanttVO;
import com.kakarote.work.service.IProjectService;
import com.kakarote.work.service.IProjectTaskTimeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * <p>
 * 项目表 前端控制器
 * </p>
 *
 * @author bai
 * @since 2022-09-08
 */
@RestController
@RequestMapping("/project")
@Api(tags = "项目列表new")
public class ProjectController {

    @Autowired
    IProjectService projectService;
    @Autowired
    IProjectTaskTimeService projectTaskTimeService;

    @PostMapping("/addProject")
    @ApiOperation("新建项目")
    public Result<Project> addProject(@RequestBody Project project) {
        Project work = projectService.addProject(project);
        return Result.ok(work);
    }

    @PostMapping("/updateProject")
    @ApiOperation("编辑项目")
    public Result<Project> updateProject(@RequestBody ProjectVo projectVo) {
        Project project1 = projectService.updateProject(projectVo);
        return Result.ok(project1);
    }

    @PostMapping("/queryOpenAuthEdit")
    @ApiOperation("查询公开项目是否有编辑权限")
    public Result<ProjectOwnerRoleBO> queryOpenAuthEdit(@PathVariable @NotNull Long projectId) {
        return Result.ok(projectService.queryOpenAuthEdit(projectId));
    }

    @PostMapping("/queryProjectById")
    @ApiOperation("项目详情查询")
    public Result<Project> queryProjectById(@RequestParam("projectId") Long projectId,@RequestParam(value = "taskId",required = false) Long taskId) {
        Project project1 = projectService.getProjectById(projectId,taskId);

        return Result.ok(project1);
    }

    @PostMapping("/getProjectById")
    @ApiOperation("通过ID查询项目")
    public Result<ProjectVo> getProjectById(@RequestParam("projectId") Long projectId, @RequestParam(value = "taskId",required = false) Long taskId) {
        Project project1 = projectService.getProjectById(projectId,taskId);
        ProjectVo projectVo = new ProjectVo();
        BeanUtil.copyProperties(project1, projectVo);
        return Result.ok(projectVo);
    }

    @PostMapping("/deleteProject/{projectId}")
    @ApiOperation("删除项目")
    public Result deleteProject(@PathVariable @NotNull Long projectId) {
        projectService.deleteProject(projectId);
        return Result.ok();
    }

    @PostMapping("/queryProjectList")
    @ApiOperation("查询项目信息列表")
    public Result<BasePage<Project>> queryProjectList(@RequestBody ProjectQueryBO projectQueryBO) {
        return Result.ok(projectService.queryProjectList(projectQueryBO));
    }

    @PostMapping("/iParticipateProjectList")
    @ApiOperation("查询我参与的项目")
    public Result<BasePage<Project>> iParticipateProjectList(@RequestBody ProjectQueryBO projectQueryBO) {
        return Result.ok(projectService.iParticipateProjectList(projectQueryBO));
    }

    @PostMapping("/iManagementProjectList")
    @ApiOperation("查询我管理的项目")
    public Result<BasePage<Project>> iManagementProjectList(@RequestBody ProjectQueryBO projectQueryBO) {
        return Result.ok(projectService.iManagementProjectList(projectQueryBO));
    }

    @PostMapping("/allProjectList")
    @ApiOperation("查询全部项目")
    public Result<BasePage<Project>> allProjectList(@RequestBody ProjectQueryBO projectQueryBO) {
        return Result.ok(projectService.allProjectList(projectQueryBO));
    }

    @PostMapping("/archiveProject")
    @ApiOperation("设置项目状态(1恢复 2归档 3删除)")
    public Result archiveProject(@RequestParam("projectId") Long projectId, @RequestParam("setType") Integer setType) {
        projectService.archiveProject(projectId, setType);
        return Result.ok();
    }

    @PostMapping("/archiveProjectList")
    @ApiOperation("查询项目( 2归档 3删除)")
    public Result<BasePage<Project>> archiveProjectList(@RequestBody ProjectQueryBO projectQueryBO) {
        return Result.ok(projectService.archiveProjectList(projectQueryBO));
    }

    @PostMapping("/getProjectGantt")
    @ApiOperation("项目（甘特图）")
    public Result<List<ProjectGanttVO>> getProjectGantt(@RequestBody ProjectGanttQueryBO projectGanttQueryBO) {
        return Result.ok(projectTaskTimeService.getProjectGantt(projectGanttQueryBO));
    }
    @PostMapping("/myProjectList")
    @ApiOperation("查询我的全部项目")
    public Result<BasePage<Project>> myProjectList(@RequestBody ProjectQueryBO projectQueryBO) {
        return Result.ok(projectService.myProjectList(projectQueryBO));
    }
}

