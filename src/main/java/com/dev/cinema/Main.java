package com.dev.cinema;

import com.dev.cinema.lib.Injector;
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

public class Main {
    private static Injector injector = Injector.getInstance("com.dev.cinema");

    public static void main(String[] args) throws AuthenticationException {
        Movie movie = new Movie();
        movie.setTitle("Fast and Furious");
        MovieService movieService = (MovieService) injector.getInstance(MovieService.class);
        movieService.add(movie);
        movieService.getAll().forEach(System.out::println);

        CinemaHallService cinemaHallService
                = (CinemaHallService) injector.getInstance(CinemaHallService.class);

        CinemaHall cinemaHall = new CinemaHall();
        cinemaHall.setDescription("Cinema Hall 1");
        cinemaHallService.add(cinemaHall);

        MovieSession movieSession1 = new MovieSession();
        movieSession1.setCinemaHall(cinemaHall);
        movieSession1.setMovie(movie);
        movieSession1.setShowTime(LocalDateTime.of(2020, 11, 14, 17, 30));

        MovieSessionService movieSessionService
                = (MovieSessionService) injector.getInstance(MovieSessionService.class);
        movieSessionService.add(movieSession1);

        System.out.println(movieSessionService
                .findAvailableSessions(1L, LocalDate.of(2020, 11, 14)));

        AuthenticationService authenticationService =
                (AuthenticationService) injector.getInstance(AuthenticationService.class);
        User user1 = new User();
        user1.setEmail("user1@gmail.com");
        user1.setPassword("password");
        System.out.println(authenticationService.register(user1.getEmail(), user1.getPassword()));
        System.out.println(authenticationService.login(user1.getEmail(), user1.getPassword()));

        ShoppingCartService shoppingCartService
                = (ShoppingCartService) injector.getInstance(ShoppingCartService.class);
        UserService userService
                = (UserService) injector.getInstance(UserService.class);
        User userFromDb = userService.findByEmail("user1@gmail.com").get();
        shoppingCartService.addSession(movieSession1, userFromDb);
        ShoppingCart shoppingCart = shoppingCartService.getByUser(userFromDb);
        System.out.println("Cart with tickets: " + shoppingCart);

        OrderService orderService
                = (OrderService) injector.getInstance(OrderService.class);
        orderService.completeOrder(shoppingCart.getTickets(), userFromDb);
        orderService.getOrderHistory(userFromDb).forEach(System.out::println);
    }
}
