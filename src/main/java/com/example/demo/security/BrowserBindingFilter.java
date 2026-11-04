package com.example.demo.security;

import java.time.Duration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class BrowserBindingFilter extends OncePerRequestFilter {

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();

        return path.equals("/login")
                || path.startsWith("/css/")
                || path.startsWith("/images/")
                || path.startsWith("/js/")
                || path.equals("/favicon.ico");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        boolean authenticated = authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);

        if (session != null && authenticated) {

            String expectedToken =
                    (String) session.getAttribute("ZOE_DEVICE_TOKEN");

            String receivedToken = null;

            if (request.getCookies() != null) {
                for (var cookie : request.getCookies()) {
                    if ("ZOE_DEVICE".equals(cookie.getName())) {
                        receivedToken = cookie.getValue();
                        break;
                    }
                }
            }

            if (expectedToken == null || !expectedToken.equals(receivedToken)) {

                // No invalidamos la sesión del servidor:
                // así el navegador original conserva su acceso.
                SecurityContextHolder.clearContext();

                ResponseCookie deviceCookie = ResponseCookie
                        .from("ZOE_DEVICE", "")
                        .httpOnly(true)
                        .secure(request.isSecure())
                        .sameSite("Strict")
                        .path("/")
                        .maxAge(Duration.ZERO)
                        .build();

                ResponseCookie sessionCookie = ResponseCookie
                        .from("JSESSIONID", "")
                        .httpOnly(true)
                        .secure(request.isSecure())
                        .sameSite("Strict")
                        .path("/")
                        .maxAge(Duration.ZERO)
                        .build();

                response.addHeader(
                        HttpHeaders.SET_COOKIE, deviceCookie.toString());

                response.addHeader(
                        HttpHeaders.SET_COOKIE, sessionCookie.toString());

                response.sendRedirect(request.getContextPath()
                        + "/login?invalidDevice");

                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
