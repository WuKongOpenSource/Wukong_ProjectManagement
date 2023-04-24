package com.kakarote.work.controller;



import com.kakarote.common.result.Result;
import com.kakarote.work.entity.BO.ProjectTransferStatusBO;
import com.kakarote.work.entity.PO.ProjectEventStatus;
import com.kakarote.work.service.IProjectEventStatusService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 事件属性表 前端控制器
 * </p>
 *
 * @author bai
 * @since 2022-09-19
 */
@RestController
@RequestMapping("/projectEventStatus")
@Api(tags = "事件状态new")
public class ProjectEventStatusController {

    @Autowired
    IProjectEventStatusService projectEventStatusService;

    @PostMapping("/add")
    @ApiOperation("新建事件状态")
    public Result add(@RequestBody ProjectEventStatus projectEventStatus) {
        projectEventStatusService.add(projectEventStatus);
        return Result.ok();
    }

    @PostMapping("/update")
    @ApiOperation("更新事件状态")
    public Result update(@RequestBody ProjectEventStatus projectEventStatus) {
        projectEventStatusService.update(projectEventStatus);
        return Result.ok();
    }

    @PostMapping("/delete")
    @ApiOperation("删除事件状态")
    public Result delete(@RequestParam("statusId") Long statusId) {
        projectEventStatusService.delete(statusId);
        return Result.ok();
    }

    @PostMapping("/updateSorting")
    @ApiOperation("修改状态顺序")
    public Result updateSorting(@RequestBody List<Long> ids) {
        projectEventStatusService.updateSorting(ids);
        return Result.ok();
    }

    @PostMapping("/list")
    @ApiOperation("事件状态列表")
    public Result<List<ProjectEventStatus>> list(@RequestParam("eventId") Long eventId,@RequestParam(value = "sysType",required = false) Integer sysType,
                                                 @RequestParam("projectId") Long projectId) {
        List<ProjectEventStatus> projectEventStatusList = projectEventStatusService.queryEventStatusByEventId(eventId,sysType,projectId);
        return Result.ok(projectEventStatusList);
    }

    @PostMapping("/applicationSchemeStatusList")
    @ApiOperation("应用配置状态列表")
    public Result<List<ProjectEventStatus>> applicationSchemeStatusList(@RequestParam("projectId") Long projectId,@RequestParam("eventId") Long eventId) {
        List<ProjectEventStatus> projectEventStatusList = projectEventStatusService.applicationSchemeStatusList(projectId,eventId);
        return Result.ok(projectEventStatusList);
    }

    @PostMapping("/transferStatus")
    @ApiOperation("状态迁移")
    public Result transferStatus(@RequestBody ProjectTransferStatusBO projectTransferStatusBO) {
        projectEventStatusService.transferStatus(projectTransferStatusBO);
        return Result.ok();
    }

    @PostMapping("/updateInitStatus")
    @ApiOperation("更新初始状态")
    public Result updateInitStatus(@RequestParam("eventId") Long eventId, @RequestParam("projectStatusId") Long projectStatusId) {
        projectEventStatusService.updateInitStatus(eventId, projectStatusId);
        return Result.ok();
    }

    @PostMapping("/remove")
    @ApiOperation("移除事件状态")
    public Result remove(@RequestParam("eventStatusId") Long eventStatusId) {
        projectEventStatusService.lambdaUpdate().eq(ProjectEventStatus::getId, eventStatusId).set(ProjectEventStatus::getUseStatus, 2).update();
        return Result.ok();
    }

    @PostMapping("/giveUpApplicationScheme")
    @ApiOperation("放弃编辑应用方案")
    public Result giveUpApplicationScheme(@RequestParam("eventId") Long eventId) {
        projectEventStatusService.lambdaUpdate().eq(ProjectEventStatus::getProjectEventId, eventId).eq(ProjectEventStatus::getUseStatus, 0).remove();
        projectEventStatusService.lambdaUpdate().eq(ProjectEventStatus::getProjectEventId, eventId).eq(ProjectEventStatus::getUseStatus, 2)
                .set(ProjectEventStatus::getUseStatus, 1).update();

        return Result.ok();
    }
}

