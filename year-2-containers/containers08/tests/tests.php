<?php
require_once __DIR__ . '/testframework.php';
require_once __DIR__ . '/../modules/database.php';
require_once __DIR__ . '/../modules/page.php';

$test = new TestFramework();

// Test Database connection
$test->addTest("Database Connection", function() {
    try {
        $db = new Database(":memory:");
        return true;
    } catch (Exception $e) {
        return false;
    }
});

// Test Database methods
$test->addTest("Database Count", function() {
    $db = new Database(":memory:");
    $db->Execute("CREATE TABLE page (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, content TEXT)");
    $db->Execute("INSERT INTO page (title, content) VALUES ('Test', 'Content')");
    return $db->Count("page") === 1;
});

$test->addTest("Database Create", function() {
    $db = new Database(":memory:");
    $db->Execute("CREATE TABLE page (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, content TEXT)");
    $id = $db->Create("page", ["title" => "Test", "content" => "Content"]);
    return $id > 0;
});

$test->addTest("Database Read", function() {
    $db = new Database(":memory:");
    $db->Execute("CREATE TABLE page (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, content TEXT)");
    $id = $db->Create("page", ["title" => "Test", "content" => "Content"]);
    $data = $db->Read("page", $id);
    return $data["title"] === "Test" && $data["content"] === "Content";
});

$test->addTest("Database Update", function() {
    $db = new Database(":memory:");
    $db->Execute("CREATE TABLE page (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, content TEXT)");
    $id = $db->Create("page", ["title" => "Test", "content" => "Content"]);
    $result = $db->Update("page", $id, ["title" => "Updated", "content" => "New Content"]);
    $data = $db->Read("page", $id);
    return $result && $data["title"] === "Updated" && $data["content"] === "New Content";
});

$test->addTest("Database Delete", function() {
    $db = new Database(":memory:");
    $db->Execute("CREATE TABLE page (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, content TEXT)");
    $id = $db->Create("page", ["title" => "Test", "content" => "Content"]);
    $result = $db->Delete("page", $id);
    $data = $db->Read("page", $id);
    return $result && empty($data);
});

// Test Page rendering
$test->addTest("Page Render", function() {
    $templatePath = __DIR__ . '/../templates/index.tpl';
    if (!file_exists($templatePath)) {
        throw new Exception("Template file not found: $templatePath");
    }
    $page = new Page($templatePath);
    $data = ["title" => "Test Page", "content" => "Test Content"];
    $output = $page->Render($data);
    $titleFound = strpos($output, $data["title"]) !== false;
    $contentFound = strpos($output, $data["content"]) !== false;
    if (!$titleFound || !$contentFound) {
        throw new Exception("Rendered output missing expected content. Title found: $titleFound, Content found: $contentFound, Output: $output");
    }
    return true;
});

$test->run();
?>