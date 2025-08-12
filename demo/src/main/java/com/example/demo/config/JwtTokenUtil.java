package com.example.demo.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT工具类
 * 
 * 本类是JWT认证机制的核心工具类，负责：
 * 1. 生成JWT令牌（访问令牌和刷新令牌）
 * 2. 解析JWT令牌内容
 * 3. 验证JWT令牌的有效性
 * 4. 提取令牌中的用户信息
 * 
 * JWT(JSON Web Token)的结构包含三部分：
 * - 头部(Header)：指定加密算法和令牌类型
 * - 载荷(Payload)：包含声明(Claims)，如用户身份、过期时间等
 * - 签名(Signature)：使用密钥对头部和载荷进行签名，确保数据完整性
 */
@Component  // 注册为Spring组件
public class JwtTokenUtil {

    /**
     * JWT签名密钥
     * 从application.properties配置文件中读取
     * 用于生成令牌签名，确保令牌未被篡改
     */
    @Value("${jwt.secret}")
    private String secret;

    /**
     * 访问令牌过期时间（毫秒）
     * 从application.properties配置文件中读取
     */
    @Value("${jwt.expiration}")
    private Long expiration;

    /**
     * 刷新令牌过期时间（毫秒）
     * 从application.properties配置文件中读取
     * 通常比访问令牌有更长的有效期
     */
    @Value("${jwt.refresh-expiration}")
    private Long refreshExpiration;

    /**
     * 从令牌中提取用户名
     * 
     * 使用令牌中的subject声明作为用户名
     * 
     * @param token JWT令牌字符串
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * 从令牌中提取过期时间
     * 
     * @param token JWT令牌字符串
     * @return 过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * 从令牌中提取指定类型的声明
     * 
     * 使用函数式接口作为参数，提高代码复用性和灵活性
     * 
     * @param token JWT令牌字符串
     * @param claimsResolver 声明解析函数
     * @return 解析后的声明值
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 从令牌中获取所有声明
     * 
     * 解析JWT令牌并验证其签名，返回所有声明内容
     * 
     * @param token JWT令牌字符串
     * @return 所有声明
     */
    private Claims getAllClaimsFromToken(String token) {
        // 使用JWT解析器验证并解析令牌
        return Jwts.parser()
                .verifyWith(getSigningKey())  // 使用签名密钥验证令牌
                .build()
                .parseSignedClaims(token)     // 解析已签名的令牌
                .getPayload();                // 获取载荷部分
    }

    /**
     * 检查令牌是否已过期
     * 
     * 通过比较当前时间和令牌过期时间判断令牌是否已过期
     * 
     * @param token JWT令牌字符串
     * @return 如果过期返回true，否则返回false
     */
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());  // 过期时间早于当前时间则表示已过期
    }

    /**
     * 为用户生成标准访问令牌
     * 
     * @param userDetails 用户详情对象
     * @return 生成的JWT令牌字符串
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    /**
     * 为用户生成带有额外声明的访问令牌
     * 
     * 可以在令牌中添加额外信息，如用户ID、角色等
     * 
     * @param userDetails 用户详情对象
     * @param extraClaims 额外的声明键值对
     * @return 生成的JWT令牌字符串
     */
    public String generateToken(UserDetails userDetails, Map<String, Object> extraClaims) {
        Map<String, Object> claims = new HashMap<>(extraClaims);
        return createToken(claims, userDetails.getUsername());
    }

    /**
     * 创建JWT令牌
     * 
     * 设置声明内容、主题、颁发时间、过期时间，并使用密钥签名
     * 
     * @param claims 声明内容
     * @param subject 主题（通常是用户名）
     * @return 生成的JWT令牌字符串
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)                                               // 设置声明内容
                .subject(subject)                                             // 设置主题（用户标识）
                .issuedAt(new Date(System.currentTimeMillis()))               // 设置颁发时间
                .expiration(new Date(System.currentTimeMillis() + expiration)) // 设置过期时间
                .signWith(getSigningKey())                                     // 使用密钥签名
                .compact();                                                    // 生成紧凑字符串形式
    }

    /**
     * 生成刷新令牌
     * 
     * 刷新令牌用于获取新的访问令牌，通常具有更长的有效期
     * 添加"type":"refresh"声明以标识这是刷新令牌
     * 
     * @param userDetails 用户详情对象
     * @return 生成的刷新令牌字符串
     */
    public String generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh");  // 标记为刷新令牌
        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + refreshExpiration))  // 使用更长的过期时间
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 验证令牌对指定用户是否有效
     * 
     * 验证内容包括：
     * 1. 令牌中的用户名与给定用户名是否匹配
     * 2. 令牌是否已过期
     * 
     * @param token JWT令牌字符串
     * @param userDetails 用户详情对象
     * @return 如果有效返回true，否则返回false
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * 验证令牌是否有效（不需要用户详情）
     * 
     * 仅验证令牌的签名和过期状态，不验证用户
     * 用于令牌预检查，比如在过滤器中快速判断令牌是否有效
     * 
     * @param token JWT令牌字符串
     * @return 如果有效返回true，否则返回false
     */
    public Boolean validateToken(String token) {
        try {
            // 尝试解析令牌，验证签名
            Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token);
            // 验证是否过期
            return !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            // 解析失败、签名无效或其他异常情况
            return false;
        }
    }

    /**
     * 获取用于签名的密钥
     * 
     * 将配置的密钥字符串转换为适合HMAC-SHA算法的SecretKey对象
     * 
     * @return 签名密钥对象
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);  // 使用HMAC-SHA算法生成密钥
    }

    /**
     * 从令牌中获取用户ID
     * 
     * 假设令牌中包含"userId"声明
     * 
     * @param token JWT令牌字符串
     * @return 用户ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.get("userId", Long.class);  // 提取并转换为Long类型
    }

    /**
     * 检查是否为刷新令牌
     * 
     * 通过检查令牌中是否包含type="refresh"声明
     * 
     * @param token JWT令牌字符串
     * @return 如果是刷新令牌返回true，否则返回false
     */
    public Boolean isRefreshToken(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            return "refresh".equals(claims.get("type"));  // 检查type声明是否为"refresh"
        } catch (Exception e) {
            // 解析失败时认为不是刷新令牌
            return false;
        }
    }
}
