package br.edu.ifsuldeminas.mch.codefacil.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.edu.ifsuldeminas.mch.codefacil.R;
import br.edu.ifsuldeminas.mch.codefacil.model.Challenge;
import br.edu.ifsuldeminas.mch.codefacil.model.UserProgress;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "codefacil.db";
    private static final int DATABASE_VERSION = 5; // Mantenha ou incremente se fizer alterações
    private Context context;

    public static final String TABLE_CHALLENGES = "challenges";
    public static final String COLUMN_CHALLENGE_ID = "_id";
    public static final String COLUMN_CHALLENGE_TITLE = "title";
    public static final String COLUMN_CHALLENGE_ENUNCIADO = "enunciado";
    public static final String COLUMN_CHALLENGE_CORRECT_ANSWER = "correct_answer";
    public static final String COLUMN_CHALLENGE_LEVEL = "level";
    public static final String COLUMN_CHALLENGE_EXPLANATION = "explanation";
    public static final String COLUMN_CHALLENGE_OPTIONS = "options";

    public static final String TABLE_USER_PROGRESS = "user_progress";
    public static final String COLUMN_PROGRESS_CHALLENGE_ID = "challenge_id";
    public static final String COLUMN_PROGRESS_IS_COMPLETED = "is_completed";
    public static final String COLUMN_PROGRESS_IS_CORRECT = "is_correct";
    public static final String COLUMN_PROGRESS_LAST_ACCESSED = "last_accessed";
    public static final String COLUMN_PROGRESS_ANNOTATION = "annotation";

    private static final String CREATE_TABLE_CHALLENGES = "CREATE TABLE " + TABLE_CHALLENGES + "("
            + COLUMN_CHALLENGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_CHALLENGE_TITLE + " TEXT NOT NULL,"
            + COLUMN_CHALLENGE_ENUNCIADO + " TEXT NOT NULL,"
            + COLUMN_CHALLENGE_CORRECT_ANSWER + " TEXT NOT NULL,"
            + COLUMN_CHALLENGE_LEVEL + " TEXT NOT NULL,"
            + COLUMN_CHALLENGE_EXPLANATION + " TEXT NOT NULL,"
            + COLUMN_CHALLENGE_OPTIONS + " TEXT NOT NULL"
            + ");";

    private static final String CREATE_TABLE_USER_PROGRESS = "CREATE TABLE " + TABLE_USER_PROGRESS + "("
            + COLUMN_PROGRESS_CHALLENGE_ID + " INTEGER PRIMARY KEY,"
            + COLUMN_PROGRESS_IS_COMPLETED + " INTEGER DEFAULT 0,"
            + COLUMN_PROGRESS_IS_CORRECT + " INTEGER DEFAULT 0,"
            + COLUMN_PROGRESS_LAST_ACCESSED + " INTEGER DEFAULT 0,"
            + COLUMN_PROGRESS_ANNOTATION + " TEXT,"
            + "FOREIGN KEY(" + COLUMN_PROGRESS_CHALLENGE_ID + ") REFERENCES " + TABLE_CHALLENGES + "(" + COLUMN_CHALLENGE_ID + ") ON DELETE CASCADE"
            + ");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CHALLENGES);
        db.execSQL(CREATE_TABLE_USER_PROGRESS);
        insertInitialChallenges(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Implementação segura para não perder dados do usuário.
        // Se precisar adicionar uma nova coluna no futuro, faria aqui.
        // Exemplo:
        // if (oldVersion < 6) {
        //     db.execSQL("ALTER TABLE " + TABLE_CHALLENGES + " ADD COLUMN nova_coluna TEXT;");
        // }
        // A abordagem de apagar tudo só deve ser usada em desenvolvimento.
        // Por segurança, vamos comentar a linha destrutiva.
        // db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_PROGRESS);
        // db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHALLENGES);
        // onCreate(db);
    }

    public void resetChallengeProgress(long challengeId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USER_PROGRESS, COLUMN_PROGRESS_CHALLENGE_ID + " = ?", new String[]{String.valueOf(challengeId)});
        db.close();
    }

    private void insertInitialChallenges(SQLiteDatabase db) {
        String basic = context.getString(R.string.level_basic);
        String intermediate = context.getString(R.string.level_intermediate);
        String advanced = context.getString(R.string.level_advanced);

        // NÍVEL BÁSICO (10 desafios)
        addChallenge(db, "Variáveis e Tipos", "Qual tipo de variável é mais adequado para armazenar a idade de uma pessoa?", "int", basic, "O tipo 'int' é usado para armazenar números inteiros. 'String' é para texto e 'boolean' para verdadeiro/falso.", "int;String;boolean");
        addChallenge(db, "Operadores Aritméticos", "Em programação, qual o resultado da operação `10 % 3` (10 módulo 3)?", "1", basic, "O operador de módulo (%) retorna o resto de uma divisão. 10 dividido por 3 é 3, com resto 1.", "3;1;0");
        addChallenge(db, "Condicionais Simples", "Qual operador é usado para verificar se dois valores são EXATAMENTE iguais?", "==", basic, "O operador '==' compara se dois valores são iguais. '=' é usado para atribuição, e '!=' para verificar se são diferentes.", "=;==;!=");
        addChallenge(db, "Concatenação", "Se `a = \"Olá\"` e `b = \"Mundo\"`, qual o resultado de `a + \" \" + b`?", "Olá Mundo", basic, "O operador '+' com strings serve para juntá-las (concatenar).", "OláMundo;Olá Mundo;Erro");
        addChallenge(db, "Booleanos", "Qual o resultado da expressão `!true` (negação de verdadeiro)?", "false", basic, "O operador de negação `!` inverte o valor booleano. A negação de `true` é `false`.", "true;false;null");
        addChallenge(db, "Comentários", "Qual é a sintaxe para um comentário de uma única linha na maioria das linguagens (Java, C#, JS)?", "// comentário", basic, "As barras duplas `//` marcam o início de um comentário de uma linha, que é ignorado pelo compilador.", "# comentário;/* comentário */;// comentário");
        addChallenge(db, "Incremento", "Se `x = 5; x++;`, qual será o novo valor de `x`?", "6", basic, "O operador `++` incrementa o valor da variável em 1. Portanto, 5 + 1 = 6.", "5;6;7");
        addChallenge(db, "Tamanho da String", "Qual função é comumente usada para obter o número de caracteres em uma string `s`?", "s.length()", basic, "O método `.length()` (ou propriedade `.length` em algumas linguagens) retorna o tamanho da string.", "s.size();s.length();s.count()");
        addChallenge(db, "Atribuição vs Comparação", "A expressão `x = 10` faz qual operação?", "Atribuição", basic, "Um único sinal de igual `=` é usado para atribuir um valor a uma variável. Para comparação, usa-se `==`.", "Comparação;Atribuição;Negação");
        addChallenge(db, "Índice de Array", "Qual é o índice do PRIMEIRO elemento em um array?", "0", basic, "Na maioria das linguagens de programação, a contagem de índices de arrays começa em 0.", "1;0;-1");

        // NÍVEL INTERMEDIÁRIO (10 desafios)
        addChallenge(db, "Loops - For", "Um loop `for(int i = 0; i < 5; i++)` será executado quantas vezes?", "5", intermediate, "O loop executa para i = 0, 1, 2, 3 e 4. Quando i se torna 5, a condição `i < 5` se torna falsa e o loop para.", "4;5;6");
        addChallenge(db, "Arrays (Vetores)", "Se temos um array `nomes = [\"Ana\", \"Bia\", \"Carlos\"]`, qual valor está na posição de índice 1?", "Bia", intermediate, "Em programação, a contagem de índices de arrays geralmente começa em 0. Portanto, o índice 0 é \"Ana\" e o índice 1 é \"Bia\".", "Ana;Bia;Carlos");
        addChallenge(db, "Operadores Lógicos", "Se `A=true` e `B=false`, qual o resultado da expressão `A || B` (A OU B)?", "true", intermediate, "O operador lógico OU (||) retorna 'true' se PELO MENOS UMA das condições for verdadeira. Como A é 'true', o resultado é 'true'.", "true;false;Erro");
        addChallenge(db, "Switch Case", "Qual a função da palavra-chave `break` dentro de um `switch`?", "Interromper a execução do switch", intermediate, "`break` é usado para sair do bloco `switch`. Sem ele, o código continuaria a executar os casos seguintes (fall-through).", "Voltar ao início do switch;Pular para o último caso;Interromper a execução do switch");
        addChallenge(db, "Loop - While", "Qual a principal diferença entre um loop `while` e um `do-while`?", "do-while executa o bloco pelo menos uma vez", intermediate, "Um loop `do-while` sempre executa seu bloco de código uma vez antes de verificar a condição, enquanto um `while` pode nunca executar se a condição for inicialmente falsa.", "while é mais rápido;do-while executa o bloco pelo menos uma vez;Não há diferença");
        addChallenge(db, "Escopo de Variável", "Uma variável declarada dentro de uma função pode ser acessada fora dela?", "Não", intermediate, "Variáveis declaradas dentro de uma função têm escopo local, o que significa que só existem e podem ser acessadas dentro daquela função.", "Sim;Não;Apenas se for 'int'");
        addChallenge(db, "Parâmetros de Função", "O que são `parâmetros` em uma função `void print(String texto)`?", "Valores que a função recebe para trabalhar", intermediate, "Parâmetros são as variáveis listadas na definição da função que recebem os valores (argumentos) quando a função é chamada.", "Valores que a função retorna;Variáveis globais;Valores que a função recebe para trabalhar");
        addChallenge(db, "Loop Infinito", "Qual das seguintes condições em um loop `while` provavelmente causará um loop infinito?", "while(true)", intermediate, "A condição `true` nunca se tornará falsa, fazendo com que o loop se repita para sempre, a menos que haja um `break` interno.", "while(x < 10);while(true);while(x > 0)");
        addChallenge(db, "Acesso a Objeto", "Se `carro` é um objeto com a propriedade `cor`, como você acessaria essa propriedade?", "carro.cor", intermediate, "O operador ponto `.` é usado para acessar propriedades ou métodos de um objeto.", "carro->cor;carro['cor'];carro.cor");
        addChallenge(db, "Null", "O que o valor `null` representa em programação?", "Ausência de valor ou referência a um objeto", intermediate, "`null` é um valor especial que indica que uma variável de referência não aponta para nenhum objeto na memória.", "O número zero;Uma string vazia;Ausência de valor ou referência a um objeto");

        // NÍVEL AVANÇADO (10 desafios)
        addChallenge(db, "Funções/Métodos", "O que a palavra-chave `return` faz dentro de uma função?", "Encerra a função e envia um valor de volta", advanced, "`return` finaliza a execução da função atual e opcionalmente retorna um valor para o código que a chamou.", "Imprime um valor na tela;Declara uma nova variável;Encerra a função e envia um valor de volta");
        addChallenge(db, "Recursividade", "Qual é o principal risco de uma função recursiva mal implementada?", "Stack Overflow", advanced, "Se uma função recursiva não tiver uma condição de parada correta, ela continuará se chamando indefinidamente, esgotando a memória da pilha de chamadas (stack) e causando um erro de 'Stack Overflow'.", "Loop Infinito;Erro de Sintaxe;Stack Overflow");
        addChallenge(db, "Complexidade de Algoritmo", "Qual a complexidade de tempo (Big O) de uma busca linear em um array de N elementos?", "O(n)", advanced, "Na busca linear, no pior caso, precisamos verificar todos os N elementos do array, tornando a complexidade diretamente proporcional ao tamanho do array, ou seja, O(n).", "O(1);O(log n);O(n)");
        addChallenge(db, "Estrutura de Dados", "Qual estrutura de dados segue o princípio LIFO (Last-In, First-Out)?", "Pilha (Stack)", advanced, "Uma Pilha (Stack) funciona como uma pilha de pratos: o último prato colocado é o primeiro a ser retirado. Filas (Queues) são FIFO (First-In, First-Out).", "Fila (Queue);Pilha (Stack);Array");
        addChallenge(db, "Orientação a Objetos", "O conceito de agrupar dados (atributos) e métodos que operam nesses dados em uma única unidade é chamado de:", "Encapsulamento", advanced, "Encapsulamento é um dos pilares da POO e se refere a ocultar os detalhes internos de um objeto, expondo apenas o que é necessário.", "Herança;Polimorfismo;Encapsulamento");
        addChallenge(db, "Tratamento de Exceções", "Qual bloco de código é usado para capturar e tratar erros em tempo de execução?", "try-catch", advanced, "O código que pode gerar um erro (exceção) é colocado em um bloco `try`, e o código que trata o erro é colocado em um bloco `catch`.", "if-else;for-loop;try-catch");
        addChallenge(db, "Herança", "Quando uma classe `Gato` herda da classe `Animal`, dizemos que `Gato` é uma:", "Subclasse", advanced, "`Gato` é a subclasse (ou classe filha) e `Animal` é a superclasse (ou classe pai). A subclasse herda os atributos e métodos da superclasse.", "Superclasse;Interface;Subclasse");
        addChallenge(db, "Map/Dicionário", "Qual a principal característica de uma estrutura de dados do tipo Map (ou Dicionário)?", "Armazena pares de chave-valor", advanced, "Maps são coleções que associam uma chave única a um valor, permitindo uma recuperação de dados muito rápida através da chave.", "Armazena apenas valores únicos;Mantém os elementos em ordem;Armazena pares de chave-valor");
        addChallenge(db, "JSON", "O que significa JSON?", "JavaScript Object Notation", advanced, "JSON é um formato de texto leve para intercâmbio de dados, fácil para humanos lerem e para máquinas analisarem. É amplamente usado em APIs web.", "Java Standard Object Naming;JavaScript Object Notation;Java Source Open Network");
        addChallenge(db, "API", "No contexto de desenvolvimento, o que é uma API?", "Interface de Programação de Aplicações", advanced, "Uma API (Application Programming Interface) é um conjunto de regras e ferramentas que permite que diferentes aplicações de software se comuniquem umas com as outras.", "Análise Preditiva de Interface;Interface de Programação de Aplicações;Arquivo de Pacote de Instalação");

        Log.d("DatabaseHelper", "30 novos desafios de programação inseridos.");
    }

    private void addChallenge(SQLiteDatabase db, String title, String enunciado, String correctAnswer, String level, String explanation, String options) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_CHALLENGE_TITLE, title);
        values.put(COLUMN_CHALLENGE_ENUNCIADO, enunciado);
        values.put(COLUMN_CHALLENGE_CORRECT_ANSWER, correctAnswer);
        values.put(COLUMN_CHALLENGE_LEVEL, level);
        values.put(COLUMN_CHALLENGE_EXPLANATION, explanation);
        values.put(COLUMN_CHALLENGE_OPTIONS, options);
        db.insert(TABLE_CHALLENGES, null, values);
    }

    public Challenge getNextChallenge(long currentChallengeId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CHALLENGES + " WHERE " + COLUMN_CHALLENGE_ID + " > ? ORDER BY " + COLUMN_CHALLENGE_ID + " LIMIT 1", new String[]{String.valueOf(currentChallengeId)});
        Challenge challenge = null;
        if (cursor.moveToFirst()) {
            challenge = mapChallengeFromCursor(cursor);
        }
        cursor.close();
        return challenge;
    }

    public List<Challenge> getAllChallenges() {
        List<Challenge> challenges = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CHALLENGES, null, null, null, null, null, COLUMN_CHALLENGE_ID + " ASC");

        if (cursor.moveToFirst()) {
            do {
                challenges.add(mapChallengeFromCursor(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return challenges;
    }

    private Challenge mapChallengeFromCursor(Cursor cursor) {
        Challenge challenge = new Challenge();
        challenge.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CHALLENGE_ID)));
        challenge.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CHALLENGE_TITLE)));
        challenge.setEnunciado(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CHALLENGE_ENUNCIADO)));
        challenge.setCorrectAnswer(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CHALLENGE_CORRECT_ANSWER)));
        challenge.setLevel(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CHALLENGE_LEVEL)));
        challenge.setExplanation(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CHALLENGE_EXPLANATION)));
        String optionsString = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CHALLENGE_OPTIONS));
        challenge.setOptions(Arrays.asList(optionsString.split(";")));
        return challenge;
    }

    public UserProgress getUserProgress(long challengeId) {
        SQLiteDatabase db = this.getReadableDatabase();
        UserProgress userProgress = null;
        try (Cursor cursor = db.query(TABLE_USER_PROGRESS, null, COLUMN_PROGRESS_CHALLENGE_ID + " = ?", new String[]{String.valueOf(challengeId)}, null, null, null)) {
            if (cursor.moveToFirst()) {
                userProgress = new UserProgress();
                userProgress.setChallengeId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_PROGRESS_CHALLENGE_ID)));
                userProgress.setCompleted(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PROGRESS_IS_COMPLETED)) == 1);
                userProgress.setCorrect(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PROGRESS_IS_CORRECT)) == 1);
                userProgress.setLastAccessedTimestamp(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_PROGRESS_LAST_ACCESSED)));
                userProgress.setAnnotation(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROGRESS_ANNOTATION)));
            }
        }
        return userProgress;
    }

    public List<UserProgress> getAllUserProgress() {
        List<UserProgress> progressList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        try (Cursor cursor = db.query(TABLE_USER_PROGRESS, null, null, null, null, null, null)) {
            if (cursor.moveToFirst()) {
                do {
                    UserProgress userProgress = new UserProgress();
                    userProgress.setChallengeId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_PROGRESS_CHALLENGE_ID)));
                    userProgress.setCompleted(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PROGRESS_IS_COMPLETED)) == 1);
                    userProgress.setCorrect(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PROGRESS_IS_CORRECT)) == 1);
                    userProgress.setLastAccessedTimestamp(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_PROGRESS_LAST_ACCESSED)));
                    userProgress.setAnnotation(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROGRESS_ANNOTATION)));
                    progressList.add(userProgress);
                } while (cursor.moveToNext());
            }
        }
        return progressList;
    }

    public void saveOrUpdateUserProgress(UserProgress userProgress) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PROGRESS_IS_COMPLETED, userProgress.isCompleted() ? 1 : 0);
        values.put(COLUMN_PROGRESS_IS_CORRECT, userProgress.isCorrect() ? 1 : 0);
        values.put(COLUMN_PROGRESS_LAST_ACCESSED, userProgress.getLastAccessedTimestamp());
        values.put(COLUMN_PROGRESS_ANNOTATION, userProgress.getAnnotation());

        int rowsAffected = db.update(TABLE_USER_PROGRESS, values, COLUMN_PROGRESS_CHALLENGE_ID + " = ?", new String[]{String.valueOf(userProgress.getChallengeId())});
        if (rowsAffected == 0) {
            values.put(COLUMN_PROGRESS_CHALLENGE_ID, userProgress.getChallengeId());
            db.insert(TABLE_USER_PROGRESS, null, values);
        }
        db.close();
    }

    public void deleteAnnotation(long challengeId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.putNull(COLUMN_PROGRESS_ANNOTATION);
        db.update(TABLE_USER_PROGRESS, values, COLUMN_PROGRESS_CHALLENGE_ID + " = ?", new String[]{String.valueOf(challengeId)});
        db.close();
    }
}