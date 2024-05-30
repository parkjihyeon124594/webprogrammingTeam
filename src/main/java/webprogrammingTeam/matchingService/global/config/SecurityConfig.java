package webprogrammingTeam.matchingService.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import webprogrammingTeam.matchingService.auth.handler.CustomOAuth2SuccessHandler;
import webprogrammingTeam.matchingService.auth.service.CustomOAuth2UserService;
import webprogrammingTeam.matchingService.jwt.JWTFilter;
import webprogrammingTeam.matchingService.jwt.JWTService;


import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;
    private final JWTService jwtService;
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {

        return configuration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {

        return new BCryptPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        return
                http
                        .cors(corsCustomizer -> corsCustomizer.configurationSource(corsConfigurationSource()))
                        .csrf(AbstractHttpConfigurer::disable)
                        .formLogin(AbstractHttpConfigurer::disable)
                        .httpBasic(AbstractHttpConfigurer::disable)
                        .authorizeHttpRequests((auth) -> auth
                                .requestMatchers(new AntPathRequestMatcher("/login")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/auth/**")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/reissue")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/v3/**")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/user")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/googleLogin")).permitAll()
                                .anyRequest().authenticated())

                        // oauth2Login는 스프링 시큐리티가 아니기 때문에, Authentication를 SecurityContextHolder에 저장하지는 않는다.
                        // 하지만, CustomOAuth2UserService extends DefaultOAuth2UserService의  OAuth2 로그인 과정에서 loadUser 메서드가 반환하는 객체는 Authentication 객체의 Principal로 설정됨.
                        // 그래서 CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler에서  Authentication 객체의 Principal을 가져와서 SecurityContextHolder에 저장함

                        .oauth2Login(oauth2 -> oauth2
                                .userInfoEndpoint(userInfoEndpointConfig ->
                                        userInfoEndpointConfig.userService(customOAuth2UserService))
                                .successHandler(customOAuth2SuccessHandler)
                        )
                        .addFilterAfter(jwtFilter(), UsernamePasswordAuthenticationFilter.class) // JWTFilter를 OAuth2 로그인 필터 이후에 추가


                        //.addFilterAfter(customJsonUsernamePasswordAuthenticationFilter(), LogoutFilter.class)
                        //.addFilterBefore(jwtAuthorizationFilter(), CustomJsonUsernamePasswordAuthenticationFilter.class)
                        // 해당 필터의 위치는 기본적으로 스피링 시큐리티에 활성화돼 있는 로그아웃 필터를 기준으로 그 앞에다가 하면됨.
                        // 내가 만든 커스텀 필터가 로그아웃 전에 먼저 실행이 됨.
                       // .addFilterBefore(new CustomLogoutFilter(jwtService,refreshRepository), LogoutFilter.class)
        //세션 설정
                        .sessionManagement((session) -> session
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                        .build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:8080","http://localhost:3000/googleLogin"));
        config.setAllowedMethods(Arrays.asList("*"));
        config.setAllowCredentials(true);
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setExposedHeaders(Arrays.asList("Authorization-accesstoken"));

        config.setMaxAge(3600L); //1시간
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }

    @Bean
    public JWTFilter jwtFilter(){
        return new JWTFilter(jwtService);
    }
}
