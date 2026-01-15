import java.util.Scanner;

public class FiniteAutomaton {
    
    // Перечисление состояний автомата
    enum State {
        S0,     // Начальное состояние
        S1,     // Обработка символов 'a'
        S2,     // Ожидание 'b' для первой пары bc
        S3,     // Ожидание 'c' после 'b'
        S4,     // Четное количество пар bc (ожидание 'b' или 'd')
        S5,     // Ожидание 'c' после 'b' (для четных пар)
        S6,     // Ожидание 'd' для пар de
        S7,     // Ожидание 'e' после 'd'
        S8,     // Конечное состояние (принимающее)
        ERROR   // Состояние ошибки
    }
    
    private State currentState;
    private int bcPairCount;  // Счетчик пар "bc"
    
    public FiniteAutomaton() {
        reset();
    }
    
    // Сброс автомата в начальное состояние
    public void reset() {
        currentState = State.S0;
        bcPairCount = 0;
    }
    
    // Обработка одного символа
    public void processChar(char c) {
        switch (currentState) {
            case S0:
                if (c == 'a') {
                    currentState = State.S1;
                } else {
                    currentState = State.ERROR;
                }
                break;
                
            case S1:
                if (c == 'a') {
                    // Остаемся в S1, обрабатываем дополнительные 'a'
                    currentState = State.S1;
                } else if (c == 'b') {
                    currentState = State.S2;
                } else {
                    currentState = State.ERROR;
                }
                break;
                
            case S2:
                if (c == 'c') {
                    bcPairCount++;
                    if (bcPairCount % 2 == 1) {
                        currentState = State.S3;  // Нечетное количество пар
                    } else {
                        currentState = State.S4;  // Четное количество пар
                    }
                } else {
                    currentState = State.ERROR;
                }
                break;
                
            case S3:
                // Нечетное количество пар bc
                if (c == 'b') {
                    currentState = State.S2;
                } else if (c == 'd') {
                    currentState = State.S6;
                } else {
                    currentState = State.ERROR;
                }
                break;
                
            case S4:
                // Четное количество пар bc
                if (c == 'b') {
                    currentState = State.S2;
                } else {
                    currentState = State.ERROR;
                }
                break;
                
            case S6:
                if (c == 'e') {
                    currentState = State.S7;
                } else {
                    currentState = State.ERROR;
                }
                break;
                
            case S7:
                // После первой пары de
                if (c == 'd') {
                    currentState = State.S6;  // Обработка следующей пары de
                } else {
                    currentState = State.ERROR;
                }
                break;
                
            case ERROR:
                // Остаемся в состоянии ошибки
                break;
                
            default:
                currentState = State.ERROR;
                break;
        }
    }
    
    // Проверка строки
    public boolean recognize(String input) {
        reset();
        
        for (char c : input.toCharArray()) {
            processChar(c);
            if (currentState == State.ERROR) {
                return false;
            }
        }
        
        // Проверяем, что мы в конечном состоянии S7 
        // (последний символ должен быть 'e' из пары de)
        // и количество пар bc нечетное
        return currentState == State.S7 && bcPairCount % 2 == 1;
    }
    
    // Получение текущего состояния
    public State getCurrentState() {
        return currentState;
    }
    
    // Подробная проверка с выводом трассировки
    public void trace(String input) {
        reset();
        System.out.println("\nТрассировка для строки: \"" + input + "\"");
        System.out.println("Начальное состояние: " + currentState);
        
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            State prevState = currentState;
            processChar(c);
            System.out.println("Символ '" + c + "': " + prevState + " -> " + currentState);
        }
        
        System.out.println("Количество пар 'bc': " + bcPairCount);
        System.out.println("Результат: " + (recognize(input) ? "ПРИНЯТО" : "ОТВЕРГНУТО"));
    }
    
    public static void main(String[] args) {
        FiniteAutomaton automaton = new FiniteAutomaton();
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("=================================================");
        System.out.println("Конечный автомат для языка a^n(bc)^m(de)^k");
        System.out.println("где n>=1, m>=1 (m-нечетное), k>=1");
        System.out.println("=================================================\n");
        
        // Тестовые примеры
        String[] testStrings = {
            "abcde",           // Правильно: n=1, m=1 (нечетное), k=1
            "aabcde",          // Правильно: n=2, m=1 (нечетное), k=1
            "abcbcbcde",       // Правильно: n=1, m=3 (нечетное), k=1
            "aabcbcbcdede",    // Правильно: n=2, m=3 (нечетное), k=2
            "abcbcde",         // Неправильно: m=2 (четное)
            "bcde",            // Неправильно: нет 'a'
            "abc",             // Неправильно: нет 'de'
            "ade",             // Неправильно: нет 'bc'
            "abcdede",         // Неправильно: неполная пара 'bc'
            "aaabcbcbcdedede"  // Правильно: n=3, m=3 (нечетное), k=3
        };
        
        System.out.println("Тестирование примеров:");
        System.out.println("-----------------------");
        for (String test : testStrings) {
            boolean result = automaton.recognize(test);
            System.out.printf("%-20s : %s\n", test, result ? "ПРИНЯТО ✓" : "ОТВЕРГНУТО ✗");
        }
        
        // Интерактивный режим
        System.out.println("\n=================================================");
        System.out.println("Интерактивный режим");
        System.out.println("Введите 'exit' для выхода, 'trace' для трассировки");
        System.out.println("=================================================");
        
        while (true) {
            System.out.print("\nВведите строку для проверки: ");
            String input = scanner.nextLine();
            
            if (input.equalsIgnoreCase("exit")) {
                break;
            }
            
            if (input.startsWith("trace ")) {
                String traceInput = input.substring(6);
                automaton.trace(traceInput);
            } else if (!input.isEmpty()) {
                boolean result = automaton.recognize(input);
                System.out.println("Результат: " + (result ? "ПРИНЯТО ✓" : "ОТВЕРГНУТО ✗"));
            }
        }
        
        scanner.close();
        System.out.println("\nПрограмма завершена.");
    }
}