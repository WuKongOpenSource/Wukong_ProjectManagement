package com.kakarote.work.controller;



import com.kakarote.common.exception.BusinessException;
import com.kakarote.common.result.BasePage;
import com.kakarote.common.result.Result;

import com.kakarote.common.result.PageEntity;
import com.kakarote.work.constant.ProjectCodeEnum;
import com.kakarote.work.entity.BO.ProjectStatusQueryBO;
import com.kakarote.work.entity.PO.ProjectEventStatus;
import com.kakarote.work.entity.PO.ProjectStatus;
import com.kakarote.work.service.IProjectEventStatusService;
import com.kakarote.work.service.IProjectStatusService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 事件属性表 前端控制器
 * </p>
 *
 * @author bai
 * @since 2022-09-19
 */
@RestController
@RequestMapping("/projectStatus")
@Api(tags = "项目管理：状态信息")
public class ProjectStatusController {

    @Autowired
    IProjectStatusService projectStatusService;
    @Autowired
    IProjectEventStatusService projectEventStatusService;

    @PostMapping("/add")
    @ApiOperation("新建状态")

    public Result add(@RequestBody ProjectStatus projectStatus) {
        projectStatusService.add(projectStatus);
        return Result.ok();
    }

    @PostMapping("/update")
    @ApiOperation("更新状态")
    public Result update(@RequestBody ProjectStatus projectStatus) {
        projectStatusService.updateById(projectStatus);
        return Result.ok();
    }

    @PostMapping("/delete")
    @ApiOperation("删除状态")
    public Result delete(@RequestParam("projectStatusId") Long projectStatusId) {
        ProjectStatus projectStatus = this.projectStatusService.getById(projectStatusId);
        Long count = this.projectEventStatusService.lambdaQuery().eq(ProjectEventStatus::getProjectStatusId, projectStatusId).count();

        if (projectStatus.getSysType().equals(1)) {
            throw new BusinessException(ProjectCodeEnum.PROJECT__STATUS_SYS_ERROR);
        }
        if (count > 0) {
            throw new BusinessException(ProjectCodeEnum.PROJECT_EVENT_STATUS_BAND_ERROR);

        }
        projectStatusService.removeById(projectStatusId);
        return Result.ok();
    }

    @PostMapping("/queryProjectStatusList")
    @ApiOperation("状态列表")
    public Result<BasePage<ProjectStatus>> queryProjectStatusList(@RequestBody ProjectStatusQueryBO query) {
        BasePage<ProjectStatus> list = projectStatusService.queryProjectStatusList(query);
        return Result.ok(list);
    }

    @PostMapping("/updateSorting")
    @ApiOperation("修改状态顺序")
    public Result updateSorting(@RequestBody List<Integer> ids) {
        projectStatusService.updateSorting(ids);
        return Result.ok();
    }


}

