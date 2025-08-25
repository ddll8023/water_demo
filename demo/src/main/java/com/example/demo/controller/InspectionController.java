package com.example.demo.controller;

import com.example.demo.common.ApiResponse;
import com.example.demo.pojo.DTO.common.PageResponseDTO;
import com.example.demo.pojo.DTO.inspection.*;
import com.example.demo.pojo.entity.inspection.InspectionAttachment;
import com.example.demo.service.InspectionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/inspection")
@Tag(name = "巡检管理", description = "巡检任务和记录相关的增删改查操作")
public class InspectionController {

    @Autowired
    private InspectionService inspectionService;

    // ========================= 任务 =========================

    @GetMapping("/tasks")
    
    @Operation(summary = "获取巡检任务分页数据", description = "支持按任务状态、时间范围等条件筛选")
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
    
    @Operation(summary = "获取巡检任务详情", description = "根据ID查询巡检任务详细信息")
    public ResponseEntity<ApiResponse<InspectionTaskResponseDTO>> getTaskById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(ApiResponse.success("查询成功", inspectionService.getTaskById(id)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        }
    }

    @PostMapping("/tasks")
    
    @Operation(summary = "创建巡检任务", description = "创建新的巡检任务，需提供必要的任务信息")
    public ResponseEntity<ApiResponse<InspectionTaskResponseDTO>> createTask(@Valid @RequestBody InspectionTaskCreateDTO createDTO) {
        try {
            return ResponseEntity.ok(ApiResponse.success("创建成功", inspectionService.createTask(createDTO)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        }
    }

    @PutMapping("/tasks/{id}")
    
    @Operation(summary = "更新巡检任务信息", description = "根据ID更新巡检任务的基本信息")
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
    
    @Operation(summary = "更新巡检任务状态", description = "根据ID更新巡检任务的状态")
    public ResponseEntity<ApiResponse<Void>> updateTaskStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            // 参数验证
            if (status == null || status.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.error(400, "状态参数不能为空"));
            }

            inspectionService.updateTaskStatus(id, status);
            return ResponseEntity.ok(ApiResponse.success("状态更新成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        }
    }

    @DeleteMapping("/tasks/{id}")
    
    @Operation(summary = "删除巡检任务", description = "根据ID删除巡检任务（软删除）")
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
    
    @Operation(summary = "获取巡检记录分页数据", description = "支持按任务、时间范围等条件筛选")
    public ResponseEntity<ApiResponse<PageResponseDTO<InspectionRecordResponseDTO>>> getRecords(@Valid InspectionRecordQueryDTO queryDTO) {
        try {
            PageResponseDTO<InspectionRecordResponseDTO> result = inspectionService.getRecordPage(queryDTO);
            return ResponseEntity.ok(ApiResponse.success("查询成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        }
    }

    @GetMapping("/records/{id}")
    
    @Operation(summary = "获取巡检记录详情", description = "根据ID查询巡检记录详细信息")
    public ResponseEntity<ApiResponse<InspectionRecordResponseDTO>> getRecordById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(ApiResponse.success("查询成功", inspectionService.getRecordById(id)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        }
    }

    @GetMapping("/records/{id}/attachments")
    
    @Operation(summary = "获取巡检记录附件", description = "根据记录ID获取关联的附件列表")
    public ResponseEntity<ApiResponse<List<InspectionAttachment>>> getRecordAttachments(@PathVariable("id") Long recordId) {
        try {
            return ResponseEntity.ok(ApiResponse.success("查询成功", inspectionService.getRecordAttachments(recordId)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        }
    }

    @PostMapping(value = "/records", consumes = {"multipart/form-data"})
    
        @Operation(summary = "创建巡检记录", description = "创建新的巡检记录，需提供必要的记录信息和附件")
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
    
    @Operation(summary = "处理巡检记录", description = "根据ID处理巡检记录，支持指定处理结果")
    public ResponseEntity<ApiResponse<Void>> resolveRecord(@PathVariable Long id, @RequestParam String resolution) {
        try {
            inspectionService.resolveRecord(id, resolution);
            return ResponseEntity.ok(ApiResponse.success("处理完成"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        }
    }
} 
