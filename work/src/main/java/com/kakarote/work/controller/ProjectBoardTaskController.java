package com.kakarote.work.controller;


import com.kakarote.common.result.Result;
import com.kakarote.work.entity.BO.ProjectBoardTaskBO;
import com.kakarote.work.entity.VO.ProjectBoardVO;
import com.kakarote.work.service.IProjectBoardTaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 项目管理 看板信息
 * </p>
 *
 * @author guole
 * @since 2022-09-22
 */
@RestController
@RequestMapping("/projectBoardTask")
@Api(tags = "项目管理：看板任务信息")
public class ProjectBoardTaskController implements Serializable {

    @Autowired
    IProjectBoardTaskService boardTaskService;


    @PostMapping("/queryBoardList")
    @ApiOperation("看板任务列表")
    public Result queryBoardList(@RequestBody ProjectBoardTaskBO boardTaskBO) {
        List<ProjectBoardVO> projectBoardVOS = boardTaskService.queryBoardTaskList(boardTaskBO);
        return Result.ok(projectBoardVOS);
    }

    @PostMapping("/setTaskStatus")
    @ApiOperation("任务设置事件状态")
    public Result setTaskStatus(@RequestParam("taskId") Long taskId, @RequestParam("statusId") Long statusId) {
        boardTaskService.setTaskStatus(taskId, statusId);
        return Result.ok();
    }
}
