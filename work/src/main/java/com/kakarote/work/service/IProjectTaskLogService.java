package com.kakarote.work.service;



import com.kakarote.common.servlet.BaseService;
import com.kakarote.work.entity.PO.ProjectTaskLog;
import com.kakarote.work.entity.VO.ProjectTaskLogVO;

import java.util.List;

/**
 * <p>
 * 任务日志表 服务类
 * </p>
 *
 * @author bai
 * @since 2022-09-14
 */
public interface IProjectTaskLogService extends BaseService<ProjectTaskLog> {

    public List<ProjectTaskLogVO> queryTaskLog(Long taskId, Long type);

    public void saveTaskLog(ProjectTaskLog projectTaskLog);
    public void saveTaskLog(Long taskId, String content) ;
}
