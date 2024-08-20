package com.atguigu.lease.common.utils;

import com.atguigu.lease.common.exception.LeaseException;
import com.atguigu.lease.common.result.ResultCodeEnum;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.StringTokenizer;

public class JwtUtil {
    // 设置密钥
    private static SecretKey secretKey = Keys.hmacShaKeyFor("asdfgasdasdfgasdasdfgasdasdfgasd".getBytes());

    public static String createToken(Long userid, String username) {

        // payload, signature[.signWith()]
        String jwt = Jwts.builder()
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .setSubject("LOGIN_USER")
                .claim("userId", userid)
                .claim("username", username)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        return jwt;
    }

    /**
     * 校验 token
     * @param token
     */
    public static Claims parseToken(String token){
        // 验证 token
        if(token == null) {
            throw new LeaseException(ResultCodeEnum.ADMIN_LOGIN_AUTH);
        }

        // token 解析，校验 token 是否正确
        try {
            JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(secretKey).build();
            Jws<Claims> claimsJws = jwtParser.parseClaimsJws(token);
            return claimsJws.getBody();
        } catch (ExpiredJwtException e) {
            throw new LeaseException(ResultCodeEnum.TOKEN_EXPIRED);
        } catch (JwtException e) {
            throw new LeaseException(ResultCodeEnum.TOKEN_INVALID);
        }
    }

    public static void main(String[] args) {
        System.out.println(createToken(2L, "user"));
    }
}
