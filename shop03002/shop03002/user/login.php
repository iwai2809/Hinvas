<?php
    require_once __DIR__ . '/../header.php';

    if(isset($_SESSION['login_error'])){
        echo '<p class="error_class">' . $_SESSION['login_error'] . '</p>';
        unset($_SESSION['login_error']);
    }else{
        echo '<p>利用するにあたってはログインしてください。</p>';
    }
?>

<form method="POST" action="./login_db.php">
    <table>
        <tr><td>Eメール</td><td><input type="text" name="userId" required></td></tr>
        <tr><td>パスワード</td><td><input type="password" name="password" required></td></tr>
        <tr><td colspan="2"><input type="submit" value="ログイン"></td></tr>
    </table>
</form>
<a href="./signup.php"><span class="button_image">新規登録はこちらから</span></a>
<?php
    require __DIR__ . '/../footer.php';
?>