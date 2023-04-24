package com.kakarote.work.service;


import com.kakarote.common.servlet.BaseService;
import com.kakarote.work.entity.BO.DeleteProjectAnnouncementBO;
import com.kakarote.work.entity.BO.ProjectAnnouncementBO;
import com.kakarote.work.entity.PO.ProjectAnnouncement;
import com.kakarote.work.entity.VO.ProjectAnnouncementVO;

import java.util.List;

/**
 * <p>
 * 项目公告表 服务类
 * </p>
 *
 * @author zhangzhiwei
 * @since 2022-09-09
 */
public interface IProjectAnnouncementService extends BaseService<ProjectAnnouncement> {

    /**
     * @Description：新建项目公告
     * @Author：GuoLei
     * @Date：2022/9/9 10:53
     * @Param：ProjectAnnouncementBO
     */
    void addProjectAnnouncement(ProjectAnnouncementBO projectAnnouncementBO);

    /**
     * @Description：修改项目公告
     * @Author：GuoLei
     * @Date：2022/9/9 13:47
     * @Param：
     */
    void setProjectAnnouncement(ProjectAnnouncementBO projectAnnouncementBO);

    /**
     * @Description：删除项目公告
     * @Author：GuoLei
     * @Date：2022/9/9 13:56
     * @Param：
     */
    void delProjectAnnouncement(DeleteProjectAnnouncementBO deleteProjectAnnouncementBO);

    /**
     * @Description：查询项目公告
     * @Author：GuoLei
     * @Date：2022/9/9 14:29
     * @Param：
     */
    List<ProjectAnnouncementVO> getProjectAnnouncement(ProjectAnnouncementBO projectAnnouncementBO);
}
