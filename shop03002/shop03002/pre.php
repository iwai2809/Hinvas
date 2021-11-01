<?php
    if(!isset($_SESSION)) {
    session_start();
    }

    $userId = isset($_SESSION['userId']) ? $_SESSION['userId'] : '';
    $userName = isset($_SESSION['userName']) ? $_SESSION['userName'] : '';

    if(empty($userId) || empty($userName)){
    if(isset($_COOKIE['userId']) && isset($_COOKIE['userName'])){
        $userId = $_COOKIE['userId'];
        $userName = $_COOKIE['userName'];
        } else {
            $userId = (string)mt_rand(10000000, 99999999);
            $userName = 'ゲスト';
        setcookie("userId", $userId, time( ) + 60 * 60 * 24 * 14, '/');
        setcookie("userName", $userName, time( ) + 60 * 60 * 24 * 14, '/');
        }


$_SESSION['userId'] = $userId;
$_SESSION['userName']=$userName;
}

$http_host = '//' . $_SERVER['SERVER_NAME'];
    $shopid = mb_substr($_SERVER['REQUEST_URI'], 1, 9);


    $index_php = $http_host . '/' . $shopid . '/index.php'; 
    $cart_list_php = $http_host . '/' . $shopid . '/cart/cart_list.php';
    $order_history_php = $http_host . '/' . $shopid . '/order/order_history.php';
    $login_php = $http_host . '/' . $shopid . '/user/login.php';
    $logout_php = $http_host . '/' . $shopid . '/user/logout.php';
    $signup_php = $http_host . '/' . $shopid . '/user/signup.php';

$shop_css = $http_host . '/' . $shopid . '/css/shop.css';