package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


import java.security.SecureRandom;
import java.util.HexFormat;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.example.demo.security.BrowserBindingFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {


	@Bean
public BrowserBindingFilter browserBindingFilter() {
    return new BrowserBindingFilter();
}

	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	    http
	        .authorizeHttpRequests(auth -> auth
	            .requestMatchers(
	                "/css/**",
	                "/images/**",
	                "/login"
	            ).permitAll()
	            .requestMatchers("/coordinador/**").hasRole("COORDINADOR")
	            .requestMatchers("/estudiante/**").hasRole("ESTUDIANTE")
	            .anyRequest().authenticated()
	        )



			.formLogin(form -> form
    .loginPage("/login")
    .defaultSuccessUrl("/dashboard", true)
    .successHandler((request, response, authentication) -> {

        byte[] randomBytes = new byte[32];
        new SecureRandom().nextBytes(randomBytes);

        String deviceToken = HexFormat.of().formatHex(randomBytes);

        request.getSession(true).setAttribute(
                "ZOE_DEVICE_TOKEN", deviceToken);

        ResponseCookie cookie = ResponseCookie
                .from("ZOE_DEVICE", deviceToken)
                .httpOnly(true)
                .secure(request.isSecure())
                .sameSite("Strict")
                .path("/")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        response.sendRedirect(request.getContextPath() + "/dashboard");
    })
    .permitAll()
)

			
			.sessionManagement(session -> session
    .sessionFixation(fixation -> fixation.migrateSession())
    .maximumSessions(1)
)

	       .logout(logout -> logout
    .logoutUrl("/logout")
    .invalidateHttpSession(true)
    .clearAuthentication(true)
    .deleteCookies("JSESSIONID")
    .logoutSuccessUrl("/login?logout")
    .permitAll()
);


	    return http.build();
	}

	    @Bean
	    public PasswordEncoder passwordEncoder() {
	        return new BCryptPasswordEncoder();
	    }
	}
