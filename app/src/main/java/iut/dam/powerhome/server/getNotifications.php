<?php
header('Content-Type: application/json; charset=utf-8');

$host = 'localhost'; $db = 'powerhome_db'; $user = 'root'; $pass = '';
try {
    $pdo = new PDO("mysql:host=$host;dbname=$db;charset=utf8mb4", $user, $pass);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
} catch (Exception $e) { echo json_encode(['status'=>'error','error'=>'DB']); exit; }

$userId = intval($_GET['user_id'] ?? 0);
if ($userId <= 0) { echo json_encode(['status'=>'error','error'=>'user_id manquant']); exit; }

$nStmt = $pdo->prepare("SELECT id, message, is_read, created_at FROM notification WHERE user_id=? ORDER BY created_at DESC LIMIT 50");
$nStmt->execute([$userId]);
$notifications = $nStmt->fetchAll(PDO::FETCH_ASSOC);

$pdo->prepare("UPDATE notification SET is_read=1 WHERE user_id=?")->execute([$userId]);

$rStmt = $pdo->prepare("
    SELECT r.id, r.requester_id, r.habitat_id, r.status, r.created_at,
           u.firstname, u.lastname, h.floor, h.area
    FROM habitat_request r
    JOIN user u ON u.id = r.requester_id
    JOIN habitat h ON h.id = r.habitat_id
    WHERE r.owner_id = ? AND r.status = 'pending'
    ORDER BY r.created_at DESC
");
$rStmt->execute([$userId]);
$requests = $rStmt->fetchAll(PDO::FETCH_ASSOC);

$unreadStmt = $pdo->prepare("SELECT COUNT(*) FROM notification WHERE user_id=? AND is_read=0");
$unread = count(array_filter($notifications, fn($n) => $n['is_read'] == 0));

echo json_encode([
    'status'        => 'success',
    'unread_count'  => $unread,
    'notifications' => $notifications,
    'requests'      => $requests
], JSON_UNESCAPED_UNICODE);
?>