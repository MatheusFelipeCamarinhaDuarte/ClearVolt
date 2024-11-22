package com.fiap.m4g.clearvolt.domain.config.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import java.util.List;

@Configuration
public class SecurityConfig {

    final List<String> urlsParaTodos = List.of("/css/**", "/js/**", "/images/**", "/webjars/**", "/static/**", "/home", "/", "/cadastro", "/cadastrar");
    final List<String> urlsUsuarios = List.of("/");
    final List<String> urlsAdmin = List.of("/");

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests((requests) -> requests
                        .requestMatchers(urlsParaTodos.toArray(new String[0])).permitAll()  // Libera acesso a todos os recursos estáticos
                        .requestMatchers(urlsAdmin.toArray(new String[0])).hasAuthority("ROLE_ADMIN")
                        .requestMatchers(urlsUsuarios.toArray(new String[0])).hasAuthority("ROLE_FREE")
                        .requestMatchers(urlsUsuarios.toArray(new String[0])).hasAuthority("ROLE_PREMIUM")
                        .anyRequest().authenticated())
                .formLogin((form) -> form.loginPage("/login").failureUrl("/login?erro=true")
                        .defaultSuccessUrl("/perfil").permitAll())
                .logout((logout) -> logout.logoutUrl("/logout").logoutSuccessUrl("/login?logout=true").permitAll())
                .exceptionHandling((exception) -> exception.accessDeniedHandler(((request, response, accessDeniedException) -> {
                    response.sendRedirect("/acesso_negado");
                })));

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
