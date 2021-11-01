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
    }