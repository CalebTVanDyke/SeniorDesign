<?php
	/**
	 * Consists of a variety useful static functions
	 */
class authUtil {
	/**
	 * Creates a random salt of length $size
	 *
	 * @param
	 *        	size - the length of salt
	 * @return string
	 *
	 */
	public static function makeSalt($size){
		return mcrypt_create_iv($size, MCRYPT_DEV_URANDOM);
	}
	/**
	 * Makes a hashed password with salt
	 *
	 * @param
	 *        	string password
	 * @param
	 *        	string username
	 * @param
	 *        	salt user's salt
	 *        	
	 */
	public static function makePassHash($algo, $salt, $username, $password){
		$hash = hash($algo, $salt . $username . $password);
		return $hash;
	}
	/**
	 * Generates a random password of a certain length
	 *
	 * @param number $length
	 *        	The length of the desired random password
	 * @return string The randomly generated password
	 */
	public static function generateRandomPassword($length = 8){
		// Define characters to choose from
		$characters = '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ!@#$%^&*()_+?><';
		// Initialize the string
		$randomString = '';
		// For every character in the string...
		for($i = 0; $i < $length; $i++) {
			// Randomly pick one character from the list of defined characters
			$randomString .= $characters [rand(0, strlen($characters) - 1)];
		}
		return $randomString;
	}
	/**
	 *
	 * @return true if the hash password matches with the hash for the username and password
	 *        
	 */
	public static function verifyPass($algo, $hash, $salt, $username, $password){
		$attempt = authUtil::makePassHash($algo, $salt, $username, $password);
		// Slow equals, so check functions in linear time (more secure than traditional equals)
		// Checks if the same size (continues to check equality anyway, for constant time)
		$diff = strlen($hash) ^ strlen($attempt);
		// Iterates through every character and OR's the XOR'ed value of both string's characters at that iterative point
		for($i = 0; $i < strlen($hash) && $i < strlen($attempt); $i++) {
			$diff |= ord($hash [$i]) ^ ord($attempt [$i]);
		}
		// Return whether or not the strings are different
		return $diff === 0;
	}
}