<?php
  function h($data){
    return htmlspecialchars($data, ENT_QUOTES, "UTF-8");
  }
