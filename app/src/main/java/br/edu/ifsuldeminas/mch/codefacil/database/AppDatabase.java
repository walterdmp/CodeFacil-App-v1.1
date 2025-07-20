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
                // =================================================================
                // NÍVEL BÁSICO (25 DESAFIOS)
                // =================================================================
                createChallenge("Variáveis e Tipos", "Qual tipo de variável é mais adequado para armazenar a idade de uma pessoa?", "int", basic, "O tipo 'int' (inteiro) é usado para números sem casas decimais, como a idade.", "int;String;boolean"),
                createChallenge("Operadores Aritméticos", "Em programação, qual o resultado da operação `10 % 3` (10 módulo 3)?", "1", basic, "O operador de módulo (%) retorna o resto de uma divisão inteira. 10 dividido por 3 é 3, com resto 1.", "3;1;0"),
                createChallenge("Condicionais Simples", "Qual operador é usado para verificar se dois valores são EXATAMENTE iguais?", "==", basic, "O operador '==' compara se dois valores são iguais. '=' é usado para atribuição, e '!=' para verificar se são diferentes.", "=;==;!="),
                createChallenge("Concatenação", "Se `a = \"Olá\"` e `b = \"Mundo\"`, qual o resultado de `a + \" \" + b`?", "Olá Mundo", basic, "O operador '+' com strings serve para juntá-las (concatenar).", "OláMundo;Olá Mundo;Erro"),
                createChallenge("Booleanos", "Qual o resultado da expressão `!true` (negação de verdadeiro)?", "false", basic, "O operador de negação `!` inverte o valor booleano. A negação de `true` é `false`.", "true;false;null"),
                createChallenge("Comentários", "Qual é a sintaxe para um comentário de uma única linha na maioria das linguagens (Java, C#, JS)?", "// comentário", basic, "As barras duplas `//` marcam o início de um comentário de uma linha, que é ignorado pelo compilador.", "# comentário;/* comentário */;// comentário"),
                createChallenge("Incremento", "Se `x = 5; x++;`, qual será o novo valor de `x`?", "6", basic, "O operador `++` incrementa o valor da variável em 1. Portanto, 5 + 1 = 6.", "5;6;7"),
                createChallenge("Tamanho da String", "Qual função é comumente usada para obter o número de caracteres em uma string `s`?", "s.length()", basic, "O método `.length()` (ou propriedade `.length` em algumas linguagens) retorna o tamanho da string.", "s.size();s.length();s.count()"),
                createChallenge("Atribuição vs Comparação", "A expressão `x = 10` faz qual operação?", "Atribuição", basic, "Um único sinal de igual `=` é usado para atribuir um valor a uma variável. Para comparação, usa-se `==`.", "Comparação;Atribuição;Negação"),
                createChallenge("Índice de Array", "Qual é o índice do PRIMEIRO elemento em um array ou lista?", "0", basic, "Na maioria das linguagens de programação, a contagem de índices de arrays e listas começa em 0.", "1;0;-1"),
                createChallenge("Tipo de Dado Decimal", "Para armazenar o preço de um produto, como R$ 19,99, qual tipo de dado é mais indicado?", "float", basic, "'float' ou 'double' são usados para números com casas decimais. 'int' armazena apenas inteiros.", "int;String;float"),
                createChallenge("Operador de Subtração", "Qual é o resultado de `15 - 8`?", "7", basic, "O operador '-' realiza a subtração entre dois números.", "7;8;23"),
                createChallenge("Operador 'Diferente de'", "Qual operador verifica se dois valores NÃO são iguais?", "!=", basic, "O operador '!=' (diferente de) retorna verdadeiro se os valores comparados forem diferentes.", "==;!=;<>"),
                createChallenge("Condicional 'else'", "Em um bloco `if-else`, quando o código dentro do `else` é executado?", "Quando a condição do `if` é falsa", basic, "O bloco `else` é um caminho alternativo que só é executado se a condição do `if` principal não for atendida.", "Sempre;Quando a condição do `if` é verdadeira;Quando a condição do `if` é falsa"),
                createChallenge("Valores Booleanos", "Quais são os dois únicos valores que uma variável booleana pode ter?", "true e false", basic, "Booleanos representam valores lógicos de verdade, sendo 'true' (verdadeiro) ou 'false' (falso).", "0 e 1;yes e no;true e false"),
                createChallenge("Declaração de Variável", "O que a linha `int idade;` faz em um programa?", "Declara uma variável chamada 'idade'", basic, "Esta sintaxe reserva um espaço na memória para uma variável do tipo inteiro com o nome 'idade', sem atribuir um valor inicial.", "Atribui 10 à variável 'idade';Imprime a variável 'idade';Declara uma variável chamada 'idade'"),
                createChallenge("Operador de Multiplicação", "Qual símbolo é usado para multiplicação na maioria das linguagens?", "*", basic, "O asterisco `*` é o operador padrão para multiplicação. 'x' geralmente não é reconhecido como operador.", "x;*;%"),
                createChallenge("Estrutura de Repetição", "Para que serve um loop `for`?", "Repetir um bloco de código um número definido de vezes", basic, "O loop `for` é ideal quando se sabe de antemão quantas vezes uma ação deve ser repetida.", "Tomar uma decisão;Declarar uma variável;Repetir um bloco de código um número definido de vezes"),
                createChallenge("Tipo de Dado para Nomes", "Para armazenar o nome 'Maria', qual tipo de dado você usaria?", "String", basic, "'String' é o tipo de dado usado para armazenar sequências de caracteres, como texto.", "char;int;String"),
                createChallenge("Operador de Divisão", "Qual é o resultado da divisão `20 / 4`?", "5", basic, "A barra `/` é o operador padrão para a operação de divisão.", "4;5;80"),
                createChallenge("Bloco de Código", "O que `{ }` (chaves) definem em muitas linguagens de programação?", "Um bloco de código", basic, "As chaves são usadas para agrupar múltiplas instruções em um único bloco, como no corpo de um `if`, `for` ou de uma função.", "Um comentário;Uma variável;Um bloco de código"),
                createChallenge("Função `print`", "Qual é a principal finalidade de uma função como `print()` ou `console.log()`?", "Exibir dados na tela ou console", basic, "Essas funções são usadas para depuração e para mostrar resultados ao usuário, imprimindo valores no dispositivo de saída padrão.", "Ler dados do teclado;Exibir dados na tela ou console;Calcular valores"),
                createChallenge("Decremento", "Se `y = 10; y--;`, qual será o novo valor de `y`?", "9", basic, "O operador `--` decrementa (subtrai 1) do valor da variável.", "10;9;11"),
                createChallenge("Palavra-chave `if`", "O que a palavra-chave `if` inicia?", "Uma estrutura condicional", basic, "`if` é usado para criar uma estrutura que executa um bloco de código apenas se uma determinada condição for verdadeira.", "Um loop;Uma função;Uma estrutura condicional"),
                createChallenge("Variável Constante", "Se você precisa de uma variável que nunca mude de valor, o que você declara?", "Uma constante", basic, "Constantes (usando palavras-chave como `const` ou `final`) são variáveis cujo valor não pode ser alterado após a sua atribuição inicial.", "Uma variável global;Uma constante;Uma variável de texto"),

                // =================================================================
                // NÍVEL INTERMEDIÁRIO (25 DESAFIOS)
                // =================================================================
                createChallenge("Loops - For", "Um loop `for(int i = 0; i < 5; i++)` será executado quantas vezes?", "5", intermediate, "O loop executa para i = 0, 1, 2, 3 e 4. Quando i se torna 5, a condição `i < 5` se torna falsa e o loop para.", "4;5;6"),
                createChallenge("Arrays (Vetores)", "Se temos um array `nomes = [\"Ana\", \"Bia\", \"Carlos\"]`, qual valor está na posição de índice 1?", "Bia", intermediate, "Em programação, a contagem de índices de arrays geralmente começa em 0. Portanto, o índice 0 é \"Ana\" e o índice 1 é \"Bia\".", "Ana;Bia;Carlos"),
                createChallenge("Operadores Lógicos", "Se `A=true` e `B=false`, qual o resultado da expressão `A && B` (A E B)?", "false", intermediate, "O operador lógico E (&&) só retorna 'true' se AMBAS as condições forem verdadeiras. Como B é 'false', o resultado é 'false'.", "true;false;Erro"),
                createChallenge("Switch Case", "Qual a função da palavra-chave `break` dentro de um `switch`?", "Interromper a execução do switch", intermediate, "`break` é usado para sair do bloco `switch`. Sem ele, o código continuaria a executar os casos seguintes (fall-through).", "Voltar ao início do switch;Pular para o último caso;Interromper a execução do switch"),
                createChallenge("Loop - While", "Qual a principal diferença entre um loop `while` e um `do-while`?", "do-while executa o bloco pelo menos uma vez", intermediate, "Um loop `do-while` sempre executa seu bloco de código uma vez antes de verificar a condição, enquanto um `while` pode nunca executar se a condição for inicialmente falsa.", "while é mais rápido;do-while executa o bloco pelo menos uma vez;Não há diferença"),
                createChallenge("Escopo de Variável", "Uma variável declarada dentro de uma função pode ser acessada fora dela?", "Não", intermediate, "Variáveis declaradas dentro de uma função têm escopo local, o que significa que só existem e podem ser acessadas dentro daquela função.", "Sim;Não;Apenas se for 'int'"),
                createChallenge("Parâmetros de Função", "O que são `parâmetros` em uma função `void print(String texto)`?", "Valores que a função recebe para trabalhar", intermediate, "Parâmetros são as variáveis listadas na definição da função que recebem os valores (argumentos) quando a função é chamada.", "Valores que a função retorna;Variáveis globais;Valores que a função recebe para trabalhar"),
                createChallenge("Loop Infinito", "Qual das seguintes condições em um loop `while` provavelmente causará um loop infinito?", "while(true)", intermediate, "A condição `true` nunca se tornará falsa, fazendo com que o loop se repita para sempre, a menos que haja um `break` interno.", "while(x < 10);while(true);while(x > 0)"),
                createChallenge("Acesso a Objeto", "Se `carro` é um objeto com a propriedade `cor`, como você acessaria essa propriedade?", "carro.cor", intermediate, "O operador ponto `.` é usado para acessar propriedades ou métodos de um objeto.", "carro->cor;carro['cor'];carro.cor"),
                createChallenge("Null", "O que o valor `null` representa em programação?", "Ausência de valor ou referência a um objeto", intermediate, "`null` é um valor especial que indica que uma variável de referência não aponta para nenhum objeto na memória.", "O número zero;Uma string vazia;Ausência de valor ou referência a um objeto"),
                createChallenge("Funções com Retorno", "Uma função definida como `int soma(int a, int b)` deve obrigatoriamente ter qual comando?", "return", intermediate, "Funções que declaram um tipo de retorno (como `int`) devem usar a palavra-chave `return` para devolver um valor desse tipo.", "print;break;return"),
                createChallenge("Array Multidimensional", "Como você acessaria o elemento na primeira linha e segunda coluna de uma matriz `m`?", "m[0][1]", intermediate, "Em matrizes, o primeiro índice representa a linha e o segundo a coluna, ambos começando em 0.", "m[1][2];m[0][1];m[2][1]"),
                createChallenge("Operador Ternário", "Qual o resultado da expressão `(5 > 3) ? \"Sim\" : \"Não\"`?", "Sim", intermediate, "O operador ternário é um atalho para o `if-else`. Se a condição `(5 > 3)` for verdadeira, ele retorna o primeiro valor (\"Sim\").", "Sim;Não;Erro"),
                createChallenge("Conversão de Tipos (Casting)", "O que a expressão `(int) 9.9;` faz?", "Converte 9.9 para o inteiro 9", intermediate, "A conversão explícita (casting) de um número decimal para inteiro trunca a parte fracionária, não arredonda.", "Arredonda para 10;Converte 9.9 para o inteiro 9;Causa um erro"),
                createChallenge("Loop `for-each`", "Para que serve o loop `for-each` (ou `for...of`)?", "Iterar sobre todos os elementos de uma coleção", intermediate, "Este tipo de loop simplifica a iteração sobre arrays e outras coleções, acessando cada elemento diretamente sem a necessidade de um contador de índice.", "Executar um loop um número fixo de vezes;Iterar sobre todos os elementos de uma coleção;Criar um loop infinito"),
                createChallenge("Métodos de String", "Qual método de string é geralmente usado para converter todo o texto para letras maiúsculas?", "toUpperCase()", intermediate, "O método `toUpperCase()` retorna uma nova string com todos os caracteres convertidos para maiúsculo.", "toUpperCase();upper();toCapital()"),
                createChallenge("Argumentos vs Parâmetros", "Quando chamamos uma função `soma(5, 10)`, os valores 5 e 10 são chamados de:", "Argumentos", intermediate, "'Parâmetros' são as variáveis na declaração da função, enquanto 'argumentos' são os valores reais passados durante a chamada.", "Parâmetros;Argumentos;Variáveis"),
                createChallenge("Listas vs Arrays", "Qual é uma vantagem comum de usar uma Lista (List) em vez de um Array tradicional?", "Tamanho dinâmico", intermediate, "Listas podem crescer e encolher dinamicamente, enquanto arrays geralmente têm um tamanho fixo definido na sua criação.", "Acesso mais rápido;Usa menos memória;Tamanho dinâmico"),
                createChallenge("Sobrecarga de Métodos", "O que significa sobrecarregar um método (method overloading)?", "Ter múltiplos métodos com o mesmo nome, mas parâmetros diferentes", intermediate, "A sobrecarga permite criar várias versões de um método que realizam tarefas semelhantes, mas com diferentes tipos ou número de parâmetros.", "Substituir um método da classe pai;Ter múltiplos métodos com o mesmo nome, mas parâmetros diferentes;Um método que chama a si mesmo"),
                createChallenge("Objeto `Math`", "Qual função do objeto `Math` é usada para encontrar o maior de dois números?", "Math.max()", intermediate, "`Math.max(a, b)` retorna o maior valor entre `a` e `b`.", "Math.round();Math.max();Math.bigger()"),
                createChallenge("Palavra-chave `this`", "Dentro de um método de um objeto, a que a palavra-chave `this` (ou `self`) geralmente se refere?", "À própria instância do objeto", intermediate, "`this` é usado para desambiguar e se referir a membros (propriedades, métodos) da instância atual do objeto.", "À classe pai;A uma variável global;À própria instância do objeto"),
                createChallenge("Importação de Módulos", "Para que serve o comando `import` (ou `include`, `require`) no início de um arquivo?", "Para usar código de outras bibliotecas ou arquivos", intermediate, "Este comando permite que seu código acesse funções, classes e variáveis que foram definidas em outros locais, promovendo a reutilização e organização.", "Para imprimir texto;Para iniciar o programa;Para usar código de outras bibliotecas ou arquivos"),
                createChallenge("Verificação de Nulo", "Qual é a maneira mais segura de usar uma variável que pode ser `null`?", "Verificar se ela é diferente de `null` antes de usar", intermediate, "Tentar acessar um método ou propriedade de uma variável nula causa um erro (`NullPointerException`). A verificação prévia evita isso.", "Sempre atribuir um valor a ela;Verificar se ela é diferente de `null` antes de usar;Usá-la dentro de um loop"),
                createChallenge("Operador Lógico OU", "Se `A=false` e `B=false`, qual o resultado de `A || B`?", "false", intermediate, "O operador OU (||) só retorna `false` se AMBAS as condições forem falsas.", "true;false;Erro de compilação"),
                createChallenge("Índice do Último Elemento", "Em um array de tamanho `N`, qual é o índice do último elemento?", "N-1", intermediate, "Como a indexação começa em 0, um array de `N` elementos terá índices de 0 até `N-1`.", "N;N+1;N-1"),

                // =================================================================
                // NÍVEL AVANÇADO (25 DESAFIOS)
                // =================================================================
                createChallenge("Funções/Métodos", "O que a palavra-chave `return` faz dentro de uma função?", "Encerra a função e envia um valor de volta", advanced, "`return` finaliza a execução da função atual e opcionalmente retorna um valor para o código que a chamou.", "Imprime um valor na tela;Declara uma nova variável;Encerra a função e envia um valor de volta"),
                createChallenge("Recursividade", "Qual é o principal risco de uma função recursiva mal implementada?", "Stack Overflow", advanced, "Se uma função recursiva não tiver uma condição de parada correta, ela continuará se chamando indefinidamente, esgotando a memória da pilha de chamadas (stack) e causando um erro de 'Stack Overflow'.", "Loop Infinito;Erro de Sintaxe;Stack Overflow"),
                createChallenge("Complexidade de Algoritmo", "Qual a complexidade de tempo (Big O) de uma busca binária em um array ordenado de N elementos?", "O(log n)", advanced, "A busca binária divide o espaço de busca pela metade a cada passo, resultando em uma complexidade logarítmica, muito mais eficiente que a busca linear para grandes volumes de dados.", "O(1);O(log n);O(n)"),
                createChallenge("Estrutura de Dados", "Qual estrutura de dados segue o princípio LIFO (Last-In, First-Out)?", "Pilha (Stack)", advanced, "Uma Pilha (Stack) funciona como uma pilha de pratos: o último prato colocado é o primeiro a ser retirado. Filas (Queues) são FIFO (First-In, First-Out).", "Fila (Queue);Pilha (Stack);Array"),
                createChallenge("Orientação a Objetos", "O conceito de agrupar dados (atributos) e métodos que operam nesses dados em uma única unidade é chamado de:", "Encapsulamento", advanced, "Encapsulamento é um dos pilares da POO e se refere a ocultar os detalhes internos de um objeto, expondo apenas o que é necessário.", "Herança;Polimorfismo;Encapsulamento"),
                createChallenge("Tratamento de Exceções", "Qual bloco de código é usado para capturar e tratar erros em tempo de execução?", "try-catch", advanced, "O código que pode gerar um erro (exceção) é colocado em um bloco `try`, e o código que trata o erro é colocado em um bloco `catch`.", "if-else;for-loop;try-catch"),
                createChallenge("Herança", "Quando uma classe `Gato` herda da classe `Animal`, dizemos que `Gato` é uma:", "Subclasse", advanced, "`Gato` é a subclasse (ou classe filha) e `Animal` é a superclasse (ou classe pai). A subclasse herda os atributos e métodos da superclasse.", "Superclasse;Interface;Subclasse"),
                createChallenge("Map/Dicionário", "Qual a principal característica de uma estrutura de dados do tipo Map (ou Dicionário)?", "Armazena pares de chave-valor", advanced, "Maps são coleções que associam uma chave única a um valor, permitindo uma recuperação de dados muito rápida através da chave.", "Armazena apenas valores únicos;Mantém os elementos em ordem;Armazena pares de chave-valor"),
                createChallenge("JSON", "O que significa JSON?", "JavaScript Object Notation", advanced, "JSON é um formato de texto leve para intercâmbio de dados, fácil para humanos lerem e para máquinas analisarem. É amplamente usado em APIs web.", "Java Standard Object Naming;JavaScript Object Notation;Java Source Open Network"),
                createChallenge("API", "No contexto de desenvolvimento, o que é uma API?", "Interface de Programação de Aplicações", advanced, "Uma API (Application Programming Interface) é um conjunto de regras e ferramentas que permite que diferentes aplicações de software se comuniquem umas com as outras.", "Análise Preditiva de Interface;Interface de Programação de Aplicações;Arquivo de Pacote de Instalação"),
                createChallenge("Polimorfismo", "A capacidade de um objeto de assumir muitas formas, geralmente através de herança e interfaces, é chamada de:", "Polimorfismo", advanced, "Polimorfismo permite que tratemos objetos de classes diferentes de maneira uniforme, desde que compartilhem uma superclasse ou interface comum.", "Encapsulamento;Abstração;Polimorfismo"),
                createChallenge("Estrutura de Dados - Fila", "Qual princípio uma Fila (Queue) segue?", "FIFO (First-In, First-Out)", advanced, "Uma Fila funciona como uma fila de banco: a primeira pessoa a chegar é a primeira a ser atendida.", "LIFO (Last-In, First-Out);FIFO (First-In, First-Out);LILO (Last-In, Last-Out)"),
                createChallenge("Algoritmo de Ordenação", "Qual a complexidade de tempo do pior caso do algoritmo QuickSort?", "O(n²)", advanced, "Embora a complexidade média do QuickSort seja O(n log n), seu pior caso (com um pivô mal escolhido em dados já ordenados) é O(n²).", "O(n log n);O(n²);O(log n)"),
                createChallenge("Classe Abstrata vs Interface", "Qual a principal diferença entre uma classe abstrata e uma interface em linguagens como Java/C#?", "Classes abstratas podem ter implementações de métodos", advanced, "Interfaces só podem declarar assinaturas de métodos (e métodos default a partir do Java 8), enquanto classes abstratas podem conter tanto métodos abstratos quanto métodos já implementados.", "Interfaces podem ser instanciadas;Classes abstratas podem ter implementações de métodos;Não há diferença"),
                createChallenge("Ponteiros/Referências", "Em linguagens como C++, o que um ponteiro armazena?", "Um endereço de memória", advanced, "Um ponteiro não armazena o valor em si, mas sim a localização na memória onde o valor pode ser encontrado.", "Um valor inteiro;Um endereço de memória;Uma cópia do objeto"),
                createChallenge("Programação Assíncrona", "O que `async/await` visa simplificar?", "A escrita de código assíncrono", advanced, "Essa sintaxe permite escrever código que lida com operações demoradas (como chamadas de rede) de uma forma que parece síncrona, melhorando a legibilidade.", "A escrita de código mais rápido;A escrita de código assíncrono;A redução do uso de memória"),
                createChallenge("SQL Join", "Qual tipo de JOIN em SQL retorna todas as linhas da tabela da esquerda e as linhas correspondentes da tabela da direita?", "LEFT JOIN", advanced, "LEFT JOIN inclui todos os registros da primeira tabela (esquerda), mesmo que não haja correspondência na segunda tabela.", "INNER JOIN;OUTER JOIN;LEFT JOIN"),
                createChallenge("Garbage Collector", "Qual é a função de um Coletor de Lixo (Garbage Collector) em linguagens gerenciadas?", "Liberar memória não utilizada automaticamente", advanced, "Ele monitora os objetos na memória e libera aqueles que não são mais referenciados por nenhuma parte do programa, prevenindo vazamentos de memória.", "Otimizar o código em tempo de compilação;Liberar memória não utilizada automaticamente;Reportar erros de sintaxe"),
                createChallenge("imutabilidade", "O que é um objeto imutável?", "Um objeto cujo estado não pode ser modificado após sua criação", advanced, "Strings em linguagens como Java e C# são exemplos clássicos. Qualquer 'modificação' na verdade cria um novo objeto String.", "Um objeto que não pode ser referenciado;Um objeto cujo estado não pode ser modificado após sua criação;Um objeto sem métodos"),
                createChallenge("Hashing", "Qual é o principal propósito de uma função de hash?", "Mapear dados de tamanho variável para um tamanho fixo", advanced, "Hashing é usado em tabelas hash (HashMaps) para busca rápida e em criptografia para verificar a integridade dos dados.", "Compactar dados;Ordenar dados;Mapear dados de tamanho variável para um tamanho fixo"),
                createChallenge("Middleware", "Em desenvolvimento web, o que é um 'middleware'?", "Software que atua como ponte entre o servidor e a aplicação", advanced, "Middleware intercepta e processa requisições, podendo ser usado para autenticação, logging, compressão, etc., antes que a requisição chegue à lógica principal da aplicação.", "O banco de dados da aplicação;A interface do usuário (frontend);Software que atua como ponte entre o servidor e a aplicação"),
                createChallenge("Design Pattern - Singleton", "Qual é o objetivo do padrão de projeto (Design Pattern) Singleton?", "Garantir que uma classe tenha apenas uma instância", advanced, "O padrão Singleton provê um ponto de acesso global a essa única instância, útil para gerenciar recursos compartilhados como conexões com banco de dados.", "Criar objetos complexos passo a passo;Garantir que uma classe tenha apenas uma instância;Permitir que um objeto altere seu comportamento quando seu estado interno muda"),
                createChallenge("ORM", "O que significa a sigla ORM?", "Object-Relational Mapping", advanced, "ORM é uma técnica que permite consultar e manipular dados de um banco de dados usando um paradigma orientado a objetos, como o Room no Android.", "Object-Resource Management;Object-Relational Mapping;Online-Request Module"),
                createChallenge("Threads", "O que é 'race condition' (condição de corrida) em programação concorrente?", "Um erro que ocorre quando o resultado depende da sequência de execução de threads", advanced, "Acontece quando múltiplas threads acessam e manipulam o mesmo dado compartilhado, e o resultado final depende da ordem em que elas são executadas, levando a resultados imprevisíveis.", "Quando uma thread é mais rápida que a outra;Um erro que ocorre quando o resultado depende da sequência de execução de threads;Quando duas threads travam esperando uma pela outra (deadlock)"),
                createChallenge("Injeção de Dependência", "Qual é o principal benefício da Injeção de Dependência (DI)?", "Reduz o acoplamento entre os componentes", advanced, "Com DI, as dependências de um objeto são fornecidas por uma fonte externa em vez de serem criadas pelo próprio objeto. Isso torna o código mais modular, testável e fácil de manter.", "Aumenta a performance do aplicativo;Reduz o acoplamento entre os componentes;Simplifica a sintaxe do código")
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