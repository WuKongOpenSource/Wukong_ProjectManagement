package com.kakarote.work.controller;



import com.kakarote.common.result.BasePage;
import com.kakarote.common.result.Result;

import com.kakarote.work.entity.BO.ProjectTaskFollowsQueryBO;
import com.kakarote.work.entity.PO.ProjectTaskFollows;
import com.kakarote.work.entity.VO.ProjectTaskFollowsVO;
import com.kakarote.work.service.IProjectTaskFollowsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/projectTaskFollow")
@Api(tags = "任务评论记录活动new")
public class ProjectTaskFollowController {

    @Autowired
    IProjectTaskFollowsService projectFollowsService;

    @PostMapping("/getProjectFollowsPageList")
    @ApiOperation("查询任务评论记录活动列表")
    public Result<BasePage<ProjectTaskFollowsVO>> getProjectFollowsPageList(@RequestBody ProjectTaskFollowsQueryBO projectTaskFollowsQueryBO) {
        BasePage<ProjectTaskFollowsVO> pageList = projectFollowsService.getProjectFollowsPageList(projectTaskFollowsQueryBO);
        return Result.ok(pageList);
    }

    @PostMapping("/addProjectFollows")
    @ApiOperation("新建项目评论记录活动")
    public Result addProjectFollows(@RequestBody ProjectTaskFollows projectTaskFollows) {
        return Result.ok(projectFollowsService.save(projectTaskFollows));
    }

    @PostMapping("/updateProjectFollows")
    @ApiOperation("编辑任务评论记录活动")
    public Result updateProjectFollows(@RequestBody ProjectTaskFollows projectTaskFollows) {
        return Result.ok(projectFollowsService.updateById(projectTaskFollows));
    }

    @PostMapping("/delProjectFollows")
    @ApiOperation("删除评论记录活动")
    public Result delProjectFollows(@RequestParam("taskFollowId") Long taskFollowId) {
          projectFollowsService.removeById(taskFollowId);
          return Result.ok();
    }

}

