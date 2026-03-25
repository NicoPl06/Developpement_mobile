<?php
// getMyBookings.php
// GET param: user_id
// Retourne les réservations de l'utilisateur + son solde éco-coins
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

$userId = intval($_GET['user_id'] ?? 0);
if ($userId <= 0) {
    echo json_encode(['status' => 'error', 'error' => 'user_id manquant']);
    exit;
}

// Solde éco-coins
$balanceStmt = $pdo->prepare("SELECT ecocoins FROM user WHERE id = ?");
$balanceStmt->execute([$userId]);
$balance = $balanceStmt->fetch(PDO::FETCH_ASSOC);
$ecocoins = $balance ? (int)$balance['ecocoins'] : 0;

// Réservations
$sql = "
    SELECT
        b.appliance_id,
        b.timeslot_id,
        b.ecocoins_delta,
        b.bookedAt,
        a.name  AS appliance_name,
        a.wattage,
        t.begin_time,
        t.end_time
    FROM booking b
    JOIN appliance a ON a.id = b.appliance_id
    JOIN timeslot  t ON t.id = b.timeslot_id
    WHERE b.user_id = ?
    ORDER BY t.begin_time DESC
";
$stmt = $pdo->prepare($sql);
$stmt->execute([$userId]);
$bookings = $stmt->fetchAll(PDO::FETCH_ASSOC);

// Nettoyer les noms d'appareils (enlever le suffixe "(TYPE)")
foreach ($bookings as &$b) {
    $raw = $b['appliance_name'];
    $b['appliance_name'] = strpos($raw, ' (') !== false ? explode(' (', $raw)[0] : $raw;
    $b['ecocoins_delta'] = (int)$b['ecocoins_delta'];
    $b['wattage']        = (int)$b['wattage'];
}

echo json_encode([
    'status'   => 'success',
    'ecocoins' => $ecocoins,
    'bookings' => $bookings
], JSON_UNESCAPED_UNICODE);
?>