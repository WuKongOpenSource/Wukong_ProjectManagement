package com.kakarote.work.controller;



import com.kakarote.common.result.Result;
import com.kakarote.work.entity.PO.ProjectTaskComment;
import com.kakarote.work.service.IProjectTaskCommentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 任务评论表 前端控制器
 * </p>
 *
 * @author wyq
 * @since 2020-05-18
 */
@RestController
@RequestMapping("/projectTaskComment")
@Api(tags = "任务评论new")
public class ProjectTaskCommentController {

    @Autowired
    private IProjectTaskCommentService projectTaskCommentService;

    @ApiOperation("查询评论列表")
    @PostMapping("/queryCommentList")
    public Result<List<ProjectTaskComment>> queryCommentList(@ApiParam("任务ID") @RequestParam("taskId") Long taskId) {

        List<ProjectTaskComment> taskComments = projectTaskCommentService.queryCommentList(taskId);
        return Result.ok(taskComments);
    }

    /**
     * @param comment 评论对象
     * @author hmb
     * 添加评论或者修改
     */
    @PostMapping("/setComment")
    @ApiOperation("添加评论或者修改")
    public Result setComment(@RequestBody ProjectTaskComment comment) {

        projectTaskCommentService.setComment(comment);
        return Result.ok(comment);
    }

    @PostMapping("/deleteComment")
    @ApiOperation("删除评论")
    public Result deleteComment(@RequestParam("commentId") Long commentId) {
        ProjectTaskComment comment = projectTaskCommentService.getById(commentId);
        if (comment != null) {

            projectTaskCommentService.removeById(commentId);
            projectTaskCommentService.lambdaUpdate().eq(ProjectTaskComment::getMainId, commentId).remove();
        }
        return Result.ok();
    }
}

