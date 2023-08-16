package com.example.security.config;

import com.example.security.config.auth.oauth.PrincipalOauth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity // 스프링 시큐리티 필터가 스프링 필터체인에 등록된다.
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true) // secured 어노테이션 활성화 -> 특정 메서드에 접근권한을 줄 수 있음, preAuthorize, postAuthorize 어노테이션 활성
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private PrincipalOauth2UserService principalOauth2UserService;

    @Bean // 해당 메서드가 리턴하는 오브젝트를 IoC로 등록해준다.
    public BCryptPasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.authorizeRequests()
                .antMatchers("/user/**").authenticated() // 회원이면(인증만 되면) 들어갈 수 있음.
                .antMatchers("/manager/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
                .antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")
                .anyRequest().permitAll()
                .and()
                .formLogin()
                .loginPage("/loginForm")
                .loginProcessingUrl("/login") // "/login" 호출되면 Security가 낚아채서 대신 로그인 진행함.
                .defaultSuccessUrl("/") // 어떠한 요청을 하고 로그인폼으로 리다이렉트된 후 로그인을 하면 원래 요청했던 페이지로 리다이렉트 됨.
                .and()
                .oauth2Login()
                .loginPage("/loginForm") // 구글 로그인이 완료된 뒤의 후처리가 필요함.
                // 1.코드(인증) - 2.엑세스토큰(권한) - 3.사용자프로필정보 - 4.회원가입 / 추가정보입력
                // 그런데 구글 로그인 시 코드X, 엑세스토큰+사용자프로필정보O
                .userInfoEndpoint()
                .userService(principalOauth2UserService);
    }
}
