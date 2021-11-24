<?php
  // 送られてきた商品番号を受け取る（エスケープ処理は不要）
  $ident = $_GET['ident'];
  // Productオブジェクトを生成する
  require_once __DIR__ . '/../classes/product.php';
  $product = new Product();
  // 選択された商品を取り出す
  $item = $product->getItem($ident);

  require_once __DIR__ . '/../header.php';
?>
<!-- <!DOCTYPE  html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<title>詳細表示</title>
<link rel="stylesheet" href="../css/minishop.css">
</head>
<body>
<h3>商品詳細</h3>
3組　2番　岩井瑛斗
<hr> -->
<form method="POST" action="../cart/cart_add.php">
<input type="hidden" name="ident" value="<?= $item['ident'] ?>">
<table>
<tr><th>商品名</th>
<td><?= $item['name'] ?></td></tr>
<tr><td colspan="2"><div class="td_center">
<img class="detail_img" src="../images/<?= $item['image'] ?>"></div></td></tr>
<tr><th>メーカー・著者<br>アーティスト</th>
<td><?= $item['maker'] ?></td></tr>
<tr><th>価　格</th>
<td>&yen;<?= number_format($item['price']) ?></td></tr>
<tr><th>注文数</th>
    <td><select name="quantity">
    <?php
      for($i=1; $i<=10; $i++){
        echo '<option value="' . $i . '">'. $i . '</option>';
      }
    ?>
    </select></td></tr>
<tr><th colspan="2"><input type="submit" value="カートに入れる"></th></tr>
</table>
</form>
<a href="product_select.php?genre=<?= $item['genre']  ?>">
<span class="button_image">ジャンル別商品一覧に戻る</span></a>
<?php
  require_once __DIR__ . '/../footer.php';
?>
<!-- </body>
</html> -->
