package com.kakarote.work.controller;


import com.kakarote.common.result.Result;
import com.kakarote.work.common.project.ProjectFieldSortAddBO;
import com.kakarote.work.common.project.ProjectFieldSortQueryBO;
import com.kakarote.work.entity.PO.ProjectFieldSort;
import com.kakarote.work.service.IProjectFieldSortService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 字段排序表 前端控制器
 * </p>
 *
 * @author bai
 * @since 2022-09-16
 */
@RestController
@RequestMapping("/projectFieldSort")
public class ProjectFieldSortController {

    @Autowired
    private IProjectFieldSortService projectFieldSortService;

    @ApiOperation("查询字自定义字段段列表")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public Result<List<ProjectFieldSort>> selectUserSort(@RequestBody ProjectFieldSortQueryBO projectFieldSortBO) {
        return Result.ok(projectFieldSortService.selectUserSort(projectFieldSortBO));
    }

    @ApiOperation("设置个人字段排序")
    @RequestMapping(value = "/addOrUpdate", method = RequestMethod.POST)
    public Result<Boolean> addOrUpdateUserSort(@RequestBody ProjectFieldSortAddBO projectFieldSortAddBO) {
        return Result.ok(projectFieldSortService.addOrUpdateUserSort(projectFieldSortAddBO));
    }

}

