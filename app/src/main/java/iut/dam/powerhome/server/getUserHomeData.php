<?php
header('Content-Type: application/json');

$host = "localhost";
$dbname = "powerhome_db";
$user = "root";
$pass = "";

try {
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8", $user, $pass);

    if (!isset($_GET['id'])) {
        echo json_encode(["error" => "ID manquant"]);
        exit;
    }

    $userId = $_GET['id'];

    $stmt = $pdo->prepare("SELECT u.firstname, u.lastname, u.email, h.id as habitat_id, h.area, h.floor
                           FROM user u
                           LEFT JOIN habitat h ON u.id = h.user_id
                           WHERE u.id = ?");
    $stmt->execute([$userId]);
    $userData = $stmt->fetch(PDO::FETCH_ASSOC);

    if (!$userData) {
        echo json_encode(["error" => "Utilisateur non trouvé"]);
        exit;
    }

    $appliances = [];
    if ($userData['habitat_id']) {
        $stmtApp = $pdo->prepare("SELECT id, name, reference, wattage FROM appliance WHERE habitat_id = ?");
        $stmtApp->execute([$userData['habitat_id']]);
        $appliances = $stmtApp->fetchAll(PDO::FETCH_ASSOC);
    }

    $response = [
        "firstname" => $userData['firstname'],
        "lastname" => $userData['lastname'],
        "email" => $userData['email'],
        "area" => (float)$userData['area'],
        "floor" => (int)$userData['floor'],
        "appliances" => $appliances
    ];

    echo json_encode($response);

} catch (PDOException $e) {
    echo json_encode(["error" => $e->getMessage()]);
}
?>