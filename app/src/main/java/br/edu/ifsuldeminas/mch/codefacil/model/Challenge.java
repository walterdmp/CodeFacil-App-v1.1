package br.edu.ifsuldeminas.mch.codefacil.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import java.io.Serializable;
import java.util.List;

@Entity(tableName = "challenges")
public class Challenge implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    private long id;

    private String title;
    private String enunciado;
    @ColumnInfo(name = "correct_answer")
    private String correctAnswer;
    private String level;
    private String explanation;
    private List<String> options;

    @Ignore
    private boolean isCompleted;
    @Ignore
    private boolean isCorrect;

    public Challenge() {
    }

    // Getters e Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getEnunciado() { return enunciado; }
    public void setEnunciado(String enunciado) { this.enunciado = enunciado; }
    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
    public List<String> getOptions() { return options; }
    public void setOptions(List<String> options) { this.options = options; }
    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }
    public boolean isCorrect() { return isCorrect; }
    public void setCorrect(boolean correct) { isCorrect = correct; }
}
