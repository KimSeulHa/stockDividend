package kimSeulHa.stockDividend.model;

import kimSeulHa.stockDividend.persist.entity.MemberEntity;
import lombok.Data;

import java.util.List;

public class Auth {
    @Data
    public static class login{
        private String username;
        private String password;
    }
    @Data
    public static class register{
        private String username;
        private String password;
        private List<String> roles;

        public MemberEntity toEntity(){
            return MemberEntity.builder()
                    .username(this.username)
                    .password(this.password)
                    .roles(this.roles)
                    .build();
        }
    }
}
