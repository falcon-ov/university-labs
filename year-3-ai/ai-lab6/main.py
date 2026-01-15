import random
import math
import matplotlib.pyplot as plt


# ============================================================================
# TARGET FUNCTION
# ============================================================================
def fitness_function(x):
    """
    Target function: f(x) = (1-x)*sin(5*pi*x) + 1
    where x ∈ [0, 1]
    """
    return (1 - x) * math.sin(5 * math.pi * x) + 1


# ============================================================================
# HELPER FUNCTIONS
# ============================================================================
def binary_to_decimal(chromosome, L=4):
    """
    Convert binary chromosome to decimal number x ∈ [0, 1]
    """
    decimal_value = int(''.join(map(str, chromosome)), 2)
    max_value = 2 ** L - 1
    return decimal_value / max_value


def decimal_to_binary(x, L=4):
    """
    Convert decimal number x ∈ [0, 1] to binary chromosome
    """
    decimal_value = int(x * (2 ** L - 1))
    return [int(bit) for bit in format(decimal_value, f'0{L}b')]


def create_initial_population(N, L):
    """
    Create initial population of random chromosomes
    """
    population = []
    for _ in range(N):
        chromosome = [random.randint(0, 1) for _ in range(L)]
        population.append(chromosome)
    return population


def evaluate_population(population):
    """
    Evaluate fitness of entire population
    Returns list of fitness values for each individual
    """
    fitness_values = []
    for chromosome in population:
        x = binary_to_decimal(chromosome)
        fitness = fitness_function(x)
        fitness_values.append(fitness)
    return fitness_values


def selection(population, fitness_values):
    """
    Selection using roulette wheel method
    Selection probability proportional to fitness
    """
    # Normalize fitness (make all positive)
    min_fitness = min(fitness_values)
    adjusted_fitness = [f - min_fitness + 0.1 for f in fitness_values]

    total_fitness = sum(adjusted_fitness)
    probabilities = [f / total_fitness for f in adjusted_fitness]

    # Select parents
    parents = random.choices(population, weights=probabilities, k=2)
    return parents


def crossover(parent1, parent2, pc):
    """
    Single-point crossover with probability pc
    Returns two offspring and crossover information
    """
    if random.random() < pc:
        # Perform crossover
        point = random.randint(1, len(parent1) - 1)
        child1 = parent1[:point] + parent2[point:]
        child2 = parent2[:point] + parent1[point:]
        return child1, child2, point
    else:
        # No crossover, return copies of parents
        return parent1.copy(), parent2.copy(), None


def mutation(chromosome, pm):
    """
    Mutation: each bit changes with probability pm
    Returns mutated chromosome and list of mutation positions
    """
    mutated = chromosome.copy()
    mutation_positions = []

    for i in range(len(mutated)):
        if random.random() < pm:
            mutated[i] = 1 - mutated[i]  # Invert bit
            mutation_positions.append(i)

    return mutated, mutation_positions


# ============================================================================
# MAIN GENETIC ALGORITHM
# ============================================================================
def genetic_algorithm(N=4, L=4, pc=0.8, pm=0.05, G=30, verbose=True):
    """
    Main genetic algorithm function

    Parameters:
    - N: population size
    - L: chromosome length (number of bits)
    - pc: crossover probability
    - pm: mutation probability
    - G: number of generations
    - verbose: whether to print detailed logs
    """

    # Initialization
    population = create_initial_population(N, L)

    # History for plotting graphs
    max_fitness_history = []
    min_fitness_history = []
    avg_fitness_history = []

    print("=" * 80)
    print(f"STARTING GENETIC ALGORITHM")
    print(f"Parameters: N={N}, L={L}, pc={pc}, pm={pm}, G={G}")
    print("=" * 80)

    # Main evolution loop
    for generation in range(G):
        # Fitness evaluation
        fitness_values = evaluate_population(population)

        # Statistics
        max_fit = max(fitness_values)
        min_fit = min(fitness_values)
        avg_fit = sum(fitness_values) / len(fitness_values)

        max_fitness_history.append(max_fit)
        min_fitness_history.append(min_fit)
        avg_fitness_history.append(avg_fit)

        # Display generation information
        if verbose:
            print(f"\n{'=' * 80}")
            print(f"GENERATION {generation + 1}")
            print(f"{'=' * 80}")
            print(f"{'#':<4} {'Chromosome':<12} {'Decimal':<15} {'x':<10} {'f(x)':<10}")
            print("-" * 80)

            for i, (chrom, fit) in enumerate(zip(population, fitness_values)):
                chrom_str = ''.join(map(str, chrom))
                decimal = int(chrom_str, 2)
                x = binary_to_decimal(chrom)
                print(f"{i + 1:<4} {chrom_str:<12} {decimal:<15} {x:<10.4f} {fit:<10.6f}")

            print("-" * 80)
            print(f"Max fitness:  {max_fit:.6f}")
            print(f"Min fitness:  {min_fit:.6f}")
            print(f"Avg fitness:  {avg_fit:.6f}")

        # Form new generation
        new_population = []

        if verbose:
            print(f"\n--- Selection, Crossover and Mutation ---")

        offspring_count = 0
        while len(new_population) < N:
            # Parent selection
            parent1, parent2 = selection(population, fitness_values)

            # Crossover
            child1, child2, crossover_point = crossover(parent1, parent2, pc)

            if verbose and crossover_point is not None:
                print(f"\nCrossover between parents:")
                print(f"  Parent 1: {''.join(map(str, parent1))}")
                print(f"  Parent 2: {''.join(map(str, parent2))}")
                print(f"  Crossover point: {crossover_point}")
                print(f"  Offspring 1:  {''.join(map(str, child1))}")
                print(f"  Offspring 2:  {''.join(map(str, child2))}")

            # Mutation of offspring 1
            child1, mutations1 = mutation(child1, pm)
            if verbose and mutations1:
                print(f"\n⚡ Mutation in offspring {offspring_count + 1}:")
                print(f"  Bits changed at positions: {mutations1}")
                print(f"  Result: {''.join(map(str, child1))}")

            new_population.append(child1)
            offspring_count += 1

            if len(new_population) < N:
                # Mutation of offspring 2
                child2, mutations2 = mutation(child2, pm)
                if verbose and mutations2:
                    print(f"\n⚡ Mutation in offspring {offspring_count + 1}:")
                    print(f"  Bits changed at positions: {mutations2}")
                    print(f"  Result: {''.join(map(str, child2))}")

                new_population.append(child2)
                offspring_count += 1

        # Update population
        population = new_population[:N]

    # Final evaluation
    fitness_values = evaluate_population(population)
    best_idx = fitness_values.index(max(fitness_values))
    best_chromosome = population[best_idx]
    best_x = binary_to_decimal(best_chromosome)
    best_fitness = fitness_values[best_idx]

    print("\n" + "=" * 80)
    print("RESULTS")
    print("=" * 80)
    print(f"Best individual: {''.join(map(str, best_chromosome))}")
    print(f"x* = {best_x:.6f}")
    print(f"f(x*) = {best_fitness:.6f}")
    print("=" * 80)

    return {
        'best_x': best_x,
        'best_fitness': best_fitness,
        'best_chromosome': best_chromosome,
        'max_history': max_fitness_history,
        'min_history': min_fitness_history,
        'avg_history': avg_fitness_history
    }


# ============================================================================
# VISUALIZATION
# ============================================================================
def plot_results(results_list, labels):
    """
    Plot graphs for multiple experiments
    """
    plt.figure(figsize=(12, 6))

    for result, label in zip(results_list, labels):
        plt.plot(result['max_history'], marker='o', label=f'{label} (max)', linewidth=2)

    plt.xlabel('Generation', fontsize=12)
    plt.ylabel('Fitness', fontsize=12)
    plt.title('Evolution of Maximum Fitness Value', fontsize=14, fontweight='bold')
    plt.legend()
    plt.grid(True, alpha=0.3)
    plt.tight_layout()
    plt.show()


# ============================================================================
# RUN EXPERIMENTS
# ============================================================================
if __name__ == "__main__":
    print("\n" + "█" * 80)
    print("█" + " " * 78 + "█")
    print("█" + " " * 25 + "LABORATORY WORK #6" + " " * 35 + "█")
    print("█" + " " * 15 + "Genetic Algorithm Optimization Implementation" + " " * 20 + "█")
    print("█" + " " * 78 + "█")
    print("█" * 80 + "\n")

    # Experiment 1: Base parameters
    print("\n" + "▓" * 80)
    print("▓ EXPERIMENT 1: Base parameters (pc=0.8, pm=0.05)")
    print("▓" * 80)
    result1 = genetic_algorithm(N=6, L=4, pc=0.8, pm=0.05, G=30, verbose=True)

    # Experiment 2: High crossover probability
    print("\n" + "▓" * 80)
    print("▓ EXPERIMENT 2: High crossover probability (pc=1.0, pm=0.05)")
    print("▓" * 80)
    result2 = genetic_algorithm(N=6, L=4, pc=1.0, pm=0.05, G=30, verbose=False)

    # Experiment 3: High mutation probability
    print("\n" + "▓" * 80)
    print("▓ EXPERIMENT 3: High mutation probability (pc=0.8, pm=0.2)")
    print("▓" * 80)
    result3 = genetic_algorithm(N=6, L=4, pc=0.8, pm=0.2, G=30, verbose=False)

    # Comparison graph
    plot_results(
        [result1, result2, result3],
        ['pc=0.8, pm=0.05', 'pc=1.0, pm=0.05', 'pc=0.8, pm=0.2']
    )

    # Summary table of results
    print("\n" + "=" * 80)
    print("SUMMARY TABLE OF RESULTS")
    print("=" * 80)
    print(f"{'Experiment':<30} {'x*':<15} {'f(x*)':<15}")
    print("-" * 80)
    print(f"{'1. pc=0.8, pm=0.05':<30} {result1['best_x']:<15.6f} {result1['best_fitness']:<15.6f}")
    print(f"{'2. pc=1.0, pm=0.05':<30} {result2['best_x']:<15.6f} {result2['best_fitness']:<15.6f}")
    print(f"{'3. pc=0.8, pm=0.2':<30} {result3['best_x']:<15.6f} {result3['best_fitness']:<15.6f}")
    print("=" * 80)