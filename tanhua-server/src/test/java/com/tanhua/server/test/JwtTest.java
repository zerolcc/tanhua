package com.tanhua.server.test;

import com.tanhua.commons.exception.TanHuaException;
import com.tanhua.domain.vo.ErrorResult;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class JwtTest {
    @Test
    public void testJwt() {
        // 密钥
        String secret = "itcast";
        // 自定要保存信息，需要在解密获取
        Map<String, Object> claims = new HashMap<String, Object>();
        claims.put("mobile", "12345789");
        claims.put("id", "2");

        // 生成token
        String jwt = Jwts.builder()
                .setClaims(claims) //设置响应数据体
                .signWith(SignatureAlgorithm.HS256, secret) //设置加密方法和加密盐
                .compact();

        System.out.println("token=" + jwt); //eyJhbGciOiJIUzI1NiJ9.eyJtb2JpbGUiOiIxMjM0NTc4OSIsImlkIjoiMiJ9.VivsfLzrsKFOJo_BdGIf6cKY_7wr2jMOMOIGaFt_tps

        // 通过token解析数据
        Map<String, Object> body = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(jwt)
                .getBody();

        System.out.println("解密后=" + body); //{mobile=12345789, id=2}
    }

    /**
     * 生成jwt使用的密钥
     */
    @Test
    public void createSecret() {
        System.out.println(DigestUtils.md5Hex("itcast_tanhua"));
        //76bd425b6f29f7fcc2e0bfc286043df1
    }
}
