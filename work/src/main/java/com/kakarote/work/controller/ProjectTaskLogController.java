package com.kakarote.work.controller;



import com.kakarote.common.result.Result;
import com.kakarote.work.entity.PO.ProjectTaskLog;
import com.kakarote.work.entity.VO.ProjectTaskLogVO;
import com.kakarote.work.service.IProjectTaskLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 任务日志表 前端控制器
 * </p>
 *
 * @author bai
 * @since 2022-09-14
 */
@RestController
@Api(tags = "日志new")
@RequestMapping("/projectTaskLog")
public class ProjectTaskLogController {

    @Autowired
    private IProjectTaskLogService projectTaskLogService;

    @PostMapping("/queryTaskLog/{taskId}/{type}")
    @ApiOperation("查询任务活动日志")
    public Result<List<ProjectTaskLogVO>> queryTaskLog(@PathVariable Long taskId, @PathVariable Long type) {
        return Result.ok(projectTaskLogService.queryTaskLog(taskId, type));
    }

    @PostMapping("/saveTaskLog")
    @ApiOperation("保存任务活动日志")
    public Result saveTaskLog(@RequestBody ProjectTaskLog projectTaskLog) {
        projectTaskLogService.saveTaskLog(projectTaskLog);
        return Result.ok();
    }


}

