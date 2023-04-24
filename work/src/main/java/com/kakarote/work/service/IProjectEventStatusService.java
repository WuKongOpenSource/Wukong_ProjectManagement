package com.kakarote.work.service;


import com.kakarote.common.servlet.BaseService;
import com.kakarote.work.entity.BO.ProjectTransferStatusBO;
import com.kakarote.work.entity.PO.ProjectEventStatus;

import java.util.List;

/**
 * <p>
 * 事件属性表 服务类
 * </p>
 *
 * @author bai
 * @since 2022-09-19
 */
public interface IProjectEventStatusService extends BaseService<ProjectEventStatus> {

    public void add(ProjectEventStatus projectEventStatus);

    public void update(ProjectEventStatus projectEventStatus);

    public void delete(Long id);

    List<ProjectEventStatus> queryEventStatusByEventId(Long eventId);

    List<ProjectEventStatus> queryEventStatusByEventId(Long eventId,Integer sysType,Long projectId);

    ProjectEventStatus queryEventStatusById(Long id);

    List<ProjectEventStatus> queryEventStatusByEventIdAndUseStatus(Long eventId, Integer userStatus);

    List<ProjectEventStatus> notUserStatusList(Long eventId);

    List<ProjectEventStatus> notAddStatus(Long eventId, Long schemeRelationId,Long projectId);

    List<ProjectEventStatus> applicationSchemeStatusList(Long projectId,Long eventId);

     void transferStatus(ProjectTransferStatusBO projectTransferStatusBO) ;
    void updateInitStatus(Long eventId,Long eventStatusId);
    void updateSorting(List<Long> ids);
    ProjectEventStatus queryEventStatusByStatusName(String statusName, Integer eventId,Long projectId);
    /**
     * 功能描述: 查询任务初始状态
     * 〈〉
     * @Param:
     * @Return:
     * @Author: guole
     * @Date: 2023/2/24 17:05
     */
    ProjectEventStatus queryInitEventStatusByTaskType(Integer taskType,Long projectId);


}
