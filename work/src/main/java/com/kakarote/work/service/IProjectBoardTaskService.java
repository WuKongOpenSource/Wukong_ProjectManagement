package com.kakarote.work.service;

import com.kakarote.work.entity.BO.ProjectBoardTaskBO;
import com.kakarote.work.entity.VO.ProjectBoardVO;

import java.util.List;

/**
 * <p>
 * 项目管理 看板信息 服务类
 * </p>
 *
 * @author guole
 * @since 2022-09-22
 */
public interface IProjectBoardTaskService {

    List<ProjectBoardVO> queryBoardTaskList(ProjectBoardTaskBO boardTaskBO);
    void  setTaskStatus(Long taskId,Long statusId);

}
