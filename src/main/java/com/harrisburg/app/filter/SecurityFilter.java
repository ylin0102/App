package com.harrisburg.app.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Slf4j
public class SecurityFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        HttpSession session = httpServletRequest.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            logger.warn("User doesn't sign in");
            httpServletResponse.sendRedirect("");
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
