package com.quanly.trungtamngoaingu.sercurity;
import com.quanly.trungtamngoaingu.sercurity.jwt.AuthEntryPointJwt;
import com.quanly.trungtamngoaingu.sercurity.jwt.AuthTokenFilter;
import com.quanly.trungtamngoaingu.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableMethodSecurity

public class WebSecurityConfig {
    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }


    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/api/tai-khoan/dang-nhap").permitAll()
                                .requestMatchers("/api/tai-khoan/them-moi").permitAll()
                                .requestMatchers("/api/tai-khoan/kiem-tra-dang-nhap").permitAll()
                                .requestMatchers("/api/tai-khoan/**").hasAnyAuthority("QuanTriVien","HocVien", "NhanVien", "GiaoVien")
                                .requestMatchers("/api/lich-hoc/**").hasAnyAuthority("QuanTriVien","HocVien", "NhanVien", "GiaoVien")
                                .requestMatchers("/api/khoa-hoc/**").hasAnyAuthority("QuanTriVien","HocVien", "NhanVien", "GiaoVien")
                                .requestMatchers("/api/tai-lieu/**").hasAnyAuthority("QuanTriVien","HocVien", "NhanVien", "GiaoVien")
                                .requestMatchers("/api/upload/**").hasAnyAuthority("QuanTriVien","HocVien", "NhanVien", "GiaoVien")
                                .requestMatchers("/api/chung-chi/**").hasAnyAuthority("QuanTriVien","HocVien", "NhanVien", "GiaoVien")

                                .requestMatchers("/api/lop-hoc/**").hasAnyAuthority("QuanTriVien","HocVien", "NhanVien", "GiaoVien")
                                .requestMatchers("/api/dang-ky-khoa-hoc/**").hasAnyAuthority("QuanTriVien","HocVien", "NhanVien", "GiaoVien")
                                .requestMatchers("/api/phong-hoc/**").hasAnyAuthority("QuanTriVien","HocVien", "NhanVien", "GiaoVien")
                                .requestMatchers("/api/loai-lop/**").hasAnyAuthority("QuanTriVien","HocVien", "NhanVien", "GiaoVien")

                                .anyRequest().authenticated()
                )
                        .cors(Customizer.withDefaults());

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
