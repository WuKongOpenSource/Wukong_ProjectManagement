package com.kakarote.work.service;

import com.kakarote.common.servlet.BaseService;
import com.kakarote.work.common.admin.AdminDeleteByBatchIdBO;
import com.kakarote.work.entity.PO.AdminFile;
import com.kakarote.work.common.project.FileUploadResultVO;
import com.kakarote.work.common.project.RenameFileBO;
import com.kakarote.work.entity.BO.FileEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * <p>
 * 附件表 服务类
 * </p>
 *
 * @author zhangzhiwei
 * @since 2020-04-27
 */
public interface IProjectFileService extends BaseService<AdminFile> {

    /**
     * 文件上传
     *
     * @param file      文件对象
     * @param batchId   批次 ID
     * @param overwrite 重写
     * @param type      文件类型
     * @param isPublic  是否公有
     * @return
     * @throws IOException
     */
    FileUploadResultVO upload(MultipartFile file,
                              String batchId,
                              Boolean overwrite,
                              String type,
                              Boolean isPublic) throws IOException;

    /**
     * 文件下载
     *
     * @param fileId   文件 ID
     * @param response http 响应
     */
    void download(Long fileId, HttpServletResponse response);

    /**
     * 根据 fileId 删除文件
     *
     * @param fileId 文件 ID
     */
    void deleteById(Long fileId);

    /**
     * 根据 batchId 删除文件
     *
     * @param requestBO
     */
    void deleteByBatchId(AdminDeleteByBatchIdBO requestBO);

    /**
     * 文件重命名
     *
     * @param requestBO
     */
    void renameFile(RenameFileBO requestBO);

    /**
     * 查询文件列表
     *
     * @param batchId 批次 ID
     * @return
     */
    List<FileEntity> queryFileList(String batchId);

    /**
     * 根据批次 ID 查询文件
     *
     * @param batchId 批次 ID
     * @return
     */
    FileEntity queryByBatchId(String batchId);

    /**
     * 根据文件 ID获取文件 URL
     *
     * @param fieldId 文件 ID
     * @return
     */
    String getUrl(Long fieldId);

}
