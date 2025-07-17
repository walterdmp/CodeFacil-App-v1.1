package br.edu.ifsuldeminas.mch.codefacil.model;

import java.io.Serializable;
import java.util.List;

public class Challenge implements Serializable {
    private long id;
    private String title;
    private String enunciado;
    private String correctAnswer;
    private String level;
    private String explanation;
    private List<String> options; // Novo campo para as opções

    public Challenge() {
    }

    // Construtor atualizado (opcional, mas bom para consistência)
    public Challenge(long id, String title, String enunciado, String correctAnswer, String level, String explanation, List<String> options) {
        this.id = id;
        this.title = title;
        this.enunciado = enunciado;
        this.correctAnswer = correctAnswer;
        this.level = level;
        this.explanation = explanation;
        this.options = options;
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

    // Getter e Setter para as opções
    public List<String> getOptions() { return options; }
    public void setOptions(List<String> options) { this.options = options; }


    // Campos de status (mantidos)
    private boolean isCompleted;
    private boolean isCorrect;

    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }
    public boolean isCorrect() { return isCorrect; }
    public void setCorrect(boolean correct) { isCorrect = correct; }
}
