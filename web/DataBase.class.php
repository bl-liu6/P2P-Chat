<?php

/****************************************************
 * Bolun Liu
 * 03/30/2023
 * supports prepared statements for SELECT/UPDATE/INSERT commands
 * SELETE/UPDATE/INSERT/DELETE commands
 ****************************************************/
class DataBase
{

    public $pdo = null;
    private $dbType = "mysql";    //db Type
    private $dbPort = "3306";     //db Port
    private $dbHost;              //db Host
    private $dbName;              //db Name
    private $userName;            //userName
    private $password;            //password
    public $debug = false;        //debug
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
            $this->pdo = new PDO($dbr, $this->userName, $this->password, array(PDO::ATTR_PERSISTENT => true));      
            $this->pdo->exec('SET NAMES utf8');
            $this->pdo->setAttribute(PDO::ATTR_EMULATE_PREPARES, false);                             
            $this->pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_WARNING);                 
//            $this->pdo->setAttribute(PDO::ATTR_ERRMODE,PDO::ERRMODE_EXCEPTION);                              
        } catch (PDOException $e) {
            die('Connection failed: ' . $e->getMessage());
        }
    }


    /* *
     * PDO::query methods
     * @param  string   $table     
     * @param  array    $fields     
     * @param  array    $where     
     * @param  string   $orderby    
     * @param  boolean  $sort      
     * @param  string   $limit    
     * @param  string   $fetch    
     * @return array           
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
     * @param  string   $table      
     * @param  array    $fields     
     * @param  array    $where      
     * @param  string   $orderby    
     * @param  boolean  $sort       
     * @param  string   $limit      
     * @param  string   $fetch      
     * @return array                
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
     * common INSERT
     * PDOStatement::exec methods
     * @param string $table 
     * @param array  $data  
     * @return int         
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
     * prepared INSERT
     * @param string $table    
     * @param array  $data      
     * @return int            
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
     * common UPDATE
     * PDO::exec methods
     * @param  string   $table      
     * @param  array    $data      
     * @param  array    $where     
     * @return int                  
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
     * prepared UPDATE
     * @param  string   $table      
     * @param  array    $fields    
     * @param  array    $where    
     * @param  string   $orderby   
     * @param  boolean  $sort      
     * @param  string   $limit    
     * @param  string   $fetch      
     * @return array                
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
     * common DELETE
     * PDO::exec methods
     * @param  string   $table     
     * @param  array    $data      
     * @param  array    $where     
     * @return int                 
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
     * SQL statement construction method
     * @param  string  $command   
     * @param  array   $paramArr  
     * @return sting            
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
     * Constructing the WHERE clause
     * @param  string $conlon    
     * @param  array  $array   
     * @return string           
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
     * @param  string $command  
     * @param  array  $arr      
     * @return string          
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
     * @param  string  $sql   
     * @param  array   $where 
     * @param  string  $fetch  
     * @return array          
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
     * SELECT command
     * @param  string  $sql    
     * @param  string  $fetch  
     * @return array          
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
     * INSERT command
     * @param   string $sql   
     * @param   array  $data  
     * @return  int          
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
     * UPDATE command
     * @param   string $sql   
     * @param   array  $data  
     * @param   array  $where 
     * @return  int           
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
     * INSERT/UPDATE/DELETE 
     * @param   string $sql   
     * @return  int          
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
     * @flag    string  $flag   
     * @param   array   $param  
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

