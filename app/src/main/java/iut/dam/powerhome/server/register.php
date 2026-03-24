<?php
header('Content-Type: application/json; charset=utf-8');

$db_host = "localhost";
$db_uid  = "root";
$db_pass = "";
$db_name = "powerhome_db";

$db_con = mysqli_connect($db_host, $db_uid, $db_pass, $db_name);
if (!$db_con) {
    echo json_encode(['status' => 'error', 'error' => 'Connexion DB impossible']);
    exit;
}


$firstname = trim($_POST['firstname'] ?? '');
$lastname  = trim($_POST['lastname']  ?? '');
$email     = trim($_POST['email']     ?? '');
$password  = trim($_POST['password']  ?? '');
$phone     = trim($_POST['phone']     ?? '');


if (empty($firstname) || empty($lastname) || empty($email) || empty($password)) {
    echo json_encode(['status' => 'error', 'error' => 'Champs obligatoires manquants']);
    exit;
}

if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
    echo json_encode(['status' => 'error', 'error' => 'Email invalide']);
    exit;
}


$checkSql = "SELECT id FROM user WHERE email = ?";
$stmt = mysqli_prepare($db_con, $checkSql);
mysqli_stmt_bind_param($stmt, 's', $email);
mysqli_stmt_execute($stmt);
mysqli_stmt_store_result($stmt);
if (mysqli_stmt_num_rows($stmt) > 0) {
    echo json_encode(['status' => 'error', 'error' => 'Cet email est déjà utilisé']);
    mysqli_stmt_close($stmt);
    mysqli_close($db_con);
    exit;
}
mysqli_stmt_close($stmt);

$insertSql = "INSERT INTO user (firstname, lastname, email, password, phone) VALUES (?, ?, ?, ?, ?)";
$stmt = mysqli_prepare($db_con, $insertSql);
mysqli_stmt_bind_param($stmt, 'sssss', $firstname, $lastname, $email, $password, $phone);

if (!mysqli_stmt_execute($stmt)) {
    echo json_encode(['status' => 'error', 'error' => 'Erreur lors de la création du compte']);
    mysqli_stmt_close($stmt);
    mysqli_close($db_con);
    exit;
}

$newUserId = mysqli_insert_id($db_con);
mysqli_stmt_close($stmt);

$token   = md5(uniqid() . rand(10000, 99999));
$expire  = date('Y-m-d H:i:s', strtotime('+30 days'));
$updSql  = "UPDATE user SET token=?, expired_at=? WHERE id=?";
$stmt    = mysqli_prepare($db_con, $updSql);
mysqli_stmt_bind_param($stmt, 'ssi', $token, $expire, $newUserId);
mysqli_stmt_execute($stmt);
mysqli_stmt_close($stmt);

mysqli_close($db_con);

echo json_encode([
    'status' => 'success',
    'id'     => $newUserId,
    'token'  => $token
]);
?>