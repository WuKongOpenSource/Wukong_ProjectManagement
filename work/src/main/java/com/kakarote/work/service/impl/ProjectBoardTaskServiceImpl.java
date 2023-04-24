package com.kakarote.work.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.kakarote.common.entity.UserInfo;
import com.kakarote.ids.provider.utils.UserCacheUtil;
import com.kakarote.work.common.project.ProjectAuthUtil;
import com.kakarote.work.common.project.ProjectUtil;
import com.kakarote.work.entity.BO.ProjectBoardTaskBO;
import com.kakarote.work.entity.PO.*;
import com.kakarote.work.entity.VO.ProjectBoardStatusVO;
import com.kakarote.work.entity.VO.ProjectBoardVO;
import com.kakarote.work.entity.VO.ProjectTaskVO;
import com.kakarote.work.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProjectBoardTaskServiceImpl implements IProjectBoardTaskService {

    @Autowired
    private IProjectSchemeRelationBoardService srb;
    @Autowired
    private IProjectBoardStatusService bs;
    @Autowired
    private IProjectTaskService taskService;
    @Autowired
    private IProjectService projectService;
    @Autowired
    private IProjectSchemeRelationService projectSchemeRelationService;
    @Autowired
    private IProjectEventStatusService eventStatusService;
    @Autowired
    private IProjectTaskLogService projectTaskLogService;
    @Autowired
    private ProjectAuthUtil projectAuthUtil;
    @Autowired
    private IProjectTaskService projectTaskService;

    @Override
    public List<ProjectBoardVO> queryBoardTaskList(ProjectBoardTaskBO boardTaskBO) {
        List<ProjectBoardVO> boardVOS = new ArrayList<>();

        Project project = projectService.getById(boardTaskBO.getProjectId());
        ProjectSchemeRelation projectSchemeRelation = projectSchemeRelationService.queryEventId(project.getSchemeId(), boardTaskBO.getTaskType());

        List<ProjectSchemeRelationBoard> boardRelationList = srb.lambdaQuery().
                eq(ProjectSchemeRelationBoard::getSchemeRelationId, projectSchemeRelation.getId()).
                eq(ProjectSchemeRelationBoard::getProjectId,project.getProjectId()).list();
        List<ProjectTask> projectTasks = taskService.lambdaQuery().eq(ProjectTask::getProjectId, boardTaskBO.getProjectId()).ne(ProjectTask::getType, 1).isNull(ProjectTask::getPid)
                .and(StrUtil.isNotBlank(boardTaskBO.getSearch()),wrapper->{
                    wrapper.or(newWrapper->{
                        newWrapper.like(ProjectTask::getName, boardTaskBO.getSearch());
                    });
                    wrapper.or(newWrapper->{
                        newWrapper.like(ProjectTask::getNum, boardTaskBO.getSearch());
                    });
                })

                //高级筛选
                .in(ObjectUtil.isNotEmpty(boardTaskBO.getTypeQuery()) && boardTaskBO.getTypeQuery().size() > 0, ProjectTask::getType, boardTaskBO.getTypeQuery())
                .in(ObjectUtil.isNotEmpty(boardTaskBO.getPriorityQuery()) && boardTaskBO.getPriorityQuery().size() > 0, ProjectTask::getPriority, boardTaskBO.getPriorityQuery())
                .in(ObjectUtil.isNotEmpty(boardTaskBO.getStatusQuery()) && boardTaskBO.getStatusQuery().size() > 0, ProjectTask::getStatus, boardTaskBO.getStatusQuery())
                .in(ObjectUtil.isNotEmpty(boardTaskBO.getBelongIterationIdQuery()) && boardTaskBO.getBelongIterationIdQuery().size() > 0, ProjectTask::getBelongIterationId, boardTaskBO.getBelongIterationIdQuery())
                .in(ObjectUtil.isNotEmpty(boardTaskBO.getLabelQuery()) && boardTaskBO.getLabelQuery().size() > 0, ProjectTask::getLabel, boardTaskBO.getLabelQuery())
                .in(ObjectUtil.isNotEmpty(boardTaskBO.getMainUserIdQuery()) && boardTaskBO.getMainUserIdQuery().size() > 0, ProjectTask::getMainUserId, boardTaskBO.getMainUserIdQuery())
                .in(ObjectUtil.isNotEmpty(boardTaskBO.getMainUserIdQuery()) && boardTaskBO.getMainUserIdQuery().size() > 0, ProjectTask::getMainUserId, boardTaskBO.getMainUserIdQuery())
                .orderByDesc(ProjectTask::getPriority)
                .orderByAsc(ProjectTask::getNum)
                .list();
        for (ProjectSchemeRelationBoard board : boardRelationList) {
            ProjectBoardVO boardVO = BeanUtil.toBean(board, ProjectBoardVO.class);
            boardVO.setProjectBoardId(board.getId());
            boardVO.setStatusList(new ArrayList<>());
            List<ProjectBoardStatusVO> projectBoardStatusList = bs.queryBoardStatusByBoardId(board.getId());
            projectBoardStatusList.forEach(projectBoardStatusVO -> {
                List<ProjectTaskVO> projectTaskVOS = new ArrayList<>();
                for (ProjectTask projectTask : projectTasks) {
                    if (ObjectUtil.isNotNull(projectTask.getBoardStatusId()) && projectBoardStatusVO.getStatusId().longValue() == projectTask.getBoardStatusId()) {
                        ProjectTaskVO projectTaskVO = BeanUtil.copyProperties(projectTask, ProjectTaskVO.class);
                        projectTaskVO.setBoardStatusName(projectBoardStatusVO.getStatusName());
                        if(ObjectUtil.isNotEmpty(projectTask.getMainUserId())){
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

    @Override
    public void setTaskStatus(Long taskId, Long boardStatusId) {
        ProjectTask projectTask = projectTaskService.lambdaQuery().eq(ProjectTask::getTaskId, taskId).one();
        Integer oldStatusType = getEventStatus(projectTask.getBoardStatusId());
        Integer newStatusType = getEventStatus(boardStatusId);
        taskService.lambdaUpdate().eq(ProjectTask::getTaskId, taskId).set(ProjectTask::getBoardStatusId, boardStatusId).set(ProjectTask::getStatus, newStatusType).update();
        ProjectTaskLog projectTaskLog = new ProjectTaskLog();
        projectTaskLog.setTaskId(taskId);
        projectTaskLog.setType(1);

         String contentLog = ProjectUtil.getLogContent("状态",getStatusDesc(oldStatusType),getStatusDesc(newStatusType));
        projectTaskLog.setContent(contentLog);
        projectTaskLogService.saveTaskLog(projectTaskLog);
        //状态修改状态功能 占时去掉
//        List<Long> adminIds = projectAuthUtil.projectAdminUser(project.getProjectId());
//        for (Long id : adminIds) {
//            if (id.equals(UserUtil.getUserId())) {
//                continue;
//            }
//
//            String content=project.getName()+","+getStatusDesc(newStatusType);
//
//            projectAuthUtil.sendMessage(UserUtil.getUserId(), id, projectTask.getTaskId(), AdminMessageEnum.PROJECT_CREATE_NOTICE.getType(), projectTask.getName(), content);
//        }


        //String content1 = " 你在 " + project.getName().concat(" 项目中修改任务 " + projectTask.getName()).concat(" 状态为 " + status);
     //   String content1=project.getName()+","+status;

//        projectAuthUtil.sendMessage(UserUtil.getUserId(), UserUtil.getUserId(), projectTask.getTaskId(), AdminMessageEnum.PROJECT_CREATE_NOTICE.getType(), projectTask.getName(), content1);
     }

     public Integer getEventStatus(Long boardStatusId){
         ProjectEventStatus projectEventStatus = eventStatusService.queryEventStatusById(boardStatusId);
         if(ObjectUtil.isNotEmpty(projectEventStatus) && ObjectUtil.isNotEmpty(projectEventStatus.getStatusType())){
             return projectEventStatus.getStatusType();
         }
         return 0;
     }


     public String getStatusDesc(Integer statusType){
         String status = "";
         if (1 == statusType) {
             status = "未完成";
         } else if (2 == statusType) {
             status = "进行中";
         } else {
             status = "已完成";
         }
         return status;
     }

}
