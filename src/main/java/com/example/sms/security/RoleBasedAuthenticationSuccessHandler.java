package com.example.sms.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Component
public class RoleBasedAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger log = LoggerFactory.getLogger(RoleBasedAuthenticationSuccessHandler.class);

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        // store username in session for dashboard
        String username = authentication.getName();
        request.getSession().setAttribute("username", username);
        log.info("User '{}' logged in", username);

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        boolean isTeacher = authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_TEACHER"));
        boolean isStudent = authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"));

        log.info("Authorities: {}", authorities);

        if (isTeacher) {
            log.info("Redirecting '{}' to teacher dashboard", username);
            response.sendRedirect("/teacher/dashboard");
        } else if (isStudent) {
            log.info("Redirecting '{}' to student dashboard", username);
            response.sendRedirect("/student/dashboard");
        } else {
            log.warn("User '{}' has no role, redirecting to home", username);
            response.sendRedirect("/");
        }
    }
}
