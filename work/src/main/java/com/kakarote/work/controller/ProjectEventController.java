package com.kakarote.work.controller;



import com.kakarote.common.result.Result;
import com.kakarote.work.entity.PO.ProjectEvent;
import com.kakarote.work.service.IProjectEventService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 事件表 前端控制器
 * </p>
 *
 * @author bai
 * @since 2022-09-19
 */
@RestController
@RequestMapping("/projectEvent")
@Api(tags = "事件类型new")
public class ProjectEventController {

    @Autowired
    IProjectEventService projectEventService;

    @PostMapping("/add")
    @ApiOperation("新建事件")
    public Result add(@RequestBody ProjectEvent projectEvent) {
        projectEventService.add(projectEvent);
        return Result.ok();
    }

    @PostMapping("/update")
    @ApiOperation("更新事件")
    public Result update(@RequestBody ProjectEvent projectEvent) {
        projectEventService.update(projectEvent);
        return Result.ok();
    }

    @PostMapping("/delete")
    @ApiOperation("删除事件")
    public Result delete(@RequestParam Long schemeId) {
        projectEventService.delete(schemeId);
        return Result.ok();
    }

    @PostMapping("/queryEventList")
    @ApiOperation("查询配置方案下的事件")
    public Result<List<ProjectEvent>> queryEventList(@RequestParam("schemeId") Long schemeId) {
        return Result.ok(projectEventService.queryEventList(schemeId));
    }
}

