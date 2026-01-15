<!DOCTYPE html>
<html lang="ru">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Галерея изображений</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 0;
            background-color: #f4f4f4;
        }

        header {
            background-color: #333;
            color: white;
            text-align: center;
            padding: 20px;
        }

        nav {
            background-color: #444;
            padding: 10px;
        }

        nav a {
            color: white;
            text-decoration: none;
            margin: 0 15px;
        }

        nav a:hover {
            text-decoration: underline;
        }

        .gallery {
            display: flex;
            flex-wrap: wrap;
            justify-content: center;
            padding: 20px;
        }

        .gallery img {
            max-width: 200px;
            height: auto;
            margin: 10px;
            border: 2px solid #ddd;
            border-radius: 5px;
        }

        footer {
            background-color: #333;
            color: white;
            text-align: center;
            padding: 10px;
            position: fixed;
            bottom: 0;
            width: 100%;
        }
    </style>
</head>

<body>
    <header>
        <h1>Галерея изображений</h1>
    </header>

    <nav>
        <a>Главная</a>
        <a>Галерея</a>
        <a>О нас</a>
        <a>Контакты</a>
    </nav>

    <div class="gallery">
        <?php
        /**
         * Отображает галерею изображений из указанной директории
         * Сканирует директорию image/ и выводит все изображения с разрешенными расширениями
         */

        // Задаем путь к папке с изображениями
        $dir = 'image/';
        
        /**
         * @var array $files Массив файлов в директории
         */
        $files = array_diff(scandir($dir), [".", ".."]);
        $files = array_values($files);

        if ($files === false) {
            echo "<p>Ошибка при сканировании директории</p>";
        } else {
            /**
             * @var array $allowed_extensions Допустимые расширения файлов изображений
             */
            $allowed_extensions = ['jpg', 'jpeg'];

            for ($i = 0; $i < count($files); $i++) {
                $extension = strtolower(pathinfo($files[$i], PATHINFO_EXTENSION));
                if (in_array($extension, $allowed_extensions)) {
                    $path = $dir . $files[$i];
                    echo "<img src='$path'>";
                }
            }
        }
        ?>
    </div>

    <footer>
        <p>Галерея изображений. Все права защищены.</p>
    </footer>
</body>

</html>