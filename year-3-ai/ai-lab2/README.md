# Inteligență artificială: Лабораторная работа №2 - Мини-макс с альфа-бета отсечением
### **Вариант 4(19)**

**Выполнил студент:\
Группы I2302\
Daniil Socolov**

**Проверил преподаватель:\
V.Trebis**

## Задание
**Цель**\
Научиться реализовывать оптимизированный алгоритм мини-маĸс с альфа-бета
отсечением для глубоĸих игровых деревьев, а таĸже анализировать эффеĸтивность
различных вариантов стратегии.

**Общие требования**
1. Глубина дерева: ≥5 уровней.
2. Игроĸи: MAX и MIN.
3. Листовые значения: целые числа, могут быть
сгенерированы случайно или заданы вручную.
4. Альфа-бета отсечение: должно быть реализовано
реĸурсивно.
5. Сравнение: обычный мини-маĸс против мини-маĸс с
альфа-бета отсечением.
6. Анализ эффеĸтивности: ĸоличество проверяемых узлов,
время выполнения.

## Ключевые характеристики:
1. Параметры согласно варианту 4:

Глубина дерева: 6 уровней
Ширина дерева: 3 (каждый узел имеет 3 потомка)
Листовые значения: от -100 до +100 (включая отрицательные)

2. Реализованные алгоритмы:

Обычный минимакс - полный перебор всех узлов
Минимакс с альфа-бета отсечением - оптимизированная версия с отсечением ветвей

3. Функциональность:

Автоматическая генерация игрового дерева
Визуализация структуры дерева
Подсчет посещенных и отсеченных узлов
Измерение времени выполнения
Сравнительный анализ эффективности

4. Эксперименты:
Программа проводит три эксперимента с разным порядком узлов:

Случайный порядок
Оптимальный порядок (для максимальной эффективности отсечения)
Худший порядок (минимальное отсечение)

5. Статистика и анализ:

Количество посещенных узлов
Количество отсеченных узлов
Время выполнения в наносекундах и миллисекундах
Процент сокращения узлов
Коэффициент ускорения

## Описание кода

**Программа автоматически:**
Сгенерирует дерево с заданными параметрами
Выполнит оба алгоритма
Покажет сравнительный анализ
Проведет дополнительные эксперименты

Результаты покажут значительное преимущество альфа-бета отсечения - обычно сокращение проверяемых узлов составляет 50-80% в зависимости от порядка значений в дереве.


### 1. Представление узла дерева

``` java
static class TreeNode {
    int value;
    List<TreeNode> children;
    int nodeId;
    static int idCounter = 0;
    
    public TreeNode() {
        this.children = new ArrayList<>();
        this.nodeId = idCounter++;
        this.value = Integer.MIN_VALUE;
    }
    
    public boolean isLeaf() {
        return children.isEmpty();
    }
}
```

### 2. Генерация дерева

``` java
private static TreeNode generateTreeHelper(int depth, int width, int currentDepth) {
    TreeNode node = new TreeNode();
    
    if (depth == 0) {
        node.value = random.nextInt(201) - 100; // от -100 до 100
    } else {
        for (int i = 0; i < width; i++) {
            node.children.add(generateTreeHelper(depth - 1, width, currentDepth + 1));
        }
    }
    return node;
}
```

### 3. Реализация Минимакса

``` java
public static int minimax(TreeNode node, int depth, boolean isMaxPlayer) {
    if (depth == 0 || node.isLeaf()) {
        return node.value;
    }
    
    if (isMaxPlayer) {
        int maxEval = Integer.MIN_VALUE;
        for (TreeNode child : node.children) {
            int eval = minimax(child, depth - 1, false);
            maxEval = Math.max(maxEval, eval);
        }
        return maxEval;
    } else {
        int minEval = Integer.MAX_VALUE;
        for (TreeNode child : node.children) {
            int eval = minimax(child, depth - 1, true);
            minEval = Math.min(minEval, eval);
        }
        return minEval;
    }
}
```

### 4. Минимакс с альфа-бета отсечением

``` java
public static int minimaxAlphaBeta(TreeNode node, int depth, int alpha, int beta, boolean isMaxPlayer) {
    if (depth == 0 || node.isLeaf()) {
        return node.value;
    }
    
    if (isMaxPlayer) {
        int maxEval = Integer.MIN_VALUE;
        for (TreeNode child : node.children) {
            int eval = minimaxAlphaBeta(child, depth - 1, alpha, beta, false);
            maxEval = Math.max(maxEval, eval);
            alpha = Math.max(alpha, eval);
            
            if (beta <= alpha) break; // отсечение
        }
        return maxEval;
    } else {
        int minEval = Integer.MAX_VALUE;
        for (TreeNode child : node.children) {
            int eval = minimaxAlphaBeta(child, depth - 1, alpha, beta, true);
            minEval = Math.min(minEval, eval);
            beta = Math.min(beta, eval);
            
            if (beta <= alpha) break; // отсечение
        }
        return minEval;
    }
}
```

### 5. Сбор статистики

``` java
static class Statistics {
    int nodesVisited = 0;
    int nodesPruned = 0;
    long executionTime = 0;

    void printStats(String algorithmName) {
        System.out.println("\n=== " + algorithmName + " ===");
        System.out.println("Узлов посещено: " + nodesVisited);
        System.out.println("Узлов отсечено: " + nodesPruned);
        System.out.println("Время выполнения: " + executionTime + " нс");
    }
}
```

### 6. Сравнительный анализ

``` java
System.out.println("Сравнительный анализ:");
System.out.println("Результаты совпадают: " + (resultMinimax == resultAlphaBeta));
System.out.println("Сокращение узлов: " + 
    String.format("%.1f%%", (1 - (double)stats.nodesVisited / normalNodesVisited) * 100));
System.out.println("Ускорение: " + 
    String.format("%.2fx", (double)normalTime / stats.executionTime));
```


## Выводы

![img](/img_1.png)

![img](/img_2.png)

![img](/img_3.png)

Реализация показала, что алгоритм с альфа-бета отсечением позволяет
существенно уменьшить количество просматриваемых узлов и ускоряет
выполнение программы, сохраняя при этом корректность результата.
