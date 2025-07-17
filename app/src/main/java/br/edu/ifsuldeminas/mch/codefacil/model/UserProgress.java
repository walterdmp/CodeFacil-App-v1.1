package br.edu.ifsuldeminas.mch.codefacil.model;

/**
 * Classe modelo para representar o progresso do usuário em um desafio.
 */
public class UserProgress {
    private long challengeId;
    private boolean isCompleted;
    private boolean isCorrect;
    private long lastAccessedTimestamp; // Timestamp em milissegundos
    private String annotation;

    public UserProgress() {
        // Construtor vazio
    }

    public UserProgress(long challengeId, boolean isCompleted, boolean isCorrect, long lastAccessedTimestamp, String annotation) {
        this.challengeId = challengeId;
        this.isCompleted = isCompleted;
        this.isCorrect = isCorrect;
        this.lastAccessedTimestamp = lastAccessedTimestamp;
        this.annotation = annotation;
    }

    // Getters e Setters
    public long getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(long challengeId) {
        this.challengeId = challengeId;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    public long getLastAccessedTimestamp() {
        return lastAccessedTimestamp;
    }

    public void setLastAccessedTimestamp(long lastAccessedTimestamp) {
        this.lastAccessedTimestamp = lastAccessedTimestamp;
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }
}
