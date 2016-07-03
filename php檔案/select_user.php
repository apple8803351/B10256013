<?php
	header("Content-Type:text/html; charset=utf-8"); //把網頁變成UTF-8

	$username = $_POST['username'];
	$password = $_POST['password'];

	if($username != null && $password != null)
	{
		$data = array();

		//連結資料庫
		$db = mysql_connect("localhost","root","a0988657035") or die("connection filed"); 
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
		$sql = stripslashes("SELECT `password` FROM `user` WHERE username = '$username'");
		//執行SQL指令
		$result = mysql_query($sql);
		//判斷SQL指令有沒有找到資料
		if(mysql_num_rows($result) > 0)
		{
			//一列一列的慢慢取 可以用欄位名稱去取得該欄位的資料
			while($row = mysql_fetch_assoc($result))
			{
				if($row['password'] === $password)
				{
					$data[] = array('login' => '登入成功');
				}
				else
				{
					$data[] = array('login' => '登入失敗');
				}
			}
		}
		else
		{
			$data[] = array('login' => '登入失敗');
		}
		//關閉資料庫
		mysql_close();
		//將陣列變成JSON
		echo json_encode($data, JSON_UNESCAPED_UNICODE); 
	}
?>