package com.kakarote.work.controller;



import com.kakarote.common.result.Result;
import com.kakarote.work.entity.BO.ProjectTaskUserBO;
import com.kakarote.work.service.IProjectTaskUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 项目任务成员表 前端控制器
 * </p>
 *
 * @author zyh
 * @since 2022-10-27
 */
@RestController
@RequestMapping("/projectTaskUser")
@Api(tags = "项目任务成员管理")
public class ProjectUserTaskController {

    @Autowired
    private IProjectTaskUserService projectTaskUserService;

    @PostMapping("/relatedProjectTaskUser")
    @ApiOperation("添加项目成员")
    public Result relatedProjectUser(@RequestBody ProjectTaskUserBO projectTaskUserBO) {
        projectTaskUserService.relatedProjectUser(projectTaskUserBO);
        return Result.ok();
    }
}

