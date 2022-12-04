package com.lab.ui.main;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lab.model.MeteorsApi;
import com.lab.model.Meteor;
import com.lab.model.MeteorsLoaderService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainViewModel extends ViewModel {

    private MutableLiveData<Meteor[]> meteors = new MutableLiveData<>();

    {
        meteors.postValue(new Meteor[0]);
    }

    public LiveData<Meteor[]> getMeteors() {
        return meteors;
    }

    public void setContext(Context context) {
        MeteorsLoaderService meteorsLoaderService = new MeteorsLoaderService(context);
        meteorsLoaderService.loadMeteors((m)->{
            meteors.postValue(m);
        });
    }
}