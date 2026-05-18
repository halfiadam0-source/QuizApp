package com.example.quizapp_halfi;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class Question {
    private int id;
    
    @SerializedName(value = "text", alternate = {"question", "title", "text_question"})
    private String text;

    @SerializedName(value = "option1", alternate = {"rep1", "answer1", "a", "option_1"})
    private String option1;

    @SerializedName(value = "option2", alternate = {"rep2", "answer2", "b", "option_2"})
    private String option2;

    @SerializedName(value = "option3", alternate = {"rep3", "answer3", "c", "option_3"})
    private String option3;

    @SerializedName(value = "option4", alternate = {"rep4", "answer4", "d", "option_4"})
    private String option4;

    @SerializedName(value = "options")
    private List<String> optionsList;

    @SerializedName(value = "correctAnswer", alternate = {"correct_answer", "reponse_correcte", "answer"})
    private String correctAnswer;

    @SerializedName(value = "imageName", alternate = {"image", "img"})
    private String imageName;

    public Question() {}

    public String getText() { return text; }

    public String getOption1() { return option1; }
    public String getOption2() { return option2; }
    public String getOption3() { return option3; }
    public String getOption4() { return option4; }

    public String getCorrectAnswer() { return correctAnswer; }
    public String getImageName() { return imageName; }

    public List<String> getOptions() {
        if (optionsList != null && !optionsList.isEmpty()) {
            return optionsList;
        }
        List<String> opts = new ArrayList<>();
        if (option1 != null && !option1.isEmpty()) opts.add(option1);
        if (option2 != null && !option2.isEmpty()) opts.add(option2);
        if (option3 != null && !option3.isEmpty()) opts.add(option3);
        if (option4 != null && !option4.isEmpty()) opts.add(option4);
        return opts;
    }
}
