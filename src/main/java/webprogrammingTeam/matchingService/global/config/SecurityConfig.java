package webprogrammingTeam.matchingService.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import webprogrammingTeam.matchingService.auth.filter.itself.CustomJsonUsernamePasswordAuthenticationFilter;
import webprogrammingTeam.matchingService.auth.filter.itself.CustomLogoutFilter;
import webprogrammingTeam.matchingService.auth.handler.Itself.LoginFailureHandler;
import webprogrammingTeam.matchingService.auth.handler.Itself.LoginSuccessHandler;

import webprogrammingTeam.matchingService.auth.service.LoginService;
import webprogrammingTeam.matchingService.domain.member.repository.MemberRepository;
import webprogrammingTeam.matchingService.domain.refresh.repository.RefreshtokenRepository;
import webprogrammingTeam.matchingService.domain.refresh.service.RefreshService;
import webprogrammingTeam.matchingService.jwt.JWTFilter;
import webprogrammingTeam.matchingService.jwt.JWTService;


import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    //private final CustomOAuth2UserService customOAuth2UserService;
    //private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;
    private final JWTService jwtService;
    private final RefreshService refreshService;
    private final ObjectMapper objectMapper;
    private final PasswordEncoder passwordEncoder;
    private final LoginService loginService;
    private final RefreshtokenRepository refreshtokenRepository;
    private final MemberRepository memberRepository;
/*
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {

        return configuration.getAuthenticationManager();
    }
*/


/*    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {

        return new BCryptPasswordEncoder();
    }*/

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
                                .requestMatchers(new AntPathRequestMatcher("/error")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/reissue")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/v3/**")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/user")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/googleLogin")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/member/signup")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/login")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/program/view/**")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/test")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/program/data/**")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/program/search")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/email/**")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/program/{programId}/review/view/**")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/ws")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/program/category/**")).permitAll()


                                .anyRequest().authenticated())


                       /* .oauth2Login(oauth2 -> oauth2
                                .userInfoEndpoint(userInfoEndpointConfig ->
                                        userInfoEndpointConfig.userService(customOAuth2UserService))
                                .successHandler(customOAuth2SuccessHandler)
                        )
*/

                        // CustomJsonUsernamePasswordAuthenticationFilter 등록
                        .addFilterBefore(customJsonUsernamePasswordAuthenticationFilter(), LogoutFilter.class)
                        // JWTFilter 등록
                        .addFilterBefore(jwtFilter(), CustomJsonUsernamePasswordAuthenticationFilter.class)
                        // CustomLogoutFilter 등록 (특정 URL에만 적용하도록 설정)
                        .addFilterBefore(new CustomLogoutFilter(jwtService, refreshtokenRepository), LogoutFilter.class)


                        .sessionManagement((session) -> session
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                        .build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:8080","http://localhost:3000/googleLogin","ws://localhost:3000"));
        config.setAllowedMethods(Arrays.asList("*"));
        config.setAllowCredentials(true);
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setExposedHeaders(Arrays.asList("Accesstoken","Refreshtoken"));


        config.setMaxAge(3600L); //1시간
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(loginService);

        return new ProviderManager(provider);
    }
    @Bean
    public CustomJsonUsernamePasswordAuthenticationFilter customJsonUsernamePasswordAuthenticationFilter() {
        CustomJsonUsernamePasswordAuthenticationFilter customJsonUsernamePasswordAuthenticationFilter
                = new CustomJsonUsernamePasswordAuthenticationFilter(objectMapper);

        customJsonUsernamePasswordAuthenticationFilter.setAuthenticationManager(authenticationManager());
        customJsonUsernamePasswordAuthenticationFilter.setAuthenticationSuccessHandler(loginSuccessHandler());
        customJsonUsernamePasswordAuthenticationFilter.setAuthenticationFailureHandler(loginFailureHandler());

        return customJsonUsernamePasswordAuthenticationFilter;
    }
    @Bean
    public LoginSuccessHandler loginSuccessHandler() {
        return new LoginSuccessHandler(jwtService,refreshService);
    }

    @Bean
    public LoginFailureHandler loginFailureHandler() {
        return new LoginFailureHandler();
    }

    @Bean
    public JWTFilter jwtFilter(){
        return new JWTFilter(jwtService,memberRepository);
    }
}
