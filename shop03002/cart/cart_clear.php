<?php
  // Cartオブジェクトを生成し、全商品削除メソッドを呼び出す
  require_once __DIR__ . '/../classes/cart.php';
  $cart = new Cart();
  session_start();

  $cart->clearCart($_SESSION['userId']);

  // cart_list.phpを読み込み、カート内の商品を画面に表示する
  require_once __DIR__ . '/cart_list.php';
