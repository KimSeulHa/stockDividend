package kimSeulHa.stockDividend.service;

import kimSeulHa.stockDividend.exception.impl.AlreadyExistUserException;
import kimSeulHa.stockDividend.model.Auth;
import kimSeulHa.stockDividend.persist.entity.MemberEntity;
import kimSeulHa.stockDividend.persist.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
@Slf4j
@Service
@AllArgsConstructor
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //UserDetailsService 상속시, 필수 메소드
        return memberRepository.findByUsername(username)
                            .orElseThrow(()->new AlreadyExistUserException());
                            //orElseThrow를 하게 되면 optional이 벗겨진 memberEntity가 반환된다.
                            //memberEntity는 userDetail를 상속받은 상태

    }

    public MemberEntity register(Auth.register member){
        if(memberRepository.existsByUsername(member.getUsername())){
            throw new AlreadyExistUserException();
        }

        //패스워드 암호화
        member.setPassword(passwordEncoder.encode(member.getPassword()));
        MemberEntity memberEntity = memberRepository.save(member.toEntity());

        return memberEntity;
    }

    public MemberEntity authentication(Auth.login member){
        MemberEntity memberEntity = memberRepository.findByUsername(member.getUsername())
                                .orElseThrow(()-> new RuntimeException("존재하지 않는 아이디입니다."));

        if(!passwordEncoder.matches(memberEntity.getPassword(),member.getPassword())){
            throw new RuntimeException("비밀번호가 틀립니다.");
        }
        return memberEntity;
    }
}
