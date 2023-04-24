package com.kakarote.work.service;



import com.kakarote.common.result.BasePage;
import com.kakarote.common.servlet.BaseService;
import com.kakarote.work.entity.BO.ProjectTaskFollowsQueryBO;
import com.kakarote.work.entity.PO.ProjectTaskFollows;
import com.kakarote.work.entity.VO.ProjectTaskFollowsVO;

/**
 * <p>
 * 项目事项跟进记录/客户动态表 服务类
 * </p>
 *
 * @author zhangyongjie
 * @since 2022-09-21
 */
public interface IProjectTaskFollowsService extends BaseService<ProjectTaskFollows> {

    BasePage<ProjectTaskFollowsVO> getProjectFollowsPageList(ProjectTaskFollowsQueryBO projectTaskFollowsQueryBO);

}
