package com.finals.cinema.util;


public class Constants {

    public static final int ROLE_USER = 1;
    public static final int ROLE_ADMIN = 2;
    public static final String API_URL_SEARCH_ALL_FILMS = "https://imdb-internet-movie-database-unofficial.p.rapidapi.com/search/";
    public static final String API_URL_SEARCH_BY_NAME = "https://moviesminidatabase.p.rapidapi.com/movie/imdb_id/byTitle/";
    public static final String API_URL_SEARCH_BY_IMDB_ID = "https://moviesminidatabase.p.rapidapi.com/movie/id/";
    public static final String MOVIE_LENGTH_REGEX = "^(([1-9]|1[0-9]|2[0-3])h)(\\s([1-5]\\d|[1-9])min)?$|^(([1-5]\\d|[1-9])min)$";
    public static final String API_KEY = "3ed724e174msh8041e704bf51fa0p1fda46jsnd793d26b2f17";
    public static final String API_HOST = "moviesminidatabase.p.rapidapi.com";

//    -----------------------PAGE ROUTES---------------------------
    public static final String MAIN_VIEW_ROUTE = "main";
    public static final String REGISTRATION_VIEW_ROUTE = "registration";
    public static final String CONFIRMATION_VIEW_ROUTE = "confirmation";
    public static final String LOGIN_VIEW_ROUTE = "/";
    public static final String CINEMA_VIEW_ROUTE = "cinemas";
    public static final String PROJECTION_VIEW_ROUTE = "projections";
    public static final String TICKET_VIEW_ROUTE = "tickets";
    public static final String ADMIN_VIEW_ROUTE = "admin_panel";


}
