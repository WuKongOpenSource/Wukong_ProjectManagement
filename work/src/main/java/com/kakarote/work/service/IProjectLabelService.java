package com.kakarote.work.service;


import com.kakarote.common.servlet.BaseService;
import com.kakarote.work.entity.PO.ProjectLabel;

import java.util.List;

/**
 * <p>
 * 任务标签表 服务类
 * </p>
 *
 * @author bai
 * @since 2022-09-20
 */
public interface IProjectLabelService extends BaseService<ProjectLabel> {

    public void add(ProjectLabel projectLabel);

    public void update(ProjectLabel projectLabel);

    public void delete(Long id);

    public List<ProjectLabel> queryTaskLabelList(String ids);
    public List<ProjectLabel> queryList(String name) ;

    }
