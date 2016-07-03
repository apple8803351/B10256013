<?php
	header("Content-Type:text/html; charset=utf-8"); //把網頁變成UTF-8

	$username = $_POST['username'];
	$password = $_POST['password'];
		
	if($username != null && $password != null)
	{	
		$data = array();
		//連結資料庫
		$db = mysql_connect("localhost","root","a0988657035") or die("connection failed");
		//資料庫名稱
		$db_name = "chat";

		//以下四個mysql_query 好像是防止查詢結果變成亂碼...
		mysql_query("SET CHARACTER SET 'UTF8'");
		mysql_query("SET NAMES UTF8");
		mysql_query("SET CHARACTER SET CLIENT=UTF8");
		mysql_query("SET CHARACTER_SET_RESULTS=UTF8");
		//指定資料庫
		mysql_select_db($db_name);
		//SQL指令
		$sql = stripslashes("INSERT INTO `user` (`username`,`password`) VALUES ('$username','$password')");
		//執行SQL指令
		$result = mysql_query($sql);
		//關閉資料庫
		mysql_close($db);
		
		if($result)
		{
			$data[] = array('register' => '註冊成功');
		}
		else
		{
			$data[] = array('register' => '註冊失敗');
		}
		//將陣列變成JSON
		echo json_encode($data, JSON_UNESCAPED_UNICODE); 	
	}
?>