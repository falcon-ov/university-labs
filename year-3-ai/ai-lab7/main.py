import heapq
from collections import deque
import time
from typing import List, Tuple, Optional, Set


class PuzzleState:
    """Represents a state of the 8-puzzle"""

    def __init__(self, board: List[List[int]], parent=None, move: str = "", g: int = 0, h: int = 0):
        self.board = board
        self.parent = parent
        self.move = move
        self.g = g  # Cost from start
        self.h = h  # Heuristic cost to goal
        self.f = g + h  # Total cost
        self.blank_pos = self._find_blank()

    def _find_blank(self) -> Tuple[int, int]:
        """Find the position of blank (0) tile"""
        for i in range(3):
            for j in range(3):
                if self.board[i][j] == 0:
                    return (i, j)
        return (0, 0)

    def __eq__(self, other):
        return self.board == other.board

    def __lt__(self, other):
        return self.f < other.f

    def __hash__(self):
        return hash(str(self.board))

    def __str__(self):
        result = ""
        for row in self.board:
            result += " ".join(str(x) if x != 0 else "_" for x in row) + "\n"
        return result

    def copy_board(self) -> List[List[int]]:
        """Create a deep copy of the board"""
        return [row[:] for row in self.board]

    def is_goal(self, goal_state) -> bool:
        """Check if current state is the goal state"""
        return self.board == goal_state.board


class PuzzleSolver:
    """Solver for 8-puzzle problem with multiple search strategies"""

    def __init__(self, initial_state: List[List[int]], goal_state: List[List[int]]):
        self.initial = PuzzleState(initial_state)
        self.goal = PuzzleState(goal_state)
        self.visited_nodes = 0
        self.max_depth = 0

    def get_neighbors(self, state: PuzzleState) -> List[PuzzleState]:
        """Generate all possible next states from current state"""
        neighbors = []
        row, col = state.blank_pos

        # Possible moves: up, down, left, right
        moves = [
            (-1, 0, "UP"),
            (1, 0, "DOWN"),
            (0, -1, "LEFT"),
            (0, 1, "RIGHT")
        ]

        for dr, dc, move_name in moves:
            new_row, new_col = row + dr, col + dc

            # Check if move is valid
            if 0 <= new_row < 3 and 0 <= new_col < 3:
                # Create new board state
                new_board = state.copy_board()
                # Swap blank with adjacent tile
                new_board[row][col], new_board[new_row][new_col] = \
                    new_board[new_row][new_col], new_board[row][col]

                neighbors.append(PuzzleState(
                    new_board,
                    parent=state,
                    move=move_name,
                    g=state.g + 1
                ))

        return neighbors

    def reconstruct_path(self, state: PuzzleState) -> List[PuzzleState]:
        """Reconstruct path from initial state to goal state"""
        path = []
        current = state
        while current is not None:
            path.append(current)
            current = current.parent
        return path[::-1]

    # Heuristic functions
    def manhattan_distance(self, state: PuzzleState) -> int:
        """Calculate Manhattan distance heuristic"""
        distance = 0
        for i in range(3):
            for j in range(3):
                if state.board[i][j] != 0:
                    value = state.board[i][j]
                    # Find goal position of this value
                    for gi in range(3):
                        for gj in range(3):
                            if self.goal.board[gi][gj] == value:
                                distance += abs(i - gi) + abs(j - gj)
        return distance

    def misplaced_tiles(self, state: PuzzleState) -> int:
        """Count number of misplaced tiles"""
        count = 0
        for i in range(3):
            for j in range(3):
                if state.board[i][j] != 0 and state.board[i][j] != self.goal.board[i][j]:
                    count += 1
        return count

    # Search strategies
    def bfs(self) -> Tuple[Optional[List[PuzzleState]], int, float]:
        """Breadth-First Search"""
        start_time = time.time()
        self.visited_nodes = 0

        queue = deque([self.initial])
        visited = {hash(self.initial)}

        while queue:
            current = queue.popleft()
            self.visited_nodes += 1

            if current.is_goal(self.goal):
                end_time = time.time()
                return self.reconstruct_path(current), self.visited_nodes, end_time - start_time

            for neighbor in self.get_neighbors(current):
                neighbor_hash = hash(neighbor)
                if neighbor_hash not in visited:
                    visited.add(neighbor_hash)
                    queue.append(neighbor)

        end_time = time.time()
        return None, self.visited_nodes, end_time - start_time

    def dfs(self, max_depth: int = 50) -> Tuple[Optional[List[PuzzleState]], int, float]:
        """Depth-First Search with depth limit"""
        start_time = time.time()
        self.visited_nodes = 0

        stack = [self.initial]
        visited = {hash(self.initial)}

        while stack:
            current = stack.pop()
            self.visited_nodes += 1

            if current.is_goal(self.goal):
                end_time = time.time()
                return self.reconstruct_path(current), self.visited_nodes, end_time - start_time

            # Depth limit to prevent infinite search
            if current.g < max_depth:
                for neighbor in self.get_neighbors(current):
                    neighbor_hash = hash(neighbor)
                    if neighbor_hash not in visited:
                        visited.add(neighbor_hash)
                        stack.append(neighbor)

        end_time = time.time()
        return None, self.visited_nodes, end_time - start_time

    def greedy_search(self) -> Tuple[Optional[List[PuzzleState]], int, float]:
        """Greedy Best-First Search using Manhattan distance"""
        start_time = time.time()
        self.visited_nodes = 0

        # Priority queue with heuristic value
        open_set = []
        initial_h = self.manhattan_distance(self.initial)
        self.initial.h = initial_h
        heapq.heappush(open_set, (initial_h, 0, self.initial))

        visited = {hash(self.initial)}
        counter = 1  # To break ties in priority queue

        while open_set:
            _, _, current = heapq.heappop(open_set)
            self.visited_nodes += 1

            if current.is_goal(self.goal):
                end_time = time.time()
                return self.reconstruct_path(current), self.visited_nodes, end_time - start_time

            for neighbor in self.get_neighbors(current):
                neighbor_hash = hash(neighbor)
                if neighbor_hash not in visited:
                    visited.add(neighbor_hash)
                    neighbor.h = self.manhattan_distance(neighbor)
                    heapq.heappush(open_set, (neighbor.h, counter, neighbor))
                    counter += 1

        end_time = time.time()
        return None, self.visited_nodes, end_time - start_time

    def a_star(self) -> Tuple[Optional[List[PuzzleState]], int, float]:
        """A* Search using Manhattan distance"""
        start_time = time.time()
        self.visited_nodes = 0

        # Priority queue with f = g + h
        open_set = []
        initial_h = self.manhattan_distance(self.initial)
        self.initial.h = initial_h
        self.initial.f = self.initial.g + initial_h
        heapq.heappush(open_set, (self.initial.f, 0, self.initial))

        visited = set()
        g_scores = {hash(self.initial): 0}
        counter = 1

        while open_set:
            _, _, current = heapq.heappop(open_set)
            current_hash = hash(current)

            if current_hash in visited:
                continue

            visited.add(current_hash)
            self.visited_nodes += 1

            if current.is_goal(self.goal):
                end_time = time.time()
                return self.reconstruct_path(current), self.visited_nodes, end_time - start_time

            for neighbor in self.get_neighbors(current):
                neighbor_hash = hash(neighbor)
                tentative_g = current.g + 1

                if neighbor_hash not in g_scores or tentative_g < g_scores[neighbor_hash]:
                    g_scores[neighbor_hash] = tentative_g
                    neighbor.g = tentative_g
                    neighbor.h = self.manhattan_distance(neighbor)
                    neighbor.f = neighbor.g + neighbor.h
                    heapq.heappush(open_set, (neighbor.f, counter, neighbor))
                    counter += 1

        end_time = time.time()
        return None, self.visited_nodes, end_time - start_time


def print_solution(path: List[PuzzleState], algorithm_name: str):
    """Print the solution path"""
    print(f"\n{'=' * 50}")
    print(f"{algorithm_name} Solution")
    print(f"{'=' * 50}")
    print(f"Number of steps: {len(path) - 1}\n")

    for i, state in enumerate(path):
        print(f"Step {i}:" + (f" {state.move}" if state.move else " INITIAL"))
        print(state)


def main():
    # Initial state (solvable configuration)
    initial_state = [
        [1, 2, 3],
        [4, 0, 5],
        [7, 8, 6]
    ]

    # Goal state
    goal_state = [
        [1, 2, 3],
        [4, 5, 6],
        [7, 8, 0]
    ]

    print("8-PUZZLE SOLVER")
    print("=" * 50)
    print("\nInitial State:")
    print(PuzzleState(initial_state))
    print("Goal State:")
    print(PuzzleState(goal_state))

    # Create solver
    solver = PuzzleSolver(initial_state, goal_state)

    # Store results for comparison
    results = {}

    # BFS
    print("\n" + "=" * 50)
    print("Running Breadth-First Search...")
    path, nodes, exec_time = solver.bfs()
    if path:
        results['BFS'] = {
            'path_length': len(path) - 1,
            'nodes_visited': nodes,
            'time': exec_time,
            'optimal': True
        }
        print_solution(path, "BFS")
    else:
        print("No solution found with BFS")

    # DFS
    print("\n" + "=" * 50)
    print("Running Depth-First Search...")
    solver = PuzzleSolver(initial_state, goal_state)
    path, nodes, exec_time = solver.dfs(max_depth=30)
    if path:
        results['DFS'] = {
            'path_length': len(path) - 1,
            'nodes_visited': nodes,
            'time': exec_time,
            'optimal': False
        }
        print_solution(path, "DFS")
    else:
        print("No solution found with DFS (depth limit reached)")

    # Greedy Search
    print("\n" + "=" * 50)
    print("Running Greedy Best-First Search...")
    solver = PuzzleSolver(initial_state, goal_state)
    path, nodes, exec_time = solver.greedy_search()
    if path:
        results['Greedy'] = {
            'path_length': len(path) - 1,
            'nodes_visited': nodes,
            'time': exec_time,
            'optimal': False
        }
        print_solution(path, "Greedy Search")
    else:
        print("No solution found with Greedy Search")

    # A* Search
    print("\n" + "=" * 50)
    print("Running A* Search...")
    solver = PuzzleSolver(initial_state, goal_state)
    path, nodes, exec_time = solver.a_star()
    if path:
        results['A*'] = {
            'path_length': len(path) - 1,
            'nodes_visited': nodes,
            'time': exec_time,
            'optimal': True
        }
        print_solution(path, "A* Search")
    else:
        print("No solution found with A*")

    # Comparison table
    print("\n" + "=" * 50)
    print("COMPARISON OF SEARCH STRATEGIES")
    print("=" * 50)
    print(f"{'Algorithm':<15} {'Steps':<10} {'Nodes Visited':<15} {'Time (s)':<15} {'Optimal':<10}")
    print("-" * 70)

    for algo, data in results.items():
        print(f"{algo:<15} {data['path_length']:<10} {data['nodes_visited']:<15} "
              f"{data['time']:<15.6f} {'Yes' if data['optimal'] else 'No':<10}")

    print("\n" + "=" * 50)
    print("CONCLUSION:")
    print("=" * 50)
    print("- BFS guarantees optimal solution but visits many nodes")
    print("- DFS may not find optimal solution and can get stuck")
    print("- Greedy Search is fast but may not find optimal solution")
    print("- A* combines optimality of BFS with efficiency of heuristics")
    print("- Manhattan distance heuristic significantly reduces search space")


if __name__ == "__main__":
    main()