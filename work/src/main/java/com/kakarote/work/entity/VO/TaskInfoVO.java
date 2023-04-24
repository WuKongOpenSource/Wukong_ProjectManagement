package com.kakarote.work.entity.VO;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.kakarote.common.entity.SimpleUser;
import com.kakarote.common.result.PageEntity;
import com.kakarote.work.entity.BO.TaskLabelBO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wyq
 */
@Data
@ApiModel("任务信息")
@Accessors(chain = true)
public class TaskInfoVO {

    @ApiModelProperty(value = "任务id")
    @TableId(value = "task_id", type = IdType.AUTO)
    private Long taskId;

    @ApiModelProperty(value = "任务名称")
    private String name;

    @ApiModelProperty(value = "项目名称")
    private String workName;
    @ApiModelProperty(value = "附件数")
    private Integer fileNum;

    @ApiModelProperty(value = "附件数")
    private Integer fileCount;

    @ApiModelProperty(value = "创建人ID")
    @TableField(fill = FieldFill.INSERT)
    private Long createUserId;

    @ApiModelProperty(value = "负责人ID")
    private Long mainUserId;

    @ApiModelProperty(value = "负责人名称")
    private String mainUserName;

    @ApiModelProperty(value = "负责人头像")
    private String mainUserImg;

    @ApiModelProperty(value = "团队成员ID")
    private String ownerUserId;

    @ApiModelProperty(value = "团队成员名称")
    private String ownerUserName;

    @ApiModelProperty(value = "新建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "完成状态 1正在进行2延期3归档 5结束")
    private Integer status;

    @ApiModelProperty(value = "分类id")
    private Long classId;

    @ApiModelProperty(value = "标签 ,号拼接")
    private String labelId;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "上级ID")
    private Long pid;

    @ApiModelProperty(value = "开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startTime;

    @ApiModelProperty(value = "结束时间")
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate stopTime;

    @ApiModelProperty(value = "优先级 从大到小 3高 2中 1低 0无")
    private Integer priority;

    @ApiModelProperty(value = "项目ID")
    private Long workId;

    @ApiModelProperty(value = "工作台展示 0收件箱 1今天要做，2下一步要做，3以后要做")
    private Integer isTop;

    @ApiModelProperty(value = "是否公开")
    private Integer isOpen;

    @ApiModelProperty(value = "排序ID")
    private Integer orderNum;

    @ApiModelProperty(value = "排序ID")
    private Integer classOrder;

    @ApiModelProperty(value = "我的任务排序ID")
    private Integer topOrderNum;

    @ApiModelProperty(value = "归档时间")
    private LocalDateTime archiveTime;

    @ApiModelProperty(value = "是否删除 0 未删除 1 删除")
    private Integer ishidden;

    @ApiModelProperty(value = "删除时间")
    private LocalDateTime hiddenTime;

    @ApiModelProperty(value = "批次")
    private String batchId;

    @ApiModelProperty(value = "1归档")
    private Integer isArchive;

    @ApiModelProperty(value = "任务是否到期 0未到期 1已到期")
    private Integer isEnd;

    @ApiModelProperty(value = "负责人信息")
    private SimpleUser mainUser;

    @ApiModelProperty(value = "标签列表")
    private List<TaskLabelBO> labelList;

    @ApiModelProperty(value = "关联的客户id")
    private String customerIds;

    @ApiModelProperty(value = "关联的联系人id")
    private String contactsIds;

    @ApiModelProperty(value = "关联的商机id")
    private String businessIds;

    @ApiModelProperty(value = "关联的合同id")
    private String contractIds;

    @ApiModelProperty(value = "关联的业务数")
    private Integer relationCount;

    @ApiModelProperty(value = "评论数")
    private Integer commentCount;

    @ApiModelProperty(value = "子任务完成数")
    private Integer childWCCount;

    @ApiModelProperty(value = "子任务数")
    private Integer childAllCount;

    @ApiModelProperty(value = "团队成员")
    private List<SimpleUser> ownerUserList;
}
