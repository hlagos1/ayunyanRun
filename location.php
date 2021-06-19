<?php

//$json_params = file_get_contents("php://input");
//version1
$json_params = file_get_contents('php://input');
$decoded_params = json_decode($json_params);


$latitude = $decoded_params->lat;
$longitude = $decoded_params->lon;
$company = $decoded_params->company;


$conn = new mysqli("localhost","chiwitec_veriapp","abcd1234","chiwitec_veriapp");//mysqli("localhost","user","password","database_name")//

if($conn ->connect_error){
die("Connection Failure".$conn->connect_error);

}

$sql = "INSERT INTO `location` (`id`, `lat`, `lon`, `company`) VALUES (NULL, $latitude, $longitude, '$company')";
// $sql = "INSERT INTO `location` (`company`,`lat`, `lon`,) VALUES ($company,$latitude,$longitude)"; // dont do this - not secure

//INSERT INTO `location` (`lat`, `lon`, `company`) VALUES (100, 100, 'ABC')

if($conn->query($sql)==TRUE){

		$data = array('success' => true);
		echo(json_encode($data));

}else{
		$data = array('success' => false,"error"=>$conn->error);
		echo(json_encode($data));
}


