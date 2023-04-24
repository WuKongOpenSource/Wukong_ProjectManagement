package com.kakarote.work.mapper;


import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.kakarote.common.result.BasePage;
import com.kakarote.common.servlet.BaseMapper;
import com.kakarote.work.common.project.ProjectUserTaskQueryBO;
import com.kakarote.work.entity.BO.ProjectTaskQueryBO;
import com.kakarote.work.entity.PO.ProjectTask;
import com.kakarote.work.entity.VO.ProjectTaskBurnoutVO;
import com.kakarote.work.entity.VO.ProjectTaskNumVO;
import com.kakarote.work.entity.VO.ProjectUserTaskCountVO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 任务表 Mapper 接口
 * </p>
 *
 * @author bai
 * @since 2022-09-08
 */
public interface ProjectTaskMapper extends BaseMapper<ProjectTask> {

    List<ProjectTaskNumVO> getProjectByTime(@Param("projectId") Long projectId, @Param("startTime") Date strTime, @Param("endTime") Date endTime);


     List<ProjectTaskNumVO> getTaskByTime(@Param("taskIds") List<Long> taskIds, @Param("startTime") Date strTime, @Param("endTime") Date endTime);

     List<ProjectTaskNumVO> getTaskByTimeNoStart(@Param("taskIds") List<Long> taskIds, @Param("startTime") Date strTime, @Param("endTime") Date endTime);

     List<ProjectTaskNumVO> getTaskByTimeRun(@Param("taskIds") List<Long> taskIds, @Param("startTime") Date strTime, @Param("endTime") Date endTime);

    @InterceptorIgnore(tenantLine = "1")
    List<ProjectTaskBurnoutVO> getTaskBurnout(@Param("taskIds") List<Long> taskIds, @Param("startTime") Date strTime, @Param("endTime") Date endTime);
    List<Map<String, Object>> projectTaskExport(@Param("taskIds") List<Long> taskIds);

    Integer getMaxNum(@Param("projectId") Long projectId);

    List<ProjectTask>  queryAddTaskPageWithUserSort(@Param("taskQuery")  ProjectTaskQueryBO projectTaskQueryBO,@Param("userId") Long userId);

    Long  queryMaxTaskPageWithUserSort(@Param("taskQuery")  ProjectTaskQueryBO projectTaskQueryBO,@Param("userId") Long userId);

    BasePage<ProjectTask> queryTaskPageWithUserSort(BasePage<Object> parse, @Param("taskQuery")  ProjectTaskQueryBO projectTaskQueryBO, @Param("userId") Long userId);
    BasePage<ProjectTask>  queryUserTaskList(BasePage<Object> parse,@Param("userTaskQueryBO") ProjectUserTaskQueryBO userTaskQueryBO);

    ProjectUserTaskCountVO queryUserTaskCount(@Param("userTaskQueryBO") ProjectUserTaskQueryBO userTaskQueryBO);

    List<ProjectTask> getTaskExtendInfo(@Param("taskIds") List<Long> taskIds);
}
