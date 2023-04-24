package com.kakarote.work.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * <p>
 * 项目公告表
 * </p>
 *
 * @author zhangzhiwei
 * @since 2022-09-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="DeleteProjectAnnouncementBO", description="项目公告表")
public class DeleteProjectAnnouncementBO {

    @ApiModelProperty(value = "项目公告ID")
    @NotEmpty(message = "id不能为空")
    private List<Long> projectAnnouncementId;


}
