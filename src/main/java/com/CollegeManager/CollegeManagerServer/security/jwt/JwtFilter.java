package com.CollegeManager.CollegeManagerServer.security.jwt;

import com.CollegeManager.CollegeManagerServer.security.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Service
public class JwtFilter extends OncePerRequestFilter {

    @Qualifier("handlerExceptionResolver")
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    public JwtFilter(HandlerExceptionResolver handlerExceptionResolver) {
        this.handlerExceptionResolver=handlerExceptionResolver;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
    {
        try
        {
            final String tokenHeader = request.getHeader(AUTHORIZATION);
            if (tokenHeader == null || !tokenHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            final String jwtToken = tokenHeader.substring(7);
            final String userEmailFromToken = jwtService.extractUserEmailFromToken(jwtToken);
            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmailFromToken);
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );
            usernamePasswordAuthenticationToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            filterChain.doFilter(request,response);
        }
        catch (Exception ex){
            handlerExceptionResolver.resolveException(request,response,null,ex);
        }

    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) throws ServletException {
        return request.getServletPath().contains("/college-manager/registration/email-validation")

                || request.getServletPath().contains("/college-manager/registration/verify")

                || request.getServletPath().contains("/college-manager/authentication")

                || request.getServletPath().contains("/college-manager/department/by-college")

                || request.getServletPath().contains("/college-manager/college/all");

    }
}