package com.kakarote.work.common.project;

import cn.hutool.core.collection.CollectionUtil;
import com.kakarote.work.entity.PO.AdminRole;
import com.kakarote.work.entity.BO.ProjectRoleQueryBO;
import com.kakarote.work.service.IProjectUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @author wyq
 */
@Component
public class ProjectAuthUtil {

    @Autowired
    private IProjectUserService projectUserService;


    /**
     * 发送通知
     *
     * @param employeeId 接收人
     * @param typeId     关联Id
     * @param type       类型
     * @param title      标题
     */
    public void sendMessage(Long userId, Long employeeId, Long typeId, Integer type, String title, String content) {
//        AdminMessage adminMessage = new AdminMessage();
//        adminMessage.setCreateUser(userId);
//        adminMessage.setCreateTime(DateUtil.formatDateTime(new Date()));
//        adminMessage.setRecipientUser(employeeId);
//        adminMessage.setTypeId(typeId);
//        adminMessage.setLabel(22);
//        adminMessage.setType(type);
//        adminMessage.setTitle(title);
//        adminMessage.setContent(content);
//        adminMessageService.save(adminMessage);
    }

    /**
     * 查询管理员ID和项目经理
     *
     * @param projectId
     * @return
     */
    public List<Long> projectAdminUser(Long projectId) {
        ProjectRoleQueryBO projectRoleQueryBO = new ProjectRoleQueryBO();
        projectRoleQueryBO.setProjectId(projectId);
        List<ProjectOwnerRoleBO> projectOwnerRoleBOS = projectUserService.queryProjectUser(projectRoleQueryBO);
        List<Long> adminIds = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(projectOwnerRoleBOS)) {
            projectOwnerRoleBOS.stream().forEach(p -> {
                for (AdminRole adminRole : p.getAdminRoles()) {
                    if (1 == adminRole.getLabel() || 8 == adminRole.getLabel()) {
                        adminIds.add(p.getUserId());
                    }
                }
            });
        }
        return adminIds.stream().distinct().collect(Collectors.toList());
    }
}
