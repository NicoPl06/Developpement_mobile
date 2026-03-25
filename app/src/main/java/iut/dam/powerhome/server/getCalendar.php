<?php
// getCalendar.php
// Retourne les créneaux des 7 prochains jours avec leur taux de charge (%)
// GET params: (aucun obligatoire)
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

// Récupère tous les créneaux à partir d'aujourd'hui (7 jours)
$sql = "
    SELECT
        t.id,
        t.begin_time,
        t.end_time,
        t.maxWattage,
        COALESCE(SUM(a.wattage), 0) AS bookedWattage,
        COUNT(b.appliance_id)       AS bookingCount
    FROM timeslot t
    LEFT JOIN booking b  ON b.timeslot_id = t.id
    LEFT JOIN appliance a ON a.id = b.appliance_id
    WHERE t.begin_time >= CURDATE()
      AND t.begin_time < DATE_ADD(CURDATE(), INTERVAL 7 DAY)
    GROUP BY t.id
    ORDER BY t.begin_time
";

$stmt = $pdo->query($sql);
$rows = $stmt->fetchAll(PDO::FETCH_ASSOC);

$result = [];
foreach ($rows as $r) {
    $maxW   = (int)$r['maxWattage'];
    $booked = (int)$r['bookedWattage'];
    $pct    = ($maxW > 0) ? round(($booked / $maxW) * 100) : 0;
    $pct    = min($pct, 100);

    if ($pct <= 30)       $color = 'green';
    elseif ($pct <= 70)   $color = 'orange';
    else                  $color = 'red';

    $result[] = [
        'id'           => (int)$r['id'],
        'begin_time'   => $r['begin_time'],
        'end_time'     => $r['end_time'],
        'maxWattage'   => $maxW,
        'bookedWattage'=> $booked,
        'percent'      => $pct,
        'color'        => $color,
        'bookingCount' => (int)$r['bookingCount']
    ];
}

echo json_encode(['status' => 'success', 'slots' => $result], JSON_UNESCAPED_UNICODE);
?>