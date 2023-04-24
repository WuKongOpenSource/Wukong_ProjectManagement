package com.kakarote.work.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.*;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.BigExcelWriter;
import cn.hutool.poi.excel.ExcelUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.kakarote.common.entity.UserInfo;
import com.kakarote.common.exception.BusinessException;
import com.kakarote.common.result.*;

import com.kakarote.common.servlet.ApplicationContextHolder;
import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.common.utils.RecursionUtil;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.ids.provider.utils.UserCacheUtil;
import com.kakarote.work.common.project.*;
import com.kakarote.work.common.admin.AdminMessageEnum;
import com.kakarote.work.common.project.ProjectOwnerRoleBO;
import com.kakarote.work.common.project.SystemCodeEnum;
import com.kakarote.work.constant.*;
import com.kakarote.work.entity.BO.*;
import com.kakarote.work.entity.BO.ProjectTaskCountBO;
import com.kakarote.work.entity.BO.ProjectTaskExportBO;
import com.kakarote.work.entity.BO.ProjectTaskNameBO;
import com.kakarote.work.entity.BO.ProjectTaskQueryBO;
import com.kakarote.work.entity.BO.ProjectTaskUserBO;
import com.kakarote.work.entity.BO.RelevancyBelongIterationBO;
import com.kakarote.work.entity.BO.RelevancyChildTaskBO;
import com.kakarote.work.entity.BO.RelevancyRelatedDemandIdBO;
import com.kakarote.work.entity.PO.*;
import com.kakarote.work.entity.VO.*;
import com.kakarote.work.mapper.ProjectTaskMapper;
import com.kakarote.work.service.*;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 任务表 服务实现类
 * </p>
 *
 * @author bai
 * @since 2022-09-08
 */
@Service
public class ProjectTaskServiceImpl extends BaseServiceImpl<ProjectTaskMapper, ProjectTask> implements IProjectTaskService {

    @Autowired
    private IProjectService projectService;
    @Autowired
    private IProjectFileService projectFileService;
    @Autowired
    private IProjectTaskLogService projectTaskLogService;
    @Autowired
    private IProjectSchemeRelationService projectSchemeRelationService;
    @Autowired
    private IProjectTaskRelationService projectTaskRelationService;
    @Autowired
    private IProjectEventStatusService projectEventStatusService;
    @Autowired
    private IProjectEventService projectEventService;
    @Autowired
    private ProjectAuthUtil projectAuthUtil;
    @Autowired
    private IProjectTaskTimeService projectTaskTimeService;
    @Autowired
    private IProjectLabelService projectLabelService;

    @Autowired
    private IProjectTaskUserService projectTaskUserService;

    @Autowired
    private IProjectSchemeRelationBoardService projectSchemeRelationBoardService;

    @Autowired
    private IProjectBoardStatusService projectBoardStatusService;

    @Autowired
    private IProjectTaskUserSortService projectTaskUserSortService;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveProjectTask(ProjectTask projectTask) {
        //添加任务基本信息
        this.saveProjectTaskBase(projectTask);
        //添加任务成员信息
        this.saveProjectTaskUser(projectTask);
        //添加任务日志信息
        this.saveProjectTaskLog(projectTask);
        //添加任务工时记录
        this.saveProjectTaskTime(projectTask);
        //添加任务子任务
        this.saveProjectTaskChild(projectTask);
//        //添加任务关联业务
//        this.saveProjectTaskRelation(projectTask);
        //发送任务通知
        this.saveProjectTaskSend(projectTask);

    }

    @Override
    public ProjectTask queryProjectTaskById(Long projectTaskId) {
        return lambdaQuery().eq(ProjectTask::getTaskId, projectTaskId).one();
    }

    @Override
    public Boolean updateProjectTask(ProjectTask projectTask) {
        return updateById(projectTask);
    }

    @Override
    public BasePage<ProjectTask> queryProjectTaskList(ProjectTaskQueryBO projectTaskQueryBO) {
        LambdaQueryWrapper<ProjectTask> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        setLamdaQuery(lambdaQueryWrapper, projectTaskQueryBO);
        if (ObjectUtil.isEmpty(projectTaskQueryBO.getType()) || projectTaskQueryBO.getType().equals(0)) {
            lambdaQueryWrapper.ne(ProjectTask::getType, 1);
        } else {
            lambdaQueryWrapper.eq(ProjectTask::getType, projectTaskQueryBO.getType());
        }
        lambdaQueryWrapper.orderByDesc(ProjectTask::getPriority);
        lambdaQueryWrapper.orderByAsc(ProjectTask::getNum);
        BasePage<ProjectTask> projectTaskPage = this.page(projectTaskQueryBO.parse(), lambdaQueryWrapper);
        if (projectTaskPage.getList().size() > 0) {
            Long projectId = projectTaskPage.getList().get(0).getProjectId();
            List<ProjectTask> projectTasks = this.lambdaQuery().eq(ProjectTask::getProjectId, projectId).list();
            setField(projectTaskPage.getList(), projectTasks, projectTaskQueryBO);
        }
        return projectTaskPage;
    }

    @Override
    public ProjectTaskCountVO getProjectByTime(ProjectTaskCountBO projectTaskQueryBO) {
        ProjectTaskCountVO projectTaskCountVO = new ProjectTaskCountVO();
        Project project = projectService.lambdaQuery().eq(Project::getProjectId, projectTaskQueryBO.getProjectId()).one();
        if (ObjectUtil.isNotEmpty(project)) {
            projectTaskQueryBO.setStartTime(DateUtil.offsetWeek(new Date(), -2));
            projectTaskQueryBO.setEndTime(new Date());
        }
        List<ProjectTaskNumVO> projectTaskNumVOs = getBaseMapper().getProjectByTime(projectTaskQueryBO.getProjectId(), DateUtil.beginOfDay(projectTaskQueryBO.getStartTime()), DateUtil.beginOfDay(projectTaskQueryBO.getEndTime()));
        if (CollUtil.isNotEmpty(projectTaskNumVOs)) {
            Integer totalNumber = projectTaskNumVOs.stream().mapToInt(ProjectTaskNumVO::getNum).sum();
            projectTaskCountVO.setTotalNumber(totalNumber);
            projectTaskCountVO.setProjectTaskNumVOEnd(projectTaskNumVOs);
        }
        return projectTaskCountVO;
    }

    @Override
    public ProjectTaskCountVO getTaskByTime(ProjectTaskCountBO projectTaskQueryBO) {
        ProjectTaskCountVO projectTaskCountVO = new ProjectTaskCountVO();
        List<Long> resultTaskId = new ArrayList<>();
        List<Long> pid = new ArrayList<>();
        ProjectTask task = lambdaQuery().in(ProjectTask::getTaskId, projectTaskQueryBO.getTaskId()).one();
        if (ObjectUtil.isNotEmpty(task)) {
            pid.add(task.getTaskId());
            queryChildTaskId(resultTaskId, pid);
        }
        if (ObjectUtil.isNotEmpty(task)) {
            projectTaskQueryBO.setStartTime(DateUtil.offsetWeek(new Date(), -2));
            projectTaskQueryBO.setEndTime(new Date());
        }
        if (CollectionUtil.isNotEmpty(resultTaskId)) {
            List<ProjectTaskNumVO> projectTaskNumVOsEnd = getBaseMapper().getTaskByTime(resultTaskId, DateUtil.beginOfDay(projectTaskQueryBO.getStartTime()), DateUtil.beginOfDay(projectTaskQueryBO.getEndTime()));
            List<ProjectTaskNumVO> projectTaskNumVOsNoStart = getBaseMapper().getTaskByTimeNoStart(resultTaskId, DateUtil.beginOfDay(projectTaskQueryBO.getStartTime()), DateUtil.beginOfDay(projectTaskQueryBO.getEndTime()));
            List<ProjectTaskNumVO> projectTaskNumVOsRun = getBaseMapper().getTaskByTimeRun(resultTaskId, DateUtil.beginOfDay(projectTaskQueryBO.getStartTime()), DateUtil.beginOfDay(projectTaskQueryBO.getEndTime()));
            Integer totalNumber = 0;
            if (CollUtil.isNotEmpty(projectTaskNumVOsEnd)) {
                totalNumber += projectTaskNumVOsEnd.stream().mapToInt(ProjectTaskNumVO::getNum).sum();
            }
            if (CollUtil.isNotEmpty(projectTaskNumVOsNoStart)) {
                totalNumber += projectTaskNumVOsNoStart.stream().mapToInt(ProjectTaskNumVO::getNum).sum();
            }
            if (CollUtil.isNotEmpty(projectTaskNumVOsRun)) {
                totalNumber += projectTaskNumVOsRun.stream().mapToInt(ProjectTaskNumVO::getNum).sum();
            }
            projectTaskCountVO.setTotalNumber(totalNumber);
            projectTaskCountVO.setProjectTaskNumVOEnd(projectTaskNumVOsEnd);
            projectTaskCountVO.setProjectTaskNumVOsNoStart(projectTaskNumVOsNoStart);
            projectTaskCountVO.setProjectTaskNumVOsRun(projectTaskNumVOsRun);
            return projectTaskCountVO;
        }
        return projectTaskCountVO;
    }

    @Override
    public List<ProjectTaskBurnoutVO> getTaskBurnout(ProjectTaskCountBO projectTaskQueryBO) {

        ProjectTask projectTask = lambdaQuery().eq(ProjectTask::getTaskId, projectTaskQueryBO.getTaskId()).one();
        //查询到当前迭代下的需求任务缺陷id
        List<ProjectTask> projectTaskList = new ArrayList<>();
        projectTaskList = lambdaQuery().in(ProjectTask::getType, Arrays.asList(2, 3, 4)).eq(ProjectTask::getBelongIterationId, projectTask.getTaskId()).list().stream().distinct().collect(Collectors.toList());
        List<ProjectTask> relatedDemandIdList = lambdaQuery().in(ProjectTask::getType, Arrays.asList(2, 3, 4)).eq(ProjectTask::getRelatedDemandId, projectTask.getTaskId()).list().stream().distinct().collect(Collectors.toList());
        if (CollectionUtil.isEmpty(projectTaskList) && CollectionUtil.isEmpty(relatedDemandIdList)) {
            return null;
        }
        projectTaskList.addAll(relatedDemandIdList);
        List<Long> resultTaskId = projectTaskList.stream().map(l -> l.getTaskId()).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(resultTaskId)) {
            resultTaskId = new ArrayList<>();
        }
        resultTaskId.add(projectTask.getTaskId());
        if (ObjectUtil.isNotEmpty(projectTask)) {
            projectTaskQueryBO.setStartTime(DateUtil.offsetDay(projectTask.getStartTime(), -1));
            projectTaskQueryBO.setEndTime(projectTask.getStopTime());
        }
        List<ProjectTaskBurnoutVO> projectTaskBurnoutVOs = null;
        if (ObjectUtil.isNotEmpty(projectTaskQueryBO.getStartTime()) && ObjectUtil.isNotEmpty(projectTaskQueryBO.getEndTime())) {
            Date dateTime = DateUtil.beginOfDay(projectTaskQueryBO.getStartTime());
            Date endTime = DateUtil.beginOfDay(projectTaskQueryBO.getEndTime());
            projectTaskBurnoutVOs = getBaseMapper().getTaskBurnout(resultTaskId, dateTime, endTime);
        }
        //计算预计总工时
        AtomicReference<Integer> totalHour = new AtomicReference<>(0);
        projectTaskList.stream().forEach(p -> {
            if (null != p.getEstimatedManHours()) {
                totalHour.updateAndGet(v -> v + p.getEstimatedManHours());
            }
        });
        Integer surplus = 0;
        //计算工时
        if (CollectionUtil.isNotEmpty(projectTaskBurnoutVOs)) {
            Date dateIndex = null;
            for (ProjectTaskBurnoutVO projectTaskBurnoutVO : projectTaskBurnoutVOs) {
                if (null != projectTaskBurnoutVO.getActualHour()) {
                    dateIndex = projectTaskBurnoutVO.getUpdateTime();
                    break;
                }
            }
            //计算剩余工时
            for (ProjectTaskBurnoutVO projectTaskBurnoutVO : projectTaskBurnoutVOs) {
                projectTaskBurnoutVO.setForecastTime(totalHour.get());
                if (ObjectUtil.isNotEmpty(projectTaskBurnoutVO.getActualHour())) {
                    surplus = totalHour.get() - projectTaskBurnoutVO.getActualHour();
                    projectTaskBurnoutVO.setSurplus(surplus);
                }
            }
            if (null != dateIndex) {
                for (int i = 1; i < projectTaskBurnoutVOs.size(); i++) {
                    if (projectTaskBurnoutVOs.get(i).getUpdateTime().compareTo(dateIndex) > 0 && null != projectTaskBurnoutVOs.get(i - 1).getActualHour() && null == projectTaskBurnoutVOs.get(i).getActualHour()) {
                        projectTaskBurnoutVOs.get(i).setForecastTime(totalHour.get());
                        projectTaskBurnoutVOs.get(i).setActualHour(projectTaskBurnoutVOs.get(i - 1).getActualHour());
                        projectTaskBurnoutVOs.get(i).setSurplus(projectTaskBurnoutVOs.get(i - 1).getSurplus());
                    }
                }
                for (ProjectTaskBurnoutVO projectTaskBurnoutVO : projectTaskBurnoutVOs) {
                    if (projectTaskBurnoutVO.getUpdateTime().compareTo(dateIndex) < 0) {
                        projectTaskBurnoutVO.setForecastTime(totalHour.get());
                        projectTaskBurnoutVO.setSurplus(totalHour.get());
                        projectTaskBurnoutVO.setActualHour(0);
                    }
                }
            }
        }
        return projectTaskBurnoutVOs;
    }

    @Override
    public ProjectTaskEventCountVO getProjectTaskEvent(ProjectTaskCountBO projectTaskQueryBO) {
        ProjectTaskEventCountVO projectTaskEventCountVO = new ProjectTaskEventCountVO();
        List<Long> resultTaskId = new ArrayList<>();
        List<Long> ids = new ArrayList<>();
        ProjectTask task = lambdaQuery().eq(ProjectTask::getTaskId, projectTaskQueryBO.getTaskId()).one();
        if (ObjectUtil.isNotEmpty(task)) {
            ids.add(task.getTaskId());
            queryChildTaskId(resultTaskId, ids);
        }
        Integer demandNoStartNum = 0;
        Integer demandRunNum = 0;
        Integer demandEndNum = 0;
        Integer taskNoStartNum = 0;
        Integer taskRunNum = 0;
        Integer taskEndNum = 0;
        Integer defectsNoStartNum = 0;
        Integer defectsRunNum = 0;
        Integer defectsEndNum = 0;
        List<Integer> types = Arrays.asList(2, 3, 4);
        if (CollectionUtil.isNotEmpty(resultTaskId)) {
            List<ProjectTask> projectTasks = lambdaQuery().in(ProjectTask::getTaskId, resultTaskId).in(ProjectTask::getType, types).list();
            if (ObjectUtil.isNotEmpty(projectTasks)) {
                projectTasks = projectTasks.stream().distinct().collect(Collectors.toList());
                for (ProjectTask projectTask : projectTasks) {
                    if (projectTask.getType() == 2) {
                        if (projectTask.getStatus() == 1) {
                            demandNoStartNum++;
                        } else if (projectTask.getStatus() == 2) {
                            demandRunNum++;
                        } else if (projectTask.getStatus() == 3) {
                            demandEndNum++;
                        }
                    } else if (projectTask.getType() == 3) {
                        if (projectTask.getStatus() == 1) {
                            taskNoStartNum++;
                        } else if (projectTask.getStatus() == 2) {
                            taskRunNum++;
                        } else if (projectTask.getStatus() == 3) {
                            taskEndNum++;
                        }
                    } else if (projectTask.getType() == 4) {
                        if (projectTask.getStatus() == 1) {
                            defectsNoStartNum++;
                        } else if (projectTask.getStatus() == 2) {
                            defectsRunNum++;
                        } else if (projectTask.getStatus() == 3) {
                            defectsEndNum++;
                        }
                    }
                }

            }
        }
        projectTaskEventCountVO.setDemandNoStartNum(demandNoStartNum);
        projectTaskEventCountVO.setDemandRunNum(demandRunNum);
        projectTaskEventCountVO.setDemandEndNum(demandEndNum);
        projectTaskEventCountVO.setTaskNoStartNum(taskNoStartNum);
        projectTaskEventCountVO.setTaskRunNum(taskRunNum);
        projectTaskEventCountVO.setTaskEndNum(taskEndNum);
        projectTaskEventCountVO.setDefectsNoStartNum(defectsNoStartNum);
        projectTaskEventCountVO.setDefectsRunNum(defectsRunNum);
        projectTaskEventCountVO.setDefectsEndNum(defectsEndNum);
        return projectTaskEventCountVO;
    }


    @Override
    public BasePage<ProjectTask> getAllMatters(ProjectTaskQueryBO projectTaskQueryBO) {
        LambdaQueryWrapper<ProjectTask> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        setLamdaQuery(lambdaQueryWrapper, projectTaskQueryBO);
        //  lambdaQueryWrapper.in(CollectionUtil.isNotEmpty(projectTaskQueryBO.getStatus()), ProjectTask::getStatus, projectTaskQueryBO.getStatus());
        lambdaQueryWrapper.ne(ProjectTask::getType, 1);
        lambdaQueryWrapper.isNull(ProjectTask::getBelongIterationId);
        BasePage<ProjectTask> projectTaskPage = this.page(projectTaskQueryBO.parse(), lambdaQueryWrapper);
        if (projectTaskPage.getList().size() > 0) {

            Long projectId = projectTaskPage.getList().get(0).getProjectId();
            List<ProjectTask> projectTasks = this.lambdaQuery().eq(ProjectTask::getProjectId, projectId).list();
            setField(projectTaskPage.getList(), projectTasks, projectTaskQueryBO);
        }
        return projectTaskPage;
    }

    @Override
    public BasePage<ProjectTask> getAllMattersByTaskId(ProjectTaskQueryBO projectTaskQueryBO) {
        LambdaQueryWrapper<ProjectTask> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ProjectTask::getBelongIterationId, projectTaskQueryBO.getTaskId());
        return this.page(projectTaskQueryBO.parse(), lambdaQueryWrapper);
    }

    @Override
    public BasePage<ProjectTask> queryProjectPlanTaskList(ProjectTaskQueryBO projectTaskQueryBO) {
        //查询
        LambdaQueryWrapper<ProjectTask> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        setLamdaQuery(lambdaQueryWrapper, projectTaskQueryBO);
        lambdaQueryWrapper.ne(ProjectTask::getType, 1);
        lambdaQueryWrapper.isNull(ProjectTask::getBelongIterationId);
        //查询没有更新到个人排序的任务
        List<ProjectTask> projectTasksAdd = this.getBaseMapper().queryAddTaskPageWithUserSort(projectTaskQueryBO,UserUtil.getUserId());
        if(CollectionUtil.isNotEmpty(projectTasksAdd)){
            Long maxSort = this.getBaseMapper().queryMaxTaskPageWithUserSort(projectTaskQueryBO,UserUtil.getUserId());
            List<ProjectTaskUserSort>  addList = new ArrayList<>();
            //如果需要增加则更新个人排序
            for (ProjectTask projectTask : projectTasksAdd) {
                maxSort+=1;
                ProjectTaskUserSort projectTaskUserSort = BeanUtil.copyProperties(projectTask, ProjectTaskUserSort.class);
                projectTaskUserSort.setUserId(UserUtil.getUserId());
                projectTaskUserSort.setSortType(TaskUserSortEnum.TOPLAN.getType());
                projectTaskUserSort.setSortNum(maxSort);
                addList.add(projectTaskUserSort);
            }
            projectTaskUserSortService.saveBatch(addList);
        }


        BasePage<ProjectTask> projectTaskPage = this.getBaseMapper().queryTaskPageWithUserSort(projectTaskQueryBO.parse(), projectTaskQueryBO,UserUtil.getUserId());
//        projectTaskPage = this.page(projectTaskQueryBO.parse(), lambdaQueryWrapper);
        if (projectTaskPage.getList().size() > 0) {
            Long projectId = projectTaskPage.getList().get(0).getProjectId();
            List<ProjectTask> projectTasks = this.lambdaQuery().eq(ProjectTask::getProjectId, projectId).list();
            setField(projectTaskPage.getList(), projectTasks, projectTaskQueryBO);
        }
        return projectTaskPage;
    }

    @Override
    public BasePage<ProjectTask> queryProjectIterationTaskList(ProjectTaskQueryBO projectTaskQueryBO) {
        LambdaQueryWrapper<ProjectTask> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        setLamdaQuery(lambdaQueryWrapper, projectTaskQueryBO);
        lambdaQueryWrapper.eq(ProjectTask::getType, ModuleTypeEnum.ITERATION.getType());
        if(ObjectUtil.isNotEmpty(projectTaskQueryBO.getType()) && ModuleTypeEnum.ITERATION.getType().equals(projectTaskQueryBO.getType())){
            //如果项目id为空则是工作台迭代，不包含已完成
            if(ObjectUtil.isEmpty(projectTaskQueryBO.getProjectId())){
                lambdaQueryWrapper.ne(ProjectTask::getStatus, 3);
                List<Long> projectIds=ApplicationContextHolder.getBean(IProjectUserService.class).queryMyProjectIds();
                if(ObjectUtil.isEmpty(projectIds) || CollUtil.isEmpty(projectIds)){
                    projectIds = CollUtil.newArrayList(0L);
                }
                lambdaQueryWrapper.in(ProjectTask::getProjectId,projectIds);
            }
        }else{
            lambdaQueryWrapper.ne(ProjectTask::getStatus, 3);
        }

        BasePage<ProjectTask> projectTaskPage = this.page(projectTaskQueryBO.parse(), lambdaQueryWrapper);
        Map<Long, Project> projectMap;
        if (projectTaskPage.getList().size() > 0) {
            //
            List<Long> projectIds = projectTaskPage.getList().stream().map(ProjectTask::getProjectId).distinct().collect(Collectors.toList());
            List<Project> projects = projectService.getBaseMapper().selectBatchIds(projectIds);
            projectMap = projects.stream().collect(Collectors.toMap(Project::getProjectId, Function.identity(), (v1, v2) -> v2));
            Long projectId = projectTaskPage.getList().get(0).getProjectId();
            List<ProjectTask> projectTasks = this.lambdaQuery().eq(ProjectTask::getProjectId, projectId).list();
            setField(projectTaskPage.getList(), projectTasks, projectTaskQueryBO);
        } else {
            projectMap = new HashMap<>();
        }
        //查询迭代完成百分比
        List<ProjectTask> list = projectTaskPage.getList().stream().map(p -> {
            ProjectTask vo = new ProjectTask();

            //设置项目信息
            if(CollUtil.isNotEmpty(projectMap) && projectMap.containsKey(p.getProjectId())){
                Project project = projectMap.get(p.getProjectId());
                p.setBelongProjectName(project.getName());
                p.setProjectType(project.getType());
                p.setProjectIcon(project.getIcon());
            }

            List<Long> resultTaskId = new ArrayList<>();
            List<Long> pid = new ArrayList<>();
            ProjectTask task = lambdaQuery().in(ProjectTask::getTaskId, p.getTaskId()).one();
            if (ObjectUtil.isNotEmpty(task)) {
                pid.add(task.getTaskId());
                queryChildTaskId(resultTaskId, pid);
            }
            //查询当前迭代已经完成的需求
            if (CollectionUtil.isNotEmpty(resultTaskId)) {
                List<ProjectTask> projectTaskEndList = lambdaQuery().in(ProjectTask::getTaskId, resultTaskId).list();
                Integer completed = projectTaskEndList.stream().filter(f -> 3 == f.getStatus()).collect(Collectors.toList()).size();
                Double num = (double) completed * 100 / projectTaskEndList.size();
                DecimalFormat df = new DecimalFormat("0.00");
                p.setBelongIterationProgress(df.format(num));
            } else {
                p.setBelongIterationProgress("0.00");
            }
            BeanUtil.copyProperties(p, vo);
            return vo;
        }).collect(Collectors.toList());
        BasePage<ProjectTask> page = new BasePage<>(projectTaskPage.getCurrent(), projectTaskPage.getSize(), projectTaskPage.getTotal());
        page.setList(list);
        return page;
    }

    private void setLamdaQuery(LambdaQueryWrapper<ProjectTask> lamdaQuery, ProjectTaskQueryBO projectTaskQueryBO) {
        if(ObjectUtil.isNotEmpty(projectTaskQueryBO.getType()) && projectTaskQueryBO.getType().equals(ModuleTypeEnum.ITERATION.getType())){
            if(ObjectUtil.isNotEmpty(projectTaskQueryBO.getProjectId())){
                lamdaQuery.eq(ProjectTask::getProjectId, projectTaskQueryBO.getProjectId());
            }
        }else{
            lamdaQuery.eq(ProjectTask::getProjectId, projectTaskQueryBO.getProjectId());
        }
        lamdaQuery.eq(StrUtil.isNotBlank(projectTaskQueryBO.getStartTime()), ProjectTask::getStartTime, projectTaskQueryBO.getStartTime());
        lamdaQuery.eq(StrUtil.isNotBlank(projectTaskQueryBO.getEndTime()), ProjectTask::getStopTime, projectTaskQueryBO.getEndTime());
        //搜索需要能根据页面ID查询
        if (StrUtil.isNotBlank(projectTaskQueryBO.getName())) {
            lamdaQuery.and(wrapper -> {
                wrapper.or(newWrapper -> {
                    newWrapper.like(StrUtil.isNotBlank(projectTaskQueryBO.getName()), ProjectTask::getName, projectTaskQueryBO.getName());
                });
                wrapper.or(newWrapper -> {
                    newWrapper.like(StrUtil.isNotBlank(projectTaskQueryBO.getName()), ProjectTask::getNum, projectTaskQueryBO.getName());
                });
            });
        }
        lamdaQuery.eq(ObjectUtil.isNotEmpty(projectTaskQueryBO.getMainUserId()), ProjectTask::getMainUserId, projectTaskQueryBO.getMainUserId());
        if (ObjectUtil.isNotEmpty(projectTaskQueryBO.getNeedBelongIteration()) && projectTaskQueryBO.getNeedBelongIteration().equals(2)) {
            //如果是needBelongIteration 为2 并且所属迭代信息为空则不包含所有已关联迭代
            if (ObjectUtil.isEmpty(projectTaskQueryBO.getBelongIterationId())) {
                lamdaQuery.isNull(ProjectTask::getBelongIterationId);
            } else {
                //如果是needBelongIteration 为2 并且所属迭代信息不为空则不包含已关联当前迭代
                lamdaQuery.and(wrapper -> {
                    wrapper.or(newWrapper -> {
                        newWrapper.ne(ProjectTask::getBelongIterationId, projectTaskQueryBO.getBelongIterationId());
                    });
                    wrapper.or(newWrapper -> {
                        newWrapper.isNull(ProjectTask::getBelongIterationId);
                    });
                });

            }

        } else {
            if (ObjectUtil.isNotEmpty(projectTaskQueryBO.getBelongIterationId())) {
                lamdaQuery.eq(ObjectUtil.isNotEmpty(projectTaskQueryBO.getBelongIterationId()), ProjectTask::getBelongIterationId, projectTaskQueryBO.getBelongIterationId());
            }
        }

        //高级筛选
        lamdaQuery.in(ObjectUtil.isNotEmpty(projectTaskQueryBO.getTypeQuery()) && projectTaskQueryBO.getTypeQuery().size() > 0, ProjectTask::getType, projectTaskQueryBO.getTypeQuery());
        lamdaQuery.in(ObjectUtil.isNotEmpty(projectTaskQueryBO.getPriorityQuery()) && projectTaskQueryBO.getPriorityQuery().size() > 0, ProjectTask::getPriority, projectTaskQueryBO.getPriorityQuery());
        lamdaQuery.in(ObjectUtil.isNotEmpty(projectTaskQueryBO.getStatusQuery()) && projectTaskQueryBO.getStatusQuery().size() > 0, ProjectTask::getStatus, projectTaskQueryBO.getStatusQuery());
        lamdaQuery.in(ObjectUtil.isNotEmpty(projectTaskQueryBO.getBelongIterationIdQuery()) && projectTaskQueryBO.getBelongIterationIdQuery().size() > 0, ProjectTask::getBelongIterationId, projectTaskQueryBO.getBelongIterationIdQuery());
        lamdaQuery.in(ObjectUtil.isNotEmpty(projectTaskQueryBO.getLabelQuery()) && projectTaskQueryBO.getLabelQuery().size() > 0, ProjectTask::getLabel, projectTaskQueryBO.getLabelQuery());
        lamdaQuery.in(ObjectUtil.isNotEmpty(projectTaskQueryBO.getMainUserIdQuery()) && projectTaskQueryBO.getMainUserIdQuery().size() > 0, ProjectTask::getMainUserId, projectTaskQueryBO.getMainUserIdQuery());
        lamdaQuery.isNull(ProjectTask::getPid);
//        //平铺显示
//        if (ObjectUtil.isNotNull(projectTaskQueryBO.getShowType()) && projectTaskQueryBO.getShowType().equals(2)) {
//            lamdaQuery.isNull(ProjectTask::getPid);
//        }
    }

    @Override
    public BasePage<ProjectTask> queryProjectTaskChildList(ProjectTaskQueryBO projectTaskQueryBO) {
        LambdaQueryWrapper<ProjectTask> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (ObjectUtil.isNotEmpty(projectTaskQueryBO.getBelongIterationId())) {
            lambdaQueryWrapper.eq(ProjectTask::getBelongIterationId, projectTaskQueryBO.getBelongIterationId());
        }
        if (ObjectUtil.isNotEmpty(projectTaskQueryBO.getRelatedDemandId())) {
            lambdaQueryWrapper.eq(ProjectTask::getRelatedDemandId, projectTaskQueryBO.getRelatedDemandId());
        }
        if (ObjectUtil.isNotEmpty(projectTaskQueryBO.getName())) {
            lambdaQueryWrapper.and(StrUtil.isNotBlank(projectTaskQueryBO.getName()),wrapper->{
                wrapper.or(newWrapper->{
                    newWrapper.like(ProjectTask::getName, projectTaskQueryBO.getName());
                });
                wrapper.or(newWrapper->{
                    newWrapper.like(ProjectTask::getNum, projectTaskQueryBO.getName());
                });
            });
        }
        if (ObjectUtil.isNotEmpty(projectTaskQueryBO.getTaskId())) {
            lambdaQueryWrapper.eq(ProjectTask::getPid, projectTaskQueryBO.getTaskId());
        } else {

            if (ObjectUtil.isNotEmpty(projectTaskQueryBO.getType())) {
                lambdaQueryWrapper.eq(ProjectTask::getType, projectTaskQueryBO.getType());
            }
        }
        //高级筛选
        lambdaQueryWrapper.in(ObjectUtil.isNotEmpty(projectTaskQueryBO.getTypeQuery()) && projectTaskQueryBO.getTypeQuery().size() > 0, ProjectTask::getType, projectTaskQueryBO.getTypeQuery());
        lambdaQueryWrapper.in(ObjectUtil.isNotEmpty(projectTaskQueryBO.getPriorityQuery()) && projectTaskQueryBO.getPriorityQuery().size() > 0, ProjectTask::getPriority, projectTaskQueryBO.getPriorityQuery());
        lambdaQueryWrapper.in(ObjectUtil.isNotEmpty(projectTaskQueryBO.getStatusQuery()) && projectTaskQueryBO.getStatusQuery().size() > 0, ProjectTask::getStatus, projectTaskQueryBO.getStatusQuery());
        // lambdaQueryWrapper.in(ObjectUtil.isNotEmpty(projectTaskQueryBO.getBelongIterationIdQuery()) && projectTaskQueryBO.getBelongIterationIdQuery().size() > 0, ProjectTask::getBelongIterationId, projectTaskQueryBO.getBelongIterationIdQuery());
        lambdaQueryWrapper.in(ObjectUtil.isNotEmpty(projectTaskQueryBO.getLabelQuery()) && projectTaskQueryBO.getLabelQuery().size() > 0, ProjectTask::getLabel, projectTaskQueryBO.getLabelQuery());
        lambdaQueryWrapper.in(ObjectUtil.isNotEmpty(projectTaskQueryBO.getMainUserIdQuery()) && projectTaskQueryBO.getMainUserIdQuery().size() > 0, ProjectTask::getMainUserId, projectTaskQueryBO.getMainUserIdQuery());
        BasePage<ProjectTask> projectTaskPage = this.page(projectTaskQueryBO.parse(), lambdaQueryWrapper);
        if (projectTaskPage.getList().size() > 0) {

            Long projectId = projectTaskPage.getList().get(0).getProjectId();
            List<ProjectTask> projectTasks = this.lambdaQuery().eq(ProjectTask::getProjectId, projectId).list();
            setField(projectTaskPage.getList(), projectTasks, projectTaskQueryBO);
        }
        return projectTaskPage;
    }


    private void setField(List<ProjectTask> projectTaskList, List<ProjectTask> projectTasks, ProjectTaskQueryBO projectTaskQueryBO) {
        if(CollectionUtil.isEmpty(projectTaskList)){
            return;
        }
        List<Long> taskIds = projectTaskList.stream().map(ProjectTask::getTaskId).distinct().collect(Collectors.toList());
        if(CollectionUtil.isEmpty(taskIds)){
            return;
        }
        List<ProjectTask> extendTaskList =  this.getBaseMapper().getTaskExtendInfo(taskIds);
        Map<Long, ProjectTask> taskMap = extendTaskList.stream().collect(Collectors.toMap(ProjectTask::getTaskId, Function.identity()));

        for (ProjectTask projectTask : projectTaskList) {
            ProjectTask extendTask = taskMap.get(projectTask.getTaskId());
            if(ObjectUtil.isNotEmpty(extendTask)){
                projectTask.setBelongIterationName(extendTask.getBelongIterationName());
                projectTask.setRelatedDemandName(extendTask.getRelatedDemandName());
                projectTask.setBoardStatusName(extendTask.getBoardStatusName());
                UserInfo mainUser = UserCacheUtil.getUserInfo(projectTask.getMainUserId());
                if(ObjectUtil.isNotEmpty(mainUser)){
                    projectTask.setMainUserName(mainUser.getNickname());
                    projectTask.setMainUserImg(mainUser.getUserImg());
                }
                UserInfo createUser = UserCacheUtil.getUserInfo(projectTask.getCreateUserId());
                if(ObjectUtil.isNotEmpty(createUser)){
                    projectTask.setCreateUserName(createUser.getNickname());
                    projectTask.setCreateUserImg(createUser.getUserImg());
                }
            }
            //获取事件id
            if (ObjectUtil.isNotEmpty(projectTask.getType()) && projectTask.getType() != 1) {
                Long eventId = projectEventService.lambdaQuery().eq(ProjectEvent::getType, projectTask.getType() - 1).list().get(0).getId();
                projectTask.setEventId(eventId);
            }

            setField(projectTask, projectTasks, projectTaskQueryBO);
        }

    }

    private void setField(ProjectTask projectTask, List<ProjectTask> projectTasks, ProjectTaskQueryBO projectTaskQueryBO) {
        //处理迭代字段
        if (ObjectUtil.isNotEmpty(projectTask.getType()) && projectTask.getType().equals(1)) {
            projectTask.setBelongIterationTaskCount(this.lambdaQuery().eq(ProjectTask::getBelongIterationId, projectTask.getTaskId()).count());
            List<ProjectOwnerRoleBO> adminRoles = projectService.queryOwnerRoleList(projectTask.getProjectId()).stream().filter(r -> {
                boolean bool = false;
                for (AdminRole adminRole : r.getAdminRoles()) {

                    if (adminRole.getRoleName().equals("项目管理员")) {
                        bool = true;
                    }
                }
                return bool;
            }).collect(Collectors.toList());
            projectTask.setProjectAdminRoleList(adminRoles);
        }
    }

    @Override
    public void relevancyChildTask(RelevancyChildTaskBO relevancyChildTaskBO) {
        this.lambdaUpdate().in(ProjectTask::getTaskId, relevancyChildTaskBO.getChildIds()).
                set(ProjectTask::getPid, relevancyChildTaskBO.getParentId()).update();
    }

    @Override
    public ProjectTask getProjectTaskDetails(Long taskId) {
        ProjectTask projectTask = this.getById(taskId);
        if(ObjectUtil.isEmpty(projectTask)){
            throw new BusinessException(ProjectCodeEnum.PROJECT_TASK_DELETE_ERROR);
        }
        ProjectTaskQueryBO projectTaskQueryBO = new ProjectTaskQueryBO();
        projectTaskQueryBO.setShowType(2);
        Long projectId = projectTask.getProjectId();
        Project project = projectService.getById(projectId);
        if(ObjectUtil.isEmpty(project)){
            //删除项目任务
            projectService.deleteProject(projectId);
            throw new BusinessException(ProjectCodeEnum.PROJECT_EXIST_ERROR);
        }
        List<ProjectTask> extendInfo = this.getBaseMapper().getTaskExtendInfo(Collections.singletonList(projectTask.getTaskId()));
        if(CollectionUtil.isNotEmpty(extendInfo)){
            if(CollectionUtil.isNotEmpty(extendInfo)){
                extendInfo.forEach(item->{
                    UserInfo mainUser = UserCacheUtil.getUserInfo(item.getMainUserId());
                    if(ObjectUtil.isNotEmpty(mainUser)){
                        item.setMainUserName(mainUser.getNickname());
                        item.setMainUserImg(mainUser.getUserImg());
                    }
                    UserInfo createUser = UserCacheUtil.getUserInfo(item.getCreateUserId());
                    if(ObjectUtil.isNotEmpty(createUser)){
                        item.setCreateUserName(createUser.getNickname());
                        item.setCreateUserImg(createUser.getUserImg());
                    }
                });
            }
        }
        if(CollectionUtil.isNotEmpty(extendInfo)){
            ProjectTask extendTask = CollectionUtil.getFirst(extendInfo);
            projectTask.setBelongIterationName(extendTask.getBelongIterationName());
            projectTask.setRelatedDemandName(extendTask.getRelatedDemandName());
            projectTask.setCreateUserName(extendTask.getCreateUserName());
            projectTask.setCreateUserImg(extendTask.getCreateUserImg());
            projectTask.setMainUserName(extendTask.getMainUserName());
            projectTask.setMainUserImg(extendTask.getMainUserImg());
            projectTask.setBoardStatusName(extendTask.getBoardStatusName());
        }
        List<ProjectTask> projectTasks = this.lambdaQuery().eq(ProjectTask::getProjectId, projectId).list();
        setField(projectTask, projectTasks, projectTaskQueryBO);
        if (StrUtil.isNotBlank(projectTask.getLabel())) {
            projectTask.setLabelTaskList(ApplicationContextHolder.getBean(IProjectLabelService.class).queryTaskLabelList(projectTask.getLabel()));
        }
        if (StrUtil.isNotEmpty(projectTask.getBatchId())) {
            List<FileEntity> data = projectFileService.queryFileList(projectTask.getBatchId());
            projectTask.setFile(data);
        } else {
            projectTask.setFile(new ArrayList<>());
        }
        //设置工时列表
        List<ProjectTaskTime> taskTimes = ApplicationContextHolder.getBean(IProjectTaskTimeService.class).lambdaQuery().eq(ProjectTaskTime::getTaskId, projectTask.getTaskId()).list();
        projectTask.setTaskTimeList(taskTimes);
        setRelation(projectTask);
        //设置项目成员列表
        projectTask.setProjectOwnerRoleList(projectService.queryOwnerRoleList(projectTask.getProjectId()));
        //获取迭代进度
        //迭代进度=事项进度总和/事项总个数
        if (ObjectUtil.isNotNull(projectTask.getBelongIterationId())) {
            List<ProjectTask> childTask = this.lambdaQuery().eq(ProjectTask::getBelongIterationId, taskId).list();
            int childSumProgress = childTask.stream().mapToInt(ProjectTask::getProgress).sum();
            if (childSumProgress > 0) {
                Double num = (double) childSumProgress * 100 / childTask.size();
                DecimalFormat df = new DecimalFormat("0.00");
                projectTask.setBelongIterationProgress(df.format(num));
            }
        }
        //获取项目类型
        projectTask.setProjectType(project.getType());
        //获取事件id
        if (projectTask.getType() != 1) {
            Long eventId = projectEventService.lambdaQuery().eq(ProjectEvent::getType, projectTask.getType() - 1).list().get(0).getId();
            projectTask.setEventId(eventId);
        }
        //查询时间详情团队成员
        if (ObjectUtil.isEmpty(projectTask.getOwnerUserId())) {
            projectTask.setOwnerUserId(projectTask.getCreateUserId().toString());
        }
        Set<Long> userIds = SeparatorUtil.toLongSet(projectTask.getOwnerUserId());
        List<TaskOwnerBO> TaskOwnerBOS = new ArrayList<>();
        for (Long id : userIds) {
            UserInfo userInfo = UserCacheUtil.getUserInfo(id);
            TaskOwnerBO taskOwnerBO = new TaskOwnerBO();
            taskOwnerBO.setUserId(id);
            taskOwnerBO.setName(userInfo.getNickname());
            taskOwnerBO.setEmail(userInfo.getEmail());
            taskOwnerBO.setMobile(userInfo.getMobile());
            TaskOwnerBOS.add(taskOwnerBO);
        }
        projectTask.setTaskOwnerBOS(TaskOwnerBOS);

        //获取子任务，只获取单级，同一数据太多树结构耗时
        List<ProjectTask> childTask = this.lambdaQuery().eq(ProjectTask::getPid, taskId).list();
        projectTask.setChildTaskList(childTask);
        return projectTask;
    }

    /**
     * 功能描述: 设置子节点树结构
     * 〈〉
     *
     * @Param:
     * @Return:
     * @Author: guole
     * @Date: 2022/9/30 17:31
     */
    private void setChildTree(ProjectTask projectTask, List<ProjectTask> projectTasks) {
        if (ObjectUtil.isNotNull(projectTasks)) {
            List<ProjectTask> childList = RecursionUtil.getChildListTree(projectTasks, "pid", projectTask.getTaskId(), "taskId", "childTaskList", ProjectTask.class);
            projectTask.setChildTaskList(childList);
        }
    }

    private List<Long> queryChildTaskId(List<Long> resultTaskId, List<Long> ids) {
        List<Long> queryIds = new ArrayList<>();
        //查询当前迭代下的需求
        List<ProjectTask> belongIterationId = lambdaQuery().in(ProjectTask::getBelongIterationId, ids).in(ProjectTask::getType, Arrays.asList(2, 3, 4)).list().stream().collect(Collectors.toList());
        if (ObjectUtil.isNotEmpty(belongIterationId)) {
            resultTaskId.addAll(belongIterationId.stream().map(p -> p.getTaskId()).collect(Collectors.toList()));
            List<Long> pIds = belongIterationId.stream().filter(p -> ObjectUtil.isNotNull(p.getPid())).map(p -> p.getPid()).collect(Collectors.toList());
            resultTaskId.addAll(pIds);
        }
        //查询当前迭代下的任务
        List<ProjectTask> belatedDemandId = lambdaQuery().in(ProjectTask::getRelatedDemandId, ids).in(ProjectTask::getType, Arrays.asList(2, 3, 4)).list().stream().collect(Collectors.toList());
        if (ObjectUtil.isNotEmpty(belatedDemandId)) {
            resultTaskId.addAll(belatedDemandId.stream().map(p -> p.getTaskId()).collect(Collectors.toList()));
            List<Long> pIds = belatedDemandId.stream().filter(p -> ObjectUtil.isNotNull(p.getPid())).map(p -> p.getPid()).collect(Collectors.toList());
            resultTaskId.addAll(pIds);
        }
        return queryIds;
    }

    /**
     * 设置关系数据
     *
     * @return void
     * @author jiao sir
     * @date 2021/11/23
     */
    private void setRelation(ProjectTask projectTask) {
        Long taskId = projectTask.getTaskId();
        Map<Integer, Set<Long>> relationIdsMap = projectTaskRelationService.lambdaQuery()
                .select(ProjectTaskRelation::getRelationId, ProjectTaskRelation::getType)
                .eq(ProjectTaskRelation::getTaskId, taskId)
                .list()
                .stream()
                .collect(Collectors.groupingBy(ProjectTaskRelation::getType, Collectors.mapping(ProjectTaskRelation::getRelationId, Collectors.toSet())));
        // 获取crmRelation
//        CrmRelationDTO crmRelation = CrmRelationUtils.getCrmRelation(relationIdsMap, taskId);
        // copy data
//        BeanUtil.copyProperties(crmRelation, projectTask);
    }

    @Override
    public void relevancyBelongIteration(RelevancyBelongIterationBO relevancyBelongIterationBO) {
        ProjectTask iteration = null;
        if (CollectionUtil.isEmpty(relevancyBelongIterationBO.getTaskIds())) {
            return;
        }
        for (Long id : relevancyBelongIterationBO.getTaskIds()) {
            ProjectTaskLog projectTaskLog = new ProjectTaskLog();
            projectTaskLog.setType(1);
            projectTaskLog.setCreateUserId(UserUtil.getUserId());
            projectTaskLog.setTaskId(id);
            ProjectTask projectTask = lambdaQuery().eq(ProjectTask::getTaskId, id).one();
            if (ObjectUtil.isNotEmpty(relevancyBelongIterationBO.getBelongIterationId())) {
                iteration = lambdaQuery().eq(ProjectTask::getTaskId, relevancyBelongIterationBO.getBelongIterationId()).one();
            } else {
                iteration = lambdaQuery().eq(ProjectTask::getTaskId, projectTask.getBelongIterationId()).one();
            }
            if (ObjectUtil.isNotEmpty(relevancyBelongIterationBO.getBelongIterationId())) {
                projectTaskLog.setContent(UserUtil.getUser().getNickname().concat(" 更新所属迭代为 ").concat(null == iteration ? "" : iteration.getName()));
            } else {
                projectTaskLog.setContent(UserUtil.getUser().getNickname().concat(" 移除迭代为 ").concat(null == iteration ? "" : iteration.getName()));
            }
            projectTaskLog.setUserId(UserUtil.getUserId());
            projectTaskLogService.save(projectTaskLog);
        }
        this.lambdaUpdate().in(ProjectTask::getTaskId, relevancyBelongIterationBO.getTaskIds()).
                set(ProjectTask::getBelongIterationId, relevancyBelongIterationBO.getBelongIterationId()).update();
    }

    @Override
    public void relevancyRelatedDemand(RelevancyRelatedDemandIdBO relatedDemandIdBO) {
        this.lambdaUpdate().in(ProjectTask::getTaskId, relatedDemandIdBO.getTaskIds()).
                set(ProjectTask::getRelatedDemandId, relatedDemandIdBO.getRelatedDemandId()).update();
    }

    @Override
    public JSONObject excelImport(MultipartFile file, Long projectId, Integer taskType) throws IOException {
        List<List<Object>> errList = new ArrayList<>();

        List<ProjectOwnerRoleBO> projectOwnerRoleBOList = projectService.queryOwnerRoleList(projectId);
        if (file.isEmpty()) {
            return new JSONObject();
        }
        String filePath = getFilePath(file);
        AtomicReference<Integer> num = new AtomicReference<>(0);
        int maxIndex = 1001;
        ExcelUtil.readBySax(filePath, 0, (int sheetIndex, long rowIndex, List<Object> rowList) -> {
            if (rowIndex > maxIndex) {
                rowList.add(0, "最多同时导入1000条数据");
                errList.add(rowList);
                return;
            }
            if (rowIndex > 1) {
                if (rowList.size() < 11) {
                    for (int i = rowList.size() - 1; i < 12; i++) {
                        rowList.add(null);
                    }
                }
                num.getAndSet(num.get() + 1);
                if (StrUtil.isEmptyIfStr(rowList.get(0))) {
                    rowList.add(0, "事项标题不能为空");
                    errList.add(rowList);
                    return;
                }
                String taskName = rowList.get(0).toString().trim();
                String description = null;
                if (ObjectUtil.isNotEmpty(rowList.get(1))) {
                    description = rowList.get(1).toString().trim();
                }
                Long mainUserId = UserUtil.getUserId();
                Integer status = null;
                Long boardStatusId = null;
                Integer priority = 0;
                if (ObjectUtil.isNotEmpty(rowList.get(2))) {
                    String mainUserName = (rowList.get(2).toString());
                    boolean isOwner = false;
                    for (ProjectOwnerRoleBO projectOwnerRoleBO : projectOwnerRoleBOList) {
                        if (projectOwnerRoleBO.getRealname().equals(mainUserName)) {
                            mainUserId = projectOwnerRoleBO.getUserId();
                            isOwner = true;
                        }
                    }

                    if (!isOwner) {
                        rowList.add(0, "负责人与当前项目成员不匹配");
                        errList.add(rowList);
                        return;
                    }
                }
                if (!StrUtil.isEmptyIfStr(rowList.get(3))) {
                    if (ObjectUtil.isEmpty(taskType)) {
                        rowList.add(0, "类型错误");
                        errList.add(rowList);
                        return;
                    }
                    ProjectEventStatus projectEventStatus = projectEventStatusService.queryEventStatusByStatusName(rowList.get(3).toString(), taskType - 1,projectId);
                    if (ObjectUtil.isNull(projectEventStatus)) {
                        rowList.add(0, "无此状态");
                        errList.add(rowList);
                        return;
                    }
                    {
                        boardStatusId = projectEventStatus.getId();
                        status = projectEventStatus.getStatusType();
                    }
                } else {
                    ProjectSchemeRelation schemeRelation = projectSchemeRelationService.queryEventId(projectService.getById(projectId).getSchemeId(), taskType);
                    List<ProjectEventStatus> eventStatuses = projectEventStatusService.queryEventStatusByEventId(schemeRelation.getEventId()).stream().filter(f -> f.getUseStatus().equals(1) && f.getInitStatus().equals(1)).collect(Collectors.toList());
                    if (eventStatuses.size() > 0) {
                        boardStatusId = eventStatuses.get(0).getId();
                        status = eventStatuses.get(0).getStatusType();
                    }

                }
                // 优先级 3高 2中 1低 0无
                if (ObjectUtil.isNotEmpty(rowList.get(4))) {
                    switch (rowList.get(4).toString()) {
                        case "无":
                            priority = 0;
                            break;
                        case "低":
                            priority = 1;
                            break;
                        case "中":
                            priority = 2;
                            break;
                        case "高":
                            priority = 3;
                            break;
                        default:
                            rowList.add(0, "请填写正确优先级：高 中 低 无");
                            errList.add(rowList);

                    }
                }

                if (!StrUtil.isEmptyIfStr(rowList.get(5))) {
                    Object object = rowList.get(5);
                    String time;
                    if (object instanceof Long) {
                        time = DateUtil.formatDate(org.apache.poi.ss.usermodel.DateUtil.getJavaDate((Long) object));
                    } else {
                        time = object.toString();
                    }
                    if (!isTime(time)) {
                        rowList.add(0, "开始时间日期格式错误，例:2000-01-01");
                        errList.add(rowList);
                        return;
                    }
                }
                if (!StrUtil.isEmptyIfStr(rowList.get(6))) {
                    Object object = rowList.get(6);
                    String time;
                    if (object instanceof Long) {
                        time = DateUtil.formatDate(org.apache.poi.ss.usermodel.DateUtil.getJavaDate((Long) object));
                    } else {
                        time = object.toString();
                    }
                    if (!isTime(time)) {
                        rowList.add(0, "结束时间日期格式错误，例:2000-01-01");
                        errList.add(rowList);
                        return;
                    }
                }


                ;
                Date startTime = null;
                if (ObjectUtil.isNotEmpty(rowList.get(5))) {
                    startTime = DateUtil.parse(rowList.get(5).toString().trim());
                }
                Date stopTime = null;
                if (ObjectUtil.isNotEmpty(rowList.get(6))) {
                    stopTime = DateUtil.parse(rowList.get(6).toString().trim());
                }


                String labelIds = "";
                if (!StrUtil.isEmptyIfStr(rowList.get(7))) {
                    Object o = rowList.get(7);
                    if (ObjectUtil.isNotEmpty(o)) {
                        String row = StrUtil.toString(o);
                        List<String> rowValues = Arrays.stream(row.split(",")).map(StrUtil::trim).filter(StrUtil::isNotEmpty).collect(Collectors.toList());
                        List<ProjectLabel> labels = projectLabelService.lambdaQuery().and(wrapper -> {
                            for (String rowValue : rowValues) {
                                wrapper.or(newWrapper -> {
                                    newWrapper.eq(ProjectLabel::getName, rowValue);
                                });
                            }
                        }).list();
                        if (CollectionUtil.isNotEmpty(labels)) {
                            labelIds = labels.stream().map(ProjectLabel::getLabelId).map(StrUtil::toString).collect(Collectors.joining(","));
                        } else {

                            rowList.add(0, "错误的标签");
                            errList.add(rowList);
                            return;
                        }
                    }

                }
                Integer progress = 0;
                Integer estimatedManHours = 0;
                Integer wrongType = null;
                Object cellProgress = rowList.get(8);
                if (ObjectUtil.isNotEmpty(cellProgress)) {
                    String cellStr = StrUtil.toString(cellProgress);
                    if (NumberUtil.isNumber(cellStr)) {
                        progress = Integer.parseInt(cellStr);
                    }
                }
                Object cellEstimatedManHours = rowList.get(9);
                if (ObjectUtil.isNotEmpty(cellEstimatedManHours)) {
                    String cellStr = StrUtil.toString(cellEstimatedManHours);
                    if (NumberUtil.isNumber(cellStr)) {
                        estimatedManHours = Integer.parseInt(cellStr);
                    }
                }

                if (taskType == 4) {
                    Object cellWrongType = rowList.get(12);
                    if (ObjectUtil.isNotEmpty(cellWrongType)) {
                        String cellStr = StrUtil.toString(cellWrongType);
                        WrongTypeEnum wrongTypeEnum = WrongTypeEnum.enumByDesc(cellStr);
                        if (ObjectUtil.isNotEmpty(wrongTypeEnum)) {
                            wrongType = wrongTypeEnum.getType();
                        }
                    }
                }


                ProjectTask projectTask = new ProjectTask();
                projectTask.setMainUserId(mainUserId);
                projectTask.setDescription(description);
                projectTask.setName(taskName);
                projectTask.setStatus(status);
                projectTask.setBoardStatusId(boardStatusId);
                projectTask.setType(taskType);
                projectTask.setProjectId(projectId);
                projectTask.setStartTime(startTime);
                projectTask.setStopTime(stopTime);
                projectTask.setLabel(labelIds);
                projectTask.setProgress(progress);
                projectTask.setEstimatedManHours(estimatedManHours);
                projectTask.setWrongType(wrongType);
                projectTask.setPriority(priority);
                projectTask.setNum(this.getBaseMapper().getMaxNum(projectId)+ 1);
                //添加事项
                this.save(projectTask);
                //添加任务成员
                ProjectTaskUserBO projectTaskUserBO = new ProjectTaskUserBO();
                projectTaskUserBO.setProjectId(projectTask.getProjectId());
                projectTaskUserBO.setTaskId(projectTask.getTaskId());
                projectTaskUserBO.setUserIds(Collections.singletonList(UserUtil.getUserId()));
                projectTaskUserService.relatedProjectUser(projectTaskUserBO);
                //添加工时
                if (ObjectUtil.isNotEmpty(rowList.get(10))) {
                    Integer actualHour = 0;
                    Integer surplusHours = 0;
                    Object cellActualHour = rowList.get(10);
                    if (ObjectUtil.isNotEmpty(cellActualHour)) {
                        String cellStr = StrUtil.toString(cellActualHour);
                        if (NumberUtil.isNumber(cellStr)) {
                            actualHour = Integer.parseInt(cellStr);
                        }
                    }
                    Object cellSurplusHours = rowList.get(11);
                    if (ObjectUtil.isNotEmpty(cellSurplusHours)) {
                        String cellStr = StrUtil.toString(cellSurplusHours);
                        if (NumberUtil.isNumber(cellStr)) {
                            surplusHours = Integer.parseInt(cellStr);
                        }
                    }
                    ProjectTaskTime projectTaskTime = new ProjectTaskTime();
                    projectTaskTime.setBeginTime(new Date());
                    projectTaskTime.setEndTime(new Date());
                    projectTaskTime.setActualHour(actualHour);
                    projectTaskTime.setTaskId(projectTask.getTaskId());
                    projectTaskTime.setUpdateTime(LocalDateTimeUtil.now());
                    if (ObjectUtil.isNotEmpty(rowList.get(11))) {
                        projectTaskTime.setSurplusHours(surplusHours);
                    }
                    projectTaskTimeService.save(projectTaskTime);
                }

            } else {
                if (rowIndex == 1) {
                    rowList.add(0, "错误信息");
                }
                errList.add(Convert.toInt(rowIndex), rowList);
            }
        });
        FileUtil.del(filePath);
        int listSize = 2;
        JSONObject result = new JSONObject().fluentPut("totalSize", num.get()).fluentPut("errSize", 0);
        if (errList.size() > listSize) {
            BigExcelWriter writer = null;

            try {
                String token = IdUtil.simpleUUID();
                writer = ExcelUtil.getBigWriter(BaseUtil.UPLOAD_PATH + "excel/" + token);
                // 取消数据的黑色边框以及数据左对齐
                CellStyle cellStyle = writer.getCellStyle();
                cellStyle.setFillBackgroundColor(IndexedColors.BLACK.getIndex());
                cellStyle.setBorderTop(BorderStyle.NONE);
                cellStyle.setBorderBottom(BorderStyle.NONE);
                cellStyle.setBorderLeft(BorderStyle.NONE);
                cellStyle.setBorderRight(BorderStyle.NONE);
                cellStyle.setAlignment(HorizontalAlignment.LEFT);
                Font defaultFont = writer.createFont();
                defaultFont.setFontHeightInPoints((short) 11);
                cellStyle.setFont(defaultFont);
                // 取消数字格式的数据的黑色边框以及数据左对齐
                CellStyle cellStyleForNumber = writer.getStyleSet().getCellStyleForNumber();
                cellStyleForNumber.setBorderTop(BorderStyle.NONE);
                cellStyleForNumber.setBorderBottom(BorderStyle.NONE);
                cellStyleForNumber.setBorderLeft(BorderStyle.NONE);
                cellStyleForNumber.setBorderRight(BorderStyle.NONE);
                cellStyleForNumber.setAlignment(HorizontalAlignment.LEFT);
                cellStyleForNumber.setFillBackgroundColor(IndexedColors.BLACK.getIndex());
                cellStyleForNumber.setFont(defaultFont);

                CellStyle textStyle = writer.getWorkbook().createCellStyle();
                DataFormat format = writer.getWorkbook().createDataFormat();
                textStyle.setDataFormat(format.getFormat("@"));

                writer.merge(errList.get(1).size() + 1, errList.get(0).get(0).toString().trim(), true);
                writer.getHeadCellStyle().setAlignment(HorizontalAlignment.LEFT);
                writer.getHeadCellStyle().setWrapText(true);
                Font headFont = writer.createFont();
                headFont.setFontHeightInPoints((short) 11);
                writer.getHeadCellStyle().setFont(headFont);
                writer.getHeadCellStyle().setFillPattern(FillPatternType.NO_FILL);
                writer.getOrCreateRow(0).setHeightInPoints(120);
                writer.setRowHeight(-1, 20);

                //writer.merge(6, "系统用户导入模板(*)为必填项");
                for (int i = 0; i < errList.get(1).size(); i++) {
                    writer.getSheet().setDefaultColumnStyle(i, textStyle);
                }
                errList.remove(0);
                writer.write(errList);
                result.fluentPut("errSize", errList.size() - 1).fluentPut("token", token);
            } finally {
                if (writer != null) {
                    writer.close();
                }
            }
        }
        return result;
    }

    @Override
    public void downloadExcel(HttpServletResponse response, Integer taskType) {
        List<JSONObject> recordList = getRecordList(taskType);

        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("事项导入表");
        sheet.setDefaultRowHeight((short) 400);
        CellStyle textStyle = wb.createCellStyle();
        DataFormat format = wb.createDataFormat();
        textStyle.setDataFormat(format.getFormat("@"));
        for (int i = 0; i < recordList.size(); i++) {
            if (Objects.equals(recordList.get(i).getInteger("type"), 4)) {
                CellStyle dateStyle = wb.createCellStyle();
                DataFormat dateFormat = wb.createDataFormat();
                dateStyle.setDataFormat(dateFormat.getFormat(DatePattern.NORM_DATE_PATTERN));
                sheet.setDefaultColumnStyle(i, dateStyle);
            } else {
                sheet.setDefaultColumnStyle(i, textStyle);
            }
            sheet.setColumnWidth(i, 20 * 256);
        }
        CellStyle cellStyle = wb.createCellStyle();
        HSSFRow titleRow = sheet.createRow(0);
        titleRow.setHeight((short) (6 * 256));
//        cellStyle.setFillForegroundColor(IndexedColors.SKY_BLUE.getIndex());
//        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        //开启自动换行
        cellStyle.setWrapText(true);
        HSSFRichTextString title = new HSSFRichTextString("注意事项\n 1、表头标“*”的红色字体为必填项\n 2、日期时间：推荐格式为2020-02-02 13:13:13\n 3、多个团队成员和多个标签时请使用英文逗号分隔符分开");
        //一个单元格内设置两种字体风格
        Font firstFont = wb.createFont();
        firstFont.setFontHeightInPoints((short) 16);
        firstFont.setBold(true);
        Font secondFont = wb.createFont();
        secondFont.setFontHeightInPoints((short) 10);
        title.applyFont(0, 6, firstFont);
        title.applyFont(6, title.length(), secondFont);
        titleRow.createCell(0).setCellValue(title);
        titleRow.getCell(0).setCellStyle(cellStyle);
        CellRangeAddress region = new CellRangeAddress(0, 0, 0, recordList.size() - 1);
        sheet.addMergedRegion(region);
        try {
            HSSFRow row = sheet.createRow(1);
            for (int i = 0; i < recordList.size(); i++) {
                JSONObject record = recordList.get(i);
                // 在第一行第一个单元格，插入选项
                HSSFCell cell = row.createCell(i);
                // 普通写入操作
                if (record.getInteger("is_null") == 1) {
                    cell.setCellValue("*" + record.getString("name"));
                    Font cellFont = wb.createFont();
                    cellFont.setFontHeightInPoints((short) 11);
                    cellFont.setColor(Font.COLOR_RED);
                    cellStyle.setFont(cellFont);
                    cell.setCellStyle(cellStyle);


                } else {
                    cell.setCellValue(record.getString("name"));
                }
            }
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.setCharacterEncoding("UTF-8");
            //test.xls是弹出下载对话框的文件名，不能为中文，中文请自行编码
            response.setHeader("Content-Disposition", "attachment;filename=task_import.xls");
            wb.write(response.getOutputStream());

        } catch (Exception e) {
            log.error("error", e);
        } finally {
            try {
                wb.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void projectTaskExport(ProjectTaskExportBO projectTaskExport, HttpServletResponse response) {
//        List<Map<String, Object>> list = this.baseMapper.projectTaskExport(projectTaskExport.getTaskIds());
        //暂时提供全部导出数据的方法，后期优化导出数据效率
        LambdaQueryWrapper<ProjectTask> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        getProjectTaskEmportQuery(lambdaQueryWrapper, projectTaskExport);
        List<Map<String, Object>> list = this.listMaps(lambdaQueryWrapper);
        List<ExcelParseUtil.ExcelDataEntity> dataList = new ArrayList<>();

        List<JSONObject> recordList = getRecordList(projectTaskExport.getTaskType());

        for (Map<String, Object> map : list) {
            map.put("mainUserId", UserCacheUtil.getUserName((Long) map.get("mainUserId")));
            ProjectEventStatus eventStatus = projectEventStatusService.queryEventStatusById((Long) map.get("boardStatusId"));
            map.put("boardStatusId", eventStatus.getStatusName());
            List<String> projectLabels = projectLabelService.lambdaQuery().in(ProjectLabel::getLabelId, map.get("label")).list().stream().map(ProjectLabel::getName).collect(Collectors.toList());
            map.put("label", StrUtil.join(",",projectLabels));
            //描述去除html标签获取文本
            Object description = map.get("description");
            String clean = EscapeUtil.clean(Optional.ofNullable(description).orElse(StrUtil.EMPTY).toString());
            map.put("description", clean);
            //转译优先级
            Object priority = map.get("priority");
            if (ObjectUtil.isNotEmpty(priority)) {
                String toString = StrUtil.toString(priority);
                if (NumberUtil.isNumber(toString)) {
                    String priorityDesc = this.getPriorityDesc(Integer.parseInt(toString));
                    map.put("priority", priorityDesc);
                }
            }
            //转译缺陷类型
            Object wrongType = map.get("wrongType");
            if (ObjectUtil.isNotEmpty(wrongType)) {
                String toString = StrUtil.toString(wrongType);
                if (NumberUtil.isNumber(toString)) {
                    WrongTypeEnum wrongTypeEnum = WrongTypeEnum.enumByType(Integer.parseInt(toString));
                    if (ObjectUtil.isNotEmpty(wrongTypeEnum)) {
                        map.put("wrongType", wrongTypeEnum.getDesc());
                    }
                }
            }

        }
        if (projectTaskExport.getExportColumn().size() > 0) {


            for (JSONObject jsonObject : recordList) {
                if (projectTaskExport.getExportColumn().contains(jsonObject.getString("fieldName"))) {
                    dataList.add(ExcelParseUtil.toEntity(jsonObject.getString("fieldName"), jsonObject.getString("name")));


                }

            }

        } else {


            for (JSONObject jsonObject : recordList) {
                dataList.add(ExcelParseUtil.toEntity(jsonObject.getString("fieldName"), jsonObject.getString("name")));

            }

        }


        ExcelParseUtil.exportExcel(list, new ExcelParseUtil.ExcelParseService() {
            @Override
            public String getExcelName() {
                if (ObjectUtil.isEmpty(projectTaskExport.getTaskType())) {
                    return "项目全部事项";
                } else if (ObjectUtil.isNotEmpty(projectTaskExport.getTaskType()) && projectTaskExport.getTaskType().equals(2)) {
                    return "项目需求";
                } else if (ObjectUtil.isNotEmpty(projectTaskExport.getTaskType()) && projectTaskExport.getTaskType().equals(3)) {
                    return "项目任务";
                } else if (ObjectUtil.isNotEmpty(projectTaskExport.getTaskType()) && projectTaskExport.getTaskType().equals(4)) {
                    return "项目缺陷";
                } else {
                    return "项目任务";
                }
            }

            @Override
            public boolean isXlsx() {
                return true;
            }
        }, dataList,response,1);
    }

    @Override
    public List<JSONObject> projectTaskExportColumn(Integer taskType) {
        return getRecordList(taskType);
    }


    /**
     * 删除任务
     */
    @Override
    public void deleteTask(Long taskId) {
        ProjectTask projectTask = this.getById(taskId);
        if (projectTask.getType() == 2) {

            this.lambdaUpdate().eq(ProjectTask::getRelatedDemandId, taskId).
                    set(ProjectTask::getRelatedDemandId, null).update();
        }

        this.removeById(taskId);
        //删除任务成员
        LambdaUpdateWrapper<ProjectTaskUser> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ProjectTaskUser::getProjectId, projectTask.getProjectId());
        updateWrapper.eq(ProjectTaskUser::getTaskId, taskId);
        projectTaskUserService.remove(updateWrapper);
    }

    @Override
    public List<ProjectBoardVO> queryProjectTaskChildBoardList(ProjectTaskQueryBO projectTaskQueryBO) {
        List<ProjectBoardVO> boardVOS = new ArrayList<>();

        Project project = projectService.getById(projectTaskQueryBO.getProjectId());
        ProjectSchemeRelation projectSchemeRelation = projectSchemeRelationService.queryEventId(project.getSchemeId(), projectTaskQueryBO.getType());

        List<ProjectSchemeRelationBoard> boardRelationList = projectSchemeRelationBoardService.lambdaQuery().
                eq(ProjectSchemeRelationBoard::getSchemeRelationId, projectSchemeRelation.getId()).eq(ProjectSchemeRelationBoard::getProjectId,projectTaskQueryBO.getProjectId()).list();
        List<ProjectTask> projectTasks = this.getTaskList(projectTaskQueryBO);
        for (ProjectSchemeRelationBoard board : boardRelationList) {
            ProjectBoardVO boardVO = BeanUtil.toBean(board, ProjectBoardVO.class);
            boardVO.setStatusList(new ArrayList<>());
            List<ProjectBoardStatusVO> projectBoardStatusList = projectBoardStatusService.queryBoardStatusByBoardId(board.getId());
            projectBoardStatusList.forEach(projectBoardStatusVO -> {
                List<ProjectTaskVO> projectTaskVOS = new ArrayList<>();
                for (ProjectTask projectTask : projectTasks) {
                    if (ObjectUtil.isNotNull(projectTask.getBoardStatusId()) && projectBoardStatusVO.getStatusId().longValue() == projectTask.getBoardStatusId()) {
                        ProjectTaskVO projectTaskVO = BeanUtil.copyProperties(projectTask, ProjectTaskVO.class);
                        projectTaskVO.setBoardStatusName(projectBoardStatusVO.getStatusName());
                        if (ObjectUtil.isNotEmpty(projectTask.getMainUserId())) {
                            UserInfo userInfo = UserCacheUtil.getUserInfo(projectTask.getMainUserId());
                            projectTaskVO.setMainUserId(userInfo.getUserId());
                            projectTaskVO.setMainUserImg(userInfo.getUserImg());
                            projectTaskVO.setMainUserName(userInfo.getNickname());
                        }
                        projectTaskVOS.add(projectTaskVO);
                    }
                }
                projectBoardStatusVO.setTaskVOList(projectTaskVOS);
                boardVO.getStatusList().add(projectBoardStatusVO);
            });
            boardVOS.add(boardVO);
        }
        return boardVOS;
    }

    /**
     * 对待办事项进行排序
     */
    @Override
    public void sortBackLog(ProjectTaskUserSortBO projectTaskUserSortBO) {
        if (ObjectUtil.isEmpty(projectTaskUserSortBO.getProjectId()) || CollectionUtil.isEmpty(projectTaskUserSortBO.getSortList())) {
            return;
        }
        List<Long> sortList = projectTaskUserSortBO.getSortList().stream().distinct().collect(Collectors.toList());

        LambdaUpdateWrapper<ProjectTaskUserSort> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ProjectTaskUserSort::getProjectId, projectTaskUserSortBO.getProjectId());
        updateWrapper.eq(ProjectTaskUserSort::getUserId, UserUtil.getUserId());
        updateWrapper.in(ProjectTaskUserSort::getTaskId, sortList);
        projectTaskUserSortService.remove(updateWrapper);
        //新增
        //增加新的 sortNum降序排列
        List<ProjectTaskUserSort> projectTaskUserSortList = new ArrayList<>();

        for (int i = 0; i < sortList.size(); i++) {
            ProjectTaskUserSort projectTaskUserSort = new ProjectTaskUserSort();
            projectTaskUserSort.setProjectId(projectTaskUserSortBO.getProjectId());
            projectTaskUserSort.setTaskId(sortList.get(i));
            projectTaskUserSort.setUserId(UserUtil.getUserId());
            projectTaskUserSort.setSortNum(Integer.toUnsignedLong(sortList.size()) - i);
            projectTaskUserSort.setSortType(TaskUserSortEnum.TOPLAN.getType());
            projectTaskUserSortList.add(projectTaskUserSort);


        }
        projectTaskUserSortService.saveBatch(projectTaskUserSortList);
    }

    public List<ProjectTask> getTaskList(ProjectTaskQueryBO projectTaskQueryBO) {
        LambdaQueryWrapper<ProjectTask> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (ObjectUtil.isNotEmpty(projectTaskQueryBO.getBelongIterationId())) {
            lambdaQueryWrapper.eq(ProjectTask::getBelongIterationId, projectTaskQueryBO.getBelongIterationId());
        }
        if (ObjectUtil.isNotEmpty(projectTaskQueryBO.getRelatedDemandId())) {
            lambdaQueryWrapper.eq(ProjectTask::getRelatedDemandId, projectTaskQueryBO.getRelatedDemandId());
        }
        if (ObjectUtil.isNotEmpty(projectTaskQueryBO.getName())) {
            lambdaQueryWrapper.eq(ProjectTask::getName, projectTaskQueryBO.getName());
        }
        if (ObjectUtil.isNotEmpty(projectTaskQueryBO.getTaskId())) {
            lambdaQueryWrapper.eq(ProjectTask::getPid, projectTaskQueryBO.getTaskId());
        } else {

            if (ObjectUtil.isNotEmpty(projectTaskQueryBO.getType())) {
                lambdaQueryWrapper.eq(ProjectTask::getType, projectTaskQueryBO.getType());
            }
        }
        //高级筛选
        lambdaQueryWrapper.in(ObjectUtil.isNotEmpty(projectTaskQueryBO.getTypeQuery()) && projectTaskQueryBO.getTypeQuery().size() > 0, ProjectTask::getType, projectTaskQueryBO.getTypeQuery());
        lambdaQueryWrapper.in(ObjectUtil.isNotEmpty(projectTaskQueryBO.getPriorityQuery()) && projectTaskQueryBO.getPriorityQuery().size() > 0, ProjectTask::getPriority, projectTaskQueryBO.getPriorityQuery());
        lambdaQueryWrapper.in(ObjectUtil.isNotEmpty(projectTaskQueryBO.getStatusQuery()) && projectTaskQueryBO.getStatusQuery().size() > 0, ProjectTask::getStatus, projectTaskQueryBO.getStatusQuery());
        // lambdaQueryWrapper.in(ObjectUtil.isNotEmpty(projectTaskQueryBO.getBelongIterationIdQuery()) && projectTaskQueryBO.getBelongIterationIdQuery().size() > 0, ProjectTask::getBelongIterationId, projectTaskQueryBO.getBelongIterationIdQuery());
        lambdaQueryWrapper.in(ObjectUtil.isNotEmpty(projectTaskQueryBO.getLabelQuery()) && projectTaskQueryBO.getLabelQuery().size() > 0, ProjectTask::getLabel, projectTaskQueryBO.getLabelQuery());
        lambdaQueryWrapper.in(ObjectUtil.isNotEmpty(projectTaskQueryBO.getMainUserIdQuery()) && projectTaskQueryBO.getMainUserIdQuery().size() > 0, ProjectTask::getMainUserId, projectTaskQueryBO.getMainUserIdQuery());
        List<ProjectTask> projectTaskPage = this.list(lambdaQueryWrapper);
        return projectTaskPage;
    }


    /**
     * 判断日期格式是否正确
     *
     * @param str 日期str
     * @return true代表日期格式正确
     */
    private boolean isTime(String str) {
        try {
            DateTime dateTime = DateUtil.parse(str);
            return dateTime != null;
        } catch (DateException dateException) {
            return false;
        }
    }

    private List<JSONObject> getRecordList(Integer taskType) {
        //*事项标题	*描述	处理人	状态	团队成员	优先级	开始时间	截止时间	标签	进度	预估工时	实际投入时长	剩余工时
        List<JSONObject> recordList = new LinkedList<>();
        recordList.add(new JSONObject().fluentPut("fieldName", "name").fluentPut("name", "事项标题").fluentPut("is_null", 1).fluentPut("type", 1));
        recordList.add(new JSONObject().fluentPut("fieldName", "description").fluentPut("name", "描述").fluentPut("is_null", 0).fluentPut("type", 1));
        recordList.add(new JSONObject().fluentPut("fieldName", "mainUserId").fluentPut("name", "处理人").fluentPut("is_null", 0).fluentPut("type", 1));
        recordList.add(new JSONObject().fluentPut("fieldName", "boardStatusId").fluentPut("name", "状态").fluentPut("is_null", 0).fluentPut("type", 1));
        recordList.add(new JSONObject().fluentPut("fieldName", "priority").fluentPut("name", "优先级").fluentPut("is_null", 0).fluentPut("type", 1));
        recordList.add(new JSONObject().fluentPut("fieldName", "startTime").fluentPut("name", "开始时间").fluentPut("is_null", 0).fluentPut("type", 4));
        recordList.add(new JSONObject().fluentPut("fieldName", "stopTime").fluentPut("name", "截止时间").fluentPut("is_null", 0).fluentPut("type", 4));
        recordList.add(new JSONObject().fluentPut("fieldName", "label").fluentPut("name", "标签").fluentPut("is_null", 0).fluentPut("type", 1));
        recordList.add(new JSONObject().fluentPut("fieldName", "progress").fluentPut("name", "进度").fluentPut("is_null", 0).fluentPut("type", 1));
        recordList.add(new JSONObject().fluentPut("fieldName", "estimatedManHours").fluentPut("name", "预估工时").fluentPut("is_null", 0).fluentPut("type", 1));
        recordList.add(new JSONObject().fluentPut("fieldName", "actualHour").fluentPut("name", "实际投入时长").fluentPut("is_null", 0).fluentPut("type", 1));
        recordList.add(new JSONObject().fluentPut("fieldName", "surplusHours").fluentPut("name", "剩余工时").fluentPut("is_null", 0).fluentPut("type", 1));

        if (ObjectUtil.isNotEmpty(taskType)) {
            if (taskType.equals(4)) {
                recordList.add(new JSONObject().fluentPut("fieldName", "wrongType").fluentPut("name", "缺陷类型").fluentPut("is_null", 0).fluentPut("type", 1));
            }
        } else {
            recordList.add(new JSONObject().fluentPut("fieldName", "wrongType").fluentPut("name", "缺陷类型").fluentPut("is_null", 0).fluentPut("type", 1));
        }

        return recordList;
    }


    public void getProjectTaskEmportQuery(LambdaQueryWrapper<ProjectTask> lambdaQueryWrapper, ProjectTaskExportBO projectTaskExportBO) {
        lambdaQueryWrapper.eq(ProjectTask::getProjectId, projectTaskExportBO.getProjectId());
        if (ObjectUtil.isEmpty(projectTaskExportBO.getTaskType())) {
            setLamdaQuery(lambdaQueryWrapper, BeanUtil.copyProperties(projectTaskExportBO, ProjectTaskQueryBO.class));
            if (ObjectUtil.isEmpty(projectTaskExportBO.getType()) || projectTaskExportBO.getType().equals(0)) {
                lambdaQueryWrapper.ne(ProjectTask::getType, 1);
            } else {
                lambdaQueryWrapper.eq(ProjectTask::getType, projectTaskExportBO.getType());
            }
        } else {
            lambdaQueryWrapper.ne(ProjectTask::getType, 1).isNull(ProjectTask::getPid)
                    .eq(ProjectTask::getType, projectTaskExportBO.getTaskType())
                    .and(StrUtil.isNotBlank(projectTaskExportBO.getSearch()), wrapper -> wrapper.like(ProjectTask::getName, projectTaskExportBO.getSearch()).or().eq(ProjectTask::getDescription, projectTaskExportBO.getSearch()))

                    //高级筛选
                    .in(ObjectUtil.isNotEmpty(projectTaskExportBO.getTypeQuery()) && projectTaskExportBO.getTypeQuery().size() > 0, ProjectTask::getType, projectTaskExportBO.getTypeQuery())
                    .in(ObjectUtil.isNotEmpty(projectTaskExportBO.getPriorityQuery()) && projectTaskExportBO.getPriorityQuery().size() > 0, ProjectTask::getPriority, projectTaskExportBO.getPriorityQuery())
                    .in(ObjectUtil.isNotEmpty(projectTaskExportBO.getStatusQuery()) && projectTaskExportBO.getStatusQuery().size() > 0, ProjectTask::getStatus, projectTaskExportBO.getStatusQuery())
                    .in(ObjectUtil.isNotEmpty(projectTaskExportBO.getBelongIterationIdQuery()) && projectTaskExportBO.getBelongIterationIdQuery().size() > 0, ProjectTask::getBelongIterationId, projectTaskExportBO.getBelongIterationIdQuery())
                    .in(ObjectUtil.isNotEmpty(projectTaskExportBO.getLabelQuery()) && projectTaskExportBO.getLabelQuery().size() > 0, ProjectTask::getLabel, projectTaskExportBO.getLabelQuery())
                    .in(ObjectUtil.isNotEmpty(projectTaskExportBO.getMainUserIdQuery()) && projectTaskExportBO.getMainUserIdQuery().size() > 0, ProjectTask::getMainUserId, projectTaskExportBO.getMainUserIdQuery());
        }


    }

    private String getFilePath(MultipartFile file) {
        String dirPath = FileUtil.getTmpDirPath();
        try {
            InputStream inputStream = file.getInputStream();
            File fromStream = FileUtil.writeFromStream(inputStream, dirPath + "/" + IdUtil.simpleUUID() + file.getOriginalFilename());
            return fromStream.getAbsolutePath();
        } catch (IOException e) {
            throw new BusinessException(SystemCodeEnum.SYSTEM_UPLOAD_FILE_ERROR);
        }
    }

    @Override
    public BasePage<ProjectTask> queryUserTaskList(ProjectUserTaskQueryBO userTaskQueryBO) {

        userTaskQueryBO.setMainUserId(UserUtil.getUserId());
        BasePage<ProjectTask> projectTaskPage=this.getBaseMapper().queryUserTaskList(userTaskQueryBO.parse(),userTaskQueryBO);
        if(CollectionUtil.isNotEmpty(projectTaskPage.getList())){
            projectTaskPage.getList().forEach(item->{
                UserInfo mainUser = UserCacheUtil.getUserInfo(item.getMainUserId());
                if(ObjectUtil.isNotEmpty(mainUser)){
                    item.setMainUserName(mainUser.getNickname());
                    item.setMainUserImg(mainUser.getUserImg());
                }
                UserInfo createUser = UserCacheUtil.getUserInfo(item.getCreateUserId());
                if(ObjectUtil.isNotEmpty(createUser)){
                    item.setCreateUserName(createUser.getNickname());
                    item.setCreateUserImg(createUser.getUserImg());
                }
            });
        }
        return projectTaskPage;
    }



    @Override
    public Boolean setProgress(ProjectTask projectTask) {
        ProjectTask oldProjectTask = this.getById(projectTask.getTaskId());
        String contentLog = ProjectUtil.getLogContent("进度", oldProjectTask.getProgress(), projectTask.getProgress());
        projectTaskLogService.saveTaskLog(projectTask.getTaskId(), contentLog);
        return this.updateProjectTask(projectTask);
    }

    @Override
    public Boolean setPriority(ProjectTask projectTask) {
        ProjectTask oldProjectTask = this.getById(projectTask.getTaskId());
        String contentLog = ProjectUtil.getLogContent("优先级", PriorityEnum.enumByType(oldProjectTask.getPriority()).getDesc(), PriorityEnum.enumByType(projectTask.getPriority()).getDesc());
        projectTaskLogService.saveTaskLog(projectTask.getTaskId(), contentLog);
        return this.updateProjectTask(projectTask);
    }

    @Override
    public void setProjectTaskMainUser(ProjectTask projectTask) {
        ProjectTask oldProjectTask = this.lambdaQuery().eq(ProjectTask::getTaskId, projectTask.getTaskId()).one();
        Project project = projectService.lambdaQuery().eq(Project::getProjectId, oldProjectTask.getProjectId()).one();
        if (ObjectUtil.isNull(projectTask.getMainUserId())) {
            this.lambdaUpdate().eq(ProjectTask::getTaskId, projectTask.getTaskId()).set(ProjectTask::getMainUserId, null).update();
        } else {
            this.lambdaUpdate().eq(ProjectTask::getTaskId, projectTask.getTaskId()).set(ProjectTask::getMainUserId, projectTask.getMainUserId()).update();
        }

        String content = "";
        if (ObjectUtil.isNotEmpty(projectTask.getMainUserId()) && !UserUtil.getUserId().equals(projectTask.getMainUserId())) {
            UserInfo userInfo = UserCacheUtil.getUserInfo(projectTask.getMainUserId());

            content = project.getName() + "," + userInfo.getNickname();
            projectAuthUtil.sendMessage(UserUtil.getUserId(), projectTask.getMainUserId(), projectTask.getTaskId(), AdminMessageEnum.PROJECT_EDIT_MATTERS_USER_NOTICE.getType(), oldProjectTask.getName(), content);            //记录日志
            String oldMainUser = "";
            if (ObjectUtil.isNotEmpty(oldProjectTask.getMainUserId())) {
                UserInfo oldUserInfo = UserCacheUtil.getUserInfo(oldProjectTask.getMainUserId());
                if (ObjectUtil.isNotEmpty(oldUserInfo)) {
                    oldMainUser = oldUserInfo.getNickname();
                }
            }

            String contentLog = ProjectUtil.getLogContent("负责人", oldMainUser, userInfo.getNickname());
            projectTaskLogService.saveTaskLog(projectTask.getTaskId(), contentLog);
        }
    }

    @Override
    public void updateProjectTaskTime(ProjectTask projectTask) {
        ProjectTask oldTask = this.getById(projectTask.getTaskId());
        this.lambdaUpdate().eq(ProjectTask::getTaskId, projectTask.getTaskId()).
                set(ProjectTask::getStartTime, projectTask.getStartTime()).
                set(ProjectTask::getStopTime, projectTask.getStopTime()).
                update();
        String contentLog = "";
        String oldStartTime = "";
        String newStartTime = "";
        String oldStopTime = "";
        String newStopTime = "";
        if(ObjectUtil.isNotEmpty(oldTask.getStartTime())){
            oldStartTime = DateUtil.formatDate(oldTask.getStartTime());
        }
        if(ObjectUtil.isNotEmpty(projectTask.getStartTime())){
            newStartTime = DateUtil.formatDate(projectTask.getStartTime());
        }
        if(ObjectUtil.isNotEmpty(oldTask.getStopTime())){
            oldStopTime = DateUtil.formatDate(oldTask.getStopTime());
        }
        if(ObjectUtil.isNotEmpty(projectTask.getStopTime())){
            newStopTime = DateUtil.formatDate(projectTask.getStopTime());
        }
        if (!oldStartTime.equals(newStartTime)) {
            contentLog = ProjectUtil.getLogContent("开始时间", DateUtil.formatDate(oldTask.getStartTime()), DateUtil.formatDate(projectTask.getStartTime()));
            if(StrUtil.isNotBlank(contentLog)){
                projectTaskLogService.saveTaskLog(projectTask.getTaskId(), contentLog);
            }
        }
        if (!oldStopTime.equals(newStopTime)) {
            contentLog = ProjectUtil.getLogContent("结束时间", DateUtil.formatDate(oldTask.getStopTime()), DateUtil.formatDate(projectTask.getStopTime()));
            if(StrUtil.isNotBlank(contentLog)){
                projectTaskLogService.saveTaskLog(projectTask.getTaskId(), contentLog);
            }
        }

    }

    /**
     * 功能描述: <br>
     * 〈查询工作台中各类型数量〉
     * @param userTaskQueryBO
     * @author zyh
     */
    @Override
    public ProjectUserTaskCountVO queryUserTaskCount(ProjectUserTaskQueryBO userTaskQueryBO) {
        userTaskQueryBO.setMainUserId(UserUtil.getUserId());
        ProjectUserTaskCountVO projectUserTaskCountVO = this.getBaseMapper().queryUserTaskCount(userTaskQueryBO);
        //查询迭代数量，所有的不包含已完成
        List<Long> projectIds=ApplicationContextHolder.getBean(IProjectUserService.class).queryMyProjectIds();
        if(ObjectUtil.isEmpty(projectIds) || CollUtil.isEmpty(projectIds)){
            projectUserTaskCountVO.setIteration(0L);
        }else{
            Long iteration = this.lambdaQuery().eq(ProjectTask::getType, ModuleTypeEnum.ITERATION.getType()).ne(ProjectTask::getStatus, 3).
                    in(ProjectTask::getProjectId,projectIds).count();
            projectUserTaskCountVO.setIteration(iteration);
        }
        return projectUserTaskCountVO;
    }

    @Override
    public void projectTaskSetName(ProjectTaskNameBO projectTaskNameBO) {
        ProjectTask task = this.getById(projectTaskNameBO.getTaskId());
        this.lambdaUpdate().eq(ProjectTask::getTaskId, projectTaskNameBO.getTaskId()).set(ProjectTask::getName, projectTaskNameBO.getName()).update();
        ProjectTaskLog projectTaskLog = new ProjectTaskLog();
        projectTaskLog.setTaskId(projectTaskNameBO.getTaskId());
        projectTaskLog.setType(1);

        ModuleTypeEnum moduleTypeEnum = ModuleTypeEnum.enumByType(task.getType());
        String typeName = "";
        if(ObjectUtil.isNotEmpty(moduleTypeEnum)){
            typeName = moduleTypeEnum.getDesc();
        }
        projectTaskLog.setContent(typeName.concat("名称从 ").concat(task.getName()).concat("修改为 ").concat(projectTaskNameBO.getName()));
        projectTaskLogService.saveTaskLog(projectTaskLog);
    }

    @Transactional
    @Override
    public void batchSetProjectTask(BatchSetTaskBO batchSetTaskBO) {
        for (Long taskId : batchSetTaskBO.getTaskIds()) {
            ProjectTask projectTask = BeanUtil.copyProperties(batchSetTaskBO, ProjectTask.class);
            projectTask.setTaskId(taskId);
            if (ObjectUtil.isNotEmpty(projectTask.getPriority())) {
                this.setPriority(projectTask);
            }
            if (ObjectUtil.isNotEmpty(projectTask.getMainUserId())) {
                this.setProjectTaskMainUser(projectTask);
            }
            if (ObjectUtil.isNotEmpty(projectTask.getBoardStatusId())) {
                ApplicationContextHolder.getBean(IProjectBoardTaskService.class).setTaskStatus(taskId, batchSetTaskBO.getBoardStatusId());
            }
            if (ObjectUtil.isNotEmpty(projectTask.getStopTime())) {
                this.updateProjectTaskTime(projectTask);
            }
        }


    }

    /**
     * 功能描述: <br>
     * 〈添加事项任务基础信息〉
     * @param
     * @return void
     * @author zyh
     */
    public void saveProjectTaskBase(ProjectTask projectTask){
        projectTask.setCreateUserId(UserUtil.getUserId());
        if (ObjectUtil.isNull(projectTask.getFile()) || projectTask.getFile().size() == 0) {
            projectTask.setBatchId(IdUtil.simpleUUID());
        } else {
            projectTask.setBatchId(projectTask.getFile().get(0).getBatchId());
        }
        //设置默认状态

        ProjectEventStatus eventStatus = projectEventStatusService.queryInitEventStatusByTaskType(projectTask.getType(), projectTask.getProjectId());
        if (ObjectUtil.isNotNull(eventStatus)) {
            projectTask.setBoardStatusId(eventStatus.getId());
            projectTask.setStatus(eventStatus.getStatusType());
        }


        //查询最大编号

        projectTask.setNum(this.getBaseMapper().getMaxNum(projectTask.getProjectId()) + 1);
        projectTask.setUpdateTime(LocalDateTime.now());

        save(projectTask);
    }

    /**
     * 功能描述: <br>
     * 〈添加事项成员〉
     * @param
     * @return void
     * @author zyh
     */
    public void saveProjectTaskUser(ProjectTask projectTask){
        //添加任务成员
        ProjectTaskUserBO projectTaskUserBO = new ProjectTaskUserBO();
        projectTaskUserBO.setProjectId(projectTask.getProjectId());
        projectTaskUserBO.setTaskId(projectTask.getTaskId());
        if (ObjectUtil.isEmpty(projectTask.getOwnerUserId())) {
            projectTaskUserBO.setUserIds(Collections.singletonList(UserUtil.getUserId()));
        } else {
            List<Long> userIds = Arrays.stream(projectTask.getOwnerUserId().split(","))
                    .filter(ObjectUtil::isNotEmpty)
                    .map(StrUtil::toString)
                    .map(StrUtil::trim)
                    .filter(NumberUtil::isNumber)
                    .map(Long::parseLong)
                    .distinct().collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(userIds)) {
                projectTaskUserBO.setUserIds(userIds);
            } else {
                projectTaskUserBO.setUserIds(Collections.singletonList(UserUtil.getUserId()));
            }
        }
        projectTaskUserService.relatedProjectUser(projectTaskUserBO);
    }

    /**
     * 功能描述: <br>
     * 〈添加事项日志信息〉
     *
     * @param
     * @return void
     * @author zyh
     */
    public void saveProjectTaskLog(ProjectTask projectTask) {
        ProjectTaskLog projectTaskLog = new ProjectTaskLog();
        projectTaskLog.setCreateUserId(UserUtil.getUserId());
        projectTaskLog.setTaskId(projectTask.getTaskId());
        projectTaskLog.setType(1);
        String userName = UserUtil.getUser().getNickname();
        if (2 == projectTask.getType()) {
            projectTaskLog.setContent(userName.concat("创建需求").concat(projectTask.getName()));
        } else if (3 == projectTask.getType()) {
            projectTaskLog.setContent(userName.concat("创建任务").concat(projectTask.getName()));

        } else if (4 == projectTask.getType()) {
            projectTaskLog.setContent(userName.concat("创建缺陷").concat(projectTask.getName()));
        } else {
            projectTaskLog.setContent(userName.concat("创建迭代").concat(projectTask.getName()));
        }
        if (null != projectTask.getPid()) {
            projectTaskLog.setContent(userName.concat("创建子任务").concat(projectTask.getName()));
        }
        projectTaskLog.setUserId(UserUtil.getUserId());
        projectTaskLogService.save(projectTaskLog);
    }

    /**
     * 功能描述: <br>
     * 〈添加事项任务工时记录〉
     * @param
     * @return void
     * @author zyh
     */
    public void saveProjectTaskTime(ProjectTask projectTask){
        List<ProjectTaskTime> taskTimeList = projectTask.getTaskTimeList()
                .stream()
                .filter(item->ObjectUtil.isNotEmpty(item.getActualHour()))
                .collect(Collectors.toList());
        if(CollectionUtil.isEmpty(taskTimeList)){
            return;
        }
        //添加工时日志
        Integer actualHour = taskTimeList.stream().filter(item->ObjectUtil.isNotEmpty(item.getActualHour())).mapToInt(ProjectTaskTime::getActualHour).filter(ObjectUtil::isNotEmpty).sum();
        ProjectTaskLog projectTaskLog = new ProjectTaskLog();
        projectTaskLog.setTaskId(projectTask.getTaskId());
        projectTaskLog.setType(2);
        projectTaskLog.setContent("登记了工时 ".concat(StrUtil.toString(actualHour) + "小时"));
        projectTaskLogService.saveTaskLog(projectTaskLog);
        //添加工时记录
        for (ProjectTaskTime taskTime : taskTimeList) {
            taskTime.setTaskId(projectTask.getTaskId());
            taskTime.setBeginTime(new Date());
            taskTime.setEndTime(new Date());
        }
        projectTaskTimeService.saveBatch(taskTimeList);

    }

    /**
     * 功能描述: <br>
     * 〈添加事项子任务〉
     * @param
     * @return void
     * @author zyh
     */
    public void saveProjectTaskChild(ProjectTask projectTask){
        List<ProjectTask> taskList = projectTask.getChildTaskList();
        if(CollectionUtil.isEmpty(taskList)){
            return;
        }
        Integer maxNum = this.getBaseMapper().getMaxNum(projectTask.getProjectId());
        for (ProjectTask task : taskList) {
            maxNum += 1;
            task.setProjectId(projectTask.getProjectId());
            task.setPid(projectTask.getTaskId());
            task.setType(projectTask.getType());
            task.setNum(maxNum);
            task.setBatchId(IdUtil.simpleUUID());
            task.setBoardStatusId(projectTask.getBoardStatusId());
        }
        //保存子任务
        this.saveBatch(taskList);

    }

    /**
     * 功能描述: <br>
     * 〈添加事项关联业务〉
     * @param
     * @return void
     * @author zyh
     */
    public void saveProjectTaskRelation(ProjectTask projectTask){
        ProjectTaskRelation projectTaskRelation = projectTask.getProjectTaskRelation();
        if(ObjectUtil.isEmpty(projectTaskRelation)){
            return;
        }
        projectTaskRelation.setTaskId(projectTask.getTaskId());
        if(ObjectUtil.isEmpty(projectTaskRelation.getFlag())){
            projectTaskRelation.setFlag(1);
        }
        //保存关联业务项
//        projectTaskRelationService.saveProjectTaskRelation(projectTaskRelation);

    }

    /**
     * 功能描述: <br>
     * 〈添加事项发送消息〉
     * @param
     * @return void
     * @author zyh
     */
    public void saveProjectTaskSend(ProjectTask projectTask){
        Project project = projectService.lambdaQuery().eq(Project::getProjectId, projectTask.getProjectId()).one();
        String content = "";
        if (ObjectUtil.isNotEmpty(projectTask.getMainUserId()) && !UserUtil.getUserId().equals(projectTask.getMainUserId())) {
            UserInfo userInfo = UserCacheUtil.getUserInfo(projectTask.getMainUserId());
            //通知项目经理和管理员
//            List<Long> sendIds = projectAuthUtil.projectAdminUser(projectTask.getProjectId());
//            for (Long id : sendIds) {
//                if (id.equals(userInfo.getUserId())) {
//                    continue;
//                }
//
//                content = project.getName() + "," + userInfo.getRealname();
//                projectAuthUtil.sendMessage(UserUtil.getUserId(), id, projectTask.getTaskId(), AdminMessageEnum.PROJECT_EDIT_MATTERS_USER_NOTICE.getType(), projectTask.getName(), content);
//            }
            //通知被指定人
            content = project.getName() + "," + userInfo.getNickname();
            projectAuthUtil.sendMessage(UserUtil.getUserId(), projectTask.getMainUserId(), projectTask.getTaskId(), AdminMessageEnum.PROJECT_EDIT_MATTERS_USER_NOTICE.getType(), projectTask.getName(), content);
        }
    }

    /**
     * 获取优先级描述
     *
     * @param priority
     * @return java.lang.String
     * @date 2020/11/12 10:22
     **/
    public String getPriorityDesc(Integer priority) {
        String priorityDesc = "";
        switch (priority) {
            case 0:
                priorityDesc = "无";
                break;
            case 1:
                priorityDesc = "低";
                break;
            case 2:
                priorityDesc = "中";
                break;
            case 3:
                priorityDesc = "高";
                break;
            default:
                break;
        }
        return priorityDesc;
    }
}
