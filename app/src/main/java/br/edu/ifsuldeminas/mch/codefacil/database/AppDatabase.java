package br.edu.ifsuldeminas.mch.codefacil.database;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.Arrays;
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
                                    Executors.newSingleThreadExecutor().execute(() ->
                                            prepopulateDatabase(context, getDatabase(context).challengeDao())
                                    );
                                }
                            })
                            .allowMainThreadQueries() // Permite queries na thread principal para simplificar
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
                createChallenge("Comentários", "Qual é a sintaxe para um comentário de uma única linha na maioria das linguagens (Java, C#, JS)?", "// comentário", basic, "As barras duplas `//` marcam o início de um comentário de uma linha, que é ignorado pelo compilador.", "# comentário;/* comentário */;// comentário"),
                createChallenge("Incremento", "Se `x = 5; x++;`, qual será o novo valor de `x`?", "6", basic, "O operador `++` incrementa o valor da variável em 1. Portanto, 5 + 1 = 6.", "5;6;7"),
                createChallenge("Tamanho da String", "Qual função é comumente usada para obter o número de caracteres em uma string `s`?", "s.length()", basic, "O método `.length()` (ou propriedade `.length` em algumas linguagens) retorna o tamanho da string.", "s.size();s.length();s.count()"),
                createChallenge("Atribuição vs Comparação", "A expressão `x = 10` faz qual operação?", "Atribuição", basic, "Um único sinal de igual `=` é usado para atribuir um valor a uma variável. Para comparação, usa-se `==`.", "Comparação;Atribuição;Negação"),
                createChallenge("Índice de Array", "Qual é o índice do PRIMEIRO elemento em um array?", "0", basic, "Na maioria das linguagens de programação, a contagem de índices de arrays começa em 0.", "1;0;-1"),
                // NÍVEL INTERMEDIÁRIO
                createChallenge("Loops - For", "Um loop `for(int i = 0; i < 5; i++)` será executado quantas vezes?", "5", intermediate, "O loop executa para i = 0, 1, 2, 3 e 4. Quando i se torna 5, a condição `i < 5` se torna falsa e o loop para.", "4;5;6"),
                createChallenge("Arrays (Vetores)", "Se temos um array `nomes = [\"Ana\", \"Bia\", \"Carlos\"]`, qual valor está na posição de índice 1?", "Bia", intermediate, "Em programação, a contagem de índices de arrays geralmente começa em 0. Portanto, o índice 0 é \"Ana\" e o índice 1 é \"Bia\".", "Ana;Bia;Carlos"),
                createChallenge("Operadores Lógicos", "Se `A=true` e `B=false`, qual o resultado da expressão `A || B` (A OU B)?", "true", intermediate, "O operador lógico OU (||) retorna 'true' se PELO MENOS UMA das condições for verdadeira. Como A é 'true', o resultado é 'true'.", "true;false;Erro"),
                createChallenge("Switch Case", "Qual a função da palavra-chave `break` dentro de um `switch`?", "Interromper a execução do switch", intermediate, "`break` é usado para sair do bloco `switch`. Sem ele, o código continuaria a executar os casos seguintes (fall-through).", "Voltar ao início do switch;Pular para o último caso;Interromper a execução do switch"),
                createChallenge("Loop - While", "Qual a principal diferença entre um loop `while` e um `do-while`?", "do-while executa o bloco pelo menos uma vez", intermediate, "Um loop `do-while` sempre executa seu bloco de código uma vez antes de verificar a condição, enquanto um `while` pode nunca executar se a condição for inicialmente falsa.", "while é mais rápido;do-while executa o bloco pelo menos uma vez;Não há diferença"),
                createChallenge("Escopo de Variável", "Uma variável declarada dentro de uma função pode ser acessada fora dela?", "Não", intermediate, "Variáveis declaradas dentro de uma função têm escopo local, o que significa que só existem e podem ser acessadas dentro daquela função.", "Sim;Não;Apenas se for 'int'"),
                createChallenge("Parâmetros de Função", "O que são `parâmetros` em uma função `void print(String texto)`?", "Valores que a função recebe para trabalhar", intermediate, "Parâmetros são as variáveis listadas na definição da função que recebem os valores (argumentos) quando a função é chamada.", "Valores que a função retorna;Variáveis globais;Valores que a função recebe para trabalhar"),
                createChallenge("Loop Infinito", "Qual das seguintes condições em um loop `while` provavelmente causará um loop infinito?", "while(true)", intermediate, "A condição `true` nunca se tornará falsa, fazendo com que o loop se repita para sempre, a menos que haja um `break` interno.", "while(x < 10);while(true);while(x > 0)"),
                createChallenge("Acesso a Objeto", "Se `carro` é um objeto com a propriedade `cor`, como você acessaria essa propriedade?", "carro.cor", intermediate, "O operador ponto `.` é usado para acessar propriedades ou métodos de um objeto.", "carro->cor;carro['cor'];carro.cor"),
                createChallenge("Null", "O que o valor `null` representa em programação?", "Ausência de valor ou referência a um objeto", intermediate, "`null` é um valor especial que indica que uma variável de referência não aponta para nenhum objeto na memória.", "O número zero;Uma string vazia;Ausência de valor ou referência a um objeto"),
                // NÍVEL AVANÇADO
                createChallenge("Funções/Métodos", "O que a palavra-chave `return` faz dentro de uma função?", "Encerra a função e envia um valor de volta", advanced, "`return` finaliza a execução da função atual e opcionalmente retorna um valor para o código que a chamou.", "Imprime um valor na tela;Declara uma nova variável;Encerra a função e envia um valor de volta"),
                createChallenge("Recursividade", "Qual é o principal risco de uma função recursiva mal implementada?", "Stack Overflow", advanced, "Se uma função recursiva não tiver uma condição de parada correta, ela continuará se chamando indefinidamente, esgotando a memória da pilha de chamadas (stack) e causando um erro de 'Stack Overflow'.", "Loop Infinito;Erro de Sintaxe;Stack Overflow"),
                createChallenge("Complexidade de Algoritmo", "Qual a complexidade de tempo (Big O) de uma busca linear em um array de N elementos?", "O(n)", advanced, "Na busca linear, no pior caso, precisamos verificar todos os N elementos do array, tornando a complexidade diretamente proporcional ao tamanho do array, ou seja, O(n).", "O(1);O(log n);O(n)"),
                createChallenge("Estrutura de Dados", "Qual estrutura de dados segue o princípio LIFO (Last-In, First-Out)?", "Pilha (Stack)", advanced, "Uma Pilha (Stack) funciona como uma pilha de pratos: o último prato colocado é o primeiro a ser retirado. Filas (Queues) são FIFO (First-In, First-Out).", "Fila (Queue);Pilha (Stack);Array"),
                createChallenge("Orientação a Objetos", "O conceito de agrupar dados (atributos) e métodos que operam nesses dados em uma única unidade é chamado de:", "Encapsulamento", advanced, "Encapsulamento é um dos pilares da POO e se refere a ocultar os detalhes internos de um objeto, expondo apenas o que é necessário.", "Herança;Polimorfismo;Encapsulamento"),
                createChallenge("Tratamento de Exceções", "Qual bloco de código é usado para capturar e tratar erros em tempo de execução?", "try-catch", advanced, "O código que pode gerar um erro (exceção) é colocado em um bloco `try`, e o código que trata o erro é colocado em um bloco `catch`.", "if-else;for-loop;try-catch"),
                createChallenge("Herança", "Quando uma classe `Gato` herda da classe `Animal`, dizemos que `Gato` é uma:", "Subclasse", advanced, "`Gato` é a subclasse (ou classe filha) e `Animal` é a superclasse (ou classe pai). A subclasse herda os atributos e métodos da superclasse.", "Superclasse;Interface;Subclasse"),
                createChallenge("Map/Dicionário", "Qual a principal característica de uma estrutura de dados do tipo Map (ou Dicionário)?", "Armazena pares de chave-valor", advanced, "Maps são coleções que associam uma chave única a um valor, permitindo uma recuperação de dados muito rápida através da chave.", "Armazena apenas valores únicos;Mantém os elementos em ordem;Armazena pares de chave-valor"),
                createChallenge("JSON", "O que significa JSON?", "JavaScript Object Notation", advanced, "JSON é um formato de texto leve para intercâmbio de dados, fácil para humanos lerem e para máquinas analisarem. É amplamente usado em APIs web.", "Java Standard Object Naming;JavaScript Object Notation;Java Source Open Network"),
                createChallenge("API", "No contexto de desenvolvimento, o que é uma API?", "Interface de Programação de Aplicações", advanced, "Uma API (Application Programming Interface) é um conjunto de regras e ferramentas que permite que diferentes aplicações de software se comuniquem umas com as outras.", "Análise Preditiva de Interface;Interface de Programação de Aplicações;Arquivo de Pacote de Instalação")
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
