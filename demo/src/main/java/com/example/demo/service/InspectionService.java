package com.example.demo.service;

import com.example.demo.pojo.dto.common.PageResponseDTO;
import com.example.demo.pojo.dto.inspection.*;
import com.example.demo.pojo.dto.system.DictDataResponseDTO;
import com.example.demo.pojo.entity.inspection.InspectionAttachment;
import com.example.demo.pojo.entity.inspection.InspectionRecord;
import com.example.demo.pojo.entity.inspection.InspectionTask;
import com.example.demo.mapper.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class InspectionService {

    private final InspectionTaskMapper inspectionTaskMapper;
    private final InspectionRecordMapper inspectionRecordMapper;
    private final InspectionAttachmentMapper inspectionAttachmentMapper;
    private final DictionaryService dictionaryService;

    @Value("${file.upload-dir:uploads/inspection}")
    private String uploadDir;

    // ========================= 任务相关 =========================

    public PageResponseDTO<InspectionTaskResponseDTO> getTaskPage(InspectionTaskQueryDTO queryDTO) {
        // 兼容：如果status传入了逗号分隔的多状态，自动拆分到statuses
        if ((queryDTO.getStatuses() == null || queryDTO.getStatuses().isEmpty())
                && queryDTO.getStatus() != null && queryDTO.getStatus().contains(",")) {
            List<String> list = Arrays.asList(queryDTO.getStatus().split(","));
            queryDTO.setStatuses(list);
            queryDTO.setStatus(null);
        }

        int page = (queryDTO.getPage() == null || queryDTO.getPage() < 1) ? 1 : queryDTO.getPage();
        int size = (queryDTO.getSize() == null || queryDTO.getSize() < 1) ? 10 : queryDTO.getSize();
        PageHelper.startPage(page, size);
        List<InspectionTaskResponseDTO> list = inspectionTaskMapper.selectTaskPage(
                queryDTO.getStatus(),
                queryDTO.getStatuses(),
                queryDTO.getAssigneeId(),
                queryDTO.getFacilityType(),
                queryDTO.getFacilityId(),
                queryDTO.getKeyword(),
                queryDTO.getFrequency(),
                queryDTO.getScheduledDate() != null ? queryDTO.getScheduledDate().toString() : null,
                queryDTO.getSort()
        );
        PageInfo<InspectionTaskResponseDTO> pageInfo = new PageInfo<>(list);
        return new PageResponseDTO<>(pageInfo.getList(), (int) pageInfo.getTotal(), pageInfo.getPageNum(), pageInfo.getPageSize());
    }

    public InspectionTaskResponseDTO getTaskById(Long id) {
        InspectionTaskResponseDTO dto = inspectionTaskMapper.selectTaskById(id);
        if (dto == null) {
            throw new RuntimeException("任务不存在，ID:" + id);
        }
        return dto;
    }

    @Transactional
    public InspectionTaskResponseDTO createTask(InspectionTaskCreateDTO createDTO) {
        InspectionTask task = new InspectionTask();
        BeanUtils.copyProperties(createDTO, task);
        if (task.getStatus() == null || task.getStatus().isEmpty()) {
            task.setStatus("PENDING");
        }
        inspectionTaskMapper.insertTask(task);
        return getTaskById(task.getId());
    }

    @Transactional
    public InspectionTaskResponseDTO updateTask(InspectionTaskUpdateDTO updateDTO) {
        // 确保存在
        InspectionTaskResponseDTO exist = inspectionTaskMapper.selectTaskById(updateDTO.getId());
        if (exist == null) {
            throw new RuntimeException("任务不存在，ID:" + updateDTO.getId());
        }
        InspectionTask task = new InspectionTask();
        BeanUtils.copyProperties(updateDTO, task);
        inspectionTaskMapper.updateTask(task);
        return getTaskById(updateDTO.getId());
    }

    @Transactional
    public void deleteTask(Long id) {
        InspectionTaskResponseDTO exist = inspectionTaskMapper.selectTaskById(id);
        if (exist == null) {
            throw new RuntimeException("任务不存在，ID:" + id);
        }
        inspectionTaskMapper.softDeleteTask(id);
    }

    @Transactional
    public void updateTaskStatus(Long id, String status) {
        InspectionTaskResponseDTO exist = inspectionTaskMapper.selectTaskById(id);
        if (exist == null) {
            throw new RuntimeException("任务不存在，ID:" + id);
        }

        // 验证状态值是否有效
        if (!isValidInspectionStatus(status)) {
            throw new RuntimeException("无效的巡检状态值：" + status);
        }

        InspectionTask task = new InspectionTask();
        task.setId(id);
        task.setStatus(status);
        inspectionTaskMapper.updateTask(task);
    }

    // ========================= 记录相关 =========================

    public PageResponseDTO<InspectionRecordResponseDTO> getRecordPage(InspectionRecordQueryDTO queryDTO) {
        int page = (queryDTO.getPage() == null || queryDTO.getPage() < 1) ? 1 : queryDTO.getPage();
        int size = (queryDTO.getSize() == null || queryDTO.getSize() < 1) ? 10 : queryDTO.getSize();
        PageHelper.startPage(page, size);
        List<InspectionRecordResponseDTO> list = inspectionRecordMapper.selectRecordPage(
                queryDTO.getFacilityType(),
                queryDTO.getFacilityId(),
                queryDTO.getStartTime(),
                queryDTO.getEndTime(),
                queryDTO.getIssueFlag(),
                queryDTO.getProcessed(),
                queryDTO.getInspectorId(),
                queryDTO.getSort()
        );
        PageInfo<InspectionRecordResponseDTO> pageInfo = new PageInfo<>(list);
        return new PageResponseDTO<>(pageInfo.getList(), (int) pageInfo.getTotal(), pageInfo.getPageNum(), pageInfo.getPageSize());
    }

    public InspectionRecordResponseDTO getRecordById(Long id) {
        InspectionRecordResponseDTO dto = inspectionRecordMapper.selectRecordById(id);
        if (dto == null) {
            throw new RuntimeException("记录不存在，ID:" + id);
        }
        return dto;
    }

    public List<InspectionAttachment> getRecordAttachments(Long recordId) {
        return inspectionAttachmentMapper.selectByRecordId(recordId);
    }

    @Transactional(rollbackFor = Exception.class)
    public InspectionRecordResponseDTO createRecord(InspectionRecordCreateDTO createDTO, MultipartFile[] files) {
        InspectionRecord record = new InspectionRecord();
        BeanUtils.copyProperties(createDTO, record);
        inspectionRecordMapper.insertRecord(record);

        // 保存附件
        List<InspectionAttachment> attachments = saveAttachments(record.getId(), files);
        if (!attachments.isEmpty()) {
            inspectionAttachmentMapper.insertBatch(attachments);
        }

        // 任务联动
        if (record.getTaskId() != null) {
            if (Objects.equals(record.getIssueFlag(), 1)) {
                updateTaskStatus(record.getTaskId(), "EXCEPTION");
            } else {
                updateTaskStatus(record.getTaskId(), "COMPLETED");
            }
        }

        return getRecordById(record.getId());
    }

    @Transactional
    public void resolveRecord(Long id, String resolution) {
        // 校验记录存在
        InspectionRecordResponseDTO exist = inspectionRecordMapper.selectRecordById(id);
        if (exist == null) {
            throw new RuntimeException("记录不存在，ID:" + id);
        }
        inspectionRecordMapper.updateResolution(id, resolution);
    }

    // ========================= 辅助方法 =========================

    /**
     * 验证巡检状态值是否有效
     */
    private boolean isValidInspectionStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            return false;
        }

        try {
            // 通过字典服务获取有效的巡检状态值
            List<DictDataResponseDTO> validStatuses = dictionaryService.getDictDataByTypeCode("inspection_status");
            return validStatuses.stream()
                    .anyMatch(dictData -> status.equals(dictData.getDataValue()));
        } catch (Exception e) {
            log.error("验证巡检状态时发生错误: {}", e.getMessage());
            // 如果字典服务异常，使用硬编码的有效值作为备选方案
            return "PENDING".equals(status) || "IN_PROGRESS".equals(status) ||
                   "COMPLETED".equals(status) || "EXCEPTION".equals(status);
        }
    }

    private List<InspectionAttachment> saveAttachments(Long recordId, MultipartFile[] files) {
        List<InspectionAttachment> list = new ArrayList<>();
        if (files == null || files.length == 0) {
            return list;
        }
        // 目录：uploads/inspection/yyyy/MM/dd
        LocalDate today = LocalDate.now();
        String datePath = today.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        Path baseDir = Paths.get(uploadDir, datePath);
        try {
            Files.createDirectories(baseDir);
        } catch (IOException e) {
            throw new RuntimeException("创建上传目录失败: " + baseDir, e);
        }
        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) continue;
            String original = file.getOriginalFilename();
            String safeName = (original == null ? "file" : original).replaceAll("[\\/:*?\"<>|]", "_");
            String filename = System.currentTimeMillis() + "-" + safeName;
            Path savePath = baseDir.resolve(filename);
            try {
                file.transferTo(savePath.toFile());
            } catch (IOException e) {
                throw new RuntimeException("保存文件失败: " + safeName, e);
            }
            InspectionAttachment att = new InspectionAttachment();
            att.setRecordId(recordId);
            att.setFileName(safeName);
            // 公网访问路径以 /uploads 开头
            String publicPath = "/uploads/" + normalizePublicPath(Paths.get("inspection", datePath, filename).toString());
            att.setFilePath(publicPath);
            att.setContentType(file.getContentType());
            att.setFileSize(file.getSize());
            att.setCreatedAt(LocalDateTime.now());
            list.add(att);
        }
        return list;
    }

    private String normalizePublicPath(String path) {
        return path.replace(File.separatorChar, '/');
    }
} 