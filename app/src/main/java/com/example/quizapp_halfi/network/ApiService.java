package com.example.quizapp_halfi.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Body;
import java.util.List;
import java.util.Map;

public interface ApiService {
    // Récupérer toutes les questions
    @GET("api/questions")
    Call<QuestionsResponse> getQuestions();

    // Envoyer le résultat
    @POST("api/submit-result")
    Call<Void> submitResult(@Body Map<String, Object> result);
}