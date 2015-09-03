<?php
include_once ('publisher.php');
include_once ('constants.php');
class SqlConnect {
	private $connection;
	private $username;
	private $password;
	private $database;
	private $connUrl;
	private $lastCommand;
	public static $lastException;
	private $CONFIG_FILE_LOC = __DIR__;
	/**
	 * Grab default values
	 */
	public function __construct(){
		// Fatal error handler for PHP
		// ister_shutdown_function("Publisher::fatalHandler");
		$this->initialize();
	}
	/**
	 * Private function so it can't be called whenever
	 * Hides all connection info
	 */
	private function initialize(){
		try {
			// See 'constants.php'
			$this->connUrl = SQL_URL;
			$this->username = SQL_USERNAME;
			$this->database = SQL_SCHEMA;
			$this->password = SQL_PASSWORD;
			$this->connection = new mysqli($this->connUrl, $this->username, $this->password, $this->database);
			
			if (mysqli_connect_errno()) {
				throw new Exception("Failure to connect");
			}
		}
		catch (Exception $e) {
			// Zero is the default user, used for very low errors
			
			// Publisher::publishException($e->getTraceAsString(), $e->getMessage(), 0);
			SqlConnect::$lastException = $e;
			var_dump($this);
			return false;
		}
	}

	public function addUser($table, $args) {
		try {
			$cmd = "INSERT INTO user (username,password,email,phone)"
					. "VALUES (" . $args[0] . "," . $args[1] . "," . $args[2] . "," . $args[3] . ");"
			return $this->connection->query($cmd); 
		} catch (Exception $e) {
			return false;
		}
	}
	// /**
	//  * Calls a stored procedure by procedurename and arguments.
	//  * If there are no args, set args to null.
	//  * args must be preformatted, for an example, string Jon Doe msut have quotes around it already.
	//  */
	// public function callStoredProc($procedureName, $args){
	// 	try {
	// 		// Format the procedure
	// 		$cmd = $this->formatProcedureCall($procedureName, $args);
	// 		// Store current command as the last command
	// 		$this->lastCommand = $cmd;
	// 		// Perform the query
	// 		$results = $this->connection->query($cmd);
	// 		return $results;
	// 	}
	// 	catch (Exception $e) {
	// 		// Publisher::publishException($e->getTraceAsString(), $e->getMessage(), $_SESSION['userid']);
	// 		return false;
	// 	}
	// }
	// /**
	//  * Calls a stored procedure and grabs a single item out of the result set
	//  *
	//  * @param string $storedProcName
	//  *        	Name of the procedure to call
	//  * @param array $args
	//  *        	Arguments of procedure to call
	//  * @param string $fieldName
	//  *        	Field value to return
	//  * @return string Returns the value of the desired field
	//  */
	// public function executeScalar($storedProcName, $args, $fieldName){
	// 	// Call the procedure
	// 	$results = $this->callStoredProc($storedProcName, $args);
		
	// 	if ($results == false) {
	// 		Publisher::publishException("ExecuteScalar", "Resultset is boolean [false]", $_SESSION ['userid']);
	// 	}
	// 	// Get the first row
	// 	$row = mysqli_fetch_assoc($results);
		
	// 	// Get the field
	// 	$field = $row [$fieldName];
	// 	return $field;
	// }
	// /**
	//  * Returns the last command given
	//  * 
	//  * @return string The last command sent to the server
	//  */
	// public function getLastCommand(){
	// 	return $this->lastCommand;
	// }
	// public function freeResults(){
	// 	if (mysqli_more_results($this->connection)) {
	// 		$this->connection->next_result();
	// 	}
	// }
	// /**
	//  * Calls a stored procedure and returns a two dimensional array of all of the results
	//  *
	//  * @param string $procedureName
	//  *        	The name of the procedure to call
	//  * @param array $args
	//  *        	The arguments of the procedure
	//  * @return array All of the results from the stored procedure
	//  */
	// public function callStoredProcArr($procedureName, $args){
	// 	// Call the procedure
	// 	$results = $this->callStoredProc($procedureName, $args);
	// 	// Get all rows and store it in an array
	// 	while ($row = mysqli_fetch_assoc($results)) {
	// 		$rows [] = $row;
	// 	}
	// 	return $rows;
	// }
	// /**
	//  * Calls a stored procedure and returns a JSON encoded object of all of the results
	//  *
	//  * @param string $procedureName
	//  *        	The name of the procedure to call
	//  * @param array $args
	//  *        	The arguments of the procedure
	//  * @return array All of the results from the stored procedure
	//  */
	// public function callStoredProcJSON($procedureName, $args){
	// 	// Call the procedure, getting the array
	// 	$rows = $this->callStoredProcArr($procedureName, $args);
	// 	// JSON Encode the array
	// 	return json_encode($rows, JSON_NUMERIC_CHECK);
	// }
	// /**
	//  * Takes in a procedure name and an array of arguments and returns a formatted SQL stored procedure string
	//  *
	//  * @param string $procedureName
	//  *        	The name of the procedure to call
	//  * @param $args The
	//  *        	arguments of the procedure to format
	//  * @return string The fully formatted SQL stored procedure call
	//  */
	// private function formatProcedureCall($procedureName, $args){
	// 	// Format the procedure
		
	// 	// Start with 'Call <procedureName> (
	// 	$cmd = "Call " . $procedureName . "(";
		
	// 	// If there are arguments, formatted in an array
	// 	if ($args != null) {
			
	// 		// If a non-array single element is given for args, convert to single object array
	// 		if (!is_array($args)) {
	// 			$args = array (
	// 					$args );
	// 		}
			
	// 		// Append all arguments together
	// 		$length = count($args);
	// 		for($i = 0; $i < $length; $i++) {
	// 			// If element of arg is a string, put single quotes around it
	// 			if (is_string($args [$i])) {
	// 				$cmd = $cmd . "'" . $args [$i] . "'";
	// 			}
	// 			// If element of arg array is null, put a null string
	// 			else if ($args [$i] === null) {
	// 				$cmd = $cmd . "null";
	// 			}
	// 			// If element of arg array is true, put a 1
	// 			else if ($args [$i] === true) {
	// 				$cmd = $cmd . "1";
	// 			}
	// 			// If element of arg array is false, put a 0
	// 			else if ($args [$i] === false) {
	// 				$cmd = $cmd . "0";
	// 			}
	// 			// Otherwise, just put in whatever the element is
	// 			else {
	// 				$cmd = $cmd . $args [$i];
	// 			}
				
	// 			// Add a comma for each argument
	// 			$cmd = $cmd . ",";
	// 		}
			
	// 		// Remove the last comma
	// 		$cmd = substr($cmd, 0, -1);
	// 	}
		
	// 	// Close the argument parentheses
	// 	$cmd = $cmd . ");";
	// 	return $cmd;
	// }
}
?>