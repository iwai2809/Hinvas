<?php
    require_once __DIR__ . '/../header.php';

    $_SESSION=[];
    if(isset($_COOKIE[session_name()])){
        setcookie(session_name(),'',time()-1000,'/');

    }

    session_destroy();


    setcookie('userId','',time()-1000,'/');
    setcookie('userName','',time()-1000,'/');


    header("Location:". $index_php);