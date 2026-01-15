import argparse
import random
import time
import sys

# попытка импортировать pygame для визуализации; если не установлен, работаем в консоли
try:
    import pygame
    PYGAME_AVAILABLE = True
except Exception:
    PYGAME_AVAILABLE = False

# размер сетки для варианта
ROWS = 30
COLS = 30

# паттерн beacon (4x4)
BEACON_PATTERN = [
    [1, 1, 0, 0],
    [1, 1, 0, 0],
    [0, 0, 1, 1],
    [0, 0, 1, 1],
]

def seed_from_name(name: str) -> int:
    """получить числовой seed из строки (ФИО) для воспроизводимости"""
    h = 0
    for ch in name:
        h = (h * 31 + ord(ch)) & 0xFFFFFFFF
    return h

def make_random_grid(rows: int, cols: int, live_prob: float) -> list:
    """создать случайную сетку rows x cols с вероятностью live_prob"""
    grid = [[1 if random.random() < live_prob else 0 for _ in range(cols)] for _ in range(rows)]
    return grid

def count_neighbors(grid: list, r: int, c: int) -> int:
    """посчитать живых соседей клетки (r,c) — 8 направлений, границы — нециклические"""
    rows = len(grid)
    cols = len(grid[0])
    cnt = 0
    for dr in (-1, 0, 1):
        for dc in (-1, 0, 1):
            if dr == 0 and dc == 0:
                continue
            rr = r + dr
            cc = c + dc
            if 0 <= rr < rows and 0 <= cc < cols:
                cnt += grid[rr][cc]
    return cnt

def step_grid(grid: list) -> list:
    """применить правила Конвея к всей сетке и вернуть новую сетку"""
    rows = len(grid)
    cols = len(grid[0])
    new = [[0]*cols for _ in range(rows)]
    for r in range(rows):
        for c in range(cols):
            n = count_neighbors(grid, r, c)
            if grid[r][c] == 1:
                # живая клетка
                if n < 2:
                    new[r][c] = 0
                elif n in (2, 3):
                    new[r][c] = 1
                else:
                    new[r][c] = 0
            else:
                # мёртвая
                if n == 3:
                    new[r][c] = 1
                else:
                    new[r][c] = 0
    return new

def place_pattern_min_overlap(grid: list, pattern: list, max_trials: int = 200) -> tuple:
    """
    попытаться поместить pattern в место с минимальным перекрытием живых клеток.
    вернёт (row, col) — координату левого верхнего угла вставки; если не нашлось — None
    """
    rows = len(grid)
    cols = len(grid[0])
    pr = len(pattern)
    pc = len(pattern[0])

    best = None  # (overlap, r, c)
    trials = 0
    # перечислим все возможные позиции
    for r in range(rows - pr + 1):
        for c in range(cols - pc + 1):
            overlap = 0
            for i in range(pr):
                for j in range(pc):
                    if pattern[i][j] == 1 and grid[r+i][c+j] == 1:
                        overlap += 1
            if best is None or overlap < best[0]:
                best = (overlap, r, c)
            trials += 1
            if trials >= max_trials:
                break
        if trials >= max_trials:
            break

    if best is None:
        return None
    # если overlap очень большой, всё равно вернём лучшее место
    return (best[1], best[2])

def overlay_pattern(grid: list, pattern: list, top: int, left: int):
    """вставить pattern в grid поверх (перекрывая клетки)"""
    pr = len(pattern)
    pc = len(pattern[0])
    for i in range(pr):
        for j in range(pc):
            if 0 <= top+i < len(grid) and 0 <= left+j < len(grid[0]):
                grid[top+i][left+j] = pattern[i][j]

def grid_to_str(grid: list) -> str:
    """преобразовать сетку в строку для консольного вывода (# — живая, . — мёртвая)"""
    lines = []
    for row in grid:
        lines.append(''.join('#' if v==1 else '.' for v in row))
    return '\n'.join(lines)

def run_console_mode(grid: list, steps: int, delay: float):
    """запуск симуляции в консоли"""
    cur = grid
    print("Запуск в консольном режиме. Нажми Ctrl-C для остановки.")
    try:
        for step in range(steps):
            print(f"\nШаг {step+1}/{steps}")
            print(grid_to_str(cur))
            cur = step_grid(cur)
            time.sleep(delay)
    except KeyboardInterrupt:
        print("\nОстановлено пользователем.")

def run_pygame_mode(grid: list, steps: int, delay: float, cell_size: int = 16):
    """запуск симуляции с pygame (если доступен)"""
    if not PYGAME_AVAILABLE:
        print("pygame не установлен. Установи pygame или используй консольный режим.")
        return

    pygame.init()
    rows = len(grid)
    cols = len(grid[0])
    width = cols * cell_size
    height = rows * cell_size
    screen = pygame.display.set_mode((width, height))
    pygame.display.set_caption("Игра Жизнь — Beacon — Daniil Socolov (I2302)")
    clock = pygame.time.Clock()

    cur = grid
    running = True
    step = 0
    while running and step < steps:
        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                running = False
        # отрисовка
        screen.fill((0,0,0))
        for r in range(rows):
            for c in range(cols):
                if cur[r][c] == 1:
                    rect = pygame.Rect(c*cell_size, r*cell_size, cell_size, cell_size)
                    pygame.draw.rect(screen, (200,200,200), rect)
        # сетка (тонко)
        for x in range(0, width, cell_size):
            pygame.draw.line(screen, (40,40,40), (x,0), (x,height))
        for y in range(0, height, cell_size):
            pygame.draw.line(screen, (40,40,40), (0,y), (width,y))

        pygame.display.flip()
        time.sleep(delay)
        cur = step_grid(cur)
        step += 1
        clock.tick(60)

    pygame.quit()

def main():
    parser = argparse.ArgumentParser(description="лаб. работа №4 — игра Жизнь (Beacon, 30x30)")
    parser.add_argument('--name', type=str, default="Daniil Socolov", help="ФИО студента (по нему генерируется seed)")
    parser.add_argument('--prob', type=float, default=0.3, help="вероятность живой клетки в случайной сети (0.2-0.5 рекоменд.)")
    parser.add_argument('--mode', choices=['console','pygame'], default='console', help="режим вывода: console или pygame")
    parser.add_argument('--steps', type=int, default=200, help="число шагов симуляции")
    parser.add_argument('--delay', type=float, default=0.15, help="задержка между шагами (секунды)")
    parser.add_argument('--cell', type=int, default=16, help="размер клетки в пикселях (только для pygame)")
    args = parser.parse_args()

    # применяем seed для воспроизводимости
    seed = seed_from_name(args.name)
    random.seed(seed)

    # проверка вероятности
    prob = max(0.0, min(1.0, args.prob))

    print(f"Генерация случайной сетки {ROWS}x{COLS} с вероятностью жизни {prob:.2f}")
    print(f"seed из имени '{args.name}' -> {seed}")
    grid = make_random_grid(ROWS, COLS, prob)

    # найти место для beacon
    pos = place_pattern_min_overlap(grid, BEACON_PATTERN, max_trials=ROWS*COLS)
    if pos is None:
        print("Не удалось найти место для установки паттерна Beacon (маловероятно).")
    else:
        top, left = pos
        # небольшая проверка: если перекрытие слишком велико, предупредим
        overlap = sum(1 for i in range(len(BEACON_PATTERN)) for j in range(len(BEACON_PATTERN[0]))
                      if BEACON_PATTERN[i][j]==1 and grid[top+i][left+j]==1)
        print(f"Установка паттерна Beacon в позицию (строка {top}, колонка {left}), перекрытие живых клеток: {overlap}")
        overlay_pattern(grid, BEACON_PATTERN, top, left)

    # запуск в нужном режиме
    if args.mode == 'console' or not PYGAME_AVAILABLE:
        if args.mode == 'pygame' and not PYGAME_AVAILABLE:
            print("pygame не обнаружен — переключаюсь в консольный режим.")
        run_console_mode(grid, args.steps, args.delay)
    else:
        run_pygame_mode(grid, args.steps, args.delay, cell_size=args.cell)

if __name__ == "__main__":
    main()
