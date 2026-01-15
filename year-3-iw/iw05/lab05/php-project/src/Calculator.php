<?php

namespace App;

class Calculator
{
    /**
     * Сложение двух чисел
     */
    public function add(float $a, float $b): float
    {
        return $a + $b;
    }

    /**
     * Вычитание
     */
    public function subtract(float $a, float $b): float
    {
        return $a - $b;
    }

    /**
     * Умножение
     */
    public function multiply(float $a, float $b): float
    {
        return $a * $b;
    }

    /**
     * Деление
     */
    public function divide(float $a, float $b): float
    {
        if ($b == 0) {
            throw new \InvalidArgumentException("Division by zero is not allowed");
        }
        return $a / $b;
    }

    /**
     * Проверка на четность
     */
    public function isEven(int $number): bool
    {
        return $number % 2 === 0;
    }

    /**
     * Факториал числа
     */
    public function factorial(int $n): int
    {
        if ($n < 0) {
            throw new \InvalidArgumentException("Factorial is not defined for negative numbers");
        }
        if ($n === 0 || $n === 1) {
            return 1;
        }
        return $n * $this->factorial($n - 1);
    }
}