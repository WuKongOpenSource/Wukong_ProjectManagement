package com.kakarote.work.service;


import com.kakarote.common.servlet.BaseService;
import com.kakarote.work.entity.BO.ProjectTaskUserBO;
import com.kakarote.work.entity.PO.ProjectTaskUser;

/**
 * <p>
 * 项目成员表 服务类
 * </p>
 *
 * @author bai
 * @since 2022-10-27
 */
public interface IProjectTaskUserService extends BaseService<ProjectTaskUser> {

    void relatedProjectUser(ProjectTaskUserBO projectTaskUserBO);

}
