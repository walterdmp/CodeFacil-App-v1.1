package br.edu.ifsuldeminas.mch.codefacil.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;
import br.edu.ifsuldeminas.mch.codefacil.model.UserProgress;

@Dao
public interface UserProgressDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(UserProgress userProgress);

    @Update
    void update(UserProgress userProgress);

    @Query("SELECT * FROM user_progress WHERE challenge_id = :challengeId")
    UserProgress getProgressById(long challengeId);

    @Query("SELECT * FROM user_progress")
    List<UserProgress> getAllProgress();

    @Query("DELETE FROM user_progress WHERE challenge_id = :challengeId")
    void deleteProgressById(long challengeId);

    default void saveOrUpdate(UserProgress progress) {
        UserProgress progressFromDb = getProgressById(progress.getChallengeId());
        if (progressFromDb == null) {
            insert(progress);
        } else {
            // Garante que o ID do desafio não seja sobrescrito se já existir
            progress.setChallengeId(progressFromDb.getChallengeId());
            update(progress);
        }
    }
}