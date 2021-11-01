<?php
  // Orderオブジェクトを生成する
  require_once __DIR__ . '/../classes/order.php';
  $order = new Order();

  // 注文明細テーブルのデータを注文番号の降順で取得
  $orders = $order->getOrders();

  // 注文履歴テーブルのタイトル部を表示する関数を用意
  function echo_title(){
    echo '<table>';
    echo '<tr><th>&nbsp;</th><th>注文<br>番号</th><th>商品名</th><th>メーカー・著者<br>アーティスト</th><th>価格</th><th>注文数</th><th>金額</th></tr>';
  }

  // 注文履歴テーブルの合計金額部を表示する関数を用意
  function echo_total($total){
    echo '<tr><th colspan="6">合計金額</th><th class="td_right">&yen;' . number_format($total) . '</th></tr>';
    echo '</table><br>';
  }

?>
<!DOCTYPE  html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<title>注文履歴</title>
<link rel="stylesheet" href="../css/minishop.css">
</head>
<body>
<h3>注文履歴</h3>
3組　2番　岩井瑛斗<br>
<hr>
<p>過去のご注文履歴は次の通りです。</p>
<?php
  // 注文番号ごとにテーブルで注文商品を表示する
  $orderId = 0;   // 注文番号の切り替わりをチェックする
  $total = 0;     // 注文ごとの合計金額
  foreach($orders as $item){
    if($item['orderId'] != $orderId){   // 注文番号が切り替わったなら合計金額部を表示する必要があるが
      if($orderId != 0){                // それは合計金額が0でないときに
        echo_total($total);             // 注文履歴テーブルの合計金額部を表示
        $total = 0;
      }
      $orderId = $item['orderId'];      // これから表示する注文番号を切り替わりチェック用の$orderIdに代入しておく
      echo_title();                     // 注文履歴テーブルのタイトル部を表示する
    }
    $total += $item['price'] * $item['quantity'];
?>
      <tr> 
      <td class="td_mini_img"><img class="mini_img" src="../images/<?= $item['image'] ?>"></td>
      <td class="td_center"><?= $item['orderId'] ?></td>
      <td class="td_item_name"><?= $item['name'] ?></td> 
      <td class="td_item_maker"><?= $item['maker'] ?></td> 
      <td class="td_right td_item_price">&yen;<?= number_format($item['price']) ?></td> 
      <td class="td_right"><?= $item['quantity'] ?></td> 
      <td class="td_right td_item_total">&yen;<?= number_format($item['price'] * $item['quantity']) ?></td>
      </tr>
<?php
  }
  echo_total($total);         // 注文履歴テーブルの合計金額部を表示
?>
<a href="../index.php">ジャンル選択に戻る</a>
</body>
</html>
