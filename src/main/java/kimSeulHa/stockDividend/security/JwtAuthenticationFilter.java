package kimSeulHa.stockDividend.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    public static final String TOKEN_HEADER = "Authorization";   //토큰에서 주고 받을 키로 쓸 값을 정의
    public static final String TOKEN_PREFIX = "Bearer ";   //인증 타입 - jwt토큰을 사용하는 경우에 토큰 앞에 bearer이 붙어짐

    private final TokenProvider tokenProvider;

    @Override
    //요청이 들어왔을 때, request에 있는 토큰이 유효한지 확인
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //OncePerRequestFilter 상속시, 필수 메소드
        //요청시, filter -> servlet -> 인터셉터 -> aop레이어 -> 컨트롤러가 실행
        String token = resolveTokenFromRequest(request);

        if(StringUtils.hasText(token) && tokenProvider.validateToken(token)){
            Authentication authentication = tokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            //어떤 사용자가 어떤 url을 요청했는지 로그 남기기
            log.info(String.format("[%s] request -> %s",tokenProvider.getUsername(token),request.getRequestURI()));
        }

        filterChain.doFilter(request,response); //스프링의 필터가 연속적으로 실행될 수 있도록 함
    }

    private String resolveTokenFromRequest(HttpServletRequest request){
        String token = request.getHeader(TOKEN_HEADER);

        if(ObjectUtils.isEmpty(token) && token.startsWith(TOKEN_PREFIX)){
            return token.substring(TOKEN_PREFIX.length());
        }
        return null;
    }
}
