package com.kakarote.work.controller;


import com.kakarote.common.result.Result;
import com.kakarote.work.entity.BO.ProjectFieldModelBO;
import com.kakarote.work.entity.PO.ProjectField;
import com.kakarote.work.service.IProjectEventService;
import com.kakarote.work.service.IProjectFieldService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 自定义字段表 前端控制器
 * </p>
 *
 * @author bai
 * @since 2022-09-16
 */
@RestController
@RequestMapping("/projectField")
@Api(tags = "自定义字段new")
public class ProjectFieldController {


    @Autowired
    IProjectFieldService projectFieldService;

    @Autowired
    IProjectEventService projectEventService;

    @ApiOperation("保存自定义字段")
    @PostMapping("/saveField")
    public Result saveField(@RequestBody ProjectFieldModelBO fieldModelBo) {
        return Result.ok();
    }

    @ApiOperation("查询字自定义字段段列表")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public Result<List<List<ProjectField>>> list() {
        return Result.ok();
    }
}

