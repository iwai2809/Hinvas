<?php
    // スーパークラスであるDbDataを利用するため
    require_once __DIR__ . '/dbdata.php';

    // Productクラスの宣言
    class  Product  extends  DbData {
        // 選択されたジャンルの商品を取り出す
        public  function  getItems ( $genre ) {
            $sql  =  "select  *  from  items  where  genre  =  ?";
            $stmt = $this->query( $sql,  [$genre] );
            $items = $stmt->fetchAll( );
            return  $items;
        }

        // 選択された商品を取り出す
        public function getItem($ident){
            $sql = "select * from items where ident = ?";
            $stmt = $this->query($sql, [$ident]);
            $item = $stmt->fetch();
            return $item;
        }
    }
