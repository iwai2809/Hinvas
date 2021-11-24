<?php
  // スーパークラスであるDbDataを利用するため
  require_once __DIR__ . '/dbdata.php';

  class Cart extends DbData{
    // 商品をカートに入れる ・・ テーブルcartに登録する
    public function addItem($userId,$ident, $quantity){
      // すでにカート内にその商品がはいっているかどうかをチェックする
      $sql = "select * from cart where userId=? and ident = ?";
      $stmt = $this->query($sql, [$userId,$ident]);
      $cart_item = $stmt->fetch();
      if($cart_item){
        // カート内にすでに入っているので、今回の注文数を追加する
        $new_quantity = $quantity + $cart_item['quantity'];
        if($new_quantity > 10 ) $new_quantity = 10;
        $sql = "update cart set quantity = ? where userId=? and ident = ?";
        $result = $this->exec($sql, [$new_quantity, $userId,$ident]);
      } else {
        // カート内にはまだ入っていないので登録する
        $sql = "insert into cart values(?,?, ?)";
        $result = $this->exec($sql, [$userId,$ident, $quantity]);
      }
    }

    // カート内のすべてのデータを取り出す
    public function getItems($userId){
      $sql = "select items.ident, items.name, items.maker, items.price, cart.quantity, items.image, items.genre 
              from cart join items on cart.ident = items.ident where cart.userId=?";
      $stmt = $this->query($sql, [$userId]);
      $items = $stmt->fetchAll();
      return $items;
    }

    // カート内の商品を削除する
    public function deleteItem($userId,$ident){
      $sql = "delete from cart where userId=? and ident = ?";
      $result = $this->exec($sql, [$userId,$ident]);
    }

    // カート内のすべての商品を削除する
    public function clearCart($userId){
      $sql = "delete from cart where userId=?";
      $result = $this->exec($sql, [$userId]);
    }

    // カート内の商品の個数を変更する
    public function changeQuantity($userId,$ident, $quantity){
      $sql = "update cart set quantity = ? where userId =? and ident = ?";
      $result = $this->exec($sql, [$quantity, $userId,$ident]);
    }

    public function changeUserId($tempId,$userId){
      $sql="select * from cart where userId=?";
      $stmt=$this->query($sql,[$tempId]);
      $cart_item=$stmt->fetchAll();
      foreach($cart_item as $item){
        $this->addItem($userId,$item['ident'],$item['quantity']);
        $this->deleteItem($tempId,$item['ident']);
      }
    }
  }
