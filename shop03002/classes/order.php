<?php
  // スーパークラスであるDbDataを利用するため
  require_once __DIR__ . '/dbdata.php';

  class Order extends DbData {
    // カート内の全ての商品を注文内容として登録する
    public function addOrder($userId, $cartItems ) {
      // 注文テーブルに登録
      $sql = "insert into orders(userId,orderdate) values( ?,? )";      
      $result = $this->exec( $sql,  [$userId, date("Y-m-d H:i:s") ] );
      // 注文番号を取得する
      $sql = "select  last_insert_id( )  from  orders";
      $stmt = $this->query( $sql,  [ ]);
      $result = $stmt->fetch( );
      $orderId = $result[0];
      // 注文明細テーブルに登録する
      foreach( $cartItems  as  $item ) {
        $sql = "insert into orderdetails values( ?, ?, ? )";
        $result = $this->exec($sql,  [$orderId,  $item['ident'],  $item['quantity'] ] );
      }
    }

    // 注文履歴
    public function getOrders($userId){
      // 注文明細テーブルのデータを注文番号の降順で取得
      $sql = "select orderdetails.orderId, items.ident, items.name, items.maker, items.price,
              orderdetails.quantity, items.image, items.genre from orderdetails 
              join items on orderdetails.itemId = items.ident 
              where orderdetails.orderId in (select orders.orderId from orders where orders.userId=?)
              order by orderdetails.orderId desc";
      $stmt = $this->query($sql, [$userId]);
      $orders = $stmt->fetchAll();
      return $orders;
    }

    public function changeUserId($tempId, $userId){
      $sql="select * from orders where userId=?";
      $stmt=$this->query($sql,($tempId));
      $cartItems=$stmt->fetchAll();
      if($cartItems){
        $sql="update orders set userId=? where userId=?";
        $stmt=$this->exec($sql,[$userId,$tempId]);
      }
    }
  }
