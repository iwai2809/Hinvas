<?php
    require_once  __DIR__ . '/header.php';   

$pc_num = mt_rand(1, 5);
$book_num = mt_rand(1, 5);
$music_num = mt_rand(1, 5);
?>

<p>お好みのジャンルを選択してください。</p>
<div class="topnavi">
<div class="topbox">
<p class="topvalue">PC</p>
<hr>
<a href="product/product_select.php?genre=pc">
<img class="topimage" src="images/pc00<?=$pc_num?>.jpg">
</a>
</div>


<div class="topbox">
<p class="topvalue">BOOK</p>
<hr>
<a href="product/product_select.php?genre=book">
<img class="topimage"  src="images/book00<?=$book_num?>.jpg">
</a>
</div>
<div class="topbox">
<p class="topvalue">MUSIC</p>
<hr>
<a href="product/product_select.php?genre=music">
<img class="topimage"  src="images/music00<?=$music_num?>.jpg">
</a>
</div>
</div>
<br><br>
<?php
    require_once __DIR__ . '/footer.php';
?>