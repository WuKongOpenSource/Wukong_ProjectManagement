package com.kakarote.work.common.admin;


/**
 * 消息通知枚举类
 */
public enum AdminMessageEnum {

    /**
     * 消息通知枚举类
     */
    NULL(0,0,"NULL"),
    OA_TASK_ALLOCATION(1,1,"分配给我的任务"),
    OA_TASK_JOIN(2,1,"我参与的任务"),
    OA_TASK_OVER(3,1,"任务结束通知"),
    OA_LOG_REPLY(4,2,"日志回复提醒"),
    OA_LOG_SEND(5,2,"日志发送提醒"),
    OA_EXAMINE_REJECT(6,3,"OA审批拒绝提醒"),
    OA_EXAMINE_PASS(7,3,"OA审批通过提醒"),
    OA_NOTICE_MESSAGE(8,4,"公告通知提醒"),
    OA_EVENT_MESSAGE(9,5,"日程通知"),
    CRM_CONTRACT_REJECT(10,6,"合同拒绝通知"),
    CRM_CONTRACT_PASS(11,6,"合同全部通过通知"),
    CRM_RECEIVABLES_REJECT(12,6,"回款拒绝通知"),
    CRM_RECEIVABLES_PASS(13,6,"回款通过通知"),
    CRM_CUSTOMER_IMPORT(14,6,"导入客户通知"),
    CRM_CUSTOMER_IMPORT_CANCEL(15,6,"导入客户取消通知"),
    CRM_CONTACTS_IMPORT(16,6,"导入联系人通知"),
    CRM_CONTACTS_IMPORT_CANCEL(17,6,"导入联系人取消通知"),
    CRM_LEADS_IMPORT(18,6,"导入线索通知"),
    CRM_LEADS_IMPORT_CANCEL(19,6,"导入线索取消通知"),
    CRM_PRODUCT_IMPORT(20,6,"导入产品通知"),
    CRM_PRODUCT_IMPORT_CANCEL(21,6,"导入产品取消通知"),
    CRM_BUSINESS_USER(22,6,"商机团队成员通知"),
    CRM_CUSTOMER_USER(23,6,"客户团队成员通知"),
    CRM_CONTRACT_USER(24,6,"合同团队成员通知"),
    OA_EXAMINE_NOTICE(25,3,"OA待审核审批提醒"),
    CRM_CONTRACT_EXAMINE(26,6,"合同待审核审批提醒"),
    CRM_RECEIVABLES_EXAMINE(27,6,"回款待审核审批提醒"),
    CRM_BUSINESS_TEAM_EXIT(28, 6, "商机团队成员退出提醒"),
    CRM_CUSTOMER_TEAM_EXIT(29, 6, "客户团队成员退出提醒"),
    CRM_CONTRACT_TEAM_EXIT(30, 6, "合同团队成员退出提醒"),
    CRM_BUSINESS_REMOVE_TEAM(31, 6, "移除商机团队成员提醒"),
    CRM_CUSTOMER_REMOVE_TEAM(32, 6, "移除客户团队成员提醒"),
    CRM_CONTRACT_REMOVE_TEAM(33, 6, "移除合同团队成员提醒"),
    OA_COMMENT_REPLY(34, 2, "评论回复提醒"),
    CRM_INVOICE_REJECT(35, 6, "发票拒绝通知"),
    CRM_INVOICE_PASS(36, 6, "发票通过通知"),
    CRM_INVOICE_EXAMINE(37, 6, "发票待审核审批提醒"),
    PROJECT_ADD_USER_NOTICE(38, 22, "被添加为项目成员"),
    PROJECT_EDIT_MATTERS_USER_NOTICE(39, 22, "被修改为事项处理人"),
    PROJECT_CREATE_NOTICE(40, 22, "创建项目"),
    PROJECT_EVENT_DETAIL_NOTICE(42, 22, "事件详情处理人"),
    PROJECT_EVENT_STATUS_NOTICE(43, 22, "事件状态处理人"),
    PROJECT_REMOVE_USER_NOTICE(44, 22, "被移除为项目成员"),

    KM_DOCUMENT_SHARE_OPEN(41, 7, "知识库文档开启分享"),
    KM_DOCUMENT_SHARE_CLOSE(42, 7, "知识库文档关闭分享"),

    CRM_RETURN_VISIT_IMPORT(43,6,"导入回访通知"),
    CRM_RETURN_VISIT_IMPORT_CANCEL(44,6,"导入回访取消通知"),
    //人资
    HRM_EMPLOYEE_IMPORT(50, 8, "人资员工导入通知"),
    HRM_SEND_SLIP(80, 8, "人资发送工资条通知"),
    HRM_FIX_SALARY_IMPORT(81, 8, "人资导入定薪通知"),
    HRM_CHANGE_SALARY_IMPORT(82, 8, "人资导入调薪通知"),
    HRM_WRITE_ARCHIVES(83, 8, "邀请填写档案信息"),
    HRM_EMPLOYEE_SALARY_PASS(84, 8, "人资薪资通过通知"),
    HRM_EMPLOYEE_SALARY_REJECT(85, 8, "人资薪资拒绝通知"),
    HRM_EMPLOYEE_SALARY_EXAMINE(86, 8, "人资薪资待审核提醒"),
    HRM_EMPLOYEE_APPRAISAL_WRITE(87,8,"人资员工绩效目标待填写通知"),
    HRM_EMPLOYEE_APPRAISAL_CONFIRMATION(88,8,"人资员工绩效目标待确认通知"),
    HRM_EMPLOYEE_APPRAISAL_ASSESSED(89,8,"人资员工绩效目标结果待评定通知"),
    HRM_EMPLOYEE_APPRAISAL_CONFIRMED(90,8,"人资员工绩效目标结果待确认通知"),
    HRM_EMPLOYEE_APPRAISAL_COMPLETE(91,8,"人资员工绩效考核已完成通知"),
    HRM_EMPLOYEE_APPRAISAL_WRITE_REJECT(92,8,"人资员工绩效目标驳回通知"),
    HRM_EMPLOYEE_APPRAISAL_ASSESSED_REJECT(93,8,"人资员工绩效评定驳回通知"),
    HRM_APPRAISAL_WRITE_COMPLETE(94,8,"人资员工绩效已全部完成填写通知"),
    HRM_APPRAISAL_ASSESSED_COMPLETE(95,8,"人资员工绩效已全部完成评定通知"),
    HRM_APPRAISAL_ARCHIVE(96,8,"人资员工绩效及时归档通知"),
    HRM_EMPLOYEE_OPEN(97,8,"人力资源员工端开通通知"),
    HRM_EMPLOYEE_INSURANCE(98,8,"人力资源员工社保通知"),
    HRM_INTERVIEW_ARRANGEMENTS(99,8,"人力资源面试官面试安排通知"),
    HRM_EMPLOYEE_ATTENDANCE_IMPORT(100, 8, "人资员工考勤导入通知"),
    HRM_EMPLOYEE_APPRAISAL_SCORING(101,8,"人资员工绩效待评分通知"),
    HRM_EMPLOYEE_APPRAISAL_SCORING_SUCCESS(102,8,"人资员工绩效已评分通知"),
    HRM_EMPLOYEE_APPRAISAL_SCORING_REJECT(103,8,"人资员工绩效评分被驳回通知"),
    HRM_EMPLOYEE_APPRAISAL_RESULT_AUDIT(104,8,"人资员工绩效结果已审核通知"),
    HRM_EMPLOYEE_APPRAISAL_RESULT_AUDIT_WAITING(105,8,"人资员工绩效结果待审核通知"),
    HRM_EMPLOYEE_APPRAISAL_RESULT_APPEAL_WAITING(106,8,"人资员工绩效结果申诉待处理通知"),
    HRM_EMPLOYEE_APPRAISAL_RESULT_APPEAL_PASS(107,8,"人资员工绩效结果申诉通过通知"),
    HRM_EMPLOYEE_APPRAISAL_RESULT_APPEAL_REJECT(108,8,"人资员工绩效结果申诉驳回通知"),
    //JXC
    JXC_PURCHASE_EXAMINE(53,9,"采购订单待审核审批提醒"),
    JXC_PURCHASE_REJECT(54,9,"采购订单拒绝通知"),
    JXC_PURCHASE_PASS(55,9,"采购订单通过通知"),
    JXC_RETREAT_EXAMINE(56,9,"采购退货单待审核审批提醒"),
    JXC_RETREAT_REJECT(57,9,"采购退货单拒绝通知"),
    JXC_RETREAT_PASS(58,9,"采购退货单通过通知"),
    JXC_SALE_EXAMINE(59,9,"销售订单待审核审批提醒"),
    JXC_SALE_REJECT(60,9,"销售订单拒绝通知"),
    JXC_SALE_PASS(61,9,"销售订单通过通知"),
    JXC_SALE_RETURN_EXAMINE(62,9,"销售退货单待审核审批提醒"),
    JXC_SALE_RETURN_REJECT(63,9,"销售退货单拒绝通知"),
    JXC_SALE_RETURN_PASS(64,9,"销售退货单通过通知"),
    JXC_PAYMENT_EXAMINE(65,9,"付款单待审核审批提醒"),
    JXC_PAYMENT_REJECT(66,9,"付款单待审拒绝通知"),
    JXC_PAYMENT_PASS(67,9,"付款单待审通过通知"),
    JXC_COLLECTION_EXAMINE(68,9,"回款单待审核审批提醒"),
    JXC_COLLECTION_REJECT(69,9,"回款单拒绝通知"),
    JXC_COLLECTION_PASS(70,9,"回款单通过通知"),
    JXC_INVENTORY_EXAMINE(71,9,"盘点待审核审批提醒"),
    JXC_INVENTORY_REJECT(72,9,"盘点拒绝通知"),
    JXC_INVENTORY_PASS(73,9,"盘点通过通知"),
    JXC_ALLOCATION_EXAMINE(74,9,"调拨待审核审批提醒"),
    JXC_ALLOCATION_REJECT(75,9,"调拨拒绝通知"),
    JXC_ALLOCATION_PASS(76,9,"调拨通过通知"),
    OA_LOG_FAVOUR(77,2,"日志点赞通知"),
    CRM_CONTACTS_USER(120,6,"联系人团队成员通知"),
    CRM_RECEIVABLES_USER(121,6,"回款团队成员通知"),
    CRM_CONTACTS_TEAM_EXIT(122, 6, "联系人团队成员退出提醒"),
    CRM_RECEIVABLES_TEAM_EXIT(123, 6, "回款团队成员退出提醒"),
    CRM_CONTACTS_REMOVE_TEAM(124, 6, "移除联系人团队成员提醒"),
    CRM_RECEIVABLES_REMOVE_TEAM(125, 6, "移除回款团队成员提醒"),
    CRM_SAVE(126,6,"新建"),
    CRM_TRANSFER(127,6,"转移"),
    CRM_TRANSFORM(128,6,"转化为客户"),
    CRM_TERM_MEMBER(129,6,"添加、移除团队成员"),
    CRM_UPDATE_INVOICE_STATUS(130,6,"标记、重置开票"),
    CRM_EXCEL_IMPORT(131,6,"导入"),
    CRM_EXCEL_EXPORT(132,6,"导出"),

    CRM_FLOW_EXPORT(133,20,"阶段审批通知"),
    CRM_FLOW_PASS(134,20,"阶段审批通过通知"),
    CRM_FLOW_REJECT(135,20,"阶段审批拒绝通知"),

    OA_TASK_COMMENT_REPLY(136, 1, "任务评论回复提醒"),
    OA_TASH_REPLY(137,1,"任务回复提醒"),

    CRM_E_SING_AUTH(138,6,"签署账号认证通知"),

    CRM_CUSTOMER_TEAM_REJECT(139,6,"客户团队成员拒绝通知"),
    CRM_CUSTOMER_TEAM_PASS(140,6,"客户团队成员通过通知"),
    CRM_CUSTOMER_TEAM(141,6,"加入客户团队成员通知"),
    CRM_CUSTOMER_TEAM_WAIT(142,6,"客户团队成员待审核审批提醒"),

    CRM_RETURN_VISIT_USER(143,6,"回访团队成员通知"),
    CRM_RETURN_VISIT_REMOVE_TEAM(144,6,"移除回访团队成员提醒"),
    CRM_RETURN_VISIT_TEAM_EXIT(145,6,"回访团队成员退出提醒"),


    CRM_CONTRACT_COPY(146,6,"合同审批抄送提醒"),
    CRM_RECEIVABLES_COPY(147,6,"回款审批抄送提醒"),
    CRM_INVOICE_COPY(148,6,"发票审批抄送提醒"),
    HRM_SALARY_COPY(149,8,"薪资审批抄送提醒"),
    JXC_PURCHASE_COPY(150,9,"采购审批抄送提醒"),
    JXC_RETREAT_COPY(151,9,"采购退货审批抄送提醒"),
    JXC_SALE_COPY(152,9,"销售审批抄送提醒"),
    JXC_SALE_RETURN_COPY(153,9,"销售退货审批抄送提醒"),
    JXC_PAYMENT_COPY(154,9,"付款单审批抄送提醒"),
    JXC_COLLECTION_COPY(155,9,"回款单审批抄送提醒"),
    JXC_INVENTORY_COPY(156,9,"盘点审批抄送提醒"),
    JXC_ALLOCATION_COPY(157,9,"调拨审批抄送提醒"),
    OA_EXAMINE_COPY(158,3,"OA审批抄送提醒"),
    CRM_FLOW_COPY(159,20,"阶段审批抄送"),

    CRM_BUSINESS_IMPORT(160,6,"导入商机通知"),

    CRM_CONTRACT_IMPORT(161,6,"导入合同通知"),
    CRM_INVOICE_IMPORT(162,6,"导入发票通知"),
    CRM_RECEIVABLES_IMPORT(163,6,"导入回款通知"),
    CRM_RECEIVABLES_PLAN_IMPORT(164,6,"导入回款计划通知"),
    CRM_MARKETING_IMPORT(166,6,"导入市场活动通知"),
    ;

    AdminMessageEnum(Integer type, Integer label, String remarks){
        this.type=type;
        this.label=label;
        this.remarks=remarks;
    }
    private final int  type;
    private final int  label;
    private final String remarks;

    public int getType() {
        return type;
    }

    public String getRemarks() {
        return remarks;
    }

    public int getLabel() {
        return label;
    }

    public static AdminMessageEnum parse(int type) {
        for (AdminMessageEnum Enum : AdminMessageEnum.values()) {
            if (Enum.getType()==type) {
                return Enum;
            }
        }
        return NULL;
    }

    public static AdminMessageEnum parse(String remarks) {
        for (AdminMessageEnum Enum : AdminMessageEnum.values()) {
            if (Enum.getRemarks().equals(remarks)) {
                return Enum;
            }
        }
        return NULL;
    }
}
