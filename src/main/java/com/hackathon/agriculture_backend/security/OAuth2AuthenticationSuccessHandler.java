package com.hackathon.agriculture_backend.security;

import com.hackathon.agriculture_backend.model.User;
import com.hackathon.agriculture_backend.service.UserService;
import com.hackathon.agriculture_backend.util.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    
    private final UserService userService;
    private final JwtUtil jwtUtil;
    
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                    Authentication authentication) throws IOException, ServletException {
        
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oauth2User.getAttributes();
        
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        String googleId = (String) attributes.get("sub");
        String profilePictureUrl = (String) attributes.get("picture");
        
        log.info("OAuth2 authentication successful for user: {}", email);
        
        // Check if user exists, if not create new user
        User user = userService.findByEmailOptional(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setName(name);
                    newUser.setGoogleId(googleId);
                    newUser.setProfilePictureUrl(profilePictureUrl);
                    newUser.setRole(User.Role.USER);
                    newUser.setIsEnabled(true);
                    log.info("Creating OAuth2 user with role: {}", newUser.getRole());
                    User savedUser = userService.save(newUser);
                    log.info("OAuth2 user saved with role: {}", savedUser.getRole());
                    return savedUser;
                });
        
        // Generate JWT token
        String jwt = jwtUtil.generateToken(user);
        
        // Redirect to frontend with token (stateless - no server-side session)
        String targetUrl = UriComponentsBuilder.fromUriString("http://localhost:3000/auth/callback/google")
                .queryParam("token", jwt)
                .queryParam("success", "true")
                .queryParam("user", user.getName())
                .queryParam("email", user.getEmail())
                .build().toUriString();
        
        // Clear any potential session data
        request.getSession().invalidate();
        
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
