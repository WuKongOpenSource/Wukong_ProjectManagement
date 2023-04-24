package com.kakarote.work.service;


import com.kakarote.common.servlet.BaseService;
import com.kakarote.work.entity.PO.ProjectGroup;

import java.util.List;

/**
 * <p>
 * 项目分组表 服务类
 * </p>
 *
 * @author bai
 * @since 2022-09-09
 */
public interface IProjectGroupService extends BaseService<ProjectGroup> {

    public void addGroup(ProjectGroup projectGroup);

    public void updateGroup(ProjectGroup projectGroup);

    public void updateGroupBatch(List<ProjectGroup> projectGroups);

    public List<ProjectGroup> searchGroupList();

    public void removeGroupById(Long groupId);
}
