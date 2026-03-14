<?php
$db_host = "localhost";
$db_uid = "root";
$db_pass = "";
$db_name = "powerhome_db";
$db_con = mysqli_connect($db_host, $db_uid, $db_pass, $db_name);

$email = $_POST['email'];
$password = $_POST['password'];

$sql = "SELECT id, token, expired_at FROM user WHERE email='$email' AND password='$password'";
$result = mysqli_query($db_con, $sql);
$row = mysqli_fetch_assoc($result);

if ($row == null) {
    echo json_encode(['status' => 'error', 'error' => 'incorrect email or password !']);
} elseif ($row['token'] == null || strtotime($row['expired_at']) < time()) {
    $token = md5(uniqid() . rand(10000, 99999));
    $expire = date('Y-m-d H:i:s', strtotime('+30 days', time()));
    $updateSql = "UPDATE user SET token='$token', expired_at='$expire' WHERE email='$email'";
    mysqli_query($db_con, $updateSql);
    echo json_encode(array(
        "status" => "success",
        "token" => $token,
        "id" => $row['id'],
        "expired_at" => $expire
    ));
} else {
    echo json_encode(array(
        "status" => "success",
        "token" => $row['token'],
        "id" => $row['id'],
        "expired_at" => $row['expired_at']
    ));
}
mysqli_close($db_con);
?>