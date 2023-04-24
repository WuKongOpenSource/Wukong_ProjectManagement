package com.kakarote.work.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.kakarote.common.entity.UserInfo;
import com.kakarote.common.result.Result;
import com.kakarote.common.result.PageEntity;
import com.kakarote.common.result.PageEntity;
import com.kakarote.common.result.PageEntity;
import com.kakarote.common.result.PageEntity;
import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.ids.provider.utils.UserCacheUtil;
import com.kakarote.work.entity.BO.DeleteProjectAnnouncementBO;
import com.kakarote.work.entity.BO.ProjectAnnouncementBO;
import com.kakarote.work.entity.PO.ProjectAnnouncement;
import com.kakarote.work.entity.VO.ProjectAnnouncementVO;
import com.kakarote.work.mapper.ProjectAnnouncementMapper;
import com.kakarote.work.service.IProjectAnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 项目公告表 服务实现类
 * </p>
 *
 * @author zhangzhiwei
 * @since 2022-09-09
 */
@Service
public class ProjectAnnouncementServiceImpl extends BaseServiceImpl<ProjectAnnouncementMapper, ProjectAnnouncement> implements IProjectAnnouncementService {


    @Override
    public void addProjectAnnouncement(ProjectAnnouncementBO projectAnnouncementBO) {
        ProjectAnnouncement projectAnnouncement = BeanUtil.copyProperties(projectAnnouncementBO, ProjectAnnouncement.class);
        if (ObjectUtil.isNotNull(projectAnnouncement)) {
            projectAnnouncement.setCreateTime(DateUtil.date());
            projectAnnouncement.setCreateUserId(UserUtil.getUserId());
            save(projectAnnouncement);
        }
    }

    @Override
    public void setProjectAnnouncement(ProjectAnnouncementBO projectAnnouncementBO) {
        ProjectAnnouncement projectAnnouncement = BeanUtil.copyProperties(projectAnnouncementBO, ProjectAnnouncement.class);
        if (ObjectUtil.isNotNull(projectAnnouncement) && ObjectUtil.isNotNull(projectAnnouncement.getProjectAnnouncementId())) {
            projectAnnouncement.setUpdateTime(DateUtil.date());
            updateById(projectAnnouncement);
        }
    }

    @Override
    public void delProjectAnnouncement(DeleteProjectAnnouncementBO deleteProjectAnnouncementBO) {
        removeByIds(deleteProjectAnnouncementBO.getProjectAnnouncementId());
    }

    @Override
    public List<ProjectAnnouncementVO> getProjectAnnouncement(ProjectAnnouncementBO projectAnnouncementBO) {
        List<ProjectAnnouncementVO> projectAnnouncementVOs = new ArrayList<>();
        List<ProjectAnnouncement> projectAnnouncementList = lambdaQuery()
                .eq(ObjectUtil.isNotNull(projectAnnouncementBO.getProjectId()),ProjectAnnouncement::getProjectId, projectAnnouncementBO.getProjectId())
                .orderByDesc(ProjectAnnouncement::getCreateTime)
                .list();
        if (CollUtil.isNotEmpty(projectAnnouncementList)) {
            for (ProjectAnnouncement projectAnnouncement : projectAnnouncementList) {
                ProjectAnnouncementVO projectAnnouncementVO = BeanUtil.copyProperties(projectAnnouncement, ProjectAnnouncementVO.class);
                UserInfo userInfo = UserCacheUtil.getUserInfo(projectAnnouncement.getCreateUserId());
                if (ObjectUtil.isNotNull(userInfo) && ObjectUtil.isNotNull(userInfo)) {
                    projectAnnouncementVO.setCreateUserImg(userInfo.getUserImg());
                    projectAnnouncementVO.setCreateUser(userInfo.getNickname());
                }
                projectAnnouncementVOs.add(projectAnnouncementVO);
            }
        }
        return projectAnnouncementVOs;
    }

}
