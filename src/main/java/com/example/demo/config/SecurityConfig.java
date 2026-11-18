
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

    /*
     * VULNERABILIDAD A2: BROKEN AUTHENTICATION
     * OWASP Top 10 2017 - A2 / Session Hijacking
     *
     * 1. ANTES (VULNERABILIDAD):
     * El sistema utiliza JSESSIONID para identificar
     * la sesion autenticada. Si alguien obtiene esta
     * cookie, puede reutilizarla en otro navegador
     * mientras la sesion continue activa.
     *
     * 2. DESPUES (REMEDIACION):
     * Se genera una segunda cookie ZOE_DEVICE.
     * BrowserBindingFilter verifica que coincida
     * con el identificador guardado en la sesion.
     *
     * Si falta o no coincide, se rechaza el acceso.
     * Esta medida dificulta el uso de JSESSIONID
     * por si solo, pero no impide el robo de ambas.
     *
     * false = ANTES, sin segunda cookie.
     * true  = DESPUES, con segunda cookie.
     */

    // CAMBIAR SOLO ESTA LINEA PARA LA DEMOSTRACION
    private static final boolean REMEDIACION_A2 = true;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        // SEGURIDAD GENERAL: permisos por roles
        http.authorizeHttpRequests(auth -> auth
            .requestMatchers(
                "/css/**",
                "/images/**",
                "/login"
            ).permitAll()
            .requestMatchers("/coordinador/**")
                .hasRole("COORDINADOR")
            .requestMatchers("/estudiante/**")
                .hasRole("ESTUDIANTE")
            .anyRequest().authenticated()
        );

        /*
         * 1. INICIO DE SESION
         *
         * ANTES: login normal con JSESSIONID.
         * DESPUES: login que genera ZOE_DEVICE.
         */

        if (!REMEDIACION_A2) {

            // ANTES: VERSION SIN REMEDIACION
            http.formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard", true)
                .permitAll()
            );

        } else {

            // DESPUES: GENERACION DE SEGUNDA COOKIE
            http.formLogin(form -> form
                .loginPage("/login")
                .successHandler((request, response, authentication) -> {

                    // Crear identificador aleatorio
                    byte[] randomBytes = new byte[32];
                    new SecureRandom().nextBytes(randomBytes);

                    String deviceToken =
                        HexFormat.of().formatHex(randomBytes);

                    // Guardar identificador en la sesion
                    request.getSession(true).setAttribute(
                        "ZOE_DEVICE_TOKEN", deviceToken
                    );

                    // Crear segunda cookie de seguridad
                    ResponseCookie cookie = ResponseCookie
                        .from("ZOE_DEVICE", deviceToken)
                        .httpOnly(true)
                        .secure(request.isSecure())
                        .sameSite("Strict")
                        .path("/")
                        .build();

                    response.addHeader(
                        HttpHeaders.SET_COOKIE,
                        cookie.toString()
                    );

                    response.sendRedirect(
                        request.getContextPath() + "/dashboard"
                    );
                })
                .permitAll()
            );
        }

        /*
         * 2. CONTROL DE SESIONES
         * Se mantiene en ambas versiones.
         */
        http.sessionManagement(session -> session
            .sessionFixation(fixation -> fixation.migrateSession())
            .maximumSessions(1)
        );

        /*
         * 3. VALIDACION DE LA SEGUNDA COOKIE
         *
         * ANTES: no se ejecuta BrowserBindingFilter.
         * DESPUES: se verifica ZOE_DEVICE.
         *
         * Si alguien copia solamente JSESSIONID,
         * el filtro rechaza el acceso.
         */
        if (REMEDIACION_A2) {

            http.addFilterAfter(
                new BrowserBindingFilter(),
                UsernamePasswordAuthenticationFilter.class
            );
        }

        /*
         * 4. CIERRE DE SESION
         *
         * Se invalida la sesion y se eliminan
         * las cookies utilizadas.
         */
        http.logout(logout -> logout
            .logoutUrl("/logout")
            .invalidateHttpSession(true)
            .clearAuthentication(true)
            .deleteCookies("JSESSIONID", "ZOE_DEVICE")
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
