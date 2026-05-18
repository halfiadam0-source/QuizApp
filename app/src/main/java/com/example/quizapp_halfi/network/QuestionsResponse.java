package com.example.quizapp_halfi.network;

import java.util.List;
import com.example.quizapp_halfi.Question;

public class QuestionsResponse {
    private boolean success;
    private List<Question> questions;

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public List<Question> getQuestions() { return questions; }
    public void setQuestions(List<Question> questions) { this.questions = questions; }
}