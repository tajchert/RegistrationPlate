package pl.tajchert.tablicarejestracyjna.api;


import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

public interface ITopRegistrationPlates {
    @GET("/d4325dfe-8f0a-4d88-ba5f-055ed72b2c8c/_query?input/webpage/url=http://tablica-rejestracyjna.pl/")
    void getTopDrivers(@Query("_user") String user, @Query("_apikey") String apikey, Callback<ApiTopRegistrationPlates> callback);

}
