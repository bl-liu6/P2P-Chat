<?php
include_once "./DataBase.class.php";
$db = new DataBase("127.0.0.1","notalk","root","Fenghuayu05.28");

$sid  = $_POST["sid"];
//$sid  = 2016501308;
$nickname = $_POST["nickname"];
//$nickname = "asdsa";
$sex = $_POST["sex"];
//$sex = "1";
$signature = $_POST["signature"];
//$signature = "";

$res = $db->select("user","*",array("sid"=>$sid));
$num = count($res);

if($num!=0){
    echo -1;
}else{
    $sql = "SELECT MAX(group_id) FROM friends";
    $max = $db->selectExec($sql,"both")[0]["MAX(group_id)"];
    $list = array($max+1=>"my friends");
    $list = json_encode($list,JSON_UNESCAPED_UNICODE);
    $res1 = $db->insert("friends",array("group_id"=>$max+1));
    $res = $db->insert("user",array("sid"=>$sid,"nickname"=>$nickname,"sex"=>$sex,"signature"=>$signature,"friends_group_list"=>$list));
    echo $res;
}
