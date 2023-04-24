package com.kakarote.work.service;


import com.kakarote.common.result.BasePage;


import com.kakarote.common.servlet.BaseService;
import com.kakarote.work.common.project.ProjectOwnerRoleBO;
import com.kakarote.work.entity.BO.ProjectQueryBO;
import com.kakarote.work.entity.BO.ProjectVo;
import com.kakarote.work.entity.PO.Project;
import org.springframework.web.bind.annotation.PathVariable;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * <p>
 * 项目表 服务类
 * </p>
 *
 * @author bai
 * @since 2022-09-08
 */
public interface IProjectService extends BaseService<Project> {

    public Project addProject(Project project);

    public List<ProjectOwnerRoleBO> queryOwnerRoleList(Long projectId);

    public BasePage<Project> iParticipateProjectList(ProjectQueryBO projectQueryBO);

    public BasePage<Project> iManagementProjectList(ProjectQueryBO projectQueryBO);

    public BasePage<Project> allProjectList(ProjectQueryBO projectQueryBO);

    public Project updateProject(ProjectVo projectVo);

    public ProjectOwnerRoleBO queryOpenAuthEdit(@PathVariable @NotNull Long projectId);

    public Project getProjectById(Long projectId,Long taskId);

    public void deleteProject(Long projectId);

    public BasePage<Project> queryProjectList(ProjectQueryBO projectQueryBO);

    public void archiveProject(Long projectId, Integer setType);

    public BasePage<Project> archiveProjectList(ProjectQueryBO projectQueryBO);

    public BasePage<Project> myProjectList(ProjectQueryBO projectQueryBO);

    public void initEventStatus(Long projectId,Long eventId);


}
