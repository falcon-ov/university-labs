<!DOCTYPE html>
<html lang="en">

<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<link rel="stylesheet" href="styles.css">
	<title>Document</title>
</head>

<body>
	<?php
	// print ("Today is " . date("l") . "<br><br>");

	$dayOfWeekToday = date("l");

	$dataBase = [
		[
			'fullname' => 'John Styles',
			'workDays' => ['Monday', 'Wednesday', 'Friday'],
			'workTime' => '8:00-12:00'
		],
		[
			'fullname' => 'Jane Doe',
			'workDays' => ['Tuesday', 'Thursday', 'Saturday'],
			'workTime' => '12:00-16:00'
		]
	];

	?>

	<table>
		<tr>
			<th>№</th>
			<th>Фамилия Имя</th>
			<th>График работы</th>
		</tr>

		<?php foreach ($dataBase as $workerId => $workerInfo) {
			if (in_array($dayOfWeekToday, $workerInfo['workDays'])) {
				$workTime = $workerInfo['workTime'];
			} else {
				$workTime = "Нерабочий день";
			}
			$workerId += 1;
		?>
			<tr>
				<td><?php echo $workerId; ?></td>
				<td><?php echo $workerInfo['fullname']; ?></td>
				<td><?php echo $workTime; ?></td>
			</tr>
		<?php } ?>

	</table>
</body>

</html>
