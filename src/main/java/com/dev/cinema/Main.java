package com.dev.cinema;

import com.dev.cinema.config.AppConfig;
import com.dev.cinema.lib.exceptions.AuthenticationException;
import com.dev.cinema.model.CinemaHall;
import com.dev.cinema.model.Movie;
import com.dev.cinema.model.MovieSession;
import com.dev.cinema.model.ShoppingCart;
import com.dev.cinema.model.User;
import com.dev.cinema.security.AuthenticationService;
import com.dev.cinema.service.CinemaHallService;
import com.dev.cinema.service.MovieService;
import com.dev.cinema.service.MovieSessionService;
import com.dev.cinema.service.OrderService;
import com.dev.cinema.service.ShoppingCartService;
import com.dev.cinema.service.UserService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) throws AuthenticationException {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(AppConfig.class);
        Movie movie = new Movie();
        movie.setTitle("Fast and Furious");
        MovieService movieService = context.getBean(MovieService.class);
        movieService.add(movie);
        logger.info("All cinema halls: \n");
        movieService.getAll().forEach(logger::info);

        CinemaHallService cinemaHallService
                = context.getBean(CinemaHallService.class);

        CinemaHall cinemaHall = new CinemaHall();
        cinemaHall.setDescription("Cinema Hall 1");
        cinemaHallService.add(cinemaHall);

        MovieSession movieSession1 = new MovieSession();
        movieSession1.setCinemaHall(cinemaHall);
        movieSession1.setMovie(movie);
        movieSession1.setShowTime(LocalDateTime.of(2020, 11, 14, 17, 30));

        MovieSessionService movieSessionService
                = context.getBean(MovieSessionService.class);
        movieSessionService.add(movieSession1);

        logger.info("Available session: " + movieSessionService
                .findAvailableSessions(1L, LocalDate.of(2020, 11, 14)));

        AuthenticationService authenticationService =
                context.getBean(AuthenticationService.class);
        User user1 = new User();
        user1.setEmail("user1@gmail.com");
        user1.setPassword("password");
        logger.info("User registered: " + authenticationService
                .register(user1.getEmail(), user1.getPassword()));
        try {
            authenticationService.login(user1.getEmail(), user1.getPassword());
            logger.info("User logged in");
        } catch (AuthenticationException e) {
            logger.warn("User failed to log in. Exception: ", e);
        }

        ShoppingCartService shoppingCartService
                = context.getBean(ShoppingCartService.class);
        UserService userService
                = context.getBean(UserService.class);
        User userFromDb = userService.findByEmail("user1@gmail.com").get();
        shoppingCartService.addSession(movieSession1, userFromDb);
        ShoppingCart shoppingCart = shoppingCartService.getByUser(userFromDb);
        logger.info("Cart with tickets: " + shoppingCart);

        OrderService orderService
                = context.getBean(OrderService.class);
        orderService.completeOrder(shoppingCart.getTickets(), userFromDb);
        logger.info("User's orders: \n");
        orderService.getOrderHistory(userFromDb).forEach(logger::info);
    }
}
