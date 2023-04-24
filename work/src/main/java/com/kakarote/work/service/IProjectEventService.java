package com.kakarote.work.service;


import com.kakarote.common.servlet.BaseService;
import com.kakarote.work.entity.PO.ProjectEvent;

import java.util.List;

/**
 * <p>
 * 事件表 服务类
 * </p>
 *
 * @author bai
 * @since 2022-09-19
 */
public interface IProjectEventService extends BaseService<ProjectEvent> {

    public void add(ProjectEvent projectEvent);

    public void update(ProjectEvent projectEvent);

    public void delete(Long id);

    public List<ProjectEvent> queryEventList(Long schemeId);

}
