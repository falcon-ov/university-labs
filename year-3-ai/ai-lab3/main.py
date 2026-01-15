import pygame
import random
import math

# параметры экрана
WIDTH, HEIGHT = 800, 600
FPS = 30

# параметры стаи
NUM_BOIDS = 30
MAX_SPEED = 4
NEIGHBOR_RADIUS = 50
SEPARATION_RADIUS = 20

# веса правил
COHESION_WEIGHT = 0.01 
ALIGNMENT_WEIGHT = 0.05
SEPARATION_WEIGHT = 0.1

pygame.init()
screen = pygame.display.set_mode((WIDTH, HEIGHT))
pygame.display.set_caption("Boids Simulation")
clock = pygame.time.Clock()


class Boid:
    def __init__(self):
        self.x = random.uniform(0, WIDTH)
        self.y = random.uniform(0, HEIGHT)
        angle = random.uniform(0, 2 * math.pi)
        self.vx = math.cos(angle) * 2
        self.vy = math.sin(angle) * 2

    def update(self, boids):
        # соседи
        neighbors = []
        for other in boids:
            if other is self:
                continue
            dist = math.hypot(self.x - other.x, self.y - other.y)
            if dist < NEIGHBOR_RADIUS:
                neighbors.append(other)

        if not neighbors:
            self.x += self.vx
            self.y += self.vy
            self.wrap()
            return

        # правила
        cohesion_x, cohesion_y = 0, 0
        align_x, align_y = 0, 0
        separation_x, separation_y = 0, 0

        for other in neighbors:
            cohesion_x += other.x
            cohesion_y += other.y
            align_x += other.vx
            align_y += other.vy
            dist = math.hypot(self.x - other.x, self.y - other.y)
            if dist < SEPARATION_RADIUS:
                separation_x += self.x - other.x
                separation_y += self.y - other.y

        n = len(neighbors)
        # центрирование
        cohesion_x = (cohesion_x / n - self.x) * COHESION_WEIGHT
        cohesion_y = (cohesion_y / n - self.y) * COHESION_WEIGHT
        # выравнивание
        align_x = (align_x / n - self.vx) * ALIGNMENT_WEIGHT
        align_y = (align_y / n - self.vy) * ALIGNMENT_WEIGHT
        # разделение
        separation_x *= SEPARATION_WEIGHT
        separation_y *= SEPARATION_WEIGHT

        # итоговое движение
        self.vx += cohesion_x + align_x + separation_x
        self.vy += cohesion_y + align_y + separation_y

        # ограничение скорости
        speed = math.hypot(self.vx, self.vy)
        if speed > MAX_SPEED:
            self.vx = (self.vx / speed) * MAX_SPEED
            self.vy = (self.vy / speed) * MAX_SPEED

        # обновить позицию
        self.x += self.vx
        self.y += self.vy
        self.wrap()

    def wrap(self):
        # "выход за границы" → появление с другой стороны
        if self.x < 0:
            self.x += WIDTH
        elif self.x > WIDTH:
            self.x -= WIDTH
        if self.y < 0:
            self.y += HEIGHT
        elif self.y > HEIGHT:
            self.y -= HEIGHT

    def draw(self):
        # треугольник в направлении движения
        angle = math.atan2(self.vy, self.vx)
        points = [
            (self.x + math.cos(angle) * 8, self.y + math.sin(angle) * 8),
            (self.x + math.cos(angle + 2.5) * 6, self.y + math.sin(angle + 2.5) * 6),
            (self.x + math.cos(angle - 2.5) * 6, self.y + math.sin(angle - 2.5) * 6),
        ]
        pygame.draw.polygon(screen, (255, 255, 255), points)


# создание агентов
boids = [Boid() for _ in range(NUM_BOIDS)]

# главный цикл
running = True
while running:
    screen.fill((30, 30, 30))

    for event in pygame.event.get():
        if event.type == pygame.QUIT:
            running = False

    for boid in boids:
        boid.update(boids)
        boid.draw()

    pygame.display.flip()
    clock.tick(FPS)

pygame.quit()
