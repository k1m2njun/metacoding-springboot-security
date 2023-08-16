package com.example.security.config.auth;

import com.example.security.model.User;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

// 시큐리티가 /login 요청을 낚아채 로그인을 진행시킴.
// 로그인 수행 후 시큐리티 session을 만들어 줌. - Security ContextHolder
// 오브젝트 타입 -> Authentication 타입의 객체여야 함.
// 이 안에는 User 정보가 있어야 함.
// User 오브젝트 타입 -> UserDetails 타입의 객체여야 함

// Security Session <- Authentication <- UserDetails
@Data
public class PrincipalDetails implements UserDetails, OAuth2User {

    private User user; // 컴포지션

    public PrincipalDetails(User user) {
        this.user = user;
    }

    // 해당 User의 권한을 리턴함
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return user.getRole();
            }
        });
        return collection;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override // 계정 만료 !유무
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override // 계정 정지 !유무
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override // 비밀번호 기간 만료 !유무
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override // 계정 활성화 유무
    public boolean isEnabled() {
        return true;
    }

    // OAuth2User Override
    @Override
    public String getName() {
        return null;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }
}
