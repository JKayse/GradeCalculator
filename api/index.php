<?php

/**
* index.php.  This file contains all the backend functions that run the website
* Uses Slim framework.  
*/

session_cache_limiter(false);
session_start();

require 'Slim/Slim.php';

$app = new Slim();

//Sets up the links to the functions
$app->get(
    '/',
    function() use($app) {
        $response = $app->response();
        $response->status(200);
        $response->write('The API is working');
    });


/**
* User Registration
*/
$app->post('/Users', 'addUser');

/**
* User Login
*/
$app->post('/Login', 'login');

/**
* User Logout
*/
$app->post('/Logout', 'logout');

/**
* View Classes
*/
$app->get('/Classes/:userId', 'viewClasses');

/**
* View a Class
*/
$app->get('/Class/:classId', 'viewClass');

$app->run();


/**
* A funtion that takes the information inputed by a user and creates
* an account for them by inserting them into the database
*/
function addUser()
{
    $username = Slim::getInstance()->request()->post('username');
    $password = Slim::getInstance()->request()->post('password');

    $sql = "SELECT username FROM users WHERE username=:username";

    try
    {
        $db = getConnection();

        $stmt = $db->prepare($sql);
        $stmt->bindParam("username", $username);
        $stmt->execute();
        $db = null;
        $username_test = $stmt->fetchObject();

        if($username_test) {
            echo "error_username";
            return;
        }
    }
    catch(PDOException $e) 
    {
        echo '{"error":{"text":'. $e->getMessage() .'}}'; 
    }


    $sql = "INSERT INTO users (username, password) VALUES (:username, :password)";

    try
    {
        $db = getConnection();

        $stmt = $db->prepare($sql);
        $stmt->bindParam("username", $username);
        $stmt->bindParam("password", $password);

        $stmt->execute();
        $db = null;

    }
    catch(PDOException $e) 
    {
        echo '{"error":{"text":'. $e->getMessage() .'}}'; 
    }
}

/**
* A function to check if the user entered the correct email and password.
* If so, a cookie is created containing their username
* @return JSON The userid and email.
*/
function login() {
    $username = Slim::getInstance()->request()->post('username');
    $password = Slim::getInstance()->request()->post('password');

    try {
        $db = getConnection();

	$sql = "SELECT userId FROM users WHERE username=:username";
	$stmt = $db->prepare($sql);
	$stmt->bindParam("username", $username);
	$stmt->execute();
	$username_test = $stmt->fetchObject();
	if(empty($username_test)) {
		echo "error_username_doesnt_exists";
		return;
	}

	$sql = "SELECT password FROM users WHERE username=:username";
        $stmt = $db->prepare($sql);
        $stmt->bindParam("username", $username);
        $stmt->execute();
        $storedPassword = $stmt->fetchObject()->password;

        if(empty($storedPassword)) {
                echo "null";
        } else if(strcmp($password, $storedPassword) == 0) {
		      $query = "SELECT userId FROM users WHERE username=:username";
		      $stmt2 = $db->prepare($query);
		      $stmt2->bindParam("username", $username);
		      $stmt2->execute();
         	  echo '{"Username": "' . $username . '", "ID": ' . $stmt2->fetchObject()->userId . '}'; 
        } else {
		echo "null";
	}

        $db = null;
    } catch(PDOException $e) {
        echo '{"error":{"text":'. $e->getMessage() .'}}'; 
    }
}

/**
* A function to log the user out
*/
function logout() { 
    $_SESSION = array(); 
    if (ini_get("session.use_cookies")) {
        $params = session_get_cookie_params();
        setcookie(session_name(), '', time() - 42000,
            $params["path"], $params["domain"],
            $params["secure"], $params["httponly"]
        );
    }
    session_destroy();
}


/**
* A function to get all the classes of the user
*/
function viewClasses($userId){
    $sql = "SELECT classId, className, professor FROM classes WHERE userId=:userId ORDER BY className";
    try {
            $db = getConnection();
            $stmt = $db->prepare($sql);
            $stmt->bindParam("userId", $userId);
            $stmt->execute();
            $Classes = $stmt->fetchAll(PDO::FETCH_OBJ);
            $db = null;
            echo '{"Classes": ' . json_encode($Classes) . '}';
        } catch(PDOException $e) {
        echo '{"error":{"text":'. $e->getMessage() .'}}'; 
        }
}

/**
* A function to get information from one class
*/
function viewClass($classId){
    try {
        $db = getConnection();

        $sql = "SELECT className, professor FROM classes WHERE classId=:classId";
        $stmt = $db->prepare($sql);
        $stmt->bindParam('classId', $classId);
        $stmt->execute();
        $class = $stmt->fetchObject();
        $className = $class->className;
        $professor = $class->professor;

        $sql2 = "SELECT categoryId, categoryName, percentage FROM category WHERE classId=:classId";
        $stmt2 = $db->prepare($sql2);
        $stmt2->bindParam('classId', $classId);
        $stmt2->execute();
        $categories = $stmt2->fetchAll(PDO::FETCH_OBJ);

        echo '{"Class": { "className": "' . $className .'", "professor": "' . $professor .'", "categories": [' ;
        $i = 0;
        foreach($categories as $category) {
            if($i != 0) {
                echo ',';
            } else {
                $i++;
            }
            $categoryId = $category->categoryId;
            $categoryName = $category->categoryName;
            $percentage = $category->percentage;
            $sql3 = "SELECT gradeId, gradeName, percentage FROM grades WHERE categoryId=:categoryId";
            $stmt3 = $db->prepare($sql3);
            $stmt3->bindParam('categoryId', $categoryId);
            $stmt3->execute();
            $grades = $stmt3->fetchAll(PDO::FETCH_OBJ);
            echo '{"categoryId": "'. $categoryId .'", "categoryName": "'. $categoryName .'", "categoryPercentage": "'. $percentage .'"';
            echo ', "grades": ['; 
            $j =0;
            foreach($grades as $grade) {
                if($j != 0) {
                    echo ',';
                } else {
                    $j++;
                }
                echo json_encode($grade);
            }
            echo ']}';
        }
        echo ']}}';

            $db = null;

    } catch(PDOExection $e) {
        echo '{"error":{"text":'. $e->getMessage() .'}}';
    }
}


/**
* A function that sets up the connection to the database
*/
function getConnection() {
    $dbhost="localhost";
    $dbuser="root";
    $dbpass="";
    $dbname="GradeCalculator";
    $dbh = new PDO("mysql:host=$dbhost;dbname=$dbname", $dbuser, $dbpass);  
    $dbh->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    return $dbh;
}

?>
