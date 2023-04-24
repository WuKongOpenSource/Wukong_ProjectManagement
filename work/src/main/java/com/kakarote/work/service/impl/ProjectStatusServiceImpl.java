package com.kakarote.work.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import com.kakarote.common.result.BasePage;
import com.kakarote.common.result.PageEntity;
import com.kakarote.common.result.PageEntity;
import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.work.entity.BO.ProjectStatusQueryBO;
import com.kakarote.work.entity.PO.ProjectStatus;
import com.kakarote.work.mapper.ProjectStatusMapper;
import com.kakarote.work.service.IProjectStatusService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 项目管理：状态表 服务实现类
 * </p>
 *
 * @author guole
 * @since 2022-09-22
 */
@Service
public class ProjectStatusServiceImpl extends BaseServiceImpl<ProjectStatusMapper, ProjectStatus> implements IProjectStatusService {

    @Override
    public void add(ProjectStatus projectStatus) {
        Long sorting = this.lambdaQuery().count() + 1;
        projectStatus.setSorting(sorting);
        projectStatus.setSysType(2);
        this.baseMapper.insert(projectStatus);
    }

    @Override
    public void updateSorting(List<Integer> ids) {
        for (int i = 0; i < ids.size(); i++) {
            this.lambdaUpdate().eq(ProjectStatus::getProjectStatusId, ids.get(i)).set(ProjectStatus::getSorting, i + 1);
        }
    }

    @Override
    public BasePage<ProjectStatus> queryProjectStatusList(ProjectStatusQueryBO query) {
        LambdaQueryWrapper<ProjectStatus> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(ProjectStatus::getStatusName, query.getSearch());
        lambdaQueryWrapper.orderByAsc(ProjectStatus::getSorting);
        BasePage<ProjectStatus> page = this.page(query.parse(), lambdaQueryWrapper);
        return page;
    }


}
