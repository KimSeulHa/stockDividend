package kimSeulHa.stockDividend.web;

import kimSeulHa.stockDividend.model.Auth;
import kimSeulHa.stockDividend.persist.entity.MemberEntity;
import kimSeulHa.stockDividend.security.TokenProvider;
import kimSeulHa.stockDividend.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final MemberService memberService;
    private final TokenProvider tokenProvider;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Auth.register member){
        MemberEntity memberEntity = memberService.register(member);
        return ResponseEntity.ok(memberEntity);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Auth.login member){
        MemberEntity memberEntity = memberService.authentication(member);
        String token = tokenProvider.generateToken(memberEntity.getUsername(),memberEntity.getRoles());

        return ResponseEntity.ok(token);

    }
}
