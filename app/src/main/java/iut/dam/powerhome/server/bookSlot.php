<?php
header('Content-Type: application/json; charset=utf-8');

$host = 'localhost'; $db = 'powerhome_db'; $user = 'root'; $pass = '';
try {
    $pdo = new PDO("mysql:host=$host;dbname=$db;charset=utf8mb4", $user, $pass);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
} catch (Exception $e) { echo json_encode(['status'=>'error','error'=>'DB']); exit; }

$userId      = intval($_POST['user_id']      ?? 0);
$applianceId = intval($_POST['appliance_id'] ?? 0);
$timeslotId  = intval($_POST['timeslot_id']  ?? 0);

if ($userId <= 0 || $applianceId <= 0 || $timeslotId <= 0) {
    echo json_encode(['status'=>'error','error'=>'Paramètres invalides']); exit;
}

$checkApp = $pdo->prepare("
    SELECT a.id FROM appliance a
    JOIN habitat h ON h.id = a.habitat_id
    LEFT JOIN habitat_resident hr ON hr.habitat_id = h.id AND hr.user_id = ?
    WHERE a.id = ? AND (h.user_id = ? OR hr.user_id = ?)
");
$checkApp->execute([$userId, $applianceId, $userId, $userId]);
if (!$checkApp->fetch()) {
    echo json_encode(['status'=>'error','error'=>'Appareil introuvable pour cet utilisateur']); exit;
}

$slotStmt = $pdo->prepare("SELECT id, maxWattage, begin_time FROM timeslot WHERE id = ?");
$slotStmt->execute([$timeslotId]);
$slot = $slotStmt->fetch(PDO::FETCH_ASSOC);
if (!$slot) { echo json_encode(['status'=>'error','error'=>'Créneau introuvable']); exit; }

$dup = $pdo->prepare("SELECT 1 FROM booking WHERE appliance_id=? AND timeslot_id=?");
$dup->execute([$applianceId, $timeslotId]);
if ($dup->fetch()) { echo json_encode(['status'=>'error','error'=>'Déjà réservé sur ce créneau']); exit; }

$loadStmt = $pdo->prepare("
    SELECT COALESCE(SUM(a.wattage),0) AS bookedWattage
    FROM booking b JOIN appliance a ON a.id=b.appliance_id
    WHERE b.timeslot_id=?
");
$loadStmt->execute([$timeslotId]);
$load = $loadStmt->fetch(PDO::FETCH_ASSOC);

$maxW   = (int)$slot['maxWattage'];
$booked = (int)$load['bookedWattage'];
$pct    = ($maxW > 0) ? ($booked / $maxW) * 100 : 0;

if ($pct <= 30) {
    $cost          = 3;
    $ecocoinsDelta = 10;
    $color         = 'green';
} elseif ($pct <= 70) {
    $cost          = 8;
    $ecocoinsDelta = 0;
    $color         = 'orange';
} else {
    $cost          = 15;
    $ecocoinsDelta = -10;
    $color         = 'red';
}

$balStmt = $pdo->prepare("SELECT ecocoins FROM user WHERE id=?");
$balStmt->execute([$userId]);
$bal = $balStmt->fetch(PDO::FETCH_ASSOC);
$currentBalance = (int)$bal['ecocoins'];

if ($currentBalance < $cost) {
    echo json_encode([
        'status'  => 'error',
        'error'   => 'Solde insuffisant. Il vous faut ' . $cost . ' éco-coins pour ce créneau (solde : ' . $currentBalance . ').',
        'balance' => $currentBalance,
        'cost'    => $cost
    ]); exit;
}

$net = $ecocoinsDelta - $cost;
$pdo->prepare("
    INSERT INTO booking (appliance_id, timeslot_id, user_id, ecocoins_delta, ecocoins_cost, booked_date)
    VALUES (?,?,?,?,?,DATE(?))
")->execute([$applianceId, $timeslotId, $userId, $ecocoinsDelta, $cost, $slot['begin_time']]);

$pdo->prepare("UPDATE user SET ecocoins = ecocoins - ? + ? WHERE id=?")
    ->execute([$cost, $ecocoinsDelta, $userId]);

$balStmt->execute([$userId]);
$newBal = (int)$balStmt->fetch(PDO::FETCH_ASSOC)['ecocoins'];

echo json_encode([
    'status'         => 'success',
    'color'          => $color,
    'slot_percent'   => round($pct),
    'cost'           => $cost,
    'ecocoins_delta' => $ecocoinsDelta,
    'net_change'     => $net,
    'new_balance'    => $newBal
], JSON_UNESCAPED_UNICODE);
?>