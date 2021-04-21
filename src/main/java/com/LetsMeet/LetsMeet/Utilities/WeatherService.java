package com.LetsMeet.LetsMeet.Utilities;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class WeatherService {

    private static final Logger LOGGER= LoggerFactory.getLogger(WeatherService.class);

    @Autowired
    LetsMeetConfiguration config;

    public String getCurrentTemp(Double latitude, Double longitude){
        try {
            // Form url
            String requestUrl = String.format("https://api.openweathermap.org/data/2.5/onecall?lat=%f" +
                    "&lon=%f&appid=%s&exclude=minutely,hourly,daily,alerts&units=metric&lang=en", latitude, longitude, config.getopenweatherApiKey());

            Request request = new Request.Builder()
                    .url(requestUrl)
                    .build();

            LOGGER.info("Weather request: " + request.toString());

            // Send request
            OkHttpClient httpClient = new OkHttpClient();
            Response response = httpClient.newCall(request).execute();

            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            // Parse response
            Gson gson = new Gson();
            JsonObject json = gson.fromJson(response.body().string(), JsonObject.class);

            String currentTemperature = json.get("current").getAsJsonObject().get("temp").toString();

            return currentTemperature;

        }catch(Exception e){
            LOGGER.warn("Error accessing current weather: {}", e.getMessage());
        }
        return null;
    }

    public void getWeatherForecast(){

    }
}
