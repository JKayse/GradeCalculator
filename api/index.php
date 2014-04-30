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
		      $query = "SELECT userId FROM Users WHERE username=:username";
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
