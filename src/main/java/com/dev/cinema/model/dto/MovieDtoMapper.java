package com.dev.cinema.model.dto;

import com.dev.cinema.model.Movie;
import org.springframework.stereotype.Component;

@Component
public class MovieDtoMapper {
    public Movie mapToMovie(MovieRequestDto movieRequestDto) {
        Movie movie = new Movie();
        movie.setTitle(movieRequestDto.getTitle());
        movie.setDescription(movieRequestDto.getDescription());
        return movie;
    }

    public MovieResponseDto mapToDto(Movie movie) {
        MovieResponseDto movieResponseDto = new MovieResponseDto();
        movieResponseDto.setId(movie.getId());
        movieResponseDto.setDescription(movie.getDescription());
        movieResponseDto.setTitle(movie.getTitle());
        return movieResponseDto;
    }
}
