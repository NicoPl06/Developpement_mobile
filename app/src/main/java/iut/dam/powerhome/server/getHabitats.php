<?php
header('Content-Type: application/json; charset=utf-8');

$host = 'localhost';
$db   = 'powerhome_db';
$user = 'root';
$pass = '';

try {
    $pdo = new PDO("mysql:host=$host;dbname=$db;charset=utf8mb4", $user, $pass);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
} catch (Exception $e) {
    die(json_encode(["error" => $e->getMessage()]));
}

// Utilisation de LEFT JOIN pour forcer l'affichage même si l'user est mal lié
$sql = "SELECT h.id, h.area, h.floor, u.firstname, u.lastname
        FROM habitat h
        LEFT JOIN user u ON h.user_id = u.id";

$stmt = $pdo->query($sql);
$habitats = $stmt->fetchAll(PDO::FETCH_ASSOC);

foreach ($habitats as &$h) {
    $stmtApp = $pdo->prepare("SELECT id, name as Name, reference, wattage FROM appliance WHERE habitat_id = ?");
    $stmtApp->execute([$h['id']]);
    $h['appliances'] = $stmtApp->fetchAll(PDO::FETCH_ASSOC);
}

echo json_encode($habitats, JSON_UNESCAPED_UNICODE);