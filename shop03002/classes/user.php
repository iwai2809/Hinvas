<?php
    require_once __DIR__ . '/dbdata.php';

    class User extends DbData{
        public function authUser($userId,$password){
            $sql="select * from users where userId=? and password=?";
            $stmt=$this->query($sql,[$userId,$password]);
            return $stmt->fetch();

        }

        public function changeCartUserId($tempId,$userId){
            require_once __DIR__ . '/cart.php';
            $cart=new Cart();
            $cart->changeUserId($tempId,$userId);
        }

        public function signUp($userId, $userName, $kana, $zip, $address, $tel, $password, $tempId){
            $sql = "select * from users where userId = ?";
            $stmt= $this->query($sql, [$userId]);
            $result = $stmt->fetch();

            if( $result ){
                    return 'この' . $userId . ' は既に登録されています。';
            }

            $sql = "insert into users(userId, userName, kana, zip, address, tel, password) values(?, ?, ?, ?, ?, ?, ?)";
            $result = $this->exec($sql, [$userId, $userName, $kana, $zip, $address, $tel, $password]);

                if($result){
                    $this->changeCartUserId($tempId,$userId);
                    return '';
                } else {
                        return '新規登録できませんでした。管理者にお問い合わせください。';
                }
                }

        public function updateUser($userId,$userName,$kana,$zip,$address,$tel,$password,$tempId){
            $sql = "update users set userId=?, userName=?, kana=?, zip=?, address=?, tel=?, password=? where userId = ?";
            $result = $this->exec($sql, [$userId, $userName, $kana, $zip, $address, $tel, $password, $tempId]);

            if($result){
                if($userId!==$tempId){
                    $this->changeCartUserId($tempId,$userId);
                    $this->changeOrderHistoryUserId($tempId,$userId);
                }
                return '';
            }else{
                return 'ユーザー情報の更新ができませんでした。管理者にお問い合わせください。';
            }
        }
        
        public function changeOrderHistoryUserId($tempId,$userId){
            require_once  __DIR__  .  '/order.php';
            $order=new Order();
            $order->changeUserId($tempId,$userId);
        }
    
}