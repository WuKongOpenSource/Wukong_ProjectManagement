package com.kakarote.work.entity.PO;

import com.baomidou.mybatisplus.annotation.*;
import com.kakarote.work.common.project.ProjectOwnerRoleBO;
import com.kakarote.work.common.project.TaskOwnerBO;
import com.kakarote.work.entity.BO.FileEntity;
import com.kakarote.work.entity.BO.SimpleCrmEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 任务表
 * </p>
 *
 * @author bai
 * @since 2022-09-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("wk_project_task")
@ApiModel(value = "ProjectTask对象", description = "任务表")
public class ProjectTask implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "任务表")
    @TableId(value = "task_id", type = IdType.ASSIGN_ID)
    private Long taskId;

    @ApiModelProperty(value = "任务名称")
    private String name;

    @ApiModelProperty(value = "团队成员ID")
    private String ownerUserId;

    @ApiModelProperty(value = "事件团队成员")
    @TableField(exist = false)
    private  List<TaskOwnerBO>  taskOwnerBOS;

    @ApiModelProperty(value = "0 带规划 1迭代 2需求 3任务 4缺陷 5工时 6子任务")
    private Integer type;

    @ApiModelProperty(value = "完成状态 1未开始 2进行中 3已完成 4延期 5归档 6结束")
    private Integer status;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "开始时间")
    private Date startTime;

    @ApiModelProperty(value = "结束时间")
    private Date stopTime;

    @ApiModelProperty(value = "优先级 从大到小 3高 2中 1低 0无")
    private Integer priority;

    @ApiModelProperty(value = "标签")
    private String label;

    @ApiModelProperty(value = "关联对象")
    private Object relationIds;

    @ApiModelProperty(value = "逗号隔开")
    private String wiki;

    @ApiModelProperty(value = "项目ID")
    private Long projectId;

    @ApiModelProperty(value = "我的任务排序ID")
    private Integer topOrderNum;

    @ApiModelProperty(value = "预估工时")
    private Integer estimatedManHours;

    @ApiModelProperty(value = "是否删除 0 未删除 1 删除")
    private Integer ishidden;

    @ApiModelProperty(value = "删除时间")
    private LocalDateTime hiddenTime;

    @ApiModelProperty(value = "批次")
    private String batchId;

    @ApiModelProperty(value = "关联部门")
    private String ownerDeptId;
    @ApiModelProperty(value = "看板状态id")
    private Long boardStatusId;
    @ApiModelProperty(value = "修改人ID")
    private Long updateUserId;

    @ApiModelProperty(value = "创建人ID")
    @TableField(fill = FieldFill.INSERT)
    private Long createUserId;

    @ApiModelProperty(value = "负责人ID")
    private Long mainUserId;

    @ApiModelProperty(value = "负责人img")
    @TableField(exist = false)
    private String mainUserImg;

    @ApiModelProperty(value = "关联需求ID")
    private Long relatedDemandId;

    @ApiModelProperty(value = "所属迭代ID")
    private Long belongIterationId;

    @ApiModelProperty(value = "新建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "序号")
    private Integer num;

    @ApiModelProperty(value = "进度")
    private Integer progress;

    @TableField(exist = false)
    @ApiModelProperty(value = "迭代名称")
    private String belongIterationName;

    @TableField(exist = false)
    @ApiModelProperty(value = "需求名称")
    private String relatedDemandName;

    @ApiModelProperty(value = "创建人名称")
    @TableField(exist = false)
    private String createUserName;

    @TableField(exist = false)
    @ApiModelProperty(value = "负责人名称")
    private String mainUserName;

    @ApiModelProperty(value = "缺陷类型")
    private Integer wrongType;

    @ApiModelProperty(value = "父任务id")
    private Long pid;

    @TableField(exist = false)
    private List<ProjectTask> childTaskList;

    @ApiModelProperty(value = "标签详情")
    @TableField(exist = false)
    private List<ProjectLabel> labelTaskList;

    @TableField(exist = false)
    @ApiModelProperty(value = "看板状态名称")
    private String boardStatusName;

    @TableField(exist = false)
    @ApiModelProperty(value = "附件")
    private List<FileEntity> file;

    @TableField(exist = false)
    @ApiModelProperty(value = "客户列表")
    private List<SimpleCrmEntity> customerList = new ArrayList<>();

    @TableField(exist = false)
    @ApiModelProperty(value = "联系人列表")
    private List<SimpleCrmEntity> contactsList = new ArrayList<>();

    @TableField(exist = false)
    @ApiModelProperty(value = "商机列表")
    private List<SimpleCrmEntity> businessList = new ArrayList<>();

    @TableField(exist = false)
    @ApiModelProperty(value = "合同列表")
    private List<SimpleCrmEntity> contractList = new ArrayList<>();

    @TableField(exist = false)
    @ApiModelProperty(value = "回款列表")
    private List<SimpleCrmEntity> receivablesList = new ArrayList<>();

    @TableField(exist = false)
    @ApiModelProperty(value = "工时列表")
    private List<ProjectTaskTime> taskTimeList = new ArrayList<>();


    @ApiModelProperty(value = "项目成员列表")
    @TableField(exist = false)
    private List<ProjectOwnerRoleBO> projectOwnerRoleList;

    @ApiModelProperty(value = "项目截止时间")
    @TableField(exist = false)
    private LocalDateTime projectEndTime;

    @TableField(exist = false)
    private String createUserImg;

    @TableField(exist = false)
    @ApiModelProperty(value = "进迭进度")
    private String belongIterationProgress;

    @ApiModelProperty(value = "迭代完成时间")
    private LocalDateTime finishTime;

    @TableField(exist = false)
    @ApiModelProperty(value = "项目类型    1普通 2敏捷 ")
    private Integer projectType;

    @ApiModelProperty(value = "迭代目标")
    private String target;

    @TableField(exist = false)
    @ApiModelProperty(value = "迭代下事项数量")
    private Long belongIterationTaskCount;

    @TableField(exist = false)
    @ApiModelProperty(value = "事件id")
    private Long eventId;

    @ApiModelProperty(value = "项目管理员列表")
    @TableField(exist = false)
    private List<ProjectOwnerRoleBO> projectAdminRoleList;

    @TableField(exist = false)
    @ApiModelProperty(value = "排序值")
    private Long sortNum;
    @ApiModelProperty(value = "项目名称")
    @TableField(exist = false)
    private String projectName;

    @ApiModelProperty(value = "所属项目名称")
    @TableField(exist = false)
    private String belongProjectName;

    @ApiModelProperty(value = "任务关联业务项")
    @TableField(exist = false)
    private ProjectTaskRelation projectTaskRelation;

    @ApiModelProperty(value = "项目图标")
    @TableField(exist = false)
    private String projectIcon;

}