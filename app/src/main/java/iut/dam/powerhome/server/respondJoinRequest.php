<?php
// respondJoinRequest.php
// POST: owner_id, request_id, response ('accepted' ou 'refused')
header('Content-Type: application/json; charset=utf-8');

$host = 'localhost'; $db = 'powerhome_db'; $user = 'root'; $pass = '';
try {
    $pdo = new PDO("mysql:host=$host;dbname=$db;charset=utf8mb4", $user, $pass);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
} catch (Exception $e) { echo json_encode(['status'=>'error','error'=>'DB']); exit; }

$ownerId    = intval($_POST['owner_id']   ?? 0);
$requestId  = intval($_POST['request_id'] ?? 0);
$response   = trim($_POST['response']     ?? '');

if (!in_array($response, ['accepted','refused']) || $ownerId <= 0 || $requestId <= 0) {
    echo json_encode(['status'=>'error','error'=>'Paramètres invalides']); exit;
}

// Récupérer la demande
$stmt = $pdo->prepare("SELECT * FROM habitat_request WHERE id=? AND owner_id=? AND status='pending'");
$stmt->execute([$requestId, $ownerId]);
$req = $stmt->fetch(PDO::FETCH_ASSOC);
if (!$req) { echo json_encode(['status'=>'error','error'=>'Demande introuvable']); exit; }

$requesterId = (int)$req['requester_id'];
$habitatId   = (int)$req['habitat_id'];

// Mettre à jour le statut
$pdo->prepare("UPDATE habitat_request SET status=? WHERE id=?")->execute([$response, $requestId]);

// Récupérer le nom du propriétaire
$ownerRow = $pdo->prepare("SELECT firstname, lastname FROM user WHERE id=?");
$ownerRow->execute([$ownerId]);
$owner = $ownerRow->fetch(PDO::FETCH_ASSOC);
$ownerName = trim($owner['firstname'] . ' ' . $owner['lastname']);

if ($response === 'accepted') {
    // Lier le demandeur à l'habitat dans habitat_resident (co-résident)
    $pdo->prepare("INSERT IGNORE INTO habitat_resident (habitat_id, user_id, is_owner) VALUES (?,?,0)")
        ->execute([$habitatId, $requesterId]);

    // Optionnel : mettre à jour habitat.user_id si tu veux garder la compatibilité
    // (On laisse l'ancien user_id intact, on utilise habitat_resident pour le multi)

    // Notification au demandeur : accepté
    $msg = $ownerName . " a accepté votre demande. Vous faites maintenant partie de l'habitat !";
    $pdo->prepare("INSERT INTO notification (user_id, message) VALUES (?,?)")->execute([$requesterId, $msg]);

    // Notification au propriétaire : confirmation
    $requesterRow = $pdo->prepare("SELECT firstname, lastname FROM user WHERE id=?");
    $requesterRow->execute([$requesterId]);
    $req2 = $requesterRow->fetch(PDO::FETCH_ASSOC);
    $requesterName = trim($req2['firstname'] . ' ' . $req2['lastname']);
    $msgOwner = $requesterName . " a rejoint votre habitat. 🏠";
    $pdo->prepare("INSERT INTO notification (user_id, message) VALUES (?,?)")->execute([$ownerId, $msgOwner]);

} else {
    // Notification au demandeur : refusé
    $msg = $ownerName . " n'a pas accepté votre demande pour le moment.";
    $pdo->prepare("INSERT INTO notification (user_id, message) VALUES (?,?)")->execute([$requesterId, $msg]);
}

echo json_encode(['status'=>'success','response'=>$response], JSON_UNESCAPED_UNICODE);
?>