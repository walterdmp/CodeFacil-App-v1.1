package br.edu.ifsuldeminas.mch.codefacil.database;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import br.edu.ifsuldeminas.mch.codefacil.R;
import br.edu.ifsuldeminas.mch.codefacil.database.convert.Converters;
import br.edu.ifsuldeminas.mch.codefacil.database.dao.ChallengeDao;
import br.edu.ifsuldeminas.mch.codefacil.database.dao.UserProgressDao;
import br.edu.ifsuldeminas.mch.codefacil.model.Challenge;
import br.edu.ifsuldeminas.mch.codefacil.model.UserProgress;

@Database(entities = {Challenge.class, UserProgress.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract ChallengeDao challengeDao();
    public abstract UserProgressDao userProgressDao();

    private static volatile AppDatabase INSTANCE;

    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "codefacil_room.db")
                            .addCallback(new RoomDatabase.Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    databaseWriteExecutor.execute(() ->
                                            prepopulateDatabase(context, getDatabase(context).challengeDao())
                                    );
                                }
                            })
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static void prepopulateDatabase(Context context, ChallengeDao challengeDao) {
        String basic = context.getString(R.string.level_basic);
        String intermediate = context.getString(R.string.level_intermediate);
        String advanced = context.getString(R.string.level_advanced);

        Challenge[] challenges = new Challenge[]{
                // NÍVEL BÁSICO
                createChallenge("Variáveis e Tipos", "Qual tipo de variável é mais adequado para armazenar a idade de uma pessoa?", "int", basic, "O tipo 'int' é usado para armazenar números inteiros. 'String' é para texto e 'boolean' para verdadeiro/falso.", "int;String;boolean"),
                createChallenge("Operadores Aritméticos", "Em programação, qual o resultado da operação `10 % 3` (10 módulo 3)?", "1", basic, "O operador de módulo (%) retorna o resto de uma divisão. 10 dividido por 3 é 3, com resto 1.", "3;1;0"),
                createChallenge("Condicionais Simples", "Qual operador é usado para verificar se dois valores são EXATAMENTE iguais?", "==", basic, "O operador '==' compara se dois valores são iguais. '=' é usado para atribuição, e '!=' para verificar se são diferentes.", "=;==;!="),
                createChallenge("Concatenação", "Se `a = \"Olá\"` e `b = \"Mundo\"`, qual o resultado de `a + \" \" + b`?", "Olá Mundo", basic, "O operador '+' com strings serve para juntá-las (concatenar).", "OláMundo;Olá Mundo;Erro"),
                createChallenge("Booleanos", "Qual o resultado da expressão `!true` (negação de verdadeiro)?", "false", basic, "O operador de negação `!` inverte o valor booleano. A negação de `true` é `false`.", "true;false;null"),
                // ... (o restante da sua pré-população continua aqui)
        };
        challengeDao.insertAll(challenges);
    }

    private static Challenge createChallenge(String title, String enunciado, String correctAnswer, String level, String explanation, String options) {
        Challenge challenge = new Challenge();
        challenge.setTitle(title);
        challenge.setEnunciado(enunciado);
        challenge.setCorrectAnswer(correctAnswer);
        challenge.setLevel(level);
        challenge.setExplanation(explanation);
        challenge.setOptions(Arrays.asList(options.split(";")));
        return challenge;
    }
}