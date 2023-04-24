package com.kakarote.work.service;


import com.kakarote.common.servlet.BaseService;
import com.kakarote.work.entity.PO.Project;
import com.kakarote.work.entity.PO.ProjectGroupManagement;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * <p>
 * 项目分组管理表 服务类
 * </p>
 *
 * @author bai
 * @since 2022-09-17
 */
public interface IProjectGroupManagementService extends BaseService<ProjectGroupManagement> {


    public void moveToGroup(ProjectGroupManagement projectGroupManagement);

    public void removeToGroup(Long groupId, Long projectId);

    public List<Project> searchProjectGroupList(@RequestParam Long groupId);
}
