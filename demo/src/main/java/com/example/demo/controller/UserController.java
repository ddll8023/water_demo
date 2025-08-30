package com.example.demo.controller;

import com.example.demo.common.ApiResponse;
import com.example.demo.common.PageResult;
import com.example.demo.pojo.DTO.system.UserCreateDTO;
import com.example.demo.pojo.DTO.system.UserQueryDTO;
import com.example.demo.pojo.DTO.system.UserUpdateDTO;
import com.example.demo.pojo.VO.UserVO;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 用户管理控制器
 * <p>
 * 提供用户相关的CRUD操作
 * 需要system:manage权限
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户相关的CRUD操作，需要system:manage权限")
public class UserController {

    /**
     * 用户服务
     */
    private final UserService userService;

    /**
     * 分页查询用户列表
     *
     * @param userQueryDTO 查询条件DTO
     * @return 分页的用户信息列表
     */
    @Operation(summary = "分页查询用户列表", description = "根据条件分页查询用户信息")
    @GetMapping("")
    public ResponseEntity<ApiResponse<PageResult<UserVO>>> getUsers(@Valid UserQueryDTO userQueryDTO) {
        PageResult<UserVO> result = userService.getUsers(userQueryDTO);
        return ResponseEntity.ok(ApiResponse.success("查询成功", result));
    }

    /**
     * 创建新用户
     *
     * @param userCreateDTO 创建用户请求DTO
     * @return 创建结果
     */
    @Operation(summary = "创建新用户", description = "创建新用户")
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Void>> createUser(@Valid @RequestBody UserCreateDTO userCreateDTO) {
        userService.createUser(userCreateDTO);
        return ResponseEntity.ok(ApiResponse.success("创建成功", null));
    }

    /**
     * 更新用户信息
     *
     * @param id        用户ID
     * @param updateDTO 更新用户请求DTO
     * @return 更新后的用户信息
     */
    @Operation(summary = "更新用户信息", description = "根据用户ID更新用户信息")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO updateDTO) {
        userService.updateUser(id, updateDTO);
        return ResponseEntity.ok(ApiResponse.success("更新成功"));
    }

    /**
     * 删除用户
     *
     * @param id 用户ID
     * @return 删除结果
     */
    @Operation(summary = "删除用户", description = "根据用户ID删除用户")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("删除成功"));
    }
}
