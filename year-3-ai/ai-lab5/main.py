import tkinter as tk
from tkinter import ttk
import matplotlib.pyplot as plt
from matplotlib.backends.backend_tkagg import FigureCanvasTkAgg
import numpy as np


class FuzzyEnergyPricePredictor:
    def __init__(self, root):
        self.root = root
        self.root.title("Прогнозирование роста цен на энергию - Нечёткая логика")
        self.root.geometry("1400x900")
        self.root.configure(bg='#f0f0f0')

        # Переменные
        self.demand = tk.DoubleVar(value=50)
        self.supply = tk.DoubleVar(value=50)
        self.inflation = tk.DoubleVar(value=50)
        self.price_growth = tk.StringVar(value="50.0")

        self.create_widgets()
        self.update_prediction()

    def create_widgets(self):
        # Заголовок
        title_frame = tk.Frame(self.root, bg='#2c3e50', pady=15)
        title_frame.pack(fill=tk.X)

        title_label = tk.Label(title_frame,
                               text="Прогнозирование роста цен на энергию",
                               font=('Arial', 20, 'bold'),
                               bg='#2c3e50', fg='white')
        title_label.pack()

        subtitle_label = tk.Label(title_frame,
                                  text="Лабораторная работа №5.4 - Нечёткая логика (Fuzzy Logic)",
                                  font=('Arial', 12),
                                  bg='#2c3e50', fg='#ecf0f1')
        subtitle_label.pack()

        # Основной контейнер
        main_container = tk.Frame(self.root, bg='#f0f0f0')
        main_container.pack(fill=tk.BOTH, expand=True, padx=10, pady=10)

        # Левая панель (управление)
        left_panel = tk.Frame(main_container, bg='white', relief=tk.RAISED, borderwidth=2)
        left_panel.pack(side=tk.LEFT, fill=tk.BOTH, padx=(0, 5), pady=0)

        # Панель ввода
        input_frame = tk.LabelFrame(left_panel, text="Входные параметры",
                                    font=('Arial', 12, 'bold'),
                                    bg='white', padx=15, pady=15)
        input_frame.pack(fill=tk.X, padx=10, pady=10)

        # Спрос
        self.create_slider(input_frame, "Спрос:", self.demand,
                           "Низкий ← → Высокий", '#3498db', 0)

        # Предложение
        self.create_slider(input_frame, "Предложение:", self.supply,
                           "Низкое ← → Высокое", '#2ecc71', 1)

        # Инфляция
        self.create_slider(input_frame, "Инфляция:", self.inflation,
                           "Низкая ← → Высокая", '#e74c3c', 2)

        # Панель результата
        result_frame = tk.LabelFrame(left_panel, text="Результат прогноза",
                                     font=('Arial', 12, 'bold'),
                                     bg='white', padx=15, pady=15)
        result_frame.pack(fill=tk.X, padx=10, pady=10)

        self.result_label = tk.Label(result_frame,
                                     text="Прогноз роста цен:",
                                     font=('Arial', 14),
                                     bg='white')
        self.result_label.pack()

        self.price_label = tk.Label(result_frame,
                                    textvariable=self.price_growth,
                                    font=('Arial', 36, 'bold'),
                                    bg='white', fg='#2c3e50')
        self.price_label.pack()

        self.interpretation_label = tk.Label(result_frame,
                                             text="Стабильность",
                                             font=('Arial', 16, 'bold'),
                                             bg='white', fg='#f39c12')
        self.interpretation_label.pack()

        # Быстрые сценарии
        scenario_frame = tk.LabelFrame(left_panel, text="Быстрые сценарии",
                                       font=('Arial', 12, 'bold'),
                                       bg='white', padx=15, pady=15)
        scenario_frame.pack(fill=tk.X, padx=10, pady=10)

        scenarios = [
            ("Кризис предложения", 80, 20, 30, '#e74c3c'),
            ("Стабильный рынок", 50, 50, 20, '#2ecc71'),
            ("Высокая инфляция", 30, 80, 70, '#e67e22'),
            ("Идеальные условия", 40, 60, 15, '#1abc9c')
        ]

        for name, d, s, i, color in scenarios:
            btn = tk.Button(scenario_frame, text=name,
                            command=lambda d=d, s=s, i=i: self.set_scenario(d, s, i),
                            font=('Arial', 10, 'bold'),
                            bg=color, fg='white',
                            relief=tk.RAISED,
                            cursor='hand2')
            btn.pack(fill=tk.X, pady=3)

        # Правая панель (графики)
        right_panel = tk.Frame(main_container, bg='white', relief=tk.RAISED, borderwidth=2)
        right_panel.pack(side=tk.LEFT, fill=tk.BOTH, expand=True, padx=(5, 0), pady=0)

        # Создание графиков
        self.fig, self.axes = plt.subplots(3, 1, figsize=(8, 10))
        self.fig.tight_layout(pad=3.0)

        self.canvas = FigureCanvasTkAgg(self.fig, master=right_panel)
        self.canvas.get_tk_widget().pack(fill=tk.BOTH, expand=True, padx=5, pady=5)

        self.plot_membership_functions()

    def create_slider(self, parent, label_text, variable, hint, color, row):
        frame = tk.Frame(parent, bg='white')
        frame.pack(fill=tk.X, pady=10)

        label = tk.Label(frame, text=label_text, font=('Arial', 11, 'bold'),
                         bg='white', fg=color)
        label.pack(anchor=tk.W)

        value_label = tk.Label(frame, text=f"{variable.get():.0f}%",
                               font=('Arial', 10),
                               bg='white', fg=color)
        value_label.pack(anchor=tk.E)

        slider = ttk.Scale(frame, from_=0, to=100, variable=variable,
                           orient=tk.HORIZONTAL, length=300,
                           command=lambda v: self.on_slider_change(variable, value_label))
        slider.pack(fill=tk.X, pady=5)

        hint_label = tk.Label(frame, text=hint, font=('Arial', 9),
                              bg='white', fg='gray')
        hint_label.pack()

    def on_slider_change(self, variable, label):
        label.config(text=f"{variable.get():.0f}%")
        self.update_prediction()

    def set_scenario(self, d, s, i):
        self.demand.set(d)
        self.supply.set(s)
        self.inflation.set(i)
        self.update_prediction()

    # Функции принадлежности для спроса
    def demand_low(self, x):
        return max(0, min(1, (40 - x) / 40))

    def demand_medium(self, x):
        if x <= 30:
            return 0
        elif x <= 50:
            return (x - 30) / 20
        elif x <= 70:
            return (70 - x) / 20
        else:
            return 0

    def demand_high(self, x):
        return max(0, min(1, (x - 60) / 40))

    # Функции принадлежности для предложения
    def supply_low(self, x):
        return max(0, min(1, (40 - x) / 40))

    def supply_normal(self, x):
        if x <= 30:
            return 0
        elif x <= 50:
            return (x - 30) / 20
        elif x <= 70:
            return (70 - x) / 20
        else:
            return 0

    def supply_high(self, x):
        return max(0, min(1, (x - 60) / 40))

    # Функции принадлежности для инфляции
    def inflation_low(self, x):
        return max(0, min(1, (50 - x) / 50))

    def inflation_high(self, x):
        return max(0, min(1, (x - 40) / 60))

    def calculate_price_growth(self, d, s, inf):
        """Метод Мамдани для расчёта роста цен"""

        # Фаззификация
        d_low = self.demand_low(d)
        d_med = self.demand_medium(d)
        d_high = self.demand_high(d)

        s_low = self.supply_low(s)
        s_norm = self.supply_normal(s)
        s_high = self.supply_high(s)

        i_low = self.inflation_low(inf)
        i_high = self.inflation_high(inf)

        # База правил (27 правил - все комбинации 3x3x3)
        rules = []

        # Спрос низкий (9 правил)
        rules.append({'activation': min(d_low, s_low, i_low), 'output': 40})
        rules.append({'activation': min(d_low, s_low, i_high), 'output': 55})
        rules.append({'activation': min(d_low, s_norm, i_low), 'output': 30})
        rules.append({'activation': min(d_low, s_norm, i_high), 'output': 45})
        rules.append({'activation': min(d_low, s_high, i_low), 'output': 20})
        rules.append({'activation': min(d_low, s_high, i_high), 'output': 35})

        # Спрос средний (9 правил)
        rules.append({'activation': min(d_med, s_low, i_low), 'output': 60})
        rules.append({'activation': min(d_med, s_low, i_high), 'output': 70})
        rules.append({'activation': min(d_med, s_norm, i_low), 'output': 50})
        rules.append({'activation': min(d_med, s_norm, i_high), 'output': 60})
        rules.append({'activation': min(d_med, s_high, i_low), 'output': 35})
        rules.append({'activation': min(d_med, s_high, i_high), 'output': 50})

        # Спрос высокий (9 правил)
        rules.append({'activation': min(d_high, s_low, i_low), 'output': 75})
        rules.append({'activation': min(d_high, s_low, i_high), 'output': 85})
        rules.append({'activation': min(d_high, s_norm, i_low), 'output': 65})
        rules.append({'activation': min(d_high, s_norm, i_high), 'output': 75})
        rules.append({'activation': min(d_high, s_high, i_low), 'output': 50})
        rules.append({'activation': min(d_high, s_high, i_high), 'output': 65})

        # Дефаззификация (метод центра тяжести)
        numerator = sum(rule['activation'] * rule['output'] for rule in rules)
        denominator = sum(rule['activation'] for rule in rules)

        if denominator > 0:
            return numerator / denominator
        else:
            return 50

    def update_prediction(self):
        """Обновление прогноза"""
        d = self.demand.get()
        s = self.supply.get()
        i = self.inflation.get()

        result = self.calculate_price_growth(d, s, i)
        self.price_growth.set(f"{result:.1f}%")

        # Интерпретация результата
        if result < 35:
            interpretation = "Снижение цен"
            color = '#27ae60'
            bg_color = '#d5f4e6'
        elif result < 55:
            interpretation = "Стабильность"
            color = '#f39c12'
            bg_color = '#fef5e7'
        else:
            interpretation = "Рост цен"
            color = '#e74c3c'
            bg_color = '#fadbd8'

        self.interpretation_label.config(text=interpretation, fg=color)
        self.price_label.config(fg=color)

        # Обновление графиков
        self.plot_membership_functions()

    def plot_membership_functions(self):
        """Построение графиков функций принадлежности"""

        x = np.linspace(0, 100, 500)

        # График 1: Спрос
        self.axes[0].clear()
        self.axes[0].set_title('Функции принадлежности: Спрос',
                               fontsize=12, fontweight='bold')
        self.axes[0].set_xlabel('Спрос (%)')
        self.axes[0].set_ylabel('Степень принадлежности')
        self.axes[0].grid(True, alpha=0.3)

        y_low = [self.demand_low(xi) for xi in x]
        y_med = [self.demand_medium(xi) for xi in x]
        y_high = [self.demand_high(xi) for xi in x]

        self.axes[0].plot(x, y_low, 'b-', linewidth=2, label='Низкий')
        self.axes[0].plot(x, y_med, 'g-', linewidth=2, label='Средний')
        self.axes[0].plot(x, y_high, 'r-', linewidth=2, label='Высокий')

        # Текущее значение
        current_d = self.demand.get()
        self.axes[0].axvline(current_d, color='black', linestyle='--', alpha=0.5)
        self.axes[0].legend()
        self.axes[0].set_ylim([-0.05, 1.1])

        # График 2: Предложение
        self.axes[1].clear()
        self.axes[1].set_title('Функции принадлежности: Предложение',
                               fontsize=12, fontweight='bold')
        self.axes[1].set_xlabel('Предложение (%)')
        self.axes[1].set_ylabel('Степень принадлежности')
        self.axes[1].grid(True, alpha=0.3)

        y_low = [self.supply_low(xi) for xi in x]
        y_norm = [self.supply_normal(xi) for xi in x]
        y_high = [self.supply_high(xi) for xi in x]

        self.axes[1].plot(x, y_low, 'b-', linewidth=2, label='Низкое')
        self.axes[1].plot(x, y_norm, 'g-', linewidth=2, label='Нормальное')
        self.axes[1].plot(x, y_high, 'r-', linewidth=2, label='Высокое')

        current_s = self.supply.get()
        self.axes[1].axvline(current_s, color='black', linestyle='--', alpha=0.5)
        self.axes[1].legend()
        self.axes[1].set_ylim([-0.05, 1.1])

        # График 3: Инфляция
        self.axes[2].clear()
        self.axes[2].set_title('Функции принадлежности: Инфляция',
                               fontsize=12, fontweight='bold')
        self.axes[2].set_xlabel('Инфляция (%)')
        self.axes[2].set_ylabel('Степень принадлежности')
        self.axes[2].grid(True, alpha=0.3)

        y_low = [self.inflation_low(xi) for xi in x]
        y_high = [self.inflation_high(xi) for xi in x]

        self.axes[2].plot(x, y_low, 'g-', linewidth=2, label='Низкая')
        self.axes[2].plot(x, y_high, 'r-', linewidth=2, label='Высокая')

        current_i = self.inflation.get()
        self.axes[2].axvline(current_i, color='black', linestyle='--', alpha=0.5)
        self.axes[2].legend()
        self.axes[2].set_ylim([-0.05, 1.1])

        self.canvas.draw()


if __name__ == "__main__":
    root = tk.Tk()
    app = FuzzyEnergyPricePredictor(root)
    root.mainloop()