package com.Taskly.service;

import com.Taskly.dto.AuthRequest;
import com.Taskly.dto.AuthResponse;
import com.Taskly.dto.UserDTO;
import com.Taskly.exception.ResourceAlreadyExistsException;
import com.Taskly.model.Role;
import com.Taskly.model.User;
import com.Taskly.repository.UserRepository;
import com.Taskly.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(UserDTO userDto) {
        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new ResourceAlreadyExistsException("Username already exists");
        }

        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new ResourceAlreadyExistsException("Email already exists");
        }

        User user = User.builder()
                .username(userDto.getUsername())
                .email(userDto.getEmail())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .name(userDto.getName())
                .role(Role.ROLE_USER)// Default role
                .build();

        userRepository.save(user);

        String token = jwtService.generateToken(user);
        return buildAuthResponse(user, token);
    }

    public AuthResponse login(AuthRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = (User) authentication.getPrincipal();
        String token = jwtService.generateToken(user);

        return buildAuthResponse(user, token);
    }

    private AuthResponse buildAuthResponse(User user, String token) {
        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .userId(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .role(String.valueOf(user.getRole()))
                .build();
    }
}