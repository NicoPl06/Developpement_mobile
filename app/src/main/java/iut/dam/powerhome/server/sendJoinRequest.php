<?php
// sendJoinRequest.php
// POST: requester_id, habitat_id
// Envoie une demande de rejoindre + crée une notification pour le propriétaire
header('Content-Type: application/json; charset=utf-8');

$host = 'localhost'; $db = 'powerhome_db'; $user = 'root'; $pass = '';
try {
    $pdo = new PDO("mysql:host=$host;dbname=$db;charset=utf8mb4", $user, $pass);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
} catch (Exception $e) { echo json_encode(['status'=>'error','error'=>'DB']); exit; }

$requesterId = intval($_POST['requester_id'] ?? 0);
$habitatId   = intval($_POST['habitat_id']   ?? 0);

if ($requesterId <= 0 || $habitatId <= 0) {
    echo json_encode(['status'=>'error','error'=>'Paramètres invalides']); exit;
}

// Récupérer le propriétaire de l'habitat et le nom du demandeur
$stmt = $pdo->prepare("
    SELECT h.user_id AS owner_id, u.firstname, u.lastname
    FROM habitat h
    JOIN user u ON u.id = ?
    WHERE h.id = ?
");
$stmt->execute([$requesterId, $habitatId]);
$row = $stmt->fetch(PDO::FETCH_ASSOC);

if (!$row) { echo json_encode(['status'=>'error','error'=>'Habitat introuvable']); exit; }

$ownerId     = (int)$row['owner_id'];
$requesterName = trim($row['firstname'] . ' ' . $row['lastname']);

// Vérifier doublon
$dup = $pdo->prepare("SELECT id FROM habitat_request WHERE requester_id=? AND habitat_id=? AND status='pending'");
$dup->execute([$requesterId, $habitatId]);
if ($dup->fetch()) {
    echo json_encode(['status'=>'error','error'=>'Demande déjà en cours']); exit;
}

// Insérer la demande
$ins = $pdo->prepare("INSERT INTO habitat_request (requester_id, habitat_id, owner_id) VALUES (?,?,?)");
$ins->execute([$requesterId, $habitatId, $ownerId]);
$requestId = $pdo->lastInsertId();

// Créer une notification pour le propriétaire
$msg = $requesterName . " souhaite rejoindre votre habitat.";
$notif = $pdo->prepare("INSERT INTO notification (user_id, message) VALUES (?,?)");
$notif->execute([$ownerId, $msg]);

echo json_encode(['status'=>'success','request_id'=>(int)$requestId]);
?>