package br.edu.ifsuldeminas.mch.codefacil;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import br.edu.ifsuldeminas.mch.codefacil.model.gemini.GeminiRequest;
import br.edu.ifsuldeminas.mch.codefacil.model.gemini.GeminiResponse;
import br.edu.ifsuldeminas.mch.codefacil.retrofit.GeminiApiService;
import br.edu.ifsuldeminas.mch.codefacil.retrofit.RetrofitClient;
import br.edu.ifsuldeminas.mch.codefacil.utils.AppPreferences;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DictionaryActivity extends AppCompatActivity {

    private final String GEMINI_API_KEY = "CHAVE_API"; // Substitua pela sua chave de API do Gemini

    private EditText editTextWord;
    private Button btnSearch;
    private TextView tvWordTitle, tvDefinition;
    private ProgressBar progressBar;
    private GeminiApiService apiService;
    private AppPreferences appPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        appPreferences = new AppPreferences(this);
        if (appPreferences.isDarkModeEnabled()) {
            setTheme(R.style.Theme_CodeFacil_Dark);
        } else {
            setTheme(R.style.Theme_CodeFacil);
        }

        setContentView(R.layout.activity_dictionary);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        editTextWord = findViewById(R.id.editTextWord);
        btnSearch = findViewById(R.id.btnSearch);
        tvWordTitle = findViewById(R.id.tvWordTitle);
        tvDefinition = findViewById(R.id.tvDefinition);
        progressBar = findViewById(R.id.progressBar);

        apiService = RetrofitClient.getClient().create(GeminiApiService.class);

        btnSearch.setOnClickListener(v -> {
            String word = editTextWord.getText().toString().trim();
            if (!word.isEmpty()) {
                fetchDefinition(word);
            } else {
                Toast.makeText(this, "Por favor, insira um termo.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchDefinition(String word) {
        if(GEMINI_API_KEY.equals("SUA_CHAVE_DE_API_AQUI")){
            Toast.makeText(this, "Por favor, adicione a sua chave de API do Gemini.", Toast.LENGTH_LONG).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        tvWordTitle.setText("");
        tvDefinition.setText("");

        // PROMPT ATUALIZADO PARA PEDIR UMA RESPOSTA COMPLETA E BEM FORMATADA
        String prompt = "Aja como um especialista em programação e explique o termo '" + word + "' em português do Brasil. A sua resposta deve ser didática para um iniciante e seguir estritamente esta estrutura:\n\n" +
                "Definição:\n" +
                "[Explicação clara e concisa do termo.]\n\n" +
                "Analogia:\n" +
                "[Uma analogia simples do dia a dia para facilitar o entendimento.]\n\n" +
                "Exemplo Prático:\n" +
                "[Um exemplo curto de código ou de uso prático, se aplicável. Se não, escreva 'Não se aplica.']\n\n" +
                "Não adicione nenhuma outra formatação como aspas ou blocos de código com ```.";

        GeminiRequest request = new GeminiRequest(prompt);

        apiService.getDefinition(GEMINI_API_KEY, request).enqueue(new Callback<GeminiResponse>() {
            @Override
            public void onResponse(Call<GeminiResponse> call, Response<GeminiResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    tvWordTitle.setText(word);

                    // O tratamento para remover `*` e ``` continua a ser uma boa prática
                    String cleanedResponse = response.body().getResponseText()
                            .replace("`", "")
                            .trim();

                    tvDefinition.setText(cleanedResponse);
                } else {
                    Toast.makeText(DictionaryActivity.this, "Não foi possível obter uma definição.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GeminiResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(DictionaryActivity.this, "Erro de rede: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}