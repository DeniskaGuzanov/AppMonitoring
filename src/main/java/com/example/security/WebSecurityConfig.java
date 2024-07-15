package com.example.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Конфигурация безопасности веб-приложения.
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    /**
     * Конфигурация цепочки фильтров безопасности.
     *
     * @param http объект HttpSecurity для настройки безопасности
     * @return цепочка фильтров безопасности
     * @throws Exception если произошла ошибка при настройке безопасности
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/*.html", "/js/*", "/js/*.map","/css/*.css","/css/*.map", "/*.ico", "/",
                                "/main/**","/feedback","/error","/docs/**",
                                "/report/**","/login","/loguot","/api/log",
                                "/history/**"
                        ).permitAll()

                        .requestMatchers(HttpMethod.GET, "/input/vos5").hasAnyRole("VOS5", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/update/vos5/*").hasAnyRole("VOS5", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/input/vos5").hasAnyRole("VOS5", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/update/vos5").hasAnyRole("VOS5", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/delete/vos5/*").hasAnyRole("VOS5", "ADMIN")

                        .requestMatchers(HttpMethod.GET, "/input/vos15").hasAnyRole("VOS15", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/update/vos15/*").hasAnyRole("VOS15", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/input/vos15").hasAnyRole("VOS15", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/update/vos15").hasAnyRole("VOS15", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/delete/vos15/*").hasAnyRole("VOS15", "ADMIN")

                        .requestMatchers(HttpMethod.POST, "/admin/**").hasAnyRole( "ADMIN")


                        .anyRequest().authenticated()

                ).csrf(AbstractHttpConfigurer::disable)

                .formLogin((loginForm) -> loginForm
                        .loginPage("/login")
                        .permitAll())

                .logout(
                        logout -> logout
                                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                                .logoutSuccessUrl("/")
                                .permitAll()
                );
        return http.build();
    }

    /**
     * Создает сервис пользователей с предустановленными пользователями.
     *
     * @return сервис пользователей с предустановленными пользователями
     */
    @Bean
    public UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager inMemoryUserDetailsManager = new InMemoryUserDetailsManager();
        inMemoryUserDetailsManager.createUser(User.withDefaultPasswordEncoder()
                .username("ВОС5000")
                .password("5")
                .roles("VOS5")
                .build());

        inMemoryUserDetailsManager.createUser(User.withDefaultPasswordEncoder()
                .username("ВОС15000")
                .password("15")
                .roles("VOS15")
                .build());

        inMemoryUserDetailsManager.createUser(User.withDefaultPasswordEncoder()
                .username("Admin")
                .password("admin12345")
                .roles("ADMIN")
                .build());
        return inMemoryUserDetailsManager;
    }
}