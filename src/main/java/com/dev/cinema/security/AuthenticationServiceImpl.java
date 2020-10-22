package com.dev.cinema.security;

import static com.dev.cinema.util.HashUtil.hashPassword;

import com.dev.cinema.lib.Inject;
import com.dev.cinema.lib.Service;
import com.dev.cinema.lib.exceptions.AuthenticationException;
import com.dev.cinema.model.User;
import com.dev.cinema.service.ShoppingCartService;
import com.dev.cinema.service.UserService;
import org.apache.log4j.Logger;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private static final Logger logger = Logger.getLogger(AuthenticationServiceImpl.class);

    @Inject
    private UserService userService;

    @Inject
    private ShoppingCartService shoppingCartService;

    @Override
    public User login(String email, String password) throws AuthenticationException {
        logger.info("Try to log in user with email " + email);
        return userService.findByEmail(email)
                .filter(u -> isPasswordValid(password, u))
                .orElseThrow(() -> new AuthenticationException("Incorrect email or password."));
    }

    @Override
    public User register(String email, String password) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        User newUser = userService.add(user);
        shoppingCartService.registerNewShoppingCart(newUser);
        logger.info("User with email " + email + "was registered");
        return newUser;
    }

    private boolean isPasswordValid(String password, User user) {
        return hashPassword(password, user.getSalt()).equals(user.getPassword());
    }
}
