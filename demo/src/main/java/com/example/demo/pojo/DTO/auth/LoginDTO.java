package com.example.demo.pojo.DTO.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 登录请求DTO
 */
@Data
@Schema(description = "登录请求DTO")
public class LoginDTO implements Serializable {

    @NotBlank(message = "用户名不能为空")
    @Schema(description = "用户名")
//    @Size(min = 3, max = 50, message = "用户名长度必须在3-50个字符之间")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Schema(description = "密码")
    @Size(min = 6, max = 100, message = "密码长度必须在6-100个字符之间")
    private String password;
}
