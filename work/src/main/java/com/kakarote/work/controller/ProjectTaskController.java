package com.kakarote.work.controller;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import com.alibaba.fastjson.JSONObject;
import com.kakarote.common.result.BasePage;
import com.kakarote.common.result.Result;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.ids.provider.utils.UserCacheUtil;
import com.kakarote.work.common.project.*;
import com.kakarote.work.entity.BO.ProjectTaskCountBO;
import com.kakarote.work.entity.BO.ProjectTaskDescriptionBO;
import com.kakarote.work.entity.BO.ProjectTaskExportBO;
import com.kakarote.work.entity.BO.ProjectTaskFileLog;
import com.kakarote.work.entity.BO.ProjectTaskNameBO;
import com.kakarote.work.entity.BO.ProjectTaskQueryBO;
import com.kakarote.work.entity.BO.ProjectTaskStatusBO;
import com.kakarote.work.entity.BO.ProjectTaskWikiBO;
import com.kakarote.work.entity.BO.RelevancyChildTaskBO;
import com.kakarote.work.entity.BO.RelevancyRelatedDemandIdBO;
import com.kakarote.work.entity.BO.StartBelongIterationBO;
import com.kakarote.work.entity.PO.ProjectTask;
import com.kakarote.work.entity.PO.ProjectTaskLog;
import com.kakarote.work.entity.VO.*;
import com.kakarote.work.service.IProjectService;
import com.kakarote.work.service.IProjectTaskLogService;
import com.kakarote.work.service.IProjectTaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * <p>
 * 任务表 前端控制器
 * </p>
 *
 * @author bai
 * @since 2022-09-08
 */
@RestController
@RequestMapping("/projectTask")
@Api(tags = "任务new")
public class ProjectTaskController {

    @Autowired
    private IProjectTaskService projectTaskService;
    @Autowired
    private IProjectService projectService;
    @Autowired
    private IProjectTaskLogService projectTaskLogService;



    @PostMapping("/saveProjectTask")
    @ApiOperation("新建项目任务")
    public Result saveWorkTask(@RequestBody ProjectTask projectTask) {
        projectTaskService.saveProjectTask(projectTask);
        return Result.ok();
    }

    @PostMapping("/queryProjectTaskById/{projectTaskId}")
    @ApiOperation("根据ID查询任务")
    public Result<ProjectTask> queryProjectTaskById(@PathVariable("projectTaskId") Long projectTaskId) {
        projectTaskService.queryProjectTaskById(projectTaskId);
        return Result.ok();
    }

    @PostMapping("/updateProjectTask")
    @ApiOperation("编辑项目任务")
    public Result<Boolean> updateProjectTask(@RequestBody ProjectTask projectTask) {
        return Result.ok(projectTaskService.updateProjectTask(projectTask));
    }

    @PostMapping("/setProjectChildTask")
    @ApiOperation("关联子任务")
    public Result setProjectChildTask(@RequestBody ProjectTask projectTask) {
        ProjectTask pidProjectTask = projectTaskService.getById(projectTask.getPid());
        projectTaskService.saveProjectTask(projectTask);
        ProjectTaskLog projectTaskLog = new ProjectTaskLog();
        projectTaskLog.setTaskId(projectTask.getPid());
        projectTaskLog.setType(1);
        String contentLog ="创建了子工作项"+ projectTask.getName();
         projectTaskLog.setContent(contentLog);
        projectTaskLogService.saveTaskLog(projectTaskLog);
        return Result.ok();
    }

    @PostMapping("/setEstimatedManHours")
    @ApiOperation("修改预估工时")
    public Result<Boolean> setEstimatedManHours(@RequestBody ProjectTask projectTask) {
        ProjectTask oldProjectTask = projectTaskService.getById(projectTask.getTaskId());
        ProjectTaskLog projectTaskLog = new ProjectTaskLog();
        projectTaskLog.setTaskId(projectTask.getTaskId());
        projectTaskLog.setType(2);
        String contentLog = ProjectUtil.getLogContent("工时", oldProjectTask.getEstimatedManHours(), projectTask.getEstimatedManHours());
        projectTaskLog.setContent(contentLog);
        projectTaskLogService.saveTaskLog(projectTaskLog);
        return Result.ok(projectTaskService.updateProjectTask(projectTask));
    }

    @PostMapping("/setProgress")
    @ApiOperation("更新进度")
    public Result<Boolean> setProgress(@RequestBody ProjectTask projectTask) {

        return Result.ok(projectTaskService.setProgress(projectTask));
    }

    @PostMapping("/setPriority")
    @ApiOperation("修改优先级")
    public Result<Boolean> setPriority(@RequestBody ProjectTask projectTask) {
        return Result.ok(projectTaskService.setPriority(projectTask));
    }

    @PostMapping("/setProjectTaskMainUser")
    @ApiOperation("编辑负责人")
    public Result<Boolean> setWorkTaskMainUser(@RequestBody ProjectTask projectTask) {
        this.projectTaskService.setProjectTaskMainUser(projectTask);
        return Result.ok(true);
    }
//    @PostMapping("/batchSetTask")
//    @ApiOperation("批量设置任务")
//    public Result<Boolean> setWorkTaskMainUser(@RequestBody ProjectTask projectTask) {
//        this.setWorkTaskMainUser(projectTask);
//        return Result.ok(true);
//    }

    @PostMapping("/queryProjectTaskList")
    @ApiOperation("查询任务(type  2需求 3任务 4缺陷 5工时)")
    public Result<BasePage<ProjectTask>> queryProjectTaskList(@RequestBody ProjectTaskQueryBO projectTaskQueryBO) {
        return Result.ok(projectTaskService.queryProjectTaskList(projectTaskQueryBO));
    }

    @PostMapping("/getProjectByTime")
    @ApiOperation("项目（趋势图）")
    public Result<ProjectTaskCountVO> getProjectByTime(@RequestBody ProjectTaskCountBO projectTaskCountBO) {
        return Result.ok(projectTaskService.getProjectByTime(projectTaskCountBO));
    }

    @PostMapping("/getTaskByTime")
    @ApiOperation("任务（趋势图）")
    public Result<ProjectTaskCountVO> getTaskByTime(@RequestBody ProjectTaskCountBO projectTaskCountBO) {
        return Result.ok(projectTaskService.getTaskByTime(projectTaskCountBO));
    }

    @PostMapping("/getTaskBurnout")
    @ApiOperation("迭代（燃尽图）")
    public Result<List<ProjectTaskBurnoutVO>> getTaskBurnout(@RequestBody ProjectTaskCountBO projectTaskCountBO) {
        return Result.ok(projectTaskService.getTaskBurnout(projectTaskCountBO));
    }

    @PostMapping("/getProjectTaskEvent")
    @ApiOperation("当前迭代（事件分布图）")
    public Result<ProjectTaskEventCountVO> getProjectTaskEvent(@RequestBody ProjectTaskCountBO projectTaskCountBO) {
        return Result.ok(projectTaskService.getProjectTaskEvent(projectTaskCountBO));
    }

    @RequestMapping(value = "/getProjectTaskDetails/{taskId}", method = RequestMethod.POST)
    @ApiOperation("获取任务或者事项项详情")
    public Result<ProjectTask> getProjectTaskDetails(@PathVariable("taskId") Long taskId) {
        return Result.ok(projectTaskService.getProjectTaskDetails(taskId));
    }

    @RequestMapping(value = "/getAllMatters", method = RequestMethod.POST)
    @ApiOperation("分页查询全部事项")
    public Result<BasePage<ProjectTask>> getAllMatters(@RequestBody ProjectTaskQueryBO projectTaskQueryBO) {
        return Result.ok(projectTaskService.getAllMatters(projectTaskQueryBO));
    }


    @RequestMapping(value = "/queryProjectPlanTaskList", method = RequestMethod.POST)
    @ApiOperation("分页查询待规划")
    public Result<BasePage<ProjectTask>> queryProjectPlanTaskList(@RequestBody ProjectTaskQueryBO projectTaskQueryBO) {
        return Result.ok(projectTaskService.queryProjectPlanTaskList(projectTaskQueryBO));
    }

    @RequestMapping(value = "/queryProjectIterationTaskList", method = RequestMethod.POST)
    @ApiOperation("分页查询迭代")
    public Result<BasePage<ProjectTask>> queryProjectIterationTaskList(@RequestBody ProjectTaskQueryBO projectTaskQueryBO) {
        return Result.ok(projectTaskService.queryProjectIterationTaskList(projectTaskQueryBO));
    }

    @RequestMapping(value = "/queryProjectTaskChildList", method = RequestMethod.POST)
    @ApiOperation("查询迭代,需求,任务下的事项")
    public Result<BasePage<ProjectTask>> queryProjectTaskChildList(@RequestBody ProjectTaskQueryBO projectTaskQueryBO) {
        return Result.ok(projectTaskService.queryProjectTaskChildList(projectTaskQueryBO));
    }

    @PostMapping("/queryProjectTaskChildBoardList")
    @ApiOperation("迭代,需求,任务下的看板任务列表")
    public Result queryProjectTaskChildBoardList(@RequestBody ProjectTaskQueryBO projectTaskQueryBO) {
        List<ProjectBoardVO> projectBoardVOS = projectTaskService.queryProjectTaskChildBoardList(projectTaskQueryBO);
        return Result.ok(projectBoardVOS);
    }

    @RequestMapping(value = "/relevancyChildTask", method = RequestMethod.POST)
    @ApiOperation("关联子任务(子任务，需求)")
    public Result relevancyChildTask(@RequestBody RelevancyChildTaskBO relevancyChildTaskBO) {
        projectTaskService.relevancyChildTask(relevancyChildTaskBO);
        return Result.ok();
    }

    @RequestMapping(value = "/deleteTask/{taskId}", method = RequestMethod.POST)
    @ApiOperation("删除任务")
    public Result deleteTask(@PathVariable("taskId") Long taskId) {
        projectTaskService.deleteTask(taskId);
        return Result.ok();
    }

    @PostMapping("/updateTaskWiki")
    @ApiOperation("关联Wiki页面")
    public Result<Boolean> updateTaskWiki(@RequestBody ProjectTaskWikiBO projectTask) {
        projectTaskService.lambdaUpdate().eq(ProjectTask::getTaskId, projectTask.getTaskId()).set(ProjectTask::getWiki, projectTask.getWiki()).update();
        return Result.ok();
    }

    @PostMapping("/setProjectTaskDescription")
    @ApiOperation("设置项目任务描述")
    public Result setWorkTaskDescription(@RequestBody ProjectTaskDescriptionBO projectTaskDescriptionBO) {
        projectTaskService.lambdaUpdate().eq(ProjectTask::getTaskId, projectTaskDescriptionBO.getTaskId()).set(ProjectTask::getDescription, projectTaskDescriptionBO.getDescription()).update();
        ProjectTaskLog projectTaskLog = new ProjectTaskLog();
        projectTaskLog.setTaskId(projectTaskDescriptionBO.getTaskId());
        projectTaskLog.setType(1);
        String content = "";
        String clean = EscapeUtil.clean(Optional.ofNullable(projectTaskDescriptionBO.getDescription()).orElse(StrUtil.EMPTY).toString());
        projectTaskLog.setContent(content.concat(" 设置任务描述 ").concat(clean));
        projectTaskLogService.saveTaskLog(projectTaskLog);
        return Result.ok();
    }

    @PostMapping("/startBelongIteration")
    @ApiOperation("开始迭代")
    public Result startBelongIteration(@RequestBody StartBelongIterationBO startBelongIterationBO) {
        projectTaskService.lambdaUpdate().eq(ProjectTask::getTaskId, startBelongIterationBO.getTaskId()).set(ProjectTask::getStatus, 2).set(ProjectTask::getStartTime, startBelongIterationBO.getStartTime()).
                set(ProjectTask::getStopTime, startBelongIterationBO.getStopTime()).update();
        return Result.ok();
    }

    @PostMapping("/stopBelongIteration")
    @ApiOperation("完成迭代")
    public Result stopBelongIteration(@RequestBody StartBelongIterationBO startBelongIterationBO) {
        projectTaskService.lambdaUpdate().eq(ProjectTask::getTaskId, startBelongIterationBO.getTaskId()).set(ProjectTask::getStatus, 3).
                set(ProjectTask::getFinishTime, startBelongIterationBO.getStopTime()).update();
        return Result.ok();
    }


    @PostMapping("/belongIterationDetails/{taskId}")
    @ApiOperation("迭代详情")
    public Result<ProjectBelongIterationVO> belongIterationDesc(@PathVariable("taskId") Long taskId) {
        ProjectTask task = projectTaskService.queryProjectTaskById(taskId);
        ProjectBelongIterationVO vo = new ProjectBelongIterationVO();
        vo.setStatus(task.getStatus());
        if (ObjectUtil.isNotNull(task.getStopTime())) {
            vo.setSurplusDay(DateUtil.betweenDay(task.getStopTime(), new Date(), true));
        }
        //设置项目成员列表
        task.setProjectOwnerRoleList(projectService.queryOwnerRoleList(task.getProjectId()));
        vo.setTaskId(taskId);
        vo.setName(task.getName());
        vo.setTarget(task.getTarget());
        vo.setStartTime(task.getStartTime());
        vo.setStopTime(task.getStopTime());
        vo.setDescription(task.getDescription());
        vo.setMainUserId(task.getMainUserId());
        vo.setMainUserName(UserCacheUtil.getUserName(task.getMainUserId()));
        return Result.ok(vo);
    }

    @PostMapping("/belongIterationDel/{taskId}")
    @ApiOperation("删除迭代")
    public Result belongIterationDel(@PathVariable("taskId") Long taskId) {
        ProjectTask projectTask = projectTaskService.lambdaQuery().eq(ProjectTask::getTaskId, taskId).one();
        projectTaskService.lambdaUpdate().eq(ProjectTask::getTaskId, taskId).remove();
        projectTaskService.lambdaUpdate().eq(ProjectTask::getBelongIterationId, taskId).
                set(ProjectTask::getBelongIterationId, null).update();
        ProjectTaskLog projectTaskLog = new ProjectTaskLog();
        projectTaskLog.setTaskId(projectTask.getTaskId());
        projectTaskLog.setType(1);
        String userName = "";
        projectTaskLog.setContent(userName.concat(" 删除迭代 ").concat(projectTask.getName()));
        projectTaskLogService.saveTaskLog(projectTaskLog);
        return Result.ok();
    }

    @PostMapping("/setProjectChildTaskStatus")
    @ApiOperation("设置项目子任务状态")
    public Result setProjectChildTaskStatus(@RequestBody ProjectTaskStatusBO projectTaskStatusBO) {
        projectTaskService.lambdaUpdate().eq(ProjectTask::getTaskId, projectTaskStatusBO.getTaskId()).set(ProjectTask::getStatus, projectTaskStatusBO.getStatus()).update();
        return Result.ok();
    }

    @PostMapping("/setProjectWrongType")
    @ApiOperation("更新缺陷")
    public Result setProjectWrongType(@RequestBody ProjectTask projectTask) {
        projectTaskService.lambdaUpdate().eq(ProjectTask::getTaskId, projectTask.getTaskId()).set(ProjectTask::getWrongType, projectTask.getWrongType()).update();
        ProjectTaskLog projectTaskLog = new ProjectTaskLog();
        projectTaskLog.setTaskId(projectTask.getTaskId());
        projectTaskLog.setType(1);
        String userName = UserUtil.getUser().getNickname();
        String content = "";
        switch (projectTask.getWrongType()) {
            case 1:
                content = "功能缺陷";
                break;
            case 2:
                content = "UI界面问题";
                break;
            case 3:
                content = "易用性问题";
                break;
            case 4:
                content = "安全问题";
                break;
            case 5:
                content = "性能问题";
                break;
            case 6:
                content = "代码错误";
                break;
        }
        projectTaskLog.setContent(userName.concat(" 更新缺陷 ").concat(content));
        projectTaskLogService.saveTaskLog(projectTaskLog);
        return Result.ok();
    }

    @RequestMapping(value = "/relevancyRelatedDemand", method = RequestMethod.POST)
    @ApiOperation("关联需求/移除需求")
    public Result relevancyRelatedDemand(@RequestBody RelevancyRelatedDemandIdBO relatedDemandIdBO) {
        ProjectTask relatedDemand = null;
        for (Long id : relatedDemandIdBO.getTaskIds()) {
            ProjectTaskLog projectTaskLog = new ProjectTaskLog();
            projectTaskLog.setTaskId(id);
            projectTaskLog.setType(1);
            String userName = UserUtil.getUser().getNickname();
            ProjectTask projectTask = projectTaskService.lambdaQuery().eq(ProjectTask::getTaskId, id).one();
            if (ObjectUtil.isNotEmpty(relatedDemandIdBO.getRelatedDemandId())) {
                relatedDemand = projectTaskService.lambdaQuery().eq(ProjectTask::getTaskId, relatedDemandIdBO.getRelatedDemandId()).one();
            } else {
                relatedDemand = projectTaskService.lambdaQuery().eq(ProjectTask::getTaskId, projectTask.getRelatedDemandId()).one();
            }
            if (ObjectUtil.isNotEmpty(relatedDemandIdBO.getRelatedDemandId())) {
                projectTaskLog.setContent(userName.concat(" 更新关联需求为 ").concat(null == relatedDemand ? "" : relatedDemand.getName()));
            } else {
                projectTaskLog.setContent(userName.concat(" 移除关联需求为 ").concat(null == relatedDemand ? "" : relatedDemand.getName()));
            }
            projectTaskLogService.saveTaskLog(projectTaskLog);
        }
        projectTaskService.relevancyRelatedDemand(relatedDemandIdBO);
        return Result.ok();
    }

    @RequestMapping(value = "/upLoadFileRelationLog", method = RequestMethod.POST)
    @ApiOperation("上传和删除文件日志")
    public Result upLoadFileRelationLog(@RequestBody ProjectTaskFileLog projectTaskFileLog) {
        ProjectTaskLog projectTaskLog = new ProjectTaskLog();
        projectTaskLog.setTaskId(projectTaskFileLog.getTaskIds());
        projectTaskLog.setType(1);
        String userName = UserUtil.getUser().getNickname();
        if (1 == projectTaskFileLog.getOperationType()) {
            projectTaskLog.setContent(userName.concat(" 上传文件 ").concat(projectTaskFileLog.getFileName()));
        } else if (2 == projectTaskFileLog.getOperationType()) {
            projectTaskLog.setContent(userName.concat(" 下载文件 ").concat(projectTaskFileLog.getFileName()));
        } else if (3 == projectTaskFileLog.getOperationType()) {
            projectTaskLog.setContent(userName.concat(" 删除文件 ").concat(projectTaskFileLog.getFileName()));
        }
        projectTaskLogService.saveTaskLog(projectTaskLog);
        return Result.ok();
    }

    @PostMapping("/updateProjectTaskTime")
    @ApiOperation("编辑开始时间结束时间")
    public Result updateProjectTaskTime(@RequestBody ProjectTask projectTask) {
        this.projectTaskService.updateProjectTaskTime(projectTask);
        return Result.ok();
    }

    @PostMapping("/setProjectTaskName")
    @ApiOperation("设置项目任务名称")
    public Result setProjectTaskName(@RequestBody ProjectTaskNameBO projectTaskNameBO) {
        projectTaskService.projectTaskSetName(projectTaskNameBO);
        return Result.ok();
    }

    @PostMapping("/downloadExcel")
    @ApiOperation("导入模板")
    public void downloadExcel(HttpServletResponse response, @RequestParam("taskType") Integer taskType) {
        projectTaskService.downloadExcel(response, taskType);
    }

    /**
     * excel导入任务
     */
    @PostMapping("/excelImport")
    @ApiOperation("导入任务")
    public Result<JSONObject> excelImport(@RequestParam("file") MultipartFile file, @RequestParam("projectId") Long projectId, Integer taskType) throws IOException {
        JSONObject dict = projectTaskService.excelImport(file, projectId, taskType);
        return Result.ok(dict);
    }

    @PostMapping("/downloadErrorExcel")
    @ApiOperation("下载错误数据")
    public void downloadErrorExcel(@RequestParam("token") String token, HttpServletResponse response) {
        File file = new File(BaseUtil.UPLOAD_PATH + "excel/" + token);
        try {
            ServletUtil.write(response, new FileInputStream(file), "application/vnd.ms-excel;charset=utf-8", "task_import.xls");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/projectTaskExport")
    @ApiOperation("导出任务数据")
    public void workTaskExport(@RequestBody ProjectTaskExportBO taskExportBO, HttpServletResponse response) {
        projectTaskService.projectTaskExport(taskExportBO, response);
    }

    @PostMapping("/projectTaskExportColumn")
    @ApiOperation("获取导出字段列表")
    public Result<List<JSONObject>> projectTaskExportColumn(@RequestParam("taskType") Integer taskType) {

        return Result.ok(projectTaskService.projectTaskExportColumn(taskType));
    }

    @PostMapping("/sort/backlog")
    @ApiOperation("对待规划事项进行排序")
    public Result<Boolean> sortBackLog(@RequestBody ProjectTaskUserSortBO pojectTaskUserSortBO) {
        projectTaskService.sortBackLog(pojectTaskUserSortBO);
        return Result.ok();
    }

    @PostMapping("/queryUserTaskList")
    @ApiOperation("查询当前用户的任务(type  2需求 3任务 4缺陷 )")
    public Result<BasePage<ProjectTask>> queryUserTaskList(@RequestBody ProjectUserTaskQueryBO userTaskQueryBO) {
        return Result.ok(projectTaskService.queryUserTaskList(userTaskQueryBO));
    }

    @PostMapping("/queryUserTaskCount")
    @ApiOperation("查询当前用户的任务数量(type  2需求 3任务 4缺陷 )")
    public Result<ProjectUserTaskCountVO> queryUserTaskCount(@RequestBody ProjectUserTaskQueryBO userTaskQueryBO) {
        return Result.ok(projectTaskService.queryUserTaskCount(userTaskQueryBO));
    }

    @PostMapping("/batchSetProjectTask")
    @ApiOperation("任务批量修改")
    public Result<Boolean> batchSetProjectTask(BatchSetTaskBO batchSetTaskBO) {
        projectTaskService.batchSetProjectTask(batchSetTaskBO);
        return Result.ok();

    }

}

