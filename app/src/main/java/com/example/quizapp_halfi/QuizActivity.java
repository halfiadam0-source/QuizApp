package com.example.quizapp_halfi;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.example.quizapp_halfi.network.RetrofitClient;
import com.example.quizapp_halfi.network.QuestionsResponse;

public class QuizActivity extends AppCompatActivity {
    private RadioGroup radioGroup;
    private Button buttonNext;
    private TextView textViewQuestion;
    private ImageView imageViewQuestion;
    private RadioButton rb1, rb2, rb3, rb4;

    private List<Question> questionList;
    private int currentQuestionIndex = 0;
    private int userScore = 0;
    private int totalQuestionCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz1);

        // Initialiser les vues
        radioGroup = findViewById(R.id.rg);
        buttonNext = findViewById(R.id.bNext);
        textViewQuestion = findViewById(R.id.tvQuestion);
        imageViewQuestion = findViewById(R.id.ivQuestion);
        rb1 = findViewById(R.id.rb1);
        rb2 = findViewById(R.id.rb2);
        rb3 = findViewById(R.id.rb3);
        rb4 = findViewById(R.id.rb4);

        // Score initial
        userScore = 0;

        // Charger les questions depuis le backend
        fetchQuestionsFromBackend();

        // Gérer le clic sur "Suivant"
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswerAndGoToNext();
            }
        });
    }

    private void fetchQuestionsFromBackend() {
        RetrofitClient.getApiService().getQuestions().enqueue(new Callback<QuestionsResponse>() {
            @Override
            public void onResponse(Call<QuestionsResponse> call, Response<QuestionsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    questionList = response.body().getQuestions();
                    if (questionList != null && !questionList.isEmpty()) {
                        totalQuestionCount = questionList.size();
                        displayQuestion();
                    } else {
                        Toast.makeText(QuizActivity.this, "Aucune question disponible", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    Toast.makeText(QuizActivity.this, "Erreur de chargement des questions", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<QuestionsResponse> call, Throwable t) {
                Toast.makeText(QuizActivity.this, "Erreur réseau: " + t.getMessage(), Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private void displayQuestion() {
        if (currentQuestionIndex >= totalQuestionCount) {
            // Fin du quiz, aller à l'écran de score
            Intent intent = new Intent(QuizActivity.this, Score.class);
            intent.putExtra("score", userScore);
            intent.putExtra("total", totalQuestionCount);
            startActivity(intent);
            finish();
            return;
        }

        Question currentQuestion = questionList.get(currentQuestionIndex);
        textViewQuestion.setText(currentQuestion.getText());

        // Gérer l'image si elle existe
        if (imageViewQuestion != null) {
            if (currentQuestion.getImageName() != null && !currentQuestion.getImageName().isEmpty()) {
                int resId = getResources().getIdentifier(currentQuestion.getImageName(), "drawable", getPackageName());
                if (resId != 0) {
                    imageViewQuestion.setImageResource(resId);
                    imageViewQuestion.setVisibility(View.VISIBLE);
                } else {
                    imageViewQuestion.setVisibility(View.GONE);
                }
            } else {
                imageViewQuestion.setVisibility(View.GONE);
            }
        }

        // Récupérer les options dynamiquement (gère les formats tableau et champs séparés)
        List<String> options = currentQuestion.getOptions();
        
        // Cacher tous les RadioButtons par défaut
        rb1.setVisibility(View.GONE);
        rb2.setVisibility(View.GONE);
        rb3.setVisibility(View.GONE);
        rb4.setVisibility(View.GONE);

        // Afficher et remplir les options disponibles
        if (options != null) {
            if (options.size() > 0) {
                rb1.setText(options.get(0));
                rb1.setVisibility(View.VISIBLE);
            }
            if (options.size() > 1) {
                rb2.setText(options.get(1));
                rb2.setVisibility(View.VISIBLE);
            }
            if (options.size() > 2) {
                rb3.setText(options.get(2));
                rb3.setVisibility(View.VISIBLE);
            }
            if (options.size() > 3) {
                rb4.setText(options.get(3));
                rb4.setVisibility(View.VISIBLE);
            }
        }

        // Désélectionner les options précédentes
        radioGroup.clearCheck();
    }

    private void checkAnswerAndGoToNext() {
        int selectedId = radioGroup.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(QuizActivity.this, "Veuillez sélectionner une réponse", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selectedRadioButton = findViewById(selectedId);
        String selectedAnswer = selectedRadioButton.getText().toString();
        String correctAnswer = questionList.get(currentQuestionIndex).getCorrectAnswer();

        if (selectedAnswer != null && selectedAnswer.equalsIgnoreCase(correctAnswer)) {
            userScore++;
        }

        // Passer à la question suivante
        currentQuestionIndex++;
        displayQuestion();
    }
}
