package com.example.cp;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class Connection {

    public interface Conn {
        @GET("users/{user}/repos")
        Call<List<Conn>> listRepos(@Path("user") String user);
    }

    public static class NetworkService {
        private static NetworkService mInstance;
        private static final String BASE_URL = "https://jsonplaceholder.typicode.com";
        private Retrofit mRetrofit;

        private NetworkService() {
            mRetrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        public static NetworkService getInstance() {
            if (mInstance == null) {
                mInstance = new NetworkService();
            }
            return mInstance;
        }
    }

}