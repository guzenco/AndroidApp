package com.lab.model;

import android.content.Context;

import androidx.room.Room;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class MeteorsLoaderService {

    private final static String BASE_URL = "https://data.nasa.gov/resource/";
    private ExecutorService executorService;
    private Retrofit retrofit;
    private MeteorsApi meteorsApi;
    private MeteorsDatabase db;
    private MeteorsDao meteorsDao;

    public MeteorsLoaderService(Context context) {
        executorService = Executors.newScheduledThreadPool(1);
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        meteorsApi = retrofit.create(MeteorsApi.class);
        db = Room
                .databaseBuilder(context, MeteorsDatabase.class, "image.db")
                .build();
        meteorsDao = db.getImagesDao();
    }

    public void loadMeteors(MeteorLoadCallback callback) {
        executorService.submit(() -> {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            Meteor[] m_cashed = meteorsDao.getMeteors();
            meteorsApi.getMeteors("year>'" + LocalDateTime.now().minusYears(10).format(dtf) + "'").enqueue(new Callback<Meteor[]>() {
                @Override
                public void onResponse(Call<Meteor[]> call, Response<Meteor[]> response) {
                    if(response.isSuccessful()) {
                        Meteor[] m = response.body();
                        executorService.submit(() -> callback.onResponse(m));
                        if(!Arrays.equals(m, m_cashed)){
                            executorService.submit(() -> meteorsDao.insert(m));
                        }
                    }else {
                        executorService.submit(() -> callback.onResponse(meteorsDao.getMeteors()));
                    }
                }
                @Override
                public void onFailure(Call<Meteor[]> call, Throwable t) {
                    executorService.submit(() -> callback.onResponse(meteorsDao.getMeteors()));
                }
            });
        });
    }

    public interface MeteorLoadCallback{
        public void onResponse(Meteor meteors[]);
    }
}
