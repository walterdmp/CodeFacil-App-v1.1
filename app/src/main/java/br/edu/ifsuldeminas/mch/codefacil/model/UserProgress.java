package br.edu.ifsuldeminas.mch.codefacil.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_progress",
        foreignKeys = @ForeignKey(entity = Challenge.class,
                parentColumns = "_id",
                childColumns = "challenge_id",
                onDelete = ForeignKey.CASCADE))
public class UserProgress {

    @PrimaryKey
    @ColumnInfo(name = "challenge_id")
    private long challengeId;

    @ColumnInfo(name = "is_completed")
    private boolean isCompleted;

    @ColumnInfo(name = "is_correct")
    private boolean isCorrect;

    @ColumnInfo(name = "last_accessed")
    private long lastAccessedTimestamp;

    private String annotation;

    public UserProgress() {}

    public UserProgress(long challengeId, boolean isCompleted, boolean isCorrect, long lastAccessedTimestamp, String annotation) {
        this.challengeId = challengeId;
        this.isCompleted = isCompleted;
        this.isCorrect = isCorrect;
        this.lastAccessedTimestamp = lastAccessedTimestamp;
        this.annotation = annotation;
    }

    // Getters e Setters
    public long getChallengeId() { return challengeId; }
    public void setChallengeId(long challengeId) { this.challengeId = challengeId; }
    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }
    public boolean isCorrect() { return isCorrect; }
    public void setCorrect(boolean correct) { isCorrect = correct; }
    public long getLastAccessedTimestamp() { return lastAccessedTimestamp; }
    public void setLastAccessedTimestamp(long lastAccessedTimestamp) { this.lastAccessedTimestamp = lastAccessedTimestamp; }
    public String getAnnotation() { return annotation; }
    public void setAnnotation(String annotation) { this.annotation = annotation; }
}