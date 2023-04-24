package com.kakarote.work.service.impl;

import cn.hutool.core.util.ObjectUtil;

import com.kakarote.common.result.BasePage;
import com.kakarote.common.result.PageEntity;
import com.kakarote.common.result.PageEntity;
import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.ids.provider.utils.UserCacheUtil;
import com.kakarote.work.entity.BO.ProjectTaskFollowsQueryBO;
import com.kakarote.work.entity.PO.ProjectTaskFollows;
import com.kakarote.work.entity.VO.ProjectTaskFollowsVO;
import com.kakarote.work.mapper.ProjectTaskFollowsMapper;
import com.kakarote.work.service.IProjectTaskFollowsService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 项目事项跟进记录/客户动态表 服务实现类
 * </p>
 *
 * @author zhangyongjie
 * @since 2022-09-21
 */
@Service
public class ProjectTaskFollowsServiceImpl extends BaseServiceImpl<ProjectTaskFollowsMapper, ProjectTaskFollows> implements IProjectTaskFollowsService {

    @Override
    public BasePage<ProjectTaskFollowsVO> getProjectFollowsPageList(ProjectTaskFollowsQueryBO projectTaskFollowsQueryBO) {
        BasePage<ProjectTaskFollowsVO> followsVOBasePage = baseMapper.getProjectFollowsPageList(projectTaskFollowsQueryBO.parse(), projectTaskFollowsQueryBO);
        followsVOBasePage.getList().forEach(f -> {
            f.setUser(ObjectUtil.isNotNull(f.getUserId()) ? UserCacheUtil.getUserInfo(f.getUserId()) : null);

        });
        // projectTask.setMainUserName(ObjectUtil.isNotNull(projectTask.getMainUserId()) ? UserCacheUtil.getUserInfo(projectTask.getMainUserId()).getRealname() : null);
        return followsVOBasePage;
    }
}
