package com.kakarote.work.service.impl;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.kakarote.common.entity.SimpleUser;
import com.kakarote.common.result.PageEntity;
import com.kakarote.common.result.PageEntity;
import com.kakarote.common.result.PageEntity;
import com.kakarote.common.result.PageEntity;
import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.ids.provider.utils.UserCacheUtil;
import com.kakarote.work.entity.PO.ProjectTaskComment;
import com.kakarote.work.mapper.ProjectTaskCommentMapper;
import com.kakarote.work.service.IProjectTaskCommentService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 任务评论表 服务实现类
 * </p>
 *
 * @author wyq
 * @since 2020-05-18
 */
@Service
public class ProjectTaskCommentServiceImpl extends BaseServiceImpl<ProjectTaskCommentMapper, ProjectTaskComment> implements IProjectTaskCommentService {

    /**
     * 查询评论列表
     *
     * @param typeId typeId
     * @return data
     */
    @Override
    public List<ProjectTaskComment> queryCommentList(Long typeId) {
        LambdaQueryChainWrapper<ProjectTaskComment> chainWrapper = lambdaQuery();
         chainWrapper.eq(ProjectTaskComment::getTypeId, typeId);
        chainWrapper.orderByAsc(ProjectTaskComment::getCreateTime);
        List<ProjectTaskComment> taskCommentList = chainWrapper.list();
        if (taskCommentList == null || taskCommentList.size() == 0) {
            return new ArrayList<>();
        }
        taskCommentList.forEach(record -> {
            if (record.getUserId() != null) {
                List<SimpleUser> data = UserCacheUtil.getSimpleUsers(Collections.singletonList(record.getUserId()));
                record.setUser(data.size() > 0 ? data.get(0) : null);
            }
            if (!Objects.equals(0L, record.getPid())) {
                List<SimpleUser> data = UserCacheUtil.getSimpleUsers(Collections.singletonList(record.getPid()));
                record.setReplyUser(data.size() > 0 ? data.get(0) : null);
            }
        });
        Map<Long, List<ProjectTaskComment>> pMap = taskCommentList.stream().collect(Collectors.groupingBy(ProjectTaskComment::getMainId));
        taskCommentList = pMap.get(0L);
        taskCommentList.forEach(record -> {
            Long commentId = record.getCommentId();
            if (pMap.get(commentId) != null) {
                record.setChildCommentList(pMap.get(commentId));
            } else {
                record.setChildCommentList(new ArrayList<>());
            }
        });
        return taskCommentList;
    }

    /**
     * 新增评论
     *
     * @param comment taskComment
     */
    @Override
    public void setComment(ProjectTaskComment comment) {
        if (comment.getCommentId() == null) {
            comment.setCreateTime(LocalDateTimeUtil.now());
            comment.setUserId(UserUtil.getUserId());
            save(comment);
            int two=2;
//            AdminMessageBO adminMessageBO = new AdminMessageBO();
//            adminMessageBO.setUserId(comment.getUserId());
//            adminMessageBO.setContent(comment.getContent());
//            adminMessageBO.setTypeId(comment.getTypeId());
//            if (comment.getType().equals(two)) {
////                JSONObject object = getBaseMapper().queryOaLog(comment.getTypeId());
////                adminMessageBO.setTitle(DateUtil.formatDate(object.getDate("createTime")));
////                if (comment.getMainId() != null && !Objects.equals(0L,comment.getMainId())) {
////                    adminMessageBO.setMessageType(AdminMessageEnum.OA_COMMENT_REPLY.getType());
////                    adminMessageBO.setIds(Collections.singletonList(comment.getPid()));
////                } else {
////                    adminMessageBO.setMessageType(AdminMessageEnum.OA_LOG_REPLY.getType());
////                    adminMessageBO.setIds(Collections.singletonList(object.getLong("createUserId")));
////                }
//            }else {
//                TaskDetailVO taskDetailVO = ApplicationContextHolder.getBean(IWorkTaskService.class).queryTaskInfo(comment.getTypeId());
//                adminMessageBO.setTitle(taskDetailVO.getName());
//                List<Long> list = new ArrayList<>();
//                list.add(taskDetailVO.getMainUserId());
//                if (comment.getMainId() != null && !Objects.equals(0L,comment.getMainId())) {
//                    adminMessageBO.setMessageType(AdminMessageEnum.OA_TASK_COMMENT_REPLY.getType());
//                    list.add(comment.getPid());
//                } else {
//                    adminMessageBO.setMessageType(AdminMessageEnum.OA_TASH_REPLY.getType());
//                    list.add(taskDetailVO.getCreateUserId());
//                }
//                if(CollectionUtil.isNotEmpty(taskDetailVO.getOwnerUserList())){
//                    list.addAll(taskDetailVO.getOwnerUserList().stream().map(SimpleUser::getUserId).collect(Collectors.toList()));
//                }
//                adminMessageBO.setIds(list);
//            }
//            ApplicationContextHolder.getBean(AdminMessageService.class).sendMessage(adminMessageBO);

        } else {
            updateById(comment);
        }
    }
}
