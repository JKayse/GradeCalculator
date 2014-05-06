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

/**
* Add a class
*/
$app->post('/AddClass', 'addClass');

/**
* Add a grade for a class
*/
$app->post('/AddGrade', 'addGrade');


/**
* Delete a class for a user.
*/
$app->post('/DeleteClass', 'deleteClass');

/**
* Delete a grade for a user.
*/
$app->post('/DeleteGrade', 'deleteGrade');


/**
* Edit a grade for a user.
*/
$app->post('/EditGrade', 'editGrade');

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

        $sql2 = "SELECT categoryId, categoryName, percentage FROM category WHERE classId=:classId";
        $stmt2 = $db->prepare($sql2);
        $stmt2->bindParam('classId', $classId);
        $stmt2->execute();
        $categories = $stmt2->fetchAll(PDO::FETCH_OBJ);

        echo '{"Categories": [' ;
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
* A function to add a class
*/
function addClass(){
    $userId = Slim::getInstance()->request()->post('userId');
    $className = Slim::getInstance()->request()->post('className');
    $professor = Slim::getInstance()->request()->post('professor');
    $categories = json_decode(Slim::getInstance()->request()->post('categories'), true);
    try {
        $insertClass = "INSERT INTO classes(userId, className, professor) VALUE(:userId, :className, :professor)";
        $db = getConnection();
        $stmt = $db->prepare($insertClass);
        $stmt->bindParam("userId", $userId);
        $stmt->bindParam("className", $className);
        $stmt->bindParam("professor", $professor);
        $stmt->execute();
        $classId = $db->lastInsertId();
        
        foreach($categories['category'] as $category) {
            $categoryName = $category['categoryName'];
            $percentage =  $category['percentage'];
            $insertCategory = "INSERT INTO category(classId, categoryName, percentage) VALUE(:classId, :categoryName, :percentage)";
            $stmt = $db->prepare($insertCategory);
            $stmt->bindParam("classId", $classId);
            $stmt->bindParam("categoryName", $categoryName);
            $stmt->bindParam("percentage", $percentage);
            $stmt->execute();
        }
        $db = null;

    } catch(PDOException $e) {
    echo '{"error":{"text":'. $e->getMessage() .'}}'; 
    }   
}


/**
* Add a grade for a class
*/
function addGrade(){
    $categoryId = Slim::getInstance()->request()->post('categoryId');
    $gradeName = Slim::getInstance()->request()->post('gradeName');
    $percentage = Slim::getInstance()->request()->post('percentage');
    try {
        $insertGrade = "INSERT INTO grades(categoryId, gradeName, percentage) VALUE(:categoryId, :gradeName, :percentage)";
        $db = getConnection();
        $stmt = $db->prepare($insertGrade);
        $stmt->bindParam("categoryId", $categoryId);
        $stmt->bindParam("gradeName", $gradeName);
        $stmt->bindParam("percentage", $percentage);
        $stmt->execute();
        $db = null;

    } catch(PDOException $e) {
    echo '{"error":{"text":'. $e->getMessage() .'}}'; 
    }   
}


function deleteClass(){
    $classId = Slim::getInstance()->request()->post('classId');
    try
    {
        $sql = "SELECT categoryId FROM category WHERE classId=:classId";
        $db = getConnection();
        $stmt = $db->prepare($sql);
        $stmt->bindParam("classId", $classId);
        $stmt->execute();
        $db = null;
        $categories = $stmt->fetchAll(PDO::FETCH_OBJ);

        foreach($categories as $category) {
            echo $category->categoryId;
            $deleteRequest = "DELETE FROM grades WHERE categoryId=:category";
            $db = getConnection();
            $stmt3 = $db->prepare($deleteRequest);
            $stmt3->bindParam('category', $category->categoryId);
            $stmt3->execute();
            $deleteRequest2 = "DELETE FROM category WHERE categoryId=:category";
            $stmt3 = $db->prepare($deleteRequest2);
            $stmt3->bindParam('category', $category->categoryId);
            $stmt3->execute();
            $db = null;
        }
        $deleteRequest3 = "DELETE FROM classes WHERE classId=:classId";
        $db = getConnection();
        $stmt3 = $db->prepare($deleteRequest3);
        $stmt3->bindParam('classId', $classId);
        $stmt3->execute();
        $db = null;

    }
    catch(PDOException $e) 
    {
        echo '{"error":{"text":'. $e->getMessage() .'}}'; 
    }
}

function deleteGrade(){
    $gradeId = Slim::getInstance()->request()->post('gradeId');
    try
    {
        $sql = "DELETE FROM grades WHERE gradeId=:gradeId";
        $db = getConnection();
        $stmt = $db->prepare($sql);
        $stmt->bindParam("gradeId", $gradeId);
        $stmt->execute();
        $db = null;
    }
    catch(PDOException $e) 
    {
        echo '{"error":{"text":'. $e->getMessage() .'}}'; 
    }

}


/**
* Edit a grade for a class
*/
function editGrade(){
    $gradeName = Slim::getInstance()->request()->post('gradeName');
    $percentage = Slim::getInstance()->request()->post('percentage');
    $gradeId = Slim::getInstance()->request()->post('gradeId');
    try {
        $sql = "UPDATE grades SET gradeName = :gradeName, percentage = :percentage WHERE gradeId = :gradeId"; 
        $db = getConnection();
        $stmt = $db->prepare($sql);
        $stmt->bindParam("gradeName", $gradeName);
        $stmt->bindParam("percentage", $percentage);
        $stmt->bindParam("gradeId", $gradeId);
        $stmt->execute();
        $db = null;

    } catch(PDOException $e) {
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
