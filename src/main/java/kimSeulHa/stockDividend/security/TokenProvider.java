package kimSeulHa.stockDividend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import kimSeulHa.stockDividend.service.MemberService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TokenProvider {

    private String key= "aGVsbG8KaGVsbG8KaGVsbG8KaGVsbG8KaGVsbG8KaGVsbG8KaGVsbG8KaGVsbG8K";

    private final MemberService memberService;

    //토큰 발급
    public String generateToken(String username, List<String> roles){
        //사용자 권한 정보를 저장하기 위함
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("roles",roles);

        Date now = new Date();
        Date ExpireDate = new Date(now.getTime()+(1000 * 60 * 60)); //현재시간부터 1시간 후

        return  Jwts.builder().setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(ExpireDate)
                .signWith(SignatureAlgorithm.ES512,key) //사용할 암호화 알고리즘과 비밀키
                .compact();
    }
    public String getUsername(String token){
        return parseClaims(token).getSubject();
    }

    public boolean validateToken(String token){
        if(!StringUtils.hasText(token)){
            return false;
        }
        Claims claims = this.parseClaims(token);
        //토큰의 만료시간을 가져와 현재시간 보다 이전이 아닌지 확인
        // ture -> 사용가능 토큰 , false -> 사용 불가능 토큰
        return !claims.getExpiration().before(new Date());
    }
    //토큰 키로 부터 클레임 정보를 가져오기
    private Claims parseClaims(String token){
        try{
            return Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
        }catch (ExpiredJwtException e){
            //인자로 들어온 토큰이 만료될 경우를 위해 exception발생
            return e.getClaims();
        }

    }

    public Authentication getAuthentication(String token){
        UserDetails userDetails = memberService.loadUserByUsername(getUsername(token));
        //스프링에서 지원해주는 토큰을 반환(사용자 정보와, 사용자 권한 정보 포함)
        return new UsernamePasswordAuthenticationToken(userDetails,"",userDetails.getAuthorities());
    }


}
