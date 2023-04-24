package com.kakarote.work.controller;


import com.kakarote.common.result.Result;
import com.kakarote.work.entity.BO.RelevancyBelongIterationBO;
import com.kakarote.work.service.IProjectTaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/projectBelongIteration")
@Api(tags = "迭代")
public class BelongIterationController {

    @Autowired
    private IProjectTaskService projectTaskService;

    @RequestMapping(value = "/relevancyBelongIteration", method = RequestMethod.POST)
    @ApiOperation("关联迭代/移除迭代")
    public Result relevancyBelongIteration(@RequestBody RelevancyBelongIterationBO belongIterationBO) {
        projectTaskService.relevancyBelongIteration(belongIterationBO);
        return Result.ok();
    }
}
