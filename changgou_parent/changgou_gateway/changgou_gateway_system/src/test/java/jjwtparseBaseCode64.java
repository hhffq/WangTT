import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class jjwtparseBaseCode64 {
    public static void main(String[] args) {

        String compactJwt="eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI4ODgiLCJzdWIiOiJ7J2hhJzonaGFoJywnaGFoYSc6Jzg4J30iLCJpYXQiOjE3NTM3OTc4NjIsImV4cCI6MTc1Mzc5Nzk2Miwicm9sZXMiOiJhZG1pbiJ9.FqNmEihmzQKjft3NsBjjtEHRd7YmXMrHwx1FdfflPt4";

        Claims claims = Jwts.parser()
                .setSigningKey("itcast")
                .parseClaimsJws(compactJwt)
                .getBody();
        System.out.println(claims);

    }
}