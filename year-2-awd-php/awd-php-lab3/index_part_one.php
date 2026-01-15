<?php

// Задание 1.1. Подготовка среды
declare(strict_types=1);

/**
 * Глобальный массив транзакций
 * @var array $transactions
 */
$transactions = [
    [
        "id" => 1,
        "date" => "2019-01-01",
        "amount" => 100.00,
        "description" => "Payment for groceries",
        "merchant" => "SuperMart",
    ],
    [
        "id" => 2,
        "date" => "2020-02-15",
        "amount" => 75.50,
        "description" => "Dinner with friends",
        "merchant" => "Local Restaurant",
    ],
];

/**
 * Вычисляет общую сумму всех транзакций
 *
 * @param array $transactions Массив транзакций, где каждая транзакция - ассоциативный массив
 * @return float Общая сумма транзакций
 */
function calculateTotalAmount(array $transactions): float
{
    return array_sum(array_column($transactions, 'amount'));
}

/**
 * Ищет транзакции по частичному совпадению описания
 *
 * @param array $transactions Массив транзакций для поиска
 * @param string $descriptionPart Часть описания для поиска
 * @return array Массив найденных транзакций
 */
function findTransactionByDescription(array $transactions, string $descriptionPart): array
{
    return array_filter($transactions, function ($transaction) use ($descriptionPart) {
        return $transaction['description'] === $descriptionPart;
    });
}

/**
 * Находит транзакцию по идентификатору
 *
 * @param array $transactions Массив транзакций для поиска
 * @param int $id Идентификатор искомой транзакции
 * @return array Массив найденных транзакций (обычно одна транзакция)
 */
function findTransactionById(array $transactions, int $id): array
{
    return array_filter($transactions, function ($transaction) use ($id) {
        return $transaction['id'] === $id;
    });
}

/**
 * Вычисляет количество дней, прошедших с даты транзакции до текущей даты
 *
 * @param string $date Дата транзакции в формате "YYYY-MM-DD"
 * @return int Количество прошедших дней
 */
function daysSinceTransaction(string $date): int
{
    $transactionDate = new DateTime($date);
    $today = new DateTime();
    $interval = $transactionDate->diff($today);
    $daysPassed = $interval->days;
    return $daysPassed;
}

/**
 * Добавляет новую транзакцию в глобальный массив транзакций
 *
 * @param int $id Уникальный идентификатор транзакции
 * @param string $date Дата транзакции в формате "YYYY-MM-DD"
 * @param float $amount Сумма транзакции
 * @param string $description Описание транзакции
 * @param string $merchant Название магазина/продавца
 * @return void
 */
function addTransaction(int $id, string $date, float $amount, string $description, string $merchant): void
{
    $transaction = [
        "id" => $id,
        "date" => $date,
        "amount" => $amount,
        "description" => $description,
        "merchant" => $merchant,
    ];

    array_push($GLOBALS['transactions'], $transaction);
}

// Задание 1.5. Сортировка транзакций

/** Сортировка по дате (по возрастанию) */
usort($transactions, function($a, $b) {
    return strtotime($a['date']) - strtotime($b['date']);
});

/** Сортировка по сумме (по убыванию) */
usort($transactions, function($a, $b) {
    return $b['amount'] - $a['amount'];
});

?>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
    <style>
        table {
            border: 1px solid black;
            border-collapse: collapse;
            width: 100%;
        }
        th, td {
            border: 1px solid black;
            padding: 8px;
            text-align: left;
        }
        th {
            background-color: #f2f2f2;
        }
    </style>
</head>
<body>
    <!-- Задание 1.3. Вывод списка транзакций -->
    <table>
        <thead>
            <tr>
                <th>ID</th>
                <th>Date</th>
                <th>Amount</th>
                <th>Description</th>
                <th>Merchant</th>
            </tr>
        </thead>
        <tbody>
            <?php foreach ($transactions as $transaction) { ?>
            <tr>
                <td><?php echo $transaction['id']; ?></td>
                <td><?php echo $transaction['date']; ?></td>
                <td><?php echo number_format($transaction['amount']); ?></td>
                <td><?php echo $transaction['description']; ?></td>
                <td><?php echo $transaction['merchant']; ?></td>
            </tr>
            <?php } ?>
        </tbody>
    </table>
</body>
</html>
