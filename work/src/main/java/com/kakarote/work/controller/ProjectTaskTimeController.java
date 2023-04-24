package com.kakarote.work.controller;


import cn.hutool.core.date.LocalDateTimeUtil;

import com.kakarote.common.result.Result;
import com.kakarote.work.entity.BO.ProjectGanttQueryBO;
import com.kakarote.work.entity.PO.ProjectTaskLog;
import com.kakarote.work.entity.PO.ProjectTaskTime;
import com.kakarote.work.entity.VO.ProjectTaskTimeStatisticsVO;
import com.kakarote.work.entity.VO.ProjectTaskTimeVO;
import com.kakarote.work.service.IProjectTaskLogService;
import com.kakarote.work.service.IProjectTaskTimeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 项目事项工时表 前端控制器
 * </p>
 *
 * @author zhangyongjie
 * @since 2022-09-21
 */
@RestController
@RequestMapping("/projectTaskTime")
@Api(tags = "项目事项工时new")
public class ProjectTaskTimeController {

    @Autowired
    IProjectTaskTimeService projectTaskTimeService;
    @Autowired
    IProjectTaskLogService projectTaskLogService;

    @PostMapping("/queryProjectTaskTimeList")
    @ApiOperation("查询项目事项工时列表")
    public Result<List<ProjectTaskTimeStatisticsVO>> queryProjectTaskTimeList(@RequestBody ProjectGanttQueryBO projectGanttQueryBO) {
        List<ProjectTaskTimeStatisticsVO> pageList = projectTaskTimeService.queryProjectTaskTimeList(projectGanttQueryBO);
        return Result.ok(pageList);
    }

    @RequestMapping(value = "/queryProjectTaskTimeByTaskId/{taskId}", method = RequestMethod.POST)
    @ApiOperation("根据项目事项ID获取工时")
    public Result<ProjectTaskTimeVO> queryProjectTaskTimeByTaskId(@PathVariable("taskId") Long taskId) {
        ProjectTaskTimeVO projectTaskTimeVO = projectTaskTimeService.queryProjectTaskTimeByTaskId(taskId);
        return Result.ok(projectTaskTimeVO);
    }

    @PostMapping("/addProjectTaskTime")
    @ApiOperation("新建项目事项工时")
    public Result addProjectTaskTime(@RequestBody ProjectTaskTime projectTaskTime) {
        ProjectTaskLog projectTaskLog = new ProjectTaskLog();
        projectTaskLog.setTaskId(projectTaskTime.getTaskId());
        projectTaskLog.setType(2);
        projectTaskTime.setBeginTime(new Date());
        projectTaskTime.setEndTime(new Date());
         projectTaskLog.setContent("登记了工时 ".concat(projectTaskTime.getActualHour().toString() + "小时"));
        projectTaskLogService.saveTaskLog(projectTaskLog);
        projectTaskTime.setUpdateTime(LocalDateTimeUtil.now());
        return Result.ok(projectTaskTimeService.save(projectTaskTime));


    }

    @PostMapping("/updateProject")
    @ApiOperation("编辑项目事项工时")
    public Result updateProjectTaskTime(@RequestBody ProjectTaskTime projectTaskTime) {
        return Result.ok(projectTaskTimeService.updateById(projectTaskTime));
    }

}

