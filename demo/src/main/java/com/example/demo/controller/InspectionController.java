package com.example.demo.controller;

import com.example.demo.common.ApiResponse;
import com.example.demo.dto.common.PageResponseDTO;
import com.example.demo.dto.inspection.*;
import com.example.demo.entity.inspection.InspectionAttachment;
import com.example.demo.service.InspectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/inspection")
@RequiredArgsConstructor
public class InspectionController {

    private final InspectionService inspectionService;

    // ========================= 任务 =========================

    @GetMapping("/tasks")
    @PreAuthorize("hasAuthority('data:view')")
    public ResponseEntity<ApiResponse<PageResponseDTO<InspectionTaskResponseDTO>>> getTasks(
            @Valid InspectionTaskQueryDTO queryDTO) {
        try {
            PageResponseDTO<InspectionTaskResponseDTO> result = inspectionService.getTaskPage(queryDTO);
            return ResponseEntity.ok(ApiResponse.success("查询成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        }
    }

    @GetMapping("/tasks/{id}")
    @PreAuthorize("hasAuthority('data:view')")
    public ResponseEntity<ApiResponse<InspectionTaskResponseDTO>> getTaskById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(ApiResponse.success("查询成功", inspectionService.getTaskById(id)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        }
    }

    @PostMapping("/tasks")
    @PreAuthorize("hasAuthority('business:manage')")
    public ResponseEntity<ApiResponse<InspectionTaskResponseDTO>> createTask(@Valid @RequestBody InspectionTaskCreateDTO createDTO) {
        try {
            return ResponseEntity.ok(ApiResponse.success("创建成功", inspectionService.createTask(createDTO)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        }
    }

    @PutMapping("/tasks/{id}")
    @PreAuthorize("hasAuthority('business:manage')")
    public ResponseEntity<ApiResponse<InspectionTaskResponseDTO>> updateTask(@PathVariable Long id,
                                                                             @Valid @RequestBody InspectionTaskUpdateDTO updateDTO) {
        try {
            updateDTO.setId(id);
            return ResponseEntity.ok(ApiResponse.success("更新成功", inspectionService.updateTask(updateDTO)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        }
    }

    @PatchMapping("/tasks/{id}/status")
    @PreAuthorize("hasAuthority('business:manage')")
    public ResponseEntity<ApiResponse<Void>> updateTaskStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            inspectionService.updateTaskStatus(id, status);
            return ResponseEntity.ok(ApiResponse.success("状态更新成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        }
    }

    @DeleteMapping("/tasks/{id}")
    @PreAuthorize("hasAuthority('business:manage')")
    public ResponseEntity<ApiResponse<Void>> deleteTask(@PathVariable Long id) {
        try {
            inspectionService.deleteTask(id);
            return ResponseEntity.ok(ApiResponse.success("删除成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        }
    }

    // ========================= 记录 =========================

    @GetMapping("/records")
    @PreAuthorize("hasAuthority('data:view')")
    public ResponseEntity<ApiResponse<PageResponseDTO<InspectionRecordResponseDTO>>> getRecords(@Valid InspectionRecordQueryDTO queryDTO) {
        try {
            PageResponseDTO<InspectionRecordResponseDTO> result = inspectionService.getRecordPage(queryDTO);
            return ResponseEntity.ok(ApiResponse.success("查询成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        }
    }

    @GetMapping("/records/{id}")
    @PreAuthorize("hasAuthority('data:view')")
    public ResponseEntity<ApiResponse<InspectionRecordResponseDTO>> getRecordById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(ApiResponse.success("查询成功", inspectionService.getRecordById(id)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        }
    }

    @GetMapping("/records/{id}/attachments")
    @PreAuthorize("hasAuthority('data:view')")
    public ResponseEntity<ApiResponse<List<InspectionAttachment>>> getRecordAttachments(@PathVariable("id") Long recordId) {
        try {
            return ResponseEntity.ok(ApiResponse.success("查询成功", inspectionService.getRecordAttachments(recordId)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        }
    }

    @PostMapping(value = "/records", consumes = {"multipart/form-data"})
    @PreAuthorize("hasAuthority('business:operate')")
    public ResponseEntity<ApiResponse<InspectionRecordResponseDTO>> createRecord(
            @Valid @RequestPart("data") InspectionRecordCreateDTO createDTO,
            @RequestPart(value = "files", required = false) MultipartFile[] files) {
        try {
            InspectionRecordResponseDTO result = inspectionService.createRecord(createDTO, files);
            return ResponseEntity.ok(ApiResponse.success("创建成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        }
    }

    // 新增：再次处理接口（使用请求参数保持与前端一致）
    @PatchMapping("/records/{id}/resolve")
    @PreAuthorize("hasAuthority('business:operate')")
    public ResponseEntity<ApiResponse<Void>> resolveRecord(@PathVariable Long id, @RequestParam String resolution) {
        try {
            inspectionService.resolveRecord(id, resolution);
            return ResponseEntity.ok(ApiResponse.success("处理完成"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        }
    }
} 