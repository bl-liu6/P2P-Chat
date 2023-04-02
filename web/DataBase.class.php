<?php

/****************************************************
 * Author Howard.
 * Date: 2017/11/9
 * Version 1.0
 * 支持SELECT/UPDATE/INSERT命令的预处理及
 * SELETE/UPDATE/INSERT/DELETE命令的直接执行
 * 在WHERE子句中 仅可使用“=”操作符 其他操作符请自行组装sql语句
 ****************************************************/
class DataBase
{

    public $pdo = null;
    private $dbType = "mysql";    //数据库类型
    private $dbPort = "3306";     //数据库端口号
    private $dbHost;              //数据库主机
    private $dbName;              //数据库名
    private $userName;            //用户名
    private $password;            //用户密码
    public $debug = false;        //调试模式
    private $prepare;

    public function __construct($dbHost, $dbName, $userName, $password)
    {
        $this->dbHost = $dbHost;
        $this->dbName = $dbName;
        $this->userName = $userName;
        $this->password = $password;
        $this->dbConnect();
    }

    private function dbConnect()
    {
        try {
            $dbr = $this->dbType . ':host=' . $this->dbHost . ';port=' . $this->dbPort . ';dbname=' . $this->dbName;
            $this->pdo = new PDO($dbr, $this->userName, $this->password, array(PDO::ATTR_PERSISTENT => true));          //设置为长连接
            $this->pdo->exec('SET NAMES utf8');
            $this->pdo->setAttribute(PDO::ATTR_EMULATE_PREPARES, false);                                 //关闭本地预处理
            $this->pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_WARNING);                            //错误模式,此项为警告模式
//            $this->pdo->setAttribute(PDO::ATTR_ERRMODE,PDO::ERRMODE_EXCEPTION);                                       //错误模式,此项为异常模式
        } catch (PDOException $e) {
            die('Connection failed: ' . $e->getMessage());
        }
    }


    /* *
     * 普通的SELECT语句
     * 使用PDO::query方法
     * @param  string   $table      数据表名称
     * @param  array    $fields     选择的列名称 空值时为返回所有
     * @param  array    $where      where条件 仅支持“=”作为判断操作符 键名为字段名 键值为对应值
     * @param  string   $orderby    指定列排序
     * @param  boolean  $sort       降序/升序排列  默认为升序 false为降序
     * @param  string   $limit      限制返回结果数量及起始位置 单参数时为返回值数量 双参数时为起始位置和数量
     * @param  string   $fetch      返回数组关联方式 默认为FETCH_BOTH assoc为FETCH_ASSOC num时为FETCH_NUM both时为FETCH_BOTH
     * @return array                返回查询结果数组
     * */
    public function select($table, $fields = "", $where = "", $orderby = "", $sort = true, $limit = "", $fetch = "both")
    {
        $sql = $this->sqlCreat("select", array("table" => $table, "fields" => $fields, "where" => $where, "orderby" => $orderby, "sort" => $sort, "limit" => $limit));
        if ($this->debug) {
            print_r($sql);
        }
        $res = $this->selectExec($sql, $fetch);
        return $res;
    }


    /* *
     * 使用预处理的SELECT语句
     * 内部使用PDO的prepare预处理 并自动绑定参数 防止注入
     * @param  string   $table      数据表名称
     * @param  array    $fields     选择的列名称 空值时为返回所有
     * @param  array    $where      where条件 仅支持“=”作为判断操作符 键名为字段名 键值为对应值
     * @param  string   $orderby    指定列排序
     * @param  boolean  $sort       降序/升序排列  默认为升序 false为降序
     * @param  string   $limit      限制返回结果数量及起始位置 单参数时为返回值数量 双参数时为起始位置和数量
     * @param  string   $fetch      返回数组关联方式 默认为FETCH_BOTH assoc为FETCH_ASSOC num时为FETCH_NUM both时为FETCH_BOTH
     * @return array                返回查询结果数组
     * */
    public function select_($table, $fields = "", $where = "", $orderby = "", $sort = true, $limit = "", $fetch = "both")
    {
        $sql = $this->sqlCreat("select_", array("table" => $table, "fields" => $fields, "where" => $where, "orderby" => $orderby, "sort" => $sort, "limit" => $limit));
        if ($this->debug) {
            print_r($sql);
        }
        $res = $this->select_Exec($sql, $where, $fetch);
        return $res;
    }


    /* *
     * 普通的INSERT语句
     * 使用PDOStatement::exec方法
     * @param string $table 数据表名
     * @param array  $data  键名为列名 键值为插入值
     * @return int          返回受影响记录数
     * */
    public function insert($table, $data)
    {
        $sql = $this->sqlCreat("insert", array("table" => $table, "data" => $data));
        if ($this->debug) {
            print_r($sql);
        }
        $res = $this->normalExec($sql);

        return $res;
    }


    /* *
     * 使用预处理的INSERT语句
     * 内部使用PDO的prepare预处理 并自动绑定参数 防止注入
     * @param string $table     数据表名
     * @param array  $data      键名为列名 键值为插入值
     * @return int              返回受影响记录数
     * */
    public function insert_($table, $data)
    {
        $sql = $this->sqlCreat("insert_", array("table" => $table, "data" => $data));
        if ($this->debug) {
            print_r($sql);
        }
        $res = $this->insert_Exec($sql, $data);
        return $res;
    }


    /*
     * 普通的UPDATE语句
     * 使用PDO::exec方法
     * @param  string   $table      数据表名称
     * @param  array    $data       键名为列名 键值为要更改值
     * @param  array    $where      where条件 仅支持“=”作为判断操作符 键名为字段名 键值为对应值
     * @return int                  返回影响数目
     * */
    public function update($table, $data, $where = "")
    {
        $sql = $this->sqlCreat("update", array("table" => $table, "data" => $data, "where" => $where));
        if ($this->debug) {
            print_r($sql);
        }
        $res = $this->normalExec($sql);
        return $res;
    }


    /* *
     * 使用预处理的UPDATE语句
     * 内部使用PDO的prepare预处理 并自动绑定参数 防止注入
     * @param  string   $table      数据表名称
     * @param  array    $fields     选择的列名称 空值时为返回所有
     * @param  array    $where      where条件 仅支持“=”作为判断操作符 键名为字段名 键值为对应值
     * @param  string   $orderby    指定列排序
     * @param  boolean  $sort       降序/升序排列  默认为升序 false为降序
     * @param  string   $limit      限制返回结果数量及起始位置 单参数时为返回值数量 双参数时为起始位置和数量
     * @param  string   $fetch      返回数组关联方式 默认为FETCH_BOTH assoc为FETCH_ASSOC num时为FETCH_NUM both时为FETCH_BOTH
     * @return array                返回查询结果数组
     * */
    public function update_($table, $data, $where = "")
    {
        $sql = $this->sqlCreat("update_", array("table" => $table, "data" => $data, "where" => $where));
        if ($this->debug) {
            print_r($sql);
        }
        $res = $this->update_Exec($sql, $data, $where);
        return $res;
    }


    /*
     * 普通的DELETE语句
     * 使用PDO::exec方法
     * @param  string   $table      数据表名称
     * @param  array    $data       键名为列名 键值为要更改值
     * @param  array    $where      where条件 仅支持“=”作为判断操作符 键名为字段名 键值为对应值
     * @return int                  返回影响数目
     * */
    public function delete($table, $where = "")
    {
        $sql = $this->sqlCreat("delete", array("table" => $table, "where" => $where));
        if ($this->debug) {
            print_r($sql);
        }
        $res = $this->normalExec($sql);
        return $res;
    }


    /* *
     * sql语句构造方法
     * SELECT时默认升序
     * @param  string  $command   命令标志符 用于判断执行的命令类型
     * @param  array   $paramArr  命令参数
     * @return sting              返回组装好的sql语句
     *
     * */
    private function sqlCreat($command, $paramArr)
    {
        switch ($command) {
            case "select_":
                $fildsSql = empty($paramArr["filds"]) ? "*" : implode(",", $paramArr["filds"]);
                $whereSql = empty($paramArr["where"]) ? "1=1" : $this->whereCreat(true, $paramArr["where"]);
                $limitSql = empty($paramArr["limit"]) ? "" : " LIMIT " . $paramArr["limit"];
                $sort = $paramArr["sort"] ? " ASC" : " DESC";
                $orderbySql = empty($paramArr["orderby"]) ? "" : " ORDER BY " . $paramArr["orderby"] . $sort;
                $sql = "SELECT " . $fildsSql . " FROM " . $paramArr["table"] . " WHERE " . $whereSql . $orderbySql . $limitSql;
                break;
            case "select":
                $fildsSql = empty($paramArr["filds"]) ? "*" : implode(",", $paramArr["filds"]);
                $whereSql = empty($paramArr["where"]) ? "1=1" : $this->whereCreat(false, $paramArr["where"]);
                $limitSql = empty($paramArr["limit"]) ? "" : " LIMIT " . $paramArr["limit"];
                $sort = $paramArr["sort"] ? " ASC" : " DESC";
                $orderbySql = empty($paramArr["orderby"]) ? "" : " ORDER BY " . $paramArr["orderby"] . $sort;
                $sql = "SELECT " . $fildsSql . " FROM " . $paramArr["table"] . " WHERE " . $whereSql . $orderbySql . $limitSql;
                break;
            case "insert":
                $arrKey = array_keys($paramArr["data"]);
                $key = implode(",", $arrKey);
                $val = "'" . implode("','", $paramArr["data"]) . "'";
                $sql = "INSERT INTO " . $paramArr["table"] . " (" . $key . ") VALUES (" . $val . ")";
                break;
            case "insert_":
                $arrKey = array_keys($paramArr["data"]);
                $key = implode(",", $arrKey);
                $val = ":" . implode(",:", $arrKey);
                $sql = "INSERT INTO " . $paramArr["table"] . " (" . $key . ") VALUES (" . $val . ")";
                break;
            case "update":
                $dataSql = $this->updateSqlCreat("dataEqual", $paramArr["data"]);
                $whereSql = empty($paramArr["where"]) ? "1=1" : $this->whereCreat(false, $paramArr["where"]);
                $sql = "UPDATE " . $paramArr["table"] . " SET " . $dataSql . " WHERE " . $whereSql;
                break;
            case "update_":
                $dataSql = $this->updateSqlCreat("dataQuest", $paramArr["data"]);
                $whereSql = empty($paramArr["where"]) ? "1=1" : $this->updateSqlCreat("whereQuest", $paramArr["where"]);
                $sql = "UPDATE " . $paramArr["table"] . " SET " . $dataSql . " WHERE " . $whereSql;
                break;
            case "delete":
                $whereSql = empty($paramArr["where"]) ? "1=1" : $this->whereCreat(false, $paramArr["where"]);
                $sql = "DELETE FROM " . $paramArr["table"] . " WHERE " . $whereSql;
                break;
        }
        return $sql;
    }


    /* *
     * 构造WHERE子句的方法
     * @param  string $conlon    标记是否需要绑定参数 需要绑定参数时 构造的where子句中为带有冒号的变量形式 否则为无冒号的对应的值
     * @param  array  $array     WHERE子句的列名和变量名一般分别对应本数组的键名和键值
     * @return string            构造好的WHERE子句
     * */
    private function whereCreat($conlon, $array)
    {
        $res = "";
        if ($conlon) {
            foreach ($array as $key => $value) {
                $res .= $key . " = :" . $key . " AND ";
            }
        } else if (!$conlon) {
            foreach ($array as $key => $value) {
                $res .= $key . " = '" . $value . "' AND ";
            }
        }
        $res = $res . "1=1";
        return $res;
    }


    /* *
     * 由于UPDATE命令的语句格式较为特殊 有两处需要构造因此此命令的构造单独使用一个方法
     * @param  string $command  标记是何处以及何种方式的构造  dataEqual为无需绑定参数时构造UPDATE的列名和新值
     *                          dataQuest为需要绑定参数时的列名和新值 whereQuest为WHERE子句的构造 需绑定参数
     * @param  array  $arr      构造时的数组 列名和变量名一般分别对应本数组的键名和键值
     * @return string           构造好的子句
     * */
    private function updateSqlCreat($command, $arr)
    {
        if ($command == "dataEqual") {
            foreach ($arr as $key => $value) {
                $resArr[] = $key . " = " . $value;
            }
            $res = implode(",", $resArr);
        } else if ($command == "dataQuest") {
            foreach ($arr as $key => $value) {
                $resArr[] = $key . " = ?";
            }
            $res = implode(",", $resArr);
        } else if ($command == "whereQuest") {
            $res = "";
            foreach ($arr as $key => $value) {
                $res .= $key . " = ? AND ";
            }
            $res = $res . "1 = 1";
        }

        return $res;
    }


    /* *
     * SELECT命令预处理绑定参数并执行的方法
     * @param  string  $sql     要执行的已经构造好的sql语句
     * @param  array   $where   用于绑定参数 键名键值一般对应WHERE子句中变量名和值
     * @param  string  $fetch   标记返回结果数组的形式 assoc为关联数组 num为索引数组 both为二者都有
     * @return array            数组形式的返回结果
     * */
    private function select_Exec($sql, $where, $fetch)
    {
        try {
            $this->prepare = $this->pdo->prepare($sql);
        } catch (PDOException $e) {
            print_r($e->errorInfo);
        }
        $this->bindParam("colon", $where);
        $this->prepare->execute();
        switch ($fetch) {
            case "num":
                $res = $this->prepare->fetchAll(PDO::FETCH_NUM);
                break;
            case "assoc":
                $res = $this->prepare->fetchAll(PDO::FETCH_ASSOC);
                break;
            case "both":
                $res = $this->prepare->fetchAll(PDO::FETCH_BOTH);
                break;
            default:
                $res = $this->prepare->fetchAll(PDO::FETCH_BOTH);
        }
        return $res;
    }


    /*
     * SELECT命令直接执行的方法
     * @param  string  $sql     要执行的已经构造好的sql语句
     * @param  string  $fetch   标记返回结果数组的形式 assoc为关联数组 num为索引数组 both为二者都有
     * @return array            数组形式的返回结果
     * */
    public function selectExec($sql, $fetch)
    {
        $res = $this->pdo->query($sql);
        switch ($fetch) {
            case "num":
                $res = $res->fetchAll(PDO::FETCH_NUM);
                break;
            case "assoc":
                $res = $res->fetchAll(PDO::FETCH_ASSOC);
                break;
            case "both":
                $res = $res->fetchAll(PDO::FETCH_BOTH);
                break;
            default:
                $res = $res->fetchAll(PDO::FETCH_BOTH);
        }
        return $res;
    }


    /* *
     * INSERT命令的预处理绑定参数并执行
     * @param   string $sql   要执行的已经构造好的sql语句
     * @param   array  $data  用于绑定参数的数组 键名键值一般对应变量名和值
     * @return  int           返回受影响记录的数目
     * */
    private function insert_Exec($sql, $data)
    {
        try {
            $this->prepare = $this->pdo->prepare($sql);
        } catch (PDOException $e) {
            print_r($e->errorInfo);
        }
        $this->bindParam("colon", $data);
        $this->prepare->execute();
        $res = $this->prepare->rowCount();
        return $res;
    }


    /* *
     * UPDATE命令的预处理绑定参数并执行
     * @param   string $sql   要执行的已经构造好的sql语句
     * @param   array  $data  用于绑定参数的数组 键名键值一般对应变量名和值
     * @param   array  $where 用于绑定参数 键名键值一般对应WHERE子句中变量名和值
     * @return  int           返回受影响记录的数目
     * */
    private function update_Exec($sql, $data, $where)
    {
        try {
            $this->prepare = $this->pdo->prepare($sql);
        } catch (PDOException $e) {
            print_r($e->getMessage());
        }
        $this->bindParam("quest", array($data, $where));
        $this->prepare->execute();
        $res = $this->prepare->rowCount();
        return $res;
    }


    /* *
     * INSERT/UPDATE/DELETE 等命令直接执行的方法
     * @param   string $sql   构造好的要执行的sql语句
     * @return  int           返回受影响记录的数目
     * */
    private function normalExec($sql)
    {
        try {
            $res = $this->pdo->exec($sql);
        } catch (PDOException $e) {
            print_r($e->getMessage());
        }
        return $res;
    }


    /* *
     * sql语句经过预处理语句后绑定参数的方法
     * @flag    string  $flag   用于标记绑定参数的类型  quest为变量为问号的类型 colon为变量为冒号加变量名的格式
     * @param   array   $param  用于绑定的数组 键名键值一般分别对应为变量名和值
     * @return  null
     * */
    private function bindParam($flag, $param)
    {
        if (empty($param))
            return;
        if ($flag == "quest") {
            $dataCount = count($param[0]);
            $whereCount = count($param[1]);
            $dataArr = array_values($param[0]);
            $whereArr = array_values($param[1]);
            $j = 1;
            for ($i = 0; $i < $dataCount; $i++, $j++) {
                $this->prepare->bindValue($j, $dataArr[$i]);
            }
            for ($i = 0; $i < $whereCount; $i++, $j++) {
                $this->prepare->bindValue($j, $whereArr[$i]);
            }
        } else if ($flag == "colon") {
            foreach ($param as $key => $value) {
                $this->prepare->bindValue(":" . $key, $value);
            }
        }

    }


}//end class

