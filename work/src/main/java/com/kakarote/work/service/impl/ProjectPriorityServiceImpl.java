package com.kakarote.work.service.impl;

import com.kakarote.work.service.IProjectPriorityService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 事件绑定属性表 服务实现类
 * </p>
 *
 * @author bai
 * @since 2022-09-20
 */
@Service
public class ProjectPriorityServiceImpl  implements IProjectPriorityService {


    /**
     * 获取优先级描述
     *
     * @param priority
     * @return java.lang.String
     * @date 2020/11/12 10:22
     **/
    @Override
    public String getPriorityDesc(Integer priority) {
        String priorityDesc = "";
        switch (priority) {
            case 0:
                priorityDesc = "无";
                break;
            case 1:
                priorityDesc = "低";
                break;
            case 2:
                priorityDesc = "中";
                break;
            case 3:
                priorityDesc = "高";
                break;
            default:
                break;
        }
        return priorityDesc;
    }
}
