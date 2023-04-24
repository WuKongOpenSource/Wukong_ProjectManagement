package com.kakarote.work.controller;



import com.kakarote.common.result.Result;
import com.kakarote.work.entity.PO.ProjectSchemeRelation;
import com.kakarote.work.service.IProjectSchemeRelationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 项目配置方案和事件关系表 前端控制器
 * </p>
 *
 * @author bai
 * @since 2022-09-19
 */
@RestController
@RequestMapping("/projectSchemeRelation")
@Api(tags = "目配置方案和事件关系")
public class ProjectSchemeRelationController {

    @Autowired
    private IProjectSchemeRelationService projectSchemeRelationService;

    @PostMapping("/queryEventId")
    @ApiOperation("查询当前项目任务类型下的事件ID")
    public Result<ProjectSchemeRelation> queryEventId(@RequestParam Long schemeId, @RequestParam Integer taskType) {
        ProjectSchemeRelation projectSchemeRelation = projectSchemeRelationService.queryEventId(schemeId, taskType);
        return Result.ok(projectSchemeRelation);
    }

}

