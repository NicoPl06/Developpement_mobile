<?php
// bookSlot.php
// POST: user_id, appliance_id, timeslot_id
// Réserve un créneau, calcule le bonus/malus et met à jour les éco-coins du résident.
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

// Vérifier que l'appareil appartient bien à l'habitat de l'utilisateur
$checkApp = $pdo->prepare("
    SELECT a.id FROM appliance a
    JOIN habitat h ON h.id = a.habitat_id
    WHERE a.id = ? AND h.user_id = ?
");
$checkApp->execute([$applianceId, $userId]);
if (!$checkApp->fetch()) {
    echo json_encode(['status' => 'error', 'error' => 'Appareil introuvable pour cet utilisateur']);
    exit;
}

// Vérifier que le créneau existe
$slotStmt = $pdo->prepare("SELECT id, maxWattage, begin_time FROM timeslot WHERE id = ?");
$slotStmt->execute([$timeslotId]);
$slot = $slotStmt->fetch(PDO::FETCH_ASSOC);
if (!$slot) {
    echo json_encode(['status' => 'error', 'error' => 'Créneau introuvable']);
    exit;
}

// Vérifier qu'il n'est pas déjà réservé par cet appareil
$dupStmt = $pdo->prepare("SELECT 1 FROM booking WHERE appliance_id = ? AND timeslot_id = ?");
$dupStmt->execute([$applianceId, $timeslotId]);
if ($dupStmt->fetch()) {
    echo json_encode(['status' => 'error', 'error' => 'Cet appareil est déjà réservé sur ce créneau']);
    exit;
}

// Calculer la charge actuelle du créneau (AVANT la réservation)
$loadStmt = $pdo->prepare("
    SELECT COALESCE(SUM(a.wattage), 0) AS bookedWattage
    FROM booking b
    JOIN appliance a ON a.id = b.appliance_id
    WHERE b.timeslot_id = ?
");
$loadStmt->execute([$timeslotId]);
$load = $loadStmt->fetch(PDO::FETCH_ASSOC);

$maxW       = (int)$slot['maxWattage'];
$booked     = (int)$load['bookedWattage'];
$pct        = ($maxW > 0) ? ($booked / $maxW) * 100 : 0;

// Règle bonus/malus
if ($pct <= 30) {
    $ecocoinsDelta = 10;   // Créneau vert  → +10 éco-coins
} elseif ($pct <= 70) {
    $ecocoinsDelta = 0;    // Créneau orange → neutre
} else {
    $ecocoinsDelta = -10;  // Créneau rouge  → -10 éco-coins
}

// Insérer la réservation
$insertStmt = $pdo->prepare("
    INSERT INTO booking (appliance_id, timeslot_id, user_id, ecocoins_delta, booked_date)
    VALUES (?, ?, ?, ?, DATE(?))
");
$insertStmt->execute([$applianceId, $timeslotId, $userId, $ecocoinsDelta, $slot['begin_time']]);

// Mettre à jour le solde éco-coins du résident
$updateStmt = $pdo->prepare("UPDATE user SET ecocoins = ecocoins + ? WHERE id = ?");
$updateStmt->execute([$ecocoinsDelta, $userId]);

// Récupérer le nouveau solde
$balanceStmt = $pdo->prepare("SELECT ecocoins FROM user WHERE id = ?");
$balanceStmt->execute([$userId]);
$balance = $balanceStmt->fetch(PDO::FETCH_ASSOC);

echo json_encode([
    'status'        => 'success',
    'ecocoins_delta'=> $ecocoinsDelta,
    'new_balance'   => (int)$balance['ecocoins'],
    'slot_percent'  => round($pct)
], JSON_UNESCAPED_UNICODE);
?>