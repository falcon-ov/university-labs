import time
from collections import deque
from typing import List, Tuple, Set, Optional
import heapq


# ============================================================================
# ОПИСАНИЕ СРЕДЫ: Робот-уборщик в доме
# ============================================================================
# Робот должен собрать мусор (объекты) из разных комнат и отнести в контейнер
# Позиции: grid 4x4, объекты: A, B, C
# Начальное состояние: робот в (0,0), объекты разбросаны, рука пуста
# Целевое состояние: все объекты в контейнере (3,3), робот в любом месте

class State:
    """Состояние робота: позиция, что держит, где лежат объекты"""

    def __init__(self, robot_pos: Tuple[int, int], holding: Optional[str],
                 objects: dict):
        self.robot_pos = robot_pos  # (x, y)
        self.holding = holding  # None или название объекта
        self.objects = objects.copy()  # {объект: позиция или 'bin'}

    def __eq__(self, other):
        if not isinstance(other, State):
            return False
        return (self.robot_pos == other.robot_pos and
                self.holding == other.holding and
                self.objects == other.objects)

    def __hash__(self):
        return hash((self.robot_pos, self.holding,
                     tuple(sorted(self.objects.items()))))

    def __repr__(self):
        return f"State(pos={self.robot_pos}, hold={self.holding}, obj={self.objects})"

    def is_goal(self, goal_state):
        """Проверка достижения цели"""
        # Все объекты должны быть в контейнере
        return all(pos == 'bin' for pos in self.objects.values())

    def copy(self):
        return State(self.robot_pos, self.holding, self.objects)


class Environment:
    """Среда: сетка 4x4, действия робота"""

    def __init__(self):
        self.grid_size = 4
        self.bin_pos = (3, 3)  # Позиция контейнера
        self.generated_states = 0  # Счётчик генерируемых состояний

    def get_actions(self, state: State) -> List[str]:
        """Возможные действия из текущего состояния"""
        actions = []
        x, y = state.robot_pos

        # Перемещения (вверх, вниз, влево, вправо)
        if x > 0:
            actions.append('left')
        if x < self.grid_size - 1:
            actions.append('right')
        if y > 0:
            actions.append('up')
        if y < self.grid_size - 1:
            actions.append('down')

        # Захват объекта (если на той же позиции и рука пуста)
        if state.holding is None:
            for obj, pos in state.objects.items():
                if pos == state.robot_pos:
                    actions.append(f'pick_{obj}')

        # Положить объект в контейнер (если держим и на позиции контейнера)
        if state.holding is not None and state.robot_pos == self.bin_pos:
            actions.append(f'drop_{state.holding}')

        return actions

    def apply_action(self, state: State, action: str) -> State:
        """Применить действие и получить новое состояние"""
        self.generated_states += 1
        new_state = state.copy()
        x, y = new_state.robot_pos

        # Перемещения
        if action == 'left':
            new_state.robot_pos = (x - 1, y)
        elif action == 'right':
            new_state.robot_pos = (x + 1, y)
        elif action == 'up':
            new_state.robot_pos = (x, y - 1)
        elif action == 'down':
            new_state.robot_pos = (x, y + 1)

        # Захват объекта
        elif action.startswith('pick_'):
            obj = action.split('_')[1]
            new_state.holding = obj
            del new_state.objects[obj]

        # Положить в контейнер
        elif action.startswith('drop_'):
            obj = action.split('_')[1]
            new_state.objects[obj] = 'bin'
            new_state.holding = None

        return new_state

    def manhattan_distance(self, pos1, pos2) -> int:
        """Манхэттенское расстояние между двумя позициями"""
        if pos1 == 'bin' or pos2 == 'bin':
            return 0
        return abs(pos1[0] - pos2[0]) + abs(pos1[1] - pos2[1])


# ============================================================================
# ЭВРИСТИЧЕСКИЕ ФУНКЦИИ
# ============================================================================

def heuristic_uncollected_objects(state: State, env: Environment) -> int:
    """h1: Количество несобранных объектов"""
    return sum(1 for pos in state.objects.values() if pos != 'bin')


def heuristic_manhattan_sum(state: State, env: Environment) -> int:
    """h2: Сумма манхэттенских расстояний до всех объектов + до контейнера"""
    total = 0

    # Расстояния до несобранных объектов
    for obj, pos in state.objects.items():
        if pos != 'bin':
            total += env.manhattan_distance(state.robot_pos, pos)
            total += env.manhattan_distance(pos, env.bin_pos)

    # Если что-то держим, добавляем расстояние до контейнера
    if state.holding is not None:
        total += env.manhattan_distance(state.robot_pos, env.bin_pos)

    return total


# ============================================================================
# АЛГОРИТМЫ ПОИСКА
# ============================================================================

def forward_search(start_state: State, goal_state: State, env: Environment,
                   heuristic=None) -> Tuple[List[str], int, float]:
    """Прямой поиск (от начала к цели)"""
    start_time = time.time()
    env.generated_states = 0

    if heuristic:
        # A* с эвристикой
        frontier = [(heuristic(start_state, env), 0, start_state, [])]
        heapq.heapify(frontier)
        visited = {start_state: 0}

        while frontier:
            _, cost, state, path = heapq.heappop(frontier)

            if state.is_goal(goal_state):
                elapsed = time.time() - start_time
                return path, len(visited), elapsed

            for action in env.get_actions(state):
                new_state = env.apply_action(state, action)
                new_cost = cost + 1

                if new_state not in visited or new_cost < visited[new_state]:
                    visited[new_state] = new_cost
                    priority = new_cost + heuristic(new_state, env)
                    heapq.heappush(frontier,
                                   (priority, new_cost, new_state, path + [action]))
    else:
        # BFS без эвристики
        frontier = deque([(start_state, [])])
        visited = {start_state}

        while frontier:
            state, path = frontier.popleft()

            if state.is_goal(goal_state):
                elapsed = time.time() - start_time
                return path, len(visited), elapsed

            for action in env.get_actions(state):
                new_state = env.apply_action(state, action)
                if new_state not in visited:
                    visited.add(new_state)
                    frontier.append((new_state, path + [action]))

    elapsed = time.time() - start_time
    return None, env.generated_states, elapsed


def backward_search(start_state: State, goal_state: State, env: Environment,
                    heuristic=None) -> Tuple[List[str], int, float]:
    """Обратный поиск (от цели к началу)"""
    start_time = time.time()
    env.generated_states = 0

    # Генерируем возможные целевые состояния (все объекты в контейнере)
    goal_states = []
    for x in range(env.grid_size):
        for y in range(env.grid_size):
            goal = State((x, y), None, {obj: 'bin' for obj in start_state.objects})
            goal_states.append(goal)

    if heuristic:
        # A* с эвристикой
        frontier = []
        for g in goal_states:
            heapq.heappush(frontier, (heuristic(g, env), 0, g, []))
        visited = {g: 0 for g in goal_states}

        while frontier:
            _, cost, state, path = heapq.heappop(frontier)

            if state == start_state:
                elapsed = time.time() - start_time
                return list(reversed(path)), len(visited), elapsed

            # Генерируем предыдущие состояния (обратные действия)
            for action in env.get_actions(state):
                new_state = env.apply_action(state, action)
                new_cost = cost + 1

                if new_state not in visited or new_cost < visited[new_state]:
                    visited[new_state] = new_cost
                    priority = new_cost + heuristic(new_state, env)
                    heapq.heappush(frontier,
                                   (priority, new_cost, new_state, [action] + path))
    else:
        # BFS без эвристики
        frontier = deque([(g, []) for g in goal_states])
        visited = set(goal_states)

        while frontier:
            state, path = frontier.popleft()

            if state == start_state:
                elapsed = time.time() - start_time
                return list(reversed(path)), len(visited), elapsed

            for action in env.get_actions(state):
                new_state = env.apply_action(state, action)
                if new_state not in visited:
                    visited.add(new_state)
                    frontier.append((new_state, [action] + path))

    elapsed = time.time() - start_time
    return None, env.generated_states, elapsed


def bidirectional_search(start_state: State, goal_state: State,
                         env: Environment) -> Tuple[List[str], int, float]:
    """Двунаправленный поиск (от начала и от цели одновременно)"""
    start_time = time.time()
    env.generated_states = 0

    # Прямое направление
    forward_frontier = deque([(start_state, [])])
    forward_visited = {start_state: []}

    # Обратное направление (все возможные целевые состояния)
    goal_states = []
    for x in range(env.grid_size):
        for y in range(env.grid_size):
            goal = State((x, y), None, {obj: 'bin' for obj in start_state.objects})
            goal_states.append(goal)

    backward_frontier = deque([(g, []) for g in goal_states])
    backward_visited = {g: [] for g in goal_states}

    while forward_frontier and backward_frontier:
        # Шаг вперёд
        if forward_frontier:
            state, path = forward_frontier.popleft()

            # Проверка встречи
            if state in backward_visited:
                elapsed = time.time() - start_time
                total_path = path + list(reversed(backward_visited[state]))
                total_visited = len(forward_visited) + len(backward_visited)
                return total_path, total_visited, elapsed

            for action in env.get_actions(state):
                new_state = env.apply_action(state, action)
                if new_state not in forward_visited:
                    forward_visited[new_state] = path + [action]
                    forward_frontier.append((new_state, path + [action]))

        # Шаг назад
        if backward_frontier:
            state, path = backward_frontier.popleft()

            # Проверка встречи
            if state in forward_visited:
                elapsed = time.time() - start_time
                total_path = forward_visited[state] + list(reversed(path))
                total_visited = len(forward_visited) + len(backward_visited)
                return total_path, total_visited, elapsed

            for action in env.get_actions(state):
                new_state = env.apply_action(state, action)
                if new_state not in backward_visited:
                    backward_visited[new_state] = [action] + path
                    backward_frontier.append((new_state, [action] + path))

    elapsed = time.time() - start_time
    return None, env.generated_states, elapsed


# ============================================================================
# АНАЛИЗ И ЭКСПЕРИМЕНТЫ
# ============================================================================

def calculate_branching_factor(env: Environment, state: State) -> float:
    """Вычисление среднего коэффициента разветвления"""
    total_actions = 0
    states_checked = 0
    visited = {state}
    frontier = deque([state])

    # BFS для подсчёта среднего количества действий
    while frontier and states_checked < 100:  # Ограничение для скорости
        current = frontier.popleft()
        actions = env.get_actions(current)
        total_actions += len(actions)
        states_checked += 1

        for action in actions[:2]:  # Ограничиваем генерацию
            new_state = env.apply_action(current, action)
            if new_state not in visited:
                visited.add(new_state)
                frontier.append(new_state)

    return total_actions / states_checked if states_checked > 0 else 0


def run_experiments():
    """Запуск всех экспериментов и вывод результатов"""
    # Начальное состояние
    start = State(
        robot_pos=(0, 0),
        holding=None,
        objects={'A': (1, 1), 'B': (2, 0), 'C': (0, 3)}
    )

    # Целевое состояние (любое, где все объекты в контейнере)
    goal = State(
        robot_pos=(3, 3),
        holding=None,
        objects={'A': 'bin', 'B': 'bin', 'C': 'bin'}
    )

    env = Environment()

    print("=" * 70)
    print("ЛАБОРАТОРНАЯ РАБОТА №8: Планирование действий агента")
    print("=" * 70)
    print("\n🤖 ОПИСАНИЕ СРЕДЫ:")
    print(f"  Сетка: {env.grid_size}x{env.grid_size}")
    print(f"  Контейнер: {env.bin_pos}")
    print(f"  Начальное состояние: {start}")
    print(f"  Целевое состояние: все объекты в контейнере\n")

    # Коэффициент разветвления
    bf = calculate_branching_factor(env, start)
    print(f"📊 Коэффициент разветвления: {bf:.2f}\n")

    results = []

    # 1. Прямой поиск без эвристики
    print("🔍 Запуск прямого поиска без эвристики...")
    path, visited, time_taken = forward_search(start, goal, env)
    results.append(["Прямой", "BFS", "—", visited, len(path) if path else 0,
                    f"{time_taken:.4f}"])
    print(f"  ✓ Найден путь длиной {len(path)}, посещено {visited} состояний")

    # 2. Прямой поиск с эвристикой h1
    print("🔍 Запуск прямого поиска с эвристикой h1...")
    path, visited, time_taken = forward_search(start, goal, env,
                                               heuristic_uncollected_objects)
    results.append(["Прямой", "A*", "h₁", visited, len(path) if path else 0,
                    f"{time_taken:.4f}"])
    print(f"  ✓ Найден путь длиной {len(path)}, посещено {visited} состояний")

    # 3. Прямой поиск с эвристикой h2
    print("🔍 Запуск прямого поиска с эвристикой h2...")
    path, visited, time_taken = forward_search(start, goal, env,
                                               heuristic_manhattan_sum)
    results.append(["Прямой", "A*", "h₂", visited, len(path) if path else 0,
                    f"{time_taken:.4f}"])
    print(f"  ✓ Найден путь длиной {len(path)}, посещено {visited} состояний")

    # 4. Обратный поиск с эвристикой h1
    print("🔍 Запуск обратного поиска с эвристикой h1...")
    path, visited, time_taken = backward_search(start, goal, env,
                                                heuristic_uncollected_objects)
    results.append(["Обратный", "A*", "h₁", visited, len(path) if path else 0,
                    f"{time_taken:.4f}"])
    print(f"  ✓ Найден путь длиной {len(path)}, посещено {visited} состояний")

    # 5. Двунаправленный поиск
    print("🔍 Запуск двунаправленного поиска...")
    path, visited, time_taken = bidirectional_search(start, goal, env)
    results.append(["Двунаправленный", "BFS", "—", visited,
                    len(path) if path else 0, f"{time_taken:.4f}"])
    print(f"  ✓ Найден путь длиной {len(path)}, посещено {visited} состояний")

    # Вывод таблицы результатов
    print("\n" + "=" * 70)
    print("📋 ТАБЛИЦА РЕЗУЛЬТАТОВ:")
    print("=" * 70)
    print(f"{'Алгоритм':<20} {'Тип':<6} {'Эвристика':<10} {'Вершин':<10} "
          f"{'Глубина':<10} {'Время (с)':<12}")
    print("-" * 70)
    for r in results:
        print(f"{r[0]:<20} {r[1]:<6} {r[2]:<10} {r[3]:<10} {r[4]:<10} {r[5]:<12}")
    print("=" * 70)

    # Пример найденного пути
    if path:
        print(f"\n🎯 ПРИМЕР НАЙДЕННОГО ПУТИ (последний алгоритм):")
        print(f"  Длина пути: {len(path)} действий")
        print(f"  Действия: {' → '.join(path[:10])}{'...' if len(path) > 10 else ''}")

    print("\n✅ Эксперименты завершены!")


if __name__ == "__main__":
    run_experiments()