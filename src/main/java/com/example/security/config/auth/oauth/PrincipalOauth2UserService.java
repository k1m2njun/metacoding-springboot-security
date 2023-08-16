package com.example.security.config.auth.oauth;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    // 구글로부터 받은 userRequest 데이터에 대한 후처리 되는 함수
    // userRequest에는 엑세스토큰, User정보 등이 포함되어 있다.
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // userRequest.getClientRegistration().registrationId, 어떤 OAuth로 로그인했는지 확인 가능

        // 회원가입 강제로 진행하기 위한 회원 정보를 리턴 받음
        OAuth2User oAuth2User = super.loadUser(userRequest);

        return super.loadUser(userRequest);
    }
}

/**
 * OAuth2User oAuth2User = super.loadUser(userRequest);
 *
 *         String provider = userRequest.getClientRegistration().getRegistrationId();    //google
 *         String providerId = oAuth2User.getAttribute("sub");
 *         String username = provider+"_"+providerId;  			// 사용자가 입력한 적은 없지만 만들어준다
 *
 *         String uuid = UUID.randomUUID().toString().substring(0, 6);
 *         String password = bCryptPasswordEncoder.encode("패스워드"+uuid);  // 사용자가 입력한 적은 없지만 만들어준다
 *
 *         String email = oAuth2User.getAttribute("email");
 *
 *         User byUsername = userRepository.findByUsername(username);
 *
 *         //DB에 없는 사용자라면 회원가입처리
 *         if(byUsername == null){
 *             byUsername = User.oauth2Register()
 *                     .username(username).password(password).email(email).role("ROLE_USER")
 *                     .provider(provider).providerId(providerId)
 *                     .build();
 *             userRepository.save(byUsername);
 *         }
 *         return new PrincipalDetails(byUsername, oAuth2User.getAttributes());
 */