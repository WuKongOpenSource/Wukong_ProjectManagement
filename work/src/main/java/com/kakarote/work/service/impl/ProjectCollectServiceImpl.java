package com.kakarote.work.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.kakarote.common.result.BasePage;
import com.kakarote.common.result.PageEntity;
import com.kakarote.common.result.PageEntity;
import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.work.entity.BO.ProjectQueryBO;
import com.kakarote.work.entity.BO.ProjectTaskCountBO;
import com.kakarote.work.entity.PO.Project;
import com.kakarote.work.entity.PO.ProjectCollect;
import com.kakarote.work.mapper.ProjectCollectMapper;
import com.kakarote.work.service.IProjectCollectService;
import com.kakarote.work.service.IProjectService;
import com.kakarote.work.service.IProjectTaskService;
import com.kakarote.work.service.IProjectUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 项目收藏表 服务实现类
 * </p>
 *
 * @author bai
 * @since 2022-09-08
 */
@Service
public class ProjectCollectServiceImpl extends BaseServiceImpl<ProjectCollectMapper, ProjectCollect> implements IProjectCollectService {

    @Autowired
    private IProjectService projectService;
    @Autowired
    private IProjectTaskService projectTaskService;
    @Autowired
    private IProjectUserService projectUserService;


    @Override
    public void collect(Long projectId) {
        Long userId = UserUtil.getUserId();
        Long count = count(new QueryWrapper<ProjectCollect>().eq("user_id", userId).eq("project_id", projectId));
        if (count == 0) {
            ProjectCollect projectCollect = new ProjectCollect();
            projectCollect.setProjectId(projectId);
            projectCollect.setUserId(userId);
            save(projectCollect);
        } else {
            remove(new QueryWrapper<ProjectCollect>().eq("user_id", userId).eq("project_id", projectId));
        }
    }

    @Override
    public List<ProjectCollect> queryCollectByProjectId(Long projectId) {
        Long userId = UserUtil.getUserId();
        return lambdaQuery().eq(ProjectCollect::getProjectId, projectId).eq(ProjectCollect::getCreateUserId, userId).list();
    }


    @Override
    public BasePage<Project> myCollectByProjectList(ProjectQueryBO projectQueryBO) {
        //查询我作为管理员参加的项目
        List<ProjectCollect> collects = lambdaQuery().eq(ProjectCollect::getUserId, UserUtil.getUserId()).list();
        if (collects.size() > 0) {
            List<Long> ids = collects.stream().map(ProjectCollect::getProjectId).collect(Collectors.toList());
            LambdaQueryWrapper<Project> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.in(Project::getProjectId, ids).eq(StrUtil.isNotBlank(projectQueryBO.getName()), Project::getName, projectQueryBO.getName());
            BasePage<Project> projectList = projectService.page(projectQueryBO.parse(), lambdaQueryWrapper);
            List<Project> list = projectList.getList().stream().map(p -> {
                Project project = new Project();
                ProjectTaskCountBO projectTaskCountBO = new ProjectTaskCountBO();
                projectTaskCountBO.setProjectId(p.getProjectId());
                p.setUserProjectAuth(projectUserService.getProjectAuth(p.getProjectId()));
                p.setProjectTaskCountVO(projectTaskService.getProjectByTime(projectTaskCountBO));
                BeanUtil.copyProperties(p,project);
                return project;
            }).collect(Collectors.toList());
            BasePage<Project> page = new BasePage<>(projectList.getCurrent(), projectList.getSize(), projectList.getTotal());
            page.setList(list);
            return projectList;
        }
        return null;
    }
}
