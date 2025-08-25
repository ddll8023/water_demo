package com.example.demo.utils;

import com.example.demo.config.JwtProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

/**
 * JWT工具类
 * 提供JWT令牌的生成和验证功能
 */
@Component
public class JwtTokenUtil {

    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 生成JWT
     * 使用HS256算法, 私钥使用固定密钥
     *
     * @param secretKey jwt密钥
     * @param ttlMillis jwt过期时间(毫秒)
     * @param claims    设置的信息
     * @return
     */
    public static String createJWT(String secretKey, long ttlMillis, Map<String, Object> claims) {
        // 生成JWT的时间
        long expMillis = System.currentTimeMillis() + ttlMillis;
        Date exp = new Date(expMillis);

        // 创建密钥对象
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        // 设置jwt的body
        JwtBuilder builder = Jwts.builder()
                // 如果有私有声明，一定要先设置这个自己创建的私有的声明，这个是给builder的claim赋值，一旦写在标准的声明赋值之后，就是覆盖了那些标准的声明的
                .claims(claims)
                // 设置签名使用的密钥（使用新版本API）
                .signWith(key)
                // 设置过期时间
                .expiration(exp);

        return builder.compact();
    }

    /**
     * Token解密
     *
     * @param secretKey jwt密钥 此密钥一定要保留好在服务端, 不能暴露出去, 否则sign就可以被伪造, 如果对接多个客户端建议改造成多个
     * @param token     加密后的token
     * @return
     */
    public static Claims parseJWT(String secretKey, String token) {
        // 得到DefaultJwtParser
        Claims claims = Jwts.parser()
                // 设置签名的密钥
                .verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                .build()
                // 设置需要解析的jwt
                .parseSignedClaims(token).getPayload();
        return claims;
    }

    /**
     * 从令牌中提取用户名
     *
     * @param token JWT令牌字符串
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = parseJWT(jwtProperties.getAdminSecretKey(), token);
        return claims.getSubject();
    }

    /**
     * 从令牌中提取过期时间
     *
     * @param token JWT令牌字符串
     * @return 过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        Claims claims = parseJWT(jwtProperties.getAdminSecretKey(), token);
        return claims.getExpiration();
    }

    /**
     * 检查令牌是否已过期
     *
     * @param token JWT令牌字符串
     * @return 如果过期返回true，否则返回false
     */
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * 生成访问令牌
     *
     * @param username 用户名
     * @param claims   额外声明
     * @return 生成的JWT令牌字符串
     */
    public String generateToken(String username, Map<String, Object> claims) {
        return createJWT(jwtProperties.getAdminSecretKey(), jwtProperties.getAdminTtl(), claims);
    }



    /**
     * 验证令牌是否有效
     *
     * @param token JWT令牌字符串
     * @return 如果有效返回true，否则返回false
     */
    public Boolean validateToken(String token) {
        try {
            parseJWT(jwtProperties.getAdminSecretKey(), token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 从令牌中获取用户ID
     *
     * @param token JWT令牌字符串
     * @return 用户ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = parseJWT(jwtProperties.getAdminSecretKey(), token);
        return claims.get("userId", Long.class);
    }



    /**
     * 从HTTP请求中提取认证Token
     * 
     * @param request HTTP请求对象
     * @return 提取的Token字符串，如果没有找到则返回null
     */
    public String extractTokenFromRequest(HttpServletRequest request) {
        // 从Authorization请求头中提取Bearer令牌
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            // 去掉"Bearer "前缀，返回实际的Token
            return bearerToken.substring(7);
        }
        return null;
    }
}
