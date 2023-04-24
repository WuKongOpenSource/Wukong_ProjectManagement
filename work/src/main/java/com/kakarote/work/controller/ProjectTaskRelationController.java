package com.kakarote.work.controller;



import com.kakarote.common.result.Result;
import com.kakarote.work.entity.PO.ProjectTaskRelation;
import com.kakarote.work.service.IProjectTaskRelationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 任务关联业务表 前端控制器
 * </p>
 *
 * @author wyq
 * @since 2020-05-18
 */
@RestController
@RequestMapping("/projectTaskRelation")
@Api(tags = "new任务关联业务")
public class ProjectTaskRelationController {

    @Autowired
    private IProjectTaskRelationService projectTaskRelationService;

    @PostMapping("/saveProjectTaskRelation")
    @ApiOperation("保存任务业务关联")
    public Result saveProjectTaskRelation(@RequestBody ProjectTaskRelation projectTaskRelation){
//        projectTaskRelationService.saveProjectTaskRelation(projectTaskRelation);
        return Result.ok();
    }
}

