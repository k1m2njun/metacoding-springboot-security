package com.example.security.config.auth.oauth;

import com.example.security.config.auth.PrincipalDetails;
import com.example.security.config.auth.oauth.provider.FacebookUserInfo;
import com.example.security.config.auth.oauth.provider.GoogleUserInfo;
import com.example.security.config.auth.oauth.provider.NaverUserInfo;
import com.example.security.config.auth.oauth.provider.OAuth2UserInfo;
import com.example.security.model.User;
import com.example.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    @Autowired @Lazy
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserRepository userRepository;

    // 구글로부터 받은 userRequest 데이터에 대한 후처리 되는 함수
    // userRequest에는 엑세스토큰, User정보 등이 포함되어 있다.
    // 함수 종료 시 @AuthenticationPrincpal 어노테이션이 만들어 진다.
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        // 회원가입 강제로 진행하기 위한 회원 정보를 리턴 받음
        OAuth2User oauth2User = super.loadUser(userRequest);

        OAuth2UserInfo oauth2UserInfo = null;
        if (userRequest.getClientRegistration().getRegistrationId().equals("google"))
            oauth2UserInfo = new GoogleUserInfo(oauth2User.getAttributes());
        if (userRequest.getClientRegistration().getRegistrationId().equals("facebook"))
            oauth2UserInfo = new FacebookUserInfo(oauth2User.getAttributes());
        if (userRequest.getClientRegistration().getRegistrationId().equals("naver"))
            oauth2UserInfo = new NaverUserInfo((Map) oauth2User.getAttributes().get("response"));

        String provider = oauth2UserInfo.getProvider();
        String providerId = oauth2UserInfo.getProviderId(); // 12238572398509
        String username = provider + "_" + providerId; // google_12238572398509
        String email = oauth2UserInfo.getEmail();
        String password = bCryptPasswordEncoder.encode("k1m2njun"); // 비밀번호 의미 없음
        String role = "ROLE_USER";

        User userEntity = userRepository.findByUsername(username);
        if (userEntity == null) {
            userEntity = User.builder()
                    .username(username)
                    .password(password)
                    .email(email)
                    .provider(provider)
                    .providerId(providerId)
                    .role(role)
                    .build();
            userRepository.save(userEntity);
        }

        return new PrincipalDetails(userEntity, oauth2User.getAttributes());
    }
}