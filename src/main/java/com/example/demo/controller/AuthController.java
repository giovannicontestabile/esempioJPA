package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.service.AuthService;
import com.example.demo.utility.JwtUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @Value("${app.cookie.name}")
    private String accessCookieName;
    @Value("${app.cookie.max-age}")
    private int accessMaxAge;
    @Value("${app.cookie.refresh-name}")
    private String refreshCookieName;
    @Value("${app.cookie.refresh-max-age}")
    private int refreshMaxAge;
    @Value("${app.cookie.path}")
    private String accessCookiePath;
    @Value("${app.cookie.refresh-path}")
    private String refreshCookiePath;
    @Value("${app.cookie.secure}")
    private boolean secure;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginRequest req, HttpServletResponse res) {
        String accessToken = authService.login(req);
        String refreshToken = jwtUtil.generateRefreshToken(req.username());

        setCookie(res, accessCookieName, accessToken, accessCookiePath, accessMaxAge, secure);
        setCookie(res, refreshCookieName, refreshToken, refreshCookiePath, refreshMaxAge, secure);

        return ResponseEntity.ok(Map.of("message", "Login effettuato"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<Void> refresh(HttpServletRequest req, HttpServletResponse res) {
        String refreshToken = extractCookieValue(req, refreshCookieName);
        if (refreshToken == null || !jwtUtil.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String username = jwtUtil.extractUsername(refreshToken);
        String newAccessToken = jwtUtil.generateToken(username);
        setCookie(res, accessCookieName, newAccessToken, accessCookiePath, accessMaxAge, secure);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse res) {
        setCookie(res, accessCookieName, null, accessCookiePath, 0, secure);
        setCookie(res, refreshCookieName, null, refreshCookiePath, 0, secure);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest req) {
        return ResponseEntity.ok(authService.register(req));
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(Authentication auth) {
        if (auth == null)
            return ResponseEntity.status(401).build();
        return ResponseEntity.ok(Map.of("username", auth.getName(), "roles", auth.getAuthorities()));
    }

    /*
     * private void setAuthCookie(HttpServletResponse response, String token) {
     * ResponseCookie cookie = ResponseCookie.from(accessCookieName, token)
     * .path(accessCookiePath)
     * .maxAge(token == null ? 0 : accessMaxAge)
     * .secure(secure)
     * .httpOnly(true)
     * .sameSite("Lax")
     * .build();
     * response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
     * }
     */

    private void setCookie(HttpServletResponse res, String name, String value, String path, int maxAge,
            boolean secure) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .path(path)
                .maxAge(maxAge)
                .secure(secure)
                .httpOnly(true)
                .sameSite("Lax")
                .build();
        res.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private String extractCookieValue(HttpServletRequest req, String name) {
        if (req.getCookies() != null) {
            for (Cookie c : req.getCookies())
                if (name.equals(c.getName()))
                    return c.getValue();
        }
        return null;
    }
}