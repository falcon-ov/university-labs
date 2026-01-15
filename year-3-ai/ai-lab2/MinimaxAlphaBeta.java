import java.util.*;

/**
 * Лабораторная работа №2: Минимакс с альфа-бета отсечением
 * Вариант 4(19): Глубина 6, Ширина 3, с отрицательными значениями
 */
public class MinimaxAlphaBeta {
    
    // Класс для представления узла дерева
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
        
        public TreeNode(int value) {
            this();
            this.value = value;
        }
        
        public boolean isLeaf() {
            return children.isEmpty();
        }
    }
    
    // Класс для хранения статистики
    static class Statistics {
        int nodesVisited = 0;
        int nodesPruned = 0;
        long executionTime = 0;
        List<Integer> visitedNodes = new ArrayList<>();
        List<Integer> prunedNodes = new ArrayList<>();
        
        void reset() {
            nodesVisited = 0;
            nodesPruned = 0;
            executionTime = 0;
            visitedNodes.clear();
            prunedNodes.clear();
        }
        
        void printStats(String algorithmName) {
            System.out.println("\n=== " + algorithmName + " ===");
            System.out.println("Узлов посещено: " + nodesVisited);
            System.out.println("Узлов отсечено: " + nodesPruned);
            System.out.println("Время выполнения: " + executionTime + " нс (" + 
                             String.format("%.3f", executionTime / 1_000_000.0) + " мс)");
            if (!prunedNodes.isEmpty()) {
                System.out.println("ID отсеченных узлов: " + prunedNodes);
            }
        }
    }
    
    private static Statistics stats = new Statistics();
    private static final int TREE_DEPTH = 6;  // Глубина дерева
    private static final int TREE_WIDTH = 3;  // Ширина дерева (количество детей)
    private static final Random random = new Random(42); // Фиксированный seed для воспроизводимости
    
    /**
     * Генерация игрового дерева с заданной глубиной и шириной
     */
    public static TreeNode generateTree(int depth, int width) {
        TreeNode.idCounter = 0; // Сброс счетчика ID
        return generateTreeHelper(depth, width, 0);
    }
    
    private static TreeNode generateTreeHelper(int depth, int width, int currentDepth) {
        TreeNode node = new TreeNode();
        
        if (depth == 0) {
            // Листовой узел - генерируем значение (включая отрицательные)
            node.value = random.nextInt(201) - 100; // От -100 до 100
        } else {
            // Внутренний узел - создаем детей
            for (int i = 0; i < width; i++) {
                node.children.add(generateTreeHelper(depth - 1, width, currentDepth + 1));
            }
        }
        
        return node;
    }
    
    /**
     * Обычный алгоритм минимакс без оптимизаций
     */
    public static int minimax(TreeNode node, int depth, boolean isMaxPlayer) {
        stats.nodesVisited++;
        stats.visitedNodes.add(node.nodeId);
        
        // Базовый случай: достигли листа или максимальной глубины
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
    
    /**
     * Алгоритм минимакс с альфа-бета отсечением
     */
    public static int minimaxAlphaBeta(TreeNode node, int depth, int alpha, int beta, boolean isMaxPlayer) {
        stats.nodesVisited++;
        stats.visitedNodes.add(node.nodeId);
        
        // Базовый случай
        if (depth == 0 || node.isLeaf()) {
            return node.value;
        }
        
        if (isMaxPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (TreeNode child : node.children) {
                int eval = minimaxAlphaBeta(child, depth - 1, alpha, beta, false);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                
                // Альфа-бета отсечение
                if (beta <= alpha) {
                    // Отсекаем оставшиеся узлы
                    for (int i = node.children.indexOf(child) + 1; i < node.children.size(); i++) {
                        countPrunedNodes(node.children.get(i));
                    }
                    break;
                }
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (TreeNode child : node.children) {
                int eval = minimaxAlphaBeta(child, depth - 1, alpha, beta, true);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                
                // Альфа-бета отсечение
                if (beta <= alpha) {
                    // Отсекаем оставшиеся узлы
                    for (int i = node.children.indexOf(child) + 1; i < node.children.size(); i++) {
                        countPrunedNodes(node.children.get(i));
                    }
                    break;
                }
            }
            return minEval;
        }
    }
    
    /**
     * Подсчет отсеченных узлов
     */
    private static void countPrunedNodes(TreeNode node) {
        stats.nodesPruned++;
        stats.prunedNodes.add(node.nodeId);
        for (TreeNode child : node.children) {
            countPrunedNodes(child);
        }
    }
    
    /**
     * Визуализация дерева (упрощенная)
     */
    public static void printTree(TreeNode node, String prefix, boolean isLast, int depth) {
        if (depth > 3) return; // Ограничиваем вывод для больших деревьев
        
        System.out.println(prefix + (isLast ? "└── " : "├── ") + 
                          "Node[" + node.nodeId + "]" + 
                          (node.isLeaf() ? " = " + node.value : ""));
        
        for (int i = 0; i < node.children.size(); i++) {
            printTree(node.children.get(i), 
                     prefix + (isLast ? "    " : "│   "), 
                     i == node.children.size() - 1, 
                     depth + 1);
        }
    }
    
    /**
     * Анализ эффективности алгоритмов
     */
    public static void analyzeEfficiency(TreeNode root) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("АНАЛИЗ ЭФФЕКТИВНОСТИ АЛГОРИТМОВ");
        System.out.println("=".repeat(60));
        System.out.println("Параметры дерева:");
        System.out.println("- Глубина: " + TREE_DEPTH);
        System.out.println("- Ширина: " + TREE_WIDTH);
        System.out.println("- Общее количество узлов: " + calculateTotalNodes(TREE_DEPTH, TREE_WIDTH));
        
        // Тест обычного минимакса
        stats.reset();
        long startTime = System.nanoTime();
        int resultMinimax = minimax(root, TREE_DEPTH, true);
        stats.executionTime = System.nanoTime() - startTime;
        stats.printStats("Обычный Минимакс");
        System.out.println("Результат: " + resultMinimax);
        
        int normalNodesVisited = stats.nodesVisited;
        long normalTime = stats.executionTime;
        
        // Тест минимакса с альфа-бета отсечением
        stats.reset();
        startTime = System.nanoTime();
        int resultAlphaBeta = minimaxAlphaBeta(root, TREE_DEPTH, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
        stats.executionTime = System.nanoTime() - startTime;
        stats.printStats("Минимакс с Альфа-Бета отсечением");
        System.out.println("Результат: " + resultAlphaBeta);
        
        // Сравнительный анализ
        System.out.println("\n" + "=".repeat(60));
        System.out.println("СРАВНИТЕЛЬНЫЙ АНАЛИЗ");
        System.out.println("=".repeat(60));
        System.out.println("Результаты совпадают: " + (resultMinimax == resultAlphaBeta));
        System.out.println("Сокращение узлов: " + 
                          String.format("%.1f%%", (1 - (double)stats.nodesVisited / normalNodesVisited) * 100));
        System.out.println("Ускорение: " + 
                          String.format("%.2fx", (double)normalTime / stats.executionTime));
        System.out.println("Эффективность отсечения: " + 
                          String.format("%.1f%%", (double)stats.nodesPruned / calculateTotalNodes(TREE_DEPTH, TREE_WIDTH) * 100) + 
                          " от общего числа узлов");
    }
    
    /**
     * Расчет общего количества узлов в дереве
     */
    private static int calculateTotalNodes(int depth, int width) {
        int total = 0;
        for (int i = 0; i <= depth; i++) {
            total += Math.pow(width, i);
        }
        return total;
    }
    
    /**
     * Тестирование с различными конфигурациями
     */
    public static void runExperiments() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("ЭКСПЕРИМЕНТЫ С РАЗЛИЧНЫМ ПОРЯДКОМ УЗЛОВ");
        System.out.println("=".repeat(60));
        
        // Эксперимент 1: Случайный порядок
        System.out.println("\n### Эксперимент 1: Случайный порядок листьев ###");
        TreeNode randomTree = generateTree(TREE_DEPTH, TREE_WIDTH);
        testAlphaBetaEfficiency(randomTree);
        
        // Эксперимент 2: Отсортированные значения (лучший случай для альфа-бета)
        System.out.println("\n### Эксперимент 2: Оптимальный порядок (отсортированные) ###");
        TreeNode sortedTree = generateTree(TREE_DEPTH, TREE_WIDTH);
        sortLeaves(sortedTree, true);
        testAlphaBetaEfficiency(sortedTree);
        
        // Эксперимент 3: Обратный порядок (худший случай)
        System.out.println("\n### Эксперимент 3: Худший порядок (обратная сортировка) ###");
        TreeNode reversedTree = generateTree(TREE_DEPTH, TREE_WIDTH);
        sortLeaves(reversedTree, false);
        testAlphaBetaEfficiency(reversedTree);
    }
    
    private static void testAlphaBetaEfficiency(TreeNode root) {
        stats.reset();
        int result = minimaxAlphaBeta(root, TREE_DEPTH, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
        System.out.println("Результат: " + result);
        System.out.println("Узлов посещено: " + stats.nodesVisited);
        System.out.println("Узлов отсечено: " + stats.nodesPruned);
        System.out.println("Эффективность: " + 
                          String.format("%.1f%%", (double)stats.nodesPruned / calculateTotalNodes(TREE_DEPTH, TREE_WIDTH) * 100) + 
                          " узлов отсечено");
    }
    
    /**
     * Сортировка листьев для тестирования эффективности
     */
    private static void sortLeaves(TreeNode node, boolean ascending) {
        if (node.isLeaf()) return;
        
        List<Integer> leafValues = new ArrayList<>();
        collectLeafValues(node, leafValues);
        
        if (ascending) {
            Collections.sort(leafValues);
        } else {
            Collections.sort(leafValues, Collections.reverseOrder());
        }
        
        assignLeafValues(node, leafValues, new int[]{0});
    }
    
    private static void collectLeafValues(TreeNode node, List<Integer> values) {
        if (node.isLeaf()) {
            values.add(node.value);
        } else {
            for (TreeNode child : node.children) {
                collectLeafValues(child, values);
            }
        }
    }
    
    private static void assignLeafValues(TreeNode node, List<Integer> values, int[] index) {
        if (node.isLeaf()) {
            node.value = values.get(index[0]++);
        } else {
            for (TreeNode child : node.children) {
                assignLeafValues(child, values, index);
            }
        }
    }
    
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════╗");
        System.out.println("║     ЛАБОРАТОРНАЯ РАБОТА №2: МИНИМАКС С АЛЬФА-БЕТА      ║");
        System.out.println("║              Вариант 4(19): Глубина 6, Ширина 3        ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");
        
        // Генерация дерева
        System.out.println("\nГенерация игрового дерева...");
        TreeNode root = generateTree(TREE_DEPTH, TREE_WIDTH);
        
        // Визуализация части дерева
        System.out.println("\nСтруктура дерева (первые 3 уровня):");
        System.out.println("-".repeat(40));
        printTree(root, "", true, 0);
        
        // Основной анализ
        analyzeEfficiency(root);
        
        // Дополнительные эксперименты
        runExperiments();
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("Лабораторная работа завершена успешно!");
        System.out.println("=".repeat(60));
    }
}