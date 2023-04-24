package com.kakarote.work.mapper;

import com.kakarote.common.servlet.BaseMapper;
import com.kakarote.work.entity.PO.ProjectEventStatus;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 事件属性表 Mapper 接口
 * </p>
 *
 * @author bai
 * @since 2022-09-19
 */
public interface ProjectEventStatusMapper extends BaseMapper<ProjectEventStatus> {
    List<ProjectEventStatus> queryEventStatusByEventId(Long eventId);

    List<ProjectEventStatus> queryEventStatusByEventIdAndSysType(@Param("eventId") Long eventId,@Param("sysType") Integer sysType,@Param("projectId") Long projectId);

    List<ProjectEventStatus> queryEventStatusByEventIdAndUseStatus(@Param("eventId") Long eventId, @Param("userStatus") Integer userStatus);

    List<ProjectEventStatus> notAddStatus(@Param("eventId") Long eventId, @Param("schemeRelationId") Long schemeRelationId,@Param("projectId") Long projectId);

    ProjectEventStatus queryEventStatusById(@Param("id") Long id);

    List<ProjectEventStatus> applicationSchemeStatusList(@Param("eventId") Long eventId);

    List<ProjectEventStatus> applicationSchemeProjectStatusList(@Param("projectId") Long projectId, @Param("eventId") Long eventId);

    ProjectEventStatus queryEventStatusByStatusName(String statusName, Integer eventId,Long projectId);
}
