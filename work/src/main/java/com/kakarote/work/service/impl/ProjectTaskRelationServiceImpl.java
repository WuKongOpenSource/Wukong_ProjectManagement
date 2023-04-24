package com.kakarote.work.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import com.kakarote.common.result.PageEntity;
import com.kakarote.common.result.PageEntity;
import com.kakarote.common.result.PageEntity;
import com.kakarote.common.result.PageEntity;
import com.kakarote.common.result.PageEntity;
import com.kakarote.common.result.PageEntity;
import com.kakarote.common.result.PageEntity;
import com.kakarote.common.servlet.BaseServiceImpl;
import com.kakarote.common.utils.UserUtil;
import com.kakarote.work.entity.PO.ProjectTask;
import com.kakarote.work.entity.PO.ProjectTaskLog;
import com.kakarote.work.entity.PO.ProjectTaskRelation;
import com.kakarote.work.mapper.ProjectTaskRelationMapper;
import com.kakarote.work.service.IProjectTaskLogService;
import com.kakarote.work.service.IProjectTaskRelationService;
import com.kakarote.work.service.IProjectTaskService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 任务关联业务表 服务实现类
 * </p>
 *
 * @author wyq
 * @since 2020-05-18
 */
@Service
public class ProjectTaskRelationServiceImpl extends BaseServiceImpl<ProjectTaskRelationMapper, ProjectTaskRelation> implements IProjectTaskRelationService {

//    @Autowired
//    private CrmService crmService;
//    @Autowired
//    private IProjectTaskService projectTaskService;
//    @Autowired
//    private IProjectTaskLogService projectTaskLogService;
//
//
//    @Override
//    public void saveProjectTaskRelation(ProjectTaskRelation projectTaskRelation) {
//        Long taskId = projectTaskRelation.getTaskId();
//        // 处理ids
//        if (Objects.nonNull(projectTaskRelation)) {
//            // 获取关联的业务转集合//TODO 可以让前端直接传入集合
//            List<ProjectTaskRelation> projectTaskRelations = new ArrayList<>(16);
//            // 添加关系
//            List<Long> customerIds = new ArrayList<>();
//            List<Long> contactsIds = new ArrayList<>();
//            List<Long> businessIds = new ArrayList<>();
//            List<Long> contractIds = new ArrayList<>();
//            List<Long> receivablesIds = new ArrayList<>();
//            handleRelation(taskId, CrmRelationTypeEnum.CUSTOMER.getType(), projectTaskRelation.getCustomerIds(), projectTaskRelations, customerIds);
//            handleRelation(taskId, CrmRelationTypeEnum.CONTACTS.getType(), projectTaskRelation.getContactsIds(), projectTaskRelations, contactsIds);
//            handleRelation(taskId, CrmRelationTypeEnum.BUSINESS.getType(), projectTaskRelation.getBusinessIds(), projectTaskRelations, businessIds);
//            handleRelation(taskId, CrmRelationTypeEnum.CONTRACT.getType(), projectTaskRelation.getContractIds(), projectTaskRelations, contractIds);
//            handleRelation(taskId, CrmRelationTypeEnum.RECEIVABLES.getType(), projectTaskRelation.getReceivablesIds(), projectTaskRelations, receivablesIds);
//            if (1 == projectTaskRelation.getFlag()) {
//                ProjectTask projectTask = projectTaskService.lambdaQuery().eq(ProjectTask::getTaskId, taskId).one();
//                ProjectTaskLog projectTaskLog = new ProjectTaskLog();
//                projectTaskLog.setTaskId(projectTask.getTaskId());
//                projectTaskLog.setType(1);
//                List<String> contents = new ArrayList<>();
//                if (StrUtil.isNotEmpty(projectTaskRelation.getBusinessIds())) {
//                    Set<Long> longs = TagUtil.toLongSet(projectTaskRelation.getBusinessIds());
//                    List<Long> saveIds = lambdaQuery().eq(ProjectTaskRelation::getTaskId, projectTaskRelation.getTaskId()).eq(ProjectTaskRelation::getType, 5).list().stream().map(m -> m.getRelationId()).collect(Collectors.toList());
//                    if (CollectionUtil.isEmpty(saveIds)) {
//                        contents.add(UserUtil.getUser().getRealname().concat(" 关联了商机 "));
//                    } else {
//                        if (saveIds.size() >= longs.size()) {
//                            Collection<Long> disjunction = CollectionUtils.disjunction(longs, saveIds);
//                            if (CollectionUtil.isNotEmpty(disjunction)) {
//                                contents.add(UserUtil.getUser().getRealname().concat(" 关联了商机 "));
//                            }
//                        } else {
//                            contents.add(UserUtil.getUser().getRealname().concat(" 关联了商机 "));
//                        }
//                    }
//                }
//                if (StrUtil.isNotEmpty(projectTaskRelation.getContactsIds())) {
//                    Set<Long> longs = TagUtil.toLongSet(projectTaskRelation.getContactsIds());
//                    List<Long> saveIds = lambdaQuery().eq(ProjectTaskRelation::getTaskId, projectTaskRelation.getTaskId()).eq(ProjectTaskRelation::getType, 3).list().stream().map(m -> m.getRelationId()).collect(Collectors.toList());
//                    if (CollectionUtil.isEmpty(saveIds)) {
//                        contents.add(UserUtil.getUser().getRealname().concat(" 关联了联系人 "));
//                    } else {
//                        if (saveIds.size() >= longs.size()) {
//                            Collection<Long> disjunction = CollectionUtils.disjunction(longs, saveIds);
//                            if (CollectionUtil.isNotEmpty(disjunction)) {
//                                contents.add(UserUtil.getUser().getRealname().concat(" 关联了联系人 "));
//                            }
//                        } else {
//                            contents.add(UserUtil.getUser().getRealname().concat(" 关联了联系人 "));
//                        }
//                    }
//                }
//                if (StrUtil.isNotEmpty(projectTaskRelation.getCustomerIds())) {
//                    Set<Long> longs = TagUtil.toLongSet(projectTaskRelation.getCustomerIds());
//                    List<Long> saveIds = lambdaQuery().eq(ProjectTaskRelation::getTaskId, projectTaskRelation.getTaskId()).eq(ProjectTaskRelation::getType, 2).list().stream().map(m -> m.getRelationId()).collect(Collectors.toList());
//                    if (CollectionUtil.isEmpty(saveIds)) {
//                        contents.add(UserUtil.getUser().getRealname().concat(" 关联了客户 "));
//                    } else {
//                        if (saveIds.size() >= longs.size()) {
//                            Collection<Long> disjunction = CollectionUtils.disjunction(longs, saveIds);
//                            if (CollectionUtil.isNotEmpty(disjunction)) {
//                                contents.add(UserUtil.getUser().getRealname().concat(" 关联了客户 "));
//                            }
//                        } else {
//                            contents.add(UserUtil.getUser().getRealname().concat(" 关联了客户 "));
//                        }
//                    }
//                }
//                if (StrUtil.isNotEmpty(projectTaskRelation.getReceivablesIds())) {
//                    Set<Long> longs = TagUtil.toLongSet(projectTaskRelation.getReceivablesIds());
//                    List<Long> saveIds = lambdaQuery().eq(ProjectTaskRelation::getTaskId, projectTaskRelation.getTaskId()).eq(ProjectTaskRelation::getType, 7).list().stream().map(m -> m.getRelationId()).collect(Collectors.toList());
//                    if (CollectionUtil.isEmpty(saveIds)) {
//                        contents.add(UserUtil.getUser().getRealname().concat(" 关联了回款 "));
//                    } else {
//                        if (saveIds.size() >= longs.size()) {
//                            Collection<Long> disjunction = CollectionUtils.disjunction(longs, saveIds);
//                            if (CollectionUtil.isNotEmpty(disjunction)) {
//                                contents.add(UserUtil.getUser().getRealname().concat(" 关联了回款 "));
//                            }
//                        } else {
//                            contents.add(UserUtil.getUser().getRealname().concat(" 关联了回款 "));
//                        }
//                    }
//
//                }
//                if (StrUtil.isNotEmpty(projectTaskRelation.getContractIds())) {
//                    Set<Long> longs = TagUtil.toLongSet(projectTaskRelation.getContractIds());
//                    List<Long> saveIds = lambdaQuery().eq(ProjectTaskRelation::getTaskId, projectTaskRelation.getTaskId()).eq(ProjectTaskRelation::getType, 6).list().stream().map(m -> m.getRelationId()).collect(Collectors.toList());
//                    if (CollectionUtil.isEmpty(saveIds)) {
//                        contents.add(UserUtil.getUser().getRealname().concat(" 关联了合同 "));
//                    } else {
//                        if (saveIds.size() >= longs.size()) {
//                            Collection<Long> disjunction = CollectionUtils.disjunction(longs, saveIds);
//                            if (CollectionUtil.isNotEmpty(disjunction)) {
//                                contents.add(UserUtil.getUser().getRealname().concat(" 关联了合同 "));
//                            }
//                        } else {
//                            contents.add(UserUtil.getUser().getRealname().concat(" 关联了合同 "));
//                        }
//                    }
//                }
//                if (CollectionUtil.isNotEmpty(contents)) {
//                    contents.stream().forEach(c -> {
//                        projectTaskLog.setContent(c);
//                        projectTaskLogService.saveTaskLog(projectTaskLog);
//                    });
//
//                }
//            }
//            // 删除原有
//            lambdaUpdate().eq(ProjectTaskRelation::getTaskId, taskId)
//                    .remove();
//            if (CollUtil.isNotEmpty(projectTaskRelations)) {
//                // 保存
//                saveBatch(projectTaskRelations, projectTaskRelations.size());
//                // 保存活动记录
//                ActivityContent activityContent = new ActivityContent();
//                activityContent.setName(projectTaskRelation.getName());
//                activityContent.setContentType(11);
//                activityContent.setOwnerUserId(projectTaskRelation.getOwnerUserId());
//                activityContent.setStartTime(projectTaskRelation.getStartTime() == null ? null : LocalDateTimeUtil.formatNormal(projectTaskRelation.getStartTime().atStartOfDay()));
//                activityContent.setEndTime(projectTaskRelation.getEndTime() == null ? null : LocalDateTimeUtil.formatNormal(projectTaskRelation.getEndTime().atStartOfDay()));
//                CrmActivityBO crmActivityBO = new CrmActivityBO(2, 11, taskId, activityContent);
//                crmActivityBO.setCustomerIds(customerIds);
//                crmActivityBO.setContactsIds(contactsIds);
//                crmActivityBO.setBusinessIds(businessIds);
//                crmActivityBO.setContractIds(contractIds);
//                crmActivityBO.setReceivablesIds(receivablesIds);
//                crmService.addRelationActivity(crmActivityBO);
//                crmService.addActivity(2, 11, taskId);
//            }
//        }
//
//    }

//
//    /**
//     * 处理crm关联
//     *
//     * @param taskId               id
//     * @param type                 类型
//     * @param relationIds          关系ids
//     * @param projectTaskRelations 关系列表
//     * @author jiao sir
//     * @date 2021/11/19
//     */
//    private void handleRelation(Long taskId, int type, String relationIds, List<ProjectTaskRelation> projectTaskRelations, List<Long> longs) {
//        TagUtil.toLongSet(relationIds)
//                .forEach(id -> {
//                    ProjectTaskRelation build = ProjectTaskRelation
//                            .builder()
//                            .relationId(id)
//                            .taskId(taskId)
//                            .type(type)
//                            .build();
//                    projectTaskRelations.add(build);
//                    longs.add(id);
//                });
//    }
//
//    @Autowired
//    public void setCrmService(CrmService crmService) {
//        this.crmService = crmService;
//    }
}
