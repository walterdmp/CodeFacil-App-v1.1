package br.edu.ifsuldeminas.mch.codefacil.retrofit;

import br.edu.ifsuldeminas.mch.codefacil.model.gemini.GeminiRequest;
import br.edu.ifsuldeminas.mch.codefacil.model.gemini.GeminiResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface GeminiApiService {
    @POST("v1beta/models/gemini-1.5-flash:generateContent")
    Call<GeminiResponse> getDefinition(@Query("key") String apiKey, @Body GeminiRequest request);
}