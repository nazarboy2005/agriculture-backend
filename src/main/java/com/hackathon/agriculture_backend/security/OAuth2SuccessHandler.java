package com.hackathon.agriculture_backend.security;

import com.hackathon.agriculture_backend.model.User;
import com.hackathon.agriculture_backend.repository.UserRepository;
import com.hackathon.agriculture_backend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                      Authentication authentication) throws IOException, ServletException {
        
        try {
            if (authentication instanceof OAuth2AuthenticationToken) {
                OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
                String email = oauth2User.getAttribute("email");
                String name = oauth2User.getAttribute("name");
                String picture = oauth2User.getAttribute("picture");
                
                log.info("OAuth2 login successful for user: {}", email);
                
                // Find or create user
                Optional<User> userOptional = userRepository.findByEmail(email);
                User user;
                if (userOptional.isEmpty()) {
                    user = new User();
                    user.setEmail(email);
                    user.setName(name);
                    user.setProfilePictureUrl(picture);
                    user.setPassword("oauth2user"); // Placeholder password, actual login is via OAuth2
                    user.setRole(User.Role.USER);
                    user.setEmailVerified(true);
                    user.setIsEnabled(true);
                    userRepository.save(user);
                    log.info("Created new OAuth2 user: {}", email);
                } else {
                    user = userOptional.get();
                    log.info("Found existing OAuth2 user: {}", email);
                }
                
                // Generate JWT token using the User object (which implements UserDetails)
                String token = jwtUtil.generateToken(user);
                
                // Redirect to frontend with token
                String redirectUrl = frontendUrl + "/auth/callback/google?token=" + token + "&success=true";
                getRedirectStrategy().sendRedirect(request, response, redirectUrl);
            } else {
                super.onAuthenticationSuccess(request, response, authentication);
            }
            
        } catch (Exception e) {
            log.error("Error during OAuth2 success handling: {}", e.getMessage(), e);
            // Redirect to frontend with error
            String redirectUrl = frontendUrl + "/auth/callback/google?success=false";
            getRedirectStrategy().sendRedirect(request, response, redirectUrl);
        }
    }
}
