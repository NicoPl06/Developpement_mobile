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
    echo json_encode(['status' => 'error', 'error' => 'DB error']);
    exit;
}

$userId      = intval($_POST['user_id']      ?? 0);
$applianceId = intval($_POST['appliance_id'] ?? 0);
$timeslotId  = intval($_POST['timeslot_id']  ?? 0);

if ($userId <= 0 || $applianceId <= 0 || $timeslotId <= 0) {
    echo json_encode(['status' => 'error', 'error' => 'Paramètres invalides']);
    exit;
}


$checkStmt = $pdo->prepare("
    SELECT ecocoins_delta
    FROM booking
    WHERE appliance_id = ? AND timeslot_id = ? AND user_id = ?
");
$checkStmt->execute([$applianceId, $timeslotId, $userId]);
$booking = $checkStmt->fetch(PDO::FETCH_ASSOC);

if (!$booking) {
    echo json_encode(['status' => 'error', 'error' => 'Réservation introuvable']);
    exit;
}

$ecocoinsDelta = (int)$booking['ecocoins_delta'];


$deleteStmt = $pdo->prepare("
    DELETE FROM booking
    WHERE appliance_id = ? AND timeslot_id = ? AND user_id = ?
");
$deleteStmt->execute([$applianceId, $timeslotId, $userId]);


$updateStmt = $pdo->prepare("UPDATE user SET ecocoins = ecocoins - ? WHERE id = ?");
$updateStmt->execute([$ecocoinsDelta, $userId]);

$balanceStmt = $pdo->prepare("SELECT ecocoins FROM user WHERE id = ?");
$balanceStmt->execute([$userId]);
$balance = $balanceStmt->fetch(PDO::FETCH_ASSOC);

echo json_encode([
    'status'          => 'success',
    'ecocoins_removed'=> $ecocoinsDelta,
    'new_balance'     => (int)$balance['ecocoins']
], JSON_UNESCAPED_UNICODE);
?>