<?php
header('Content-Type: application/json');

$host   = "localhost";
$dbname = "powerhome_db";
$user   = "root";
$pass   = "";

try {
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8", $user, $pass);

    if (!isset($_GET['id'])) {
        echo json_encode(["error" => "ID manquant"]);
        exit;
    }

    $userId = intval($_GET['id']);

    // Infos utilisateur
    $stmt = $pdo->prepare("SELECT firstname, lastname, email FROM user WHERE id = ?");
    $stmt->execute([$userId]);
    $userData = $stmt->fetch(PDO::FETCH_ASSOC);

    if (!$userData) {
        echo json_encode(["error" => "Utilisateur non trouvé"]);
        exit;
    }

    // Chercher l'habitat via habitat_resident (proprio ET co-résident)
    $habitatStmt = $pdo->prepare("
        SELECT h.id AS habitat_id, h.area, h.floor
        FROM habitat_resident hr
        JOIN habitat h ON h.id = hr.habitat_id
        WHERE hr.user_id = ?
        LIMIT 1
    ");
    $habitatStmt->execute([$userId]);
    $habitatRow = $habitatStmt->fetch(PDO::FETCH_ASSOC);

    // Fallback : ancien système via habitat.user_id
    if (!$habitatRow) {
        $fallback = $pdo->prepare("SELECT id AS habitat_id, area, floor FROM habitat WHERE user_id = ?");
        $fallback->execute([$userId]);
        $habitatRow = $fallback->fetch(PDO::FETCH_ASSOC);
    }

    $appliances = [];
    $area       = 0;
    $floor      = 0;

    if ($habitatRow) {
        $area  = (float)$habitatRow['area'];
        $floor = (int)$habitatRow['floor'];

        $stmtApp = $pdo->prepare("SELECT id, name, reference, wattage FROM appliance WHERE habitat_id = ?");
        $stmtApp->execute([$habitatRow['habitat_id']]);
        $appliances = $stmtApp->fetchAll(PDO::FETCH_ASSOC);
    }

    echo json_encode([
        "firstname"  => $userData['firstname'],
        "lastname"   => $userData['lastname'],
        "email"      => $userData['email'],
        "area"       => $area,
        "floor"      => $floor,
        "appliances" => $appliances
    ]);

} catch (PDOException $e) {
    echo json_encode(["error" => $e->getMessage()]);
}
?>