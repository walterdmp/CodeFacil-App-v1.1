package br.edu.ifsuldeminas.mch.codefacil.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;
import br.edu.ifsuldeminas.mch.codefacil.model.Challenge;

@Dao
public interface ChallengeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Challenge... challenges);

    @Query("SELECT * FROM challenges ORDER BY _id ASC")
    LiveData<List<Challenge>> getAll();

    @Query("SELECT * FROM challenges WHERE _id > :currentChallengeId ORDER BY _id ASC LIMIT 1")
    LiveData<Challenge> getNextChallenge(long currentChallengeId);
}