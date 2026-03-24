<?php
header('Content-Type: application/json; charset=utf-8');

$db_host = "localhost";
$db_uid  = "root";
$db_pass = "";
$db_name = "powerhome_db";

try {
    $pdo = new PDO("mysql:host=$db_host;dbname=$db_name;charset=utf8mb4", $db_uid, $db_pass);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
} catch (Exception $e) {
    echo json_encode(['status' => 'error', 'error' => 'Connexion DB impossible']);
    exit;
}

$userId    = intval($_POST['user_id']    ?? 0);
$action    = trim($_POST['action']       ?? '');   // "create" ou "join"
$habitatId = intval($_POST['habitat_id'] ?? 0);    // utilisé si action = "join"
$area      = floatval($_POST['area']     ?? 0);    // utilisé si action = "create"
$floor     = intval($_POST['floor']      ?? 0);    // utilisé si action = "create"

if ($userId <= 0) {
    echo json_encode(['status' => 'error', 'error' => 'Utilisateur invalide']);
    exit;
}

if ($action === 'create') {
    // Créer un nouvel habitat et le lier à l'utilisateur
    $stmt = $pdo->prepare("INSERT INTO habitat (area, floor, user_id) VALUES (?, ?, ?)");
    $stmt->execute([$area, $floor, $userId]);
    $newHabitatId = $pdo->lastInsertId();

    echo json_encode(['status' => 'success', 'habitat_id' => $newHabitatId]);

} elseif ($action === 'join') {
    // Rejoindre = copier les dimensions de l'habitat choisi
    // et créer une nouvelle ligne habitat pour cet utilisateur
    // (contrainte UNIQUE user_id interdit de partager une ligne)
    if ($habitatId <= 0) {
        echo json_encode(['status' => 'error', 'error' => 'Habitat invalide']);
        exit;
    }

    $check = $pdo->prepare("SELECT area, floor FROM habitat WHERE id = ?");
    $check->execute([$habitatId]);
    $source = $check->fetch(PDO::FETCH_ASSOC);

    if (!$source) {
        echo json_encode(['status' => 'error', 'error' => 'Habitat introuvable']);
        exit;
    }

    $stmt = $pdo->prepare("INSERT INTO habitat (area, floor, user_id) VALUES (?, ?, ?)");
    $stmt->execute([$source['area'], $source['floor'], $userId]);
    $newHabitatId = $pdo->lastInsertId();

    echo json_encode(['status' => 'success', 'habitat_id' => $newHabitatId]);

} else {
    echo json_encode(['status' => 'error', 'error' => 'Action inconnue']);
}
?>