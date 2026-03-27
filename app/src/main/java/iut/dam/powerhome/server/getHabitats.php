<?php
header('Content-Type: application/json; charset=utf-8');

$host = 'localhost'; $db = 'powerhome_db'; $user = 'root'; $pass = '';
try {
    $pdo = new PDO("mysql:host=$host;dbname=$db;charset=utf8mb4", $user, $pass);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
} catch (Exception $e) { die(json_encode(["error" => $e->getMessage()])); }

$sql = "SELECT h.id, h.area, h.floor FROM habitat h ORDER BY h.id";
$stmt = $pdo->query($sql);
$habitats = $stmt->fetchAll(PDO::FETCH_ASSOC);

foreach ($habitats as &$h) {
    $resStmt = $pdo->prepare("
        SELECT u.id, u.firstname, u.lastname, hr.is_owner
        FROM habitat_resident hr
        JOIN user u ON u.id = hr.user_id
        WHERE hr.habitat_id = ?
        ORDER BY hr.is_owner DESC, hr.joined_at ASC
    ");
    $resStmt->execute([$h['id']]);
    $residents = $resStmt->fetchAll(PDO::FETCH_ASSOC);

    if (empty($residents)) {
        $fallback = $pdo->prepare("SELECT id, firstname, lastname FROM user WHERE id = (SELECT user_id FROM habitat WHERE id = ?)");
        $fallback->execute([$h['id']]);
        $u = $fallback->fetch(PDO::FETCH_ASSOC);
        if ($u) $residents = [['id'=>$u['id'],'firstname'=>$u['firstname'],'lastname'=>$u['lastname'],'is_owner'=>1]];
    }

    $h['residents'] = $residents;

    $ownerName = '';
    $coNames   = [];
    foreach ($residents as $r) {
        if ((int)$r['is_owner'] === 1) $ownerName = trim($r['firstname'] . ' ' . $r['lastname']);
        else $coNames[] = $r['firstname'];
    }
    $h['display_name'] = $ownerName;
    $h['co_names']     = $coNames;

    $appStmt = $pdo->prepare("SELECT id, name AS Name, reference, wattage FROM appliance WHERE habitat_id = ?");
    $appStmt->execute([$h['id']]);
    $h['appliances'] = $appStmt->fetchAll(PDO::FETCH_ASSOC);
}

echo json_encode($habitats, JSON_UNESCAPED_UNICODE);
?>