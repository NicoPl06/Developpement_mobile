<?php
$host = "localhost";
$dbname = "powerhome_db";
$user = "root";
$pass = "";

try {
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8", $user, $pass);

    if (isset($_POST['user_id'], $_POST['name'], $_POST['wattage'])) {
        $userId = $_POST['user_id'];
        $name = $_POST['name'];
        $wattage = $_POST['wattage'];
        $reference = $_POST['reference'] ?? "";

        // 1. On récupère l'habitat_id lié à cet utilisateur
        $sqlHabitat = "SELECT id FROM habitat WHERE user_id = ?";
        $stmtHab = $pdo->prepare($sqlHabitat);
        $stmtHab->execute([$userId]);
        $habitat = $stmtHab->fetch();

        if ($habitat) {
            $habitatId = $habitat['id'];

            // 2. Insertion dans la table appliance
            $sql = "INSERT INTO appliance (name, reference, wattage, habitat_id) VALUES (?, ?, ?, ?)";
            $stmt = $pdo->prepare($sql);

            if ($stmt->execute([$name, $reference, $wattage, $habitatId])) {
                echo json_encode(["status" => "success"]);
            }
        } else {
            echo json_encode(["status" => "error", "message" => "Aucun habitat trouvé pour cet utilisateur"]);
        }
    }
} catch (PDOException $e) {
    echo json_encode(["status" => "error", "message" => $e->getMessage()]);
}
?>