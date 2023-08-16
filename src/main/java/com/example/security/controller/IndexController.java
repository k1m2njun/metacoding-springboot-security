package com.example.security.controller;

import com.example.security.config.auth.PrincipalDetails;
import com.example.security.model.User;
import com.example.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class IndexController {

    @Autowired
    private UserRepository userRepository;

    @Autowired @Lazy
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/test/login")
    public @ResponseBody String testLogin(
            Authentication authentication,
            @AuthenticationPrincipal PrincipalDetails userDetails // 세션 정보에 접근 가능한 어노테이션
            ) { // DI(의존성 주입)

        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();

        return principalDetails.getUser().toString();
        // 즉, 세션 정보(User 객체)에 접근 가능한 방법은 아래 2가지가 있다.
        // 1. @AuthenticationPrincipal 어노테이션을 사용하는 방법
        // 2. Autentication 타입의 오브젝트에서 getPrincipal() 를 다운캐스팅 하는 방법
    }

    @GetMapping("/test/oauth/login")
    public @ResponseBody String testOAuthLogin(
            Authentication authentication,
            @AuthenticationPrincipal OAuth2User oauth
    ) {
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        System.out.println("getAttributes : " + oauth2User.getAttributes());

        return "OAuth 세션 정보 확인";
    }

    @GetMapping({"","/"})
    public String index() {
        return "index";
    }

    // OAuth, 일반 로그인 둘 다 PrincipalDetails 로 받을 수 있음.
    @GetMapping("/user")
    public @ResponseBody String user(@AuthenticationPrincipal PrincipalDetails principalDetails) {

        return principalDetails.getUser().toString();
    }

    @GetMapping("/admin")
    public @ResponseBody String admin() {
        return "admin";
    }

    @GetMapping("/manager")
    public @ResponseBody String manager() {
        return "manager";
    }

    @GetMapping("/loginForm")
    public String loginForm() {
        return "loginForm";
    }

    @GetMapping("/joinForm")
    public String joinForm() {
        return "joinForm";
    }

    @PostMapping("/join")
    public String join(User user) {
        user.setRole("ROLE_USER");
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "redirect:/loginForm";
    }

    // 근데 얘가 권한이 어떤 필드인지 어떻게 알아낼까 ? 2023-08-16화
    // 1 - PrincipalDetails.getAuthorities() ??
    // 2 -
    @Secured("ROLE_ADMIN") // 특정 메서드에 권한을 줄 수 있음. @EnableGlobalMethodSecurity(securedEnabled = true)
    @GetMapping("/info")
    public @ResponseBody String info() {
        return "개인정보";
    }

    // "/data" 요청 직전에 실행됨. 이 메서드가 실행되기 전에 인증을 확인함
    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
    // @PostAuthorize() <- 함수가 끝난 뒤에 진행됨
    @GetMapping("/data")
    public @ResponseBody String data() {
        return "데이터";
    }
}
