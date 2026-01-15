<?php

namespace Tests;

use PHPUnit\Framework\TestCase;
use App\Calculator;

class CalculatorTest extends TestCase
{
    private Calculator $calculator;

    protected function setUp(): void
    {
        $this->calculator = new Calculator();
    }

    public function testAddition(): void
    {
        $result = $this->calculator->add(5, 3);
        $this->assertEquals(8, $result, "5 + 3 should equal 8");
    }

    public function testSubtraction(): void
    {
        $result = $this->calculator->subtract(10, 4);
        $this->assertEquals(6, $result, "10 - 4 should equal 6");
    }

    public function testMultiplication(): void
    {
        $result = $this->calculator->multiply(6, 7);
        $this->assertEquals(42, $result, "6 * 7 should equal 42");
    }

    public function testDivision(): void
    {
        $result = $this->calculator->divide(20, 4);
        $this->assertEquals(5, $result, "20 / 4 should equal 5");
    }

    public function testDivisionByZeroThrowsException(): void
    {
        $this->expectException(\InvalidArgumentException::class);
        $this->expectExceptionMessage("Division by zero is not allowed");
        $this->calculator->divide(10, 0);
    }

    public function testIsEvenWithEvenNumber(): void
    {
        $result = $this->calculator->isEven(4);
        $this->assertTrue($result, "4 should be even");
    }

    public function testIsEvenWithOddNumber(): void
    {
        $result = $this->calculator->isEven(7);
        $this->assertFalse($result, "7 should not be even");
    }

    public function testFactorialOfZero(): void
    {
        $result = $this->calculator->factorial(0);
        $this->assertEquals(1, $result, "Factorial of 0 should be 1");
    }

    public function testFactorialOfPositiveNumber(): void
    {
        $result = $this->calculator->factorial(5);
        $this->assertEquals(120, $result, "Factorial of 5 should be 120");
    }

    public function testFactorialOfNegativeNumberThrowsException(): void
    {
        $this->expectException(\InvalidArgumentException::class);
        $this->expectExceptionMessage("Factorial is not defined for negative numbers");
        $this->calculator->factorial(-5);
    }
}