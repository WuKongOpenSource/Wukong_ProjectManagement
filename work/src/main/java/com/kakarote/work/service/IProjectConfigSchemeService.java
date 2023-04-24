package com.kakarote.work.service;


import com.kakarote.common.servlet.BaseService;
import com.kakarote.work.entity.PO.ProjectConfigScheme;
import com.kakarote.work.entity.PO.ProjectEvent;

import java.util.List;

/**
 * <p>
 * 项目配置方案表 服务类
 * </p>
 *
 * @author bai
 * @since 2022-09-19
 */
public interface IProjectConfigSchemeService extends BaseService<ProjectConfigScheme> {

    public void add(ProjectConfigScheme projectConfigScheme);

    public void update(ProjectConfigScheme projectConfigScheme);

    public void delete(Long schemeId);

    public List<ProjectEvent> queryBySchemeId(Long schemeId);
}
