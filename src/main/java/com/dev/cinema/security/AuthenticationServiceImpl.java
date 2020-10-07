package com.dev.cinema.security;

import static com.dev.cinema.util.HashUtil.hashPassword;

import com.dev.cinema.lib.Inject;
import com.dev.cinema.lib.Service;
import com.dev.cinema.lib.exceptions.AuthenticationException;
import com.dev.cinema.model.User;
import com.dev.cinema.service.UserService;
import com.dev.cinema.util.HashUtil;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    @Inject
    private UserService userService;

    @Override
    public User login(String email, String password) throws AuthenticationException {
        return userService.findByEmail(email)
                .filter(u -> isValid(password, u))
                .orElseThrow(() -> new AuthenticationException("Incorrect email or password."));
    }

    @Override
    public User register(String email, String password) {
        User user = new User();
        byte[] salt = HashUtil.getSalt();
        String hashPassword = HashUtil.hashPassword(password, salt);
        user.setEmail(email);
        user.setPassword(hashPassword);
        user.setSalt(salt);
        return userService.add(user);
    }

    private static boolean isValid(String password, User user) {
        return hashPassword(password, user.getSalt()).equals(user.getPassword());
    }
}
