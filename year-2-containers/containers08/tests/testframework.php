<?php
class TestFramework {
    private $tests = [];
    private $pass = 0;
    private $fail = 0;

    public function addTest($name, $callback) {
        $this->tests[$name] = $callback;
    }

    public function run() {
        foreach ($this->tests as $name => $callback) {
            try {
                $result = $callback();
                if ($result) {
                    $this->pass++;
                    echo "PASS: $name\n";
                } else {
                    $this->fail++;
                    echo "FAIL: $name\n";
                }
            } catch (Exception $e) {
                $this->fail++;
                echo "FAIL: $name - Exception: " . $e->getMessage() . "\n";
            }
        }
        echo "\nTests Passed: {$this->pass}, Failed: {$this->fail}\n";
        return $this->fail === 0;
    }
}

function assertTrue($condition, $message = "Assertion failed") {
    if (!$condition) {
        throw new Exception($message);
    }
}

function assertEquals($expected, $actual, $message = "Values are not equal") {
    if ($expected !== $actual) {
        throw new Exception("$message - Expected: $expected, Got: $actual");
    }
}
?>