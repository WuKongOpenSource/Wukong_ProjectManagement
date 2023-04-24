package com.kakarote.work.controller;


import cn.hutool.core.collection.CollectionUtil;

import com.kakarote.common.result.Result;
import com.kakarote.common.result.PageEntity;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.work.entity.PO.ProjectLabel;
import com.kakarote.work.entity.PO.ProjectTask;
import com.kakarote.work.entity.PO.ProjectTaskLog;
import com.kakarote.work.service.IProjectLabelService;
import com.kakarote.work.service.IProjectTaskLogService;
import com.kakarote.work.service.IProjectTaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 任务标签表 前端控制器
 * </p>
 *
 * @author bai
 * @since 2022-09-20
 */
@RestController
@RequestMapping("/projectLabel")
@Api(tags = "标签new")
public class ProjectLabelController {

    @Autowired
    IProjectLabelService projectLabelService;
    @Autowired
    IProjectTaskService projectTaskService;
    @Autowired
    IProjectTaskLogService projectTaskLogService;

    @PostMapping("/add")
    @ApiOperation("新建标签")
    public Result add(@RequestBody ProjectLabel projectLabel) {
        projectLabelService.add(projectLabel);
        return Result.ok();
    }

    @PostMapping("/update")
    @ApiOperation("更新标签")
    public Result update(@RequestBody ProjectLabel projectLabel) {
        projectLabelService.update(projectLabel);
        return Result.ok();
    }

    @PostMapping("/delete")
    @ApiOperation("删除标签")
    public Result delete(@RequestParam("labelId") Long labelId) {
        projectLabelService.delete(labelId);
        return Result.ok();
    }

    @PostMapping("/queryList")
    @ApiOperation("模糊查询所有标签")
    public Result<List<ProjectLabel>> queryEventList(String name) {
        return Result.ok(projectLabelService.queryList(name));
    }

    @PostMapping("/queryTaskLabelList")
    @ApiOperation("查询任务下标签")
    public Result<List<ProjectLabel>> queryTaskLabelList(@RequestParam("ids") String ids) {
        return Result.ok(projectLabelService.queryTaskLabelList(ids));
    }

    @PostMapping("/updateTaskLabel")
    @ApiOperation("更新任务下标签")
    public Result<List<ProjectLabel>> updateTaskLabel(@RequestParam("taskId") Long taskId, @RequestParam("ids") String ids) {
        List<ProjectLabel> projectLabels = projectLabelService.lambdaQuery().in(ProjectLabel::getLabelId, ids).list();
        ProjectTaskLog projectTaskLog = new ProjectTaskLog();
        projectTaskLog.setTaskId(taskId);
        projectTaskLog.setType(1);
        String userName = UserUtil.getUser().getNickname();
        ProjectTask projectTask = projectTaskService.lambdaQuery().eq(ProjectTask::getTaskId, taskId).one();
        String content = "";
        if (CollectionUtil.isNotEmpty(projectLabels)) {
            for (ProjectLabel projectLabel : projectLabels) {
                content = content.concat(projectLabel.getName() + " ");
            }
        }
        projectTaskLog.setContent(userName.concat(" 更新任务 ").concat(projectTask.getName()).concat(" 的标签为 " + content));
        projectTaskLogService.saveTaskLog(projectTaskLog);
        projectTaskService.lambdaUpdate().set(ProjectTask::getLabel, ids).eq(ProjectTask::getTaskId, taskId).update();
        return Result.ok(projectLabelService.queryTaskLabelList(ids));
    }
}

