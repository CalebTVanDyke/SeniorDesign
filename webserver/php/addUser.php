<?php
include ('UserManager.php');
$manager = new UserManager();
$user = $_GET ['user'];
$pass = $_GET ['pass'];
$email = $_GET ['email'];
$type = $_GET ['phone'];
var_dump($user)
// echo json_encode($manager->addUser($user, $pass, $email, $phone));
?>