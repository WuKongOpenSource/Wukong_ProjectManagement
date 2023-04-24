package com.kakarote.work.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.kakarote.common.entity.UserInfo;
import com.kakarote.common.exception.BusinessException;
import com.kakarote.common.result.BasePage;
import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.ids.provider.utils.UserCacheUtil;
import com.kakarote.work.common.project.*;
import com.kakarote.work.common.admin.AdminProjectRole;
import com.kakarote.work.common.admin.AdminProjectRoleBO;
import com.kakarote.work.common.project.ProjectOwnerRoleBO;
import com.kakarote.work.constant.GroupTypeEnum;
import com.kakarote.work.constant.InitStatusTypeEnum;
import com.kakarote.work.constant.ProjectCodeEnum;
import com.kakarote.work.entity.BO.*;
import com.kakarote.work.entity.BO.ProjectQueryBO;
import com.kakarote.work.entity.BO.ProjectVo;
import com.kakarote.work.entity.PO.*;
import com.kakarote.work.mapper.ProjectMapper;
import com.kakarote.work.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 项目表 服务实现类
 * </p>
 *
 * @author bai
 * @since 2022-09-08
 */
@Service
public class ProjectServiceImpl extends BaseServiceImpl<ProjectMapper, Project> implements IProjectService {

    @Autowired
    private ProjectAuthUtil projectAuthUtil;
    @Autowired
    private IProjectTaskService projectTaskService;
    @Autowired
    private IProjectCollectService projectCollectService;
    @Autowired
    private IProjectConfigSchemeService projectConfigSchemeService;
    @Autowired
    private IProjectEventService projectEventService;
    @Autowired
    private IProjectSchemeRelationService projectSchemeRelationService;
    @Autowired
    private IProjectStatusService projectStatusService;
    @Autowired
    private IProjectEventStatusService projectEventStatusService;
    @Autowired
    private IProjectBoardService projectBoardService;
    @Autowired
    private IProjectSchemeRelationBoardService relationBoardServiceService;
    @Autowired
    private IProjectBoardStatusService boardStatusService;
    @Autowired
    private IProjectGroupManagementService projectGroupManagementService;
    @Autowired
    private IProjectUserService projectUserService;
    @Autowired
    private IProjectGroupService projectGroupService;
    @Autowired
    private IProjectRoleService projectRoleService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Project addProject(Project project) {
        Long userId = UserUtil.getUserId();
        if (StrUtil.isEmpty(project.getName()) || null == project.getName()) {
            throw new BusinessException(ProjectCodeEnum.PROJECT_CREATE_NAME_NULL_ERROR);
        }
        if (StrUtil.isEmpty(project.getBatchId())) {
            project.setBatchId(IdUtil.simpleUUID());
        }
        Set<Long> ownerUserIds = new HashSet<>();
        ownerUserIds.add(userId);
        if (project.getOwnerUserIds() != null) {
            ownerUserIds.addAll(SeparatorUtil.toLongSet(project.getOwnerUserIds()));
        }
        //查询模板的schemeId
        Long schemeId = projectConfigSchemeService.lambdaQuery().eq(ProjectConfigScheme::getCollaborationType, project.getType()).one().getId();
        project.setSchemeId(schemeId);
        project.setOwnerUserIds(SeparatorUtil.fromLongSet(ownerUserIds));
        project.setCreateUserId(userId);
        project.setCreateTime(LocalDateTimeUtil.now());
        save(project);
        //初始化该项目类型默认事件和默认看板
        //普通类型只包含任务
        List<ProjectEvent> projectEvents = null;
        if (project.getType() == 1) {
            List<Integer> ids = Arrays.asList(2);
            projectEvents = projectEventService.lambdaQuery().in(ProjectEvent::getType, ids).list();
        } else {
            //敏捷开发包含 1需求 2任务 3缺陷 4子工作项目 0自定义
            List<Integer> ids = Arrays.asList(1, 2, 3, 4);
            projectEvents = projectEventService.lambdaQuery().in(ProjectEvent::getType, ids).list();
        }
        if (ObjectUtil.isNotEmpty(projectEvents)) {
            projectEvents.forEach(projectEvent -> {
                ProjectSchemeRelation projectSchemeRelation = null;
                projectSchemeRelation = projectSchemeRelationService.lambdaQuery().eq(ProjectSchemeRelation::getSchemeId, project.getSchemeId()).eq(ProjectSchemeRelation::getEventId, projectEvent.getId()).one();
                //      int statusCount = projectEventStatusService.lambdaQuery().eq(ProjectEventStatus::getProjectEventId, projectEvent.getId()).count();
                //  if (statusCount == 0) {
                //初始化事件状态
                this.initEventStatus(project.getProjectId(), projectEvent.getId());
                //    }

                if (ObjectUtil.isNotEmpty(projectSchemeRelation)) {
                    projectSchemeRelation.setType(projectEvent.getType());
                    projectSchemeRelation.setSchemeId(project.getSchemeId());
                    projectSchemeRelation.setBatchId(IdUtil.simpleUUID());
                    projectSchemeRelation.setSysType(0);
                    projectSchemeRelation.setCreateUserId(UserUtil.getUserId());
                    projectSchemeRelation.setEventId(projectEvent.getId());
                    projectSchemeRelationService.updateById(projectSchemeRelation);

                } else {
                    projectSchemeRelation = new ProjectSchemeRelation();
                    projectSchemeRelation.setType(projectEvent.getType());
                    projectSchemeRelation.setSchemeId(project.getSchemeId());
                    projectSchemeRelation.setBatchId(IdUtil.simpleUUID());
                    projectSchemeRelation.setSysType(0);
                    projectSchemeRelation.setCreateUserId(UserUtil.getUserId());
                    projectSchemeRelation.setEventId(projectEvent.getId());
                    projectSchemeRelationService.save(projectSchemeRelation);

                }
                //初始化看板
                List<ProjectEventStatus> eventStatusList = projectEventStatusService.applicationSchemeStatusList( project.getProjectId(),projectEvent.getId());
                List<ProjectBoard> projectBoards = projectBoardService.lambdaQuery().eq(ProjectBoard::getEventId, projectEvent.getId()).list();
                for (ProjectBoard projectBoard : projectBoards) {
                    ProjectSchemeRelationBoard projectSchemeRelationBoard = BeanUtil.toBean(projectBoard, ProjectSchemeRelationBoard.class);
                    projectSchemeRelationBoard.setSchemeRelationId(projectSchemeRelation.getId());
                    projectSchemeRelationBoard.setProjectId(project.getProjectId());
                    relationBoardServiceService.save(projectSchemeRelationBoard);
                    List<ProjectBoardStatus> boardStatuses = new ArrayList<>();
                    //初始化看板状态
                    for (ProjectEventStatus eventStatus : eventStatusList) {
                        if (eventStatus.getSysType().equals(1) && eventStatus.getStatusName().equals(projectBoard.getBoardName())) {
                            ProjectBoardStatus boardStatus = new ProjectBoardStatus();
                            boardStatus.setBoardId(projectSchemeRelationBoard.getId());
                            boardStatus.setStatusId(eventStatus.getId());
                            boardStatus.setSorting(0);
                            boardStatuses.add(boardStatus);
                        }
                    }
                    boardStatusService.saveBatch(boardStatuses);
                }
            });
        }
        List<AdminProjectRoleBO> adminRoleBO = new ArrayList<>();
        List<AdminRole> adminRoles = projectRoleService.queryProjectRoleByTypes(Arrays.asList(1));
        List<AdminRole> defaultRoles = projectRoleService.queryProjectRoleByTypes(Arrays.asList(0));
        ownerUserIds.forEach(ownerUserId -> {
            AdminProjectRoleBO adminProjectRoleBO = new AdminProjectRoleBO();
            adminProjectRoleBO.setDeptId(UserCacheUtil.getSimpleUser(ownerUserId).getDeptId());
            adminProjectRoleBO.setProjectId(project.getProjectId());
            adminProjectRoleBO.setUserId(ownerUserId);
            //创建赋值默认角色
            if (UserUtil.getUserId().equals(ownerUserId)) {
                adminProjectRoleBO.setRoleIds(Arrays.asList(adminRoles.get(0).getRoleId()));
            } else {
                adminProjectRoleBO.setRoleIds(Arrays.asList(defaultRoles.get(0).getRoleId()));
            }
            //公开项目给项目管理员角色
            adminProjectRoleBO.setIsOpen(project.getIsOpen());
            adminRoleBO.add(adminProjectRoleBO);
        });
        projectUserService.relatedProjectUser(adminRoleBO);
        project.setProjectOwnerRoleList(queryOwnerRoleList(project.getProjectId()));
        //删除通知
//        String content = UserUtil.getUser().getRealname().concat(" 创建了项目 ").concat(project.getName());
//        projectAuthUtil.sendMessage(UserUtil.getUserId(), UserUtil.getUserId(), project.getProjectId(), AdminMessageEnum.PROJECT_CREATE_NOTICE.getType(), "项目管理通知", content);
        return project;
    }


    @Override
    public List<ProjectOwnerRoleBO> queryOwnerRoleList(Long projectId) {
        ProjectRoleQueryBO projectRoleQueryBO = new ProjectRoleQueryBO();
        projectRoleQueryBO.setProjectId(projectId);
        return projectUserService.queryProjectUser(projectRoleQueryBO);
    }

    @Override
    public BasePage<Project> iParticipateProjectList(ProjectQueryBO projectQueryBO) {
        //查询我参加的项目
        List<Long> projectIds = projectUserService.lambdaQuery().eq(ProjectUser::getUserId, UserUtil.getUserId()).list().stream().map(m -> m.getProjectId()).distinct().collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(projectIds)) {
            LambdaQueryWrapper<Project> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.in(Project::getProjectId, projectIds).like(StrUtil.isNotBlank(projectQueryBO.getName()), Project::getName, projectQueryBO.getName());
            lambdaQueryWrapper.eq(Project::getStatus, 1);
            BasePage<Project> projectList = this.page(projectQueryBO.parse(), lambdaQueryWrapper);
            //根据项目id获取项目统计参数
            List<ProjectCountBO> countBOS = this.getBaseMapper().getProjectCount(projectIds, UserUtil.getUserId());
            Map<Long, ProjectCountBO> countBOMap = countBOS.stream().collect(Collectors.toMap(ProjectCountBO::getProjectId, Function.identity()));
            List<Project> list = projectList.getList().stream().map(p -> {
                Project vo = new Project();
                p.setUserProjectAuth(projectUserService.getProjectAuth(p.getProjectId()));
               // p.setProjectOwnerRoleList(queryOwnerRoleList(p.getProjectId()));
                p.setProjectAdminList(this.projectUserService.queryProjectAdminUser(p.getProjectId()));
                if (ObjectUtil.isNotEmpty(countBOMap) && countBOMap.containsKey(p.getProjectId())) {
                    ProjectCountBO countBO = countBOMap.get(p.getProjectId());
                    if (ObjectUtil.isNotEmpty(countBO)) {
                        p.setCollectStatus(countBO.getCollCount() == 0 ? 1 : 2);
                        p.setNotStartCount(countBO.getTaskOneCount());
                        p.setUnderwayCount(countBO.getTaskTwoCount());
                        p.setFinishedCount(countBO.getTaskThreeCount());
                    }
                }
                BeanUtil.copyProperties(p, vo);
                return vo;
            }).collect(Collectors.toList());
            //获取当前分组信息
            ProjectGroup projectGroup = projectGroupService.getById(projectQueryBO.getGroupId());
            //兼容数据老方式查询
            Boolean all = (ObjectUtil.isNotEmpty(projectGroup) && ObjectUtil.isNotEmpty(projectGroup.getType()) && GroupTypeEnum.ALL.getType().equals(projectGroup.getType())) || ObjectUtil.isEmpty(projectQueryBO.getGroupId());
            Boolean none = (ObjectUtil.isNotEmpty(projectGroup) && ObjectUtil.isNotEmpty(projectGroup.getType()) && GroupTypeEnum.NONE.getType().equals(projectGroup.getType())) || (ObjectUtil.isNotEmpty(projectQueryBO.getGroupId()) && projectQueryBO.getGroupId() == 0);

            if (all) {
                //查询全部项目

            } else if (none) {
                //查询未分组的数据
                if (CollectionUtil.isNotEmpty(list)) {
                    //查询未分组的数据
                    List<Long> groupProjects = projectGroupManagementService.lambdaQuery().list().stream().map(p -> p.getProjectId()).collect(Collectors.toList());

                    if (ObjectUtil.isNotEmpty(groupProjects)) {
                        list = list.stream().filter(f -> !groupProjects.contains(f.getProjectId())).distinct().collect(Collectors.toList());
                    }
                }

            } else {
                //查询当前分组项目
                if (CollectionUtil.isNotEmpty(list) && ObjectUtil.isNotEmpty(projectQueryBO.getGroupId()) && projectQueryBO.getGroupId() != 0) {
                    List<Long> groupProjects = projectGroupManagementService.lambdaQuery().eq(ProjectGroupManagement::getGroupId, projectQueryBO.getGroupId()).list().stream().map(p -> p.getProjectId()).collect(Collectors.toList());
                    if (CollectionUtil.isNotEmpty(groupProjects)) {
                        list = list.stream().filter(p -> groupProjects.contains(p.getProjectId())).collect(Collectors.toList());
                    } else {
                        BasePage<Project> page = new BasePage<>(projectList.getCurrent(), projectList.getSize(), projectList.getTotal());
                        page.setList(null);
                        return page;
                    }
                }
            }

            if (ObjectUtil.equal(projectQueryBO.getSortType(), 1)) {
                list = list.stream().sorted(Comparator.comparing(Project::getAccessTime, Comparator.nullsLast(LocalDateTime::compareTo)).reversed()).collect(Collectors.toList());
            } else {
                list = list.stream().sorted(Comparator.comparing(Project::getCreateTime, Comparator.nullsLast(LocalDateTime::compareTo))).collect(Collectors.toList());
            }
            BasePage<Project> page = new BasePage<>(projectList.getCurrent(), projectList.getSize(), projectList.getTotal());
            page.setList(list);
            return page;
        } else {
            return new BasePage<>();
        }
    }

    @Override
    public BasePage<Project> iManagementProjectList(ProjectQueryBO projectQueryBO) {
        //查询管理员ID
        List<AdminRole> adminRoles = projectRoleService.queryProjectRoleByTypes(Arrays.asList(1));
        List<Long> ids = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(adminRoles)) {
            ids = adminRoles.stream().map(m -> m.getRoleId()).collect(Collectors.toList());
        }
        //查询我作为管理员参加的项目
        List<Long> projectIds = projectUserService.lambdaQuery().eq(ProjectUser::getUserId, UserUtil.getUserId()).in(ProjectUser::getRoleId, ids).list().stream().map(m -> m.getProjectId()).distinct().collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(projectIds)) {
            //根据项目id获取项目统计参数
            List<ProjectCountBO> countBOS = this.getBaseMapper().getProjectCount(projectIds, UserUtil.getUserId());
            Map<Long, ProjectCountBO> countBOMap = countBOS.stream().collect(Collectors.toMap(ProjectCountBO::getProjectId, Function.identity()));
            LambdaQueryWrapper<Project> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.in(Project::getProjectId, projectIds).like(StrUtil.isNotBlank(projectQueryBO.getName()), Project::getName, projectQueryBO.getName());
            lambdaQueryWrapper.eq(Project::getStatus, 1);
            BasePage<Project> projectList = this.page(projectQueryBO.parse(), lambdaQueryWrapper);
            List<Project> list = projectList.getList().stream().map(p -> {
                Project vo = new Project();
                p.setUserProjectAuth(projectUserService.getProjectAuth(p.getProjectId()));
               // p.setProjectOwnerRoleList(queryOwnerRoleList(p.getProjectId()));
                p.setProjectAdminList(this.projectUserService.queryProjectAdminUser(p.getProjectId()));

                if (ObjectUtil.isNotEmpty(countBOMap) && countBOMap.containsKey(p.getProjectId())) {
                    ProjectCountBO countBO = countBOMap.get(p.getProjectId());
                    if (ObjectUtil.isNotEmpty(countBO)) {
                        p.setCollectStatus(countBO.getCollCount() == 0 ? 1 : 2);
                        p.setNotStartCount(countBO.getTaskOneCount());
                        p.setUnderwayCount(countBO.getTaskTwoCount());
                        p.setFinishedCount(countBO.getTaskThreeCount());
                    }
                }

                BeanUtil.copyProperties(p, vo);
                return vo;
            }).collect(Collectors.toList());
            if (ObjectUtil.equal(projectQueryBO.getSortType(), 1)) {
                list = list.stream().sorted(Comparator.comparing(Project::getAccessTime, Comparator.nullsLast(LocalDateTime::compareTo))).collect(Collectors.toList());
            } else {
                list = list.stream().sorted(Comparator.comparing(Project::getCreateTime, Comparator.nullsLast(LocalDateTime::compareTo))).collect(Collectors.toList());
            }
            BasePage<Project> page = new BasePage<>(projectList.getCurrent(), projectList.getSize(), projectList.getTotal());
            page.setList(list);
            return page;
        } else {
            return new BasePage<>();
        }
    }

    @Override
    public BasePage<Project> allProjectList(ProjectQueryBO projectQueryBO) {
        //查询公司所有的可看到项目
        BasePage<Project> projectList = new BasePage<>();
        LambdaQueryWrapper<Project> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(StrUtil.isNotBlank(projectQueryBO.getName()), Project::getName, projectQueryBO.getName());
        lambdaQueryWrapper.eq(Project::getStatus, 1);
        if (UserUtil.isAdmin()) {
            projectList = this.page(projectQueryBO.parse(), lambdaQueryWrapper);
        } else {
            List<Long> projectIds = projectUserService.lambdaQuery().eq(ProjectUser::getUserId, UserUtil.getUserId()).list().stream().map(m -> m.getProjectId()).distinct().collect(Collectors.toList());

            lambdaQueryWrapper.and(wrapper -> {
                wrapper.or(orWrapper -> {
                    orWrapper.eq(Project::getIsOpen, 1);
                });
                if (CollectionUtil.isNotEmpty(projectIds)) {
                    wrapper.or(orWrapper -> {
                        orWrapper.in(Project::getProjectId, projectIds);
                    });
                }


            });
            projectList = this.page(projectQueryBO.parse(), lambdaQueryWrapper);
        }
        if (CollectionUtil.isNotEmpty(projectList.getList())) {
            List<Long> projectIds = projectList.getList().stream().map(Project::getProjectId).collect(Collectors.toList());
            //根据项目id获取项目统计参数
            List<ProjectCountBO> countBOS = this.getBaseMapper().getProjectCount(projectIds, UserUtil.getUserId());
            Map<Long, ProjectCountBO> countBOMap = countBOS.stream().collect(Collectors.toMap(ProjectCountBO::getProjectId, Function.identity()));
            List<Project> list = projectList.getList().stream().map(p -> {
                Project vo = new Project();
                p.setUserProjectAuth(projectUserService.getProjectAuth(p.getProjectId()));
              //  p.setProjectOwnerRoleList(queryOwnerRoleList(p.getProjectId()));
                p.setProjectAdminList(this.projectUserService.queryProjectAdminUser(p.getProjectId()));

                if (ObjectUtil.isNotEmpty(countBOMap) && countBOMap.containsKey(p.getProjectId())) {
                    ProjectCountBO countBO = countBOMap.get(p.getProjectId());
                    if (ObjectUtil.isNotEmpty(countBO)) {
                        p.setCollectStatus(countBO.getCollCount() == 0 ? 1 : 2);
                        p.setNotStartCount(countBO.getTaskOneCount());
                        p.setUnderwayCount(countBO.getTaskTwoCount());
                        p.setFinishedCount(countBO.getTaskThreeCount());
                    }
                }
                BeanUtil.copyProperties(p, vo);
                return vo;
            }).collect(Collectors.toList());
            if (ObjectUtil.equal(projectQueryBO.getSortType(), 1)) {
                list = list.stream().sorted(Comparator.comparing(Project::getAccessTime, Comparator.nullsLast(LocalDateTime::compareTo))).collect(Collectors.toList());
            } else {
                list = list.stream().sorted(Comparator.comparing(Project::getCreateTime, Comparator.nullsLast(LocalDateTime::compareTo))).collect(Collectors.toList());
            }
            BasePage<Project> page = new BasePage<>(projectList.getCurrent(), projectList.getSize(), projectList.getTotal());
            page.setList(list);
            return page;
        } else {
            return projectList;
        }
    }

    @Override
    public BasePage<Project> archiveProjectList(ProjectQueryBO projectQueryBO) {
        //查询管理员ID
        List<AdminRole> adminRoles = projectRoleService.queryProjectRoleByTypes(Arrays.asList(1));
        if (ObjectUtil.isNotEmpty(adminRoles)) {
            List<Long> ids = adminRoles.stream().map(m -> m.getRoleId()).collect(Collectors.toList());
            //查询我作为管理员参加的项目
            List<Long> projectId = projectUserService.lambdaQuery().eq(ProjectUser::getUserId, UserUtil.getUserId()).in(ProjectUser::getRoleId, ids).list().stream().map(m -> m.getProjectId()).distinct().collect(Collectors.toList());
            if (ObjectUtil.isNotEmpty(projectId)) {
                LambdaQueryWrapper<Project> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper.in(Project::getProjectId, projectId).eq(StrUtil.isNotBlank(projectQueryBO.getName()), Project::getName, projectQueryBO.getName()).eq(Project::getStatus, projectQueryBO.getSetType());
                BasePage<Project> projectList = this.page(projectQueryBO.parse(), lambdaQueryWrapper);
                return projectList;
            }
        }
        return new BasePage<>();
    }

    @Override
    public Project updateProject(ProjectVo projectVo) {
        Project project = new Project();
        BeanUtil.copyProperties(projectVo, project);
        updateById(project);
        //项目归档删除收藏项目
        if (ObjectUtil.isNotEmpty(projectVo.getStatus()) && 2 == projectVo.getStatus()) {
            projectCollectService.lambdaUpdate().eq(ProjectCollect::getProjectId, projectVo.getProjectId()).remove();
        }
        return project;
    }

    @Override
    public ProjectOwnerRoleBO queryOpenAuthEdit(@NotNull Long projectId) {
        List<ProjectOwnerRoleBO> ownerRoleBOS = queryOwnerRoleList(projectId);
        if (CollectionUtil.isNotEmpty(ownerRoleBOS)) {
            ownerRoleBOS = ownerRoleBOS.stream().filter(o -> ObjectUtil.equal(o.getUserId(), UserUtil.getUserId())).collect(Collectors.toList());
        }
        return ownerRoleBOS.get(0);
    }

    @Override
    public Project getProjectById(Long projectId, Long taskId) {
        Project project = getById(projectId);
        if (ObjectUtil.isEmpty(project)) {
            //删除项目任务
            this.deleteProject(projectId);
            throw new BusinessException(ProjectCodeEnum.PROJECT_EXIST_ERROR);
        }
        //查询项目成员
        ProjectRoleQueryBO projectRoleQueryBO = new ProjectRoleQueryBO();
        projectRoleQueryBO.setProjectId(projectId);
        projectRoleQueryBO.setTaskId(taskId);
        project.setProjectOwnerRoleList(projectUserService.queryProjectUser(projectRoleQueryBO));
        //更新项目访问时间
        project.setAccessTime(LocalDateTimeUtil.now());
        updateById(project);
        //查询项目下所有的任务
        List<ProjectTask> projectTasks = projectTaskService.lambdaQuery().eq(ProjectTask::getProjectId, project.getProjectId()).in(ProjectTask::getType, Arrays.asList(2, 3, 4)).list();
        if (CollectionUtil.isNotEmpty(projectTasks)) {
            Integer completed = projectTasks.stream().filter(f -> 3 == f.getStatus()).collect(Collectors.toList()).size();
            Double num = (double) completed * 100 / projectTasks.size();
            DecimalFormat df = new DecimalFormat("0.00");
            project.setTheProgress(df.format(num));
            //增加事项团队成员
            projectTasks.stream().forEach(p -> {
                if (ObjectUtil.isEmpty(p.getOwnerUserId())) {
                    p.setOwnerUserId(p.getCreateUserId().toString());
                }
                Set<Long> userIds = SeparatorUtil.toLongSet(p.getOwnerUserId());
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
                p.setTaskOwnerBOS(TaskOwnerBOS);
            });
        } else {
            project.setTheProgress("0.00");
        }
        return project;
    }

    @Override
    public void deleteProject(Long projectId) {
        removeById(projectId);
        //删除收藏记录
        projectCollectService.lambdaUpdate().eq(ProjectCollect::getProjectId, projectId).remove();
        //删除任务
        projectTaskService.lambdaUpdate().eq(ProjectTask::getProjectId, projectId).remove();
        //删除旧的团队成员
        AdminProjectRole adminProjectRole = new AdminProjectRole();
        adminProjectRole.setProjectId(projectId);
        adminProjectRole.setUserId(UserUtil.getUserId());
        try {
            projectUserService.deleteProjectRoles(adminProjectRole);
        } catch (Exception e) {

        }
    }

    @Override
    public BasePage<Project> queryProjectList(ProjectQueryBO projectQueryBO) {
        LambdaQueryWrapper<Project> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(StrUtil.isNotBlank(projectQueryBO.getName()), Project::getName, projectQueryBO.getName());
        lambdaQueryWrapper.eq(Project::getStatus, 1);
        BasePage<Project> projectList = this.page(projectQueryBO.parse(), lambdaQueryWrapper);
        List<Project> list = projectList.getList().stream().map(p -> {
            Project vo = new Project();
            p.setProjectOwnerRoleList(queryOwnerRoleList(p.getProjectId()));
            BeanUtil.copyProperties(p, vo);
            return vo;
        }).collect(Collectors.toList());
        BasePage<Project> page = new BasePage<>(projectList.getCurrent(), projectList.getSize(), projectList.getTotal());
        page.setList(list);
        return page;
    }


    @Override
    public void archiveProject(Long projectId, Integer setType) {
        Project project = null;
        if (setType == 2) {
            //项目归档删除收藏项目
            project = new Project().setStatus(2).setArchiveTime(LocalDateTimeUtil.now());
            projectCollectService.lambdaUpdate().eq(ProjectCollect::getProjectId, projectId).remove();
            update(project, new QueryWrapper<Project>().eq("project_id", projectId));
            //删除分组信息
            projectGroupManagementService.lambdaUpdate().eq(ProjectGroupManagement::getProjectId, projectId).remove();
            // 删除归档项目通知
//            String content = UserUtil.getUser().getRealname().concat(" 归档了项目 ").concat(project.getName());
//            List<Long> adminIds = projectAuthUtil.projectAdminUser(projectId);
//            for (Long id : adminIds) {
//                if (id.equals(UserUtil.getUserId())) {
//                    continue;
//                }
//                projectAuthUtil.sendMessage(UserUtil.getUserId(), id, project.getProjectId(), AdminMessageEnum.PROJECT_CREATE_NOTICE.getType(), "项目管理通知", content);
//            }
//            //通知自己
//            String content1 = UserUtil.getUser().getRealname().concat(" 你归档了项目 ").concat(project.getName());
//            projectAuthUtil.sendMessage(UserUtil.getUserId(), UserUtil.getUserId(), project.getProjectId(), AdminMessageEnum.PROJECT_CREATE_NOTICE.getType(), "项目管理通知", content1);
        } else if (setType == 3) {
            project = new Project().setStatus(3).setDeleteTime(LocalDateTimeUtil.now());
            projectCollectService.lambdaUpdate().eq(ProjectCollect::getProjectId, projectId).remove();
            update(project, new QueryWrapper<Project>().eq("project_id", projectId));
            //删除分组信息
            projectGroupManagementService.lambdaUpdate().eq(ProjectGroupManagement::getProjectId, projectId).remove();
            //删除删除项目通知
//            String content = UserUtil.getUser().getRealname().concat(" 删除了项目 ").concat(project.getName());
//            List<Long> adminIds = projectAuthUtil.projectAdminUser(projectId);
//            for (Long id : adminIds) {
//                if (id.equals(UserUtil.getUserId())) {
//                    continue;
//                }
//                projectAuthUtil.sendMessage(UserUtil.getUserId(), id, project.getProjectId(), AdminMessageEnum.PROJECT_CREATE_NOTICE.getType(), "项目管理通知", content);
//            }
//            //通知自己
//            String content1 = UserUtil.getUser().getRealname().concat(" 你删除了项目 ").concat(project.getName());
//            projectAuthUtil.sendMessage(UserUtil.getUserId(), UserUtil.getUserId(), project.getProjectId(), AdminMessageEnum.PROJECT_CREATE_NOTICE.getType(), "项目管理通知", content1);
        } else {
            project = new Project().setStatus(1).setArchiveTime(null).setDeleteTime(null);
        }
        update(project, new QueryWrapper<Project>().eq("project_id", projectId));

    }

    @Override
    public BasePage<Project> myProjectList(ProjectQueryBO projectQueryBO) {
        //查询公司所有的可看到项目
        BasePage<Project> projectList = new BasePage<>();
        LambdaQueryWrapper<Project> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Project::getStatus, 1);
        if (UserUtil.isAdmin()) {
            projectList = this.page(projectQueryBO.parse(), lambdaQueryWrapper);
        } else {
            List<Long> projectIds = projectUserService.lambdaQuery().eq(ProjectUser::getUserId, UserUtil.getUserId()).list().stream().map(m -> m.getProjectId()).distinct().collect(Collectors.toList());
            lambdaQueryWrapper.and(wrapper -> {
                wrapper.or(orWrapper -> {
                    orWrapper.eq(Project::getIsOpen, 1);
                });
                wrapper.or(orWrapper -> {
                    orWrapper.in(Project::getProjectId, projectIds);
                });

            });
            projectList = this.page(projectQueryBO.parse(), lambdaQueryWrapper);
        }

        if (ObjectUtil.equal(projectQueryBO.getSortType(), 1)) {

            projectList.setList(projectList.getList().stream().sorted(Comparator.comparing(Project::getAccessTime, Comparator.nullsLast(LocalDateTime::compareTo))).collect(Collectors.toList()));
        } else {
            projectList.setList(projectList.getList().stream().sorted(Comparator.comparing(Project::getCreateTime, Comparator.nullsLast(LocalDateTime::compareTo))).collect(Collectors.toList()));
        }

        return projectList;
    }

    @Override
    public void initEventStatus(Long projectId,Long eventId){
        //初始化事件状态
        List<ProjectStatus> projectStatuses = projectStatusService.lambdaQuery().eq(ProjectStatus::getSysType, 1).list();
        if(CollectionUtil.isEmpty(projectStatuses)){
            return;
        }
        Map<Integer, ProjectStatus> projectStatusMap = projectStatuses.stream().collect(Collectors.toMap(ProjectStatus::getStatusType, Function.identity(), (v1, v2) -> v2));
        List<ProjectEventStatus> projectEventStatuses = new ArrayList<>();
        projectStatusMap.forEach((key,value) -> {
            ProjectEventStatus projectEventStatus  = BeanUtil.copyProperties(value, ProjectEventStatus.class);
            projectEventStatus.setProjectEventId(eventId);
            projectEventStatus.setUseStatus(1);
            projectEventStatus.setInitStatus(0);
            projectEventStatus.setProjectId(projectId);
            if (ObjectUtil.isNotEmpty(InitStatusTypeEnum.enumByDesc(projectEventStatus.getStatusName()))) {
                projectEventStatus.setInitStatus(1);
            }
            projectEventStatuses.add(projectEventStatus);
        });
        projectEventStatusService.saveBatch(projectEventStatuses);
    }

}
