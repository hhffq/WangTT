import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;


import java.util.Date;

public class jjwt {
    public static void main(String[] args) {

        long l = System.currentTimeMillis();
        l += 100000L;


        Date date = new Date(l);
        JwtBuilder builder = Jwts.builder()
                .setId("888")   //设置唯一编号
                .setSubject("{\n" +
                        "    \"loginName\": \"admin\",\n" +
                        "    \"password\": \"123456\"\n" +
                        "}")//设置主题  可以是JSON数据
                .setIssuedAt(new Date())//设置签发日期

                .setExpiration(date) // 设置过期时间，参数为Date类型数据
                .claim("roles", "admin")// 设置角色

                .signWith(SignatureAlgorithm.HS256, "itcast");//设置签名 使用HS256算法，并设置SecretKey(字符串)
//构建 并返回一个字符串
        System.out.println(builder.compact());
    }
}