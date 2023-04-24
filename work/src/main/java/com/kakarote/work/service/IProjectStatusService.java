package com.kakarote.work.service;



import com.kakarote.common.result.BasePage;
import com.kakarote.common.servlet.BaseService;
import com.kakarote.work.entity.BO.ProjectStatusQueryBO;
import com.kakarote.work.entity.PO.ProjectStatus;

import java.util.List;

/**
 * <p>
 * 项目管理：状态表 服务类
 * </p>
 *
 * @author guole
 * @since 2022-09-22
 */
public interface IProjectStatusService extends BaseService<ProjectStatus> {

    void add(ProjectStatus projectStatus);

    void updateSorting(List<Integer> ids);

    BasePage<ProjectStatus> queryProjectStatusList(ProjectStatusQueryBO query);
 }

