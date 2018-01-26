<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<sec:authentication property="principal" var="principal" />

<!DOCTYPE html>
<html lang="ko">
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title>403 Forbidden</title>

	<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
	<link href="https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.3.7/css/bootstrap.min.css" rel="stylesheet">



	<style type="text/css">
		body, html {
			/*padding-top: 50px;*/
			height: 100%;
			background-repeat: no-repeat;
			background-image: linear-gradient(rgb(104, 145, 162), rgb(12, 97, 33));
		}

		h2 {
			color: #FFF;
		}

		.panel-body > div {
			padding: 10px 0;
		}
	</style>
</head>

<body>
	<div class="container">
		<h2>Access Denied</h2>
		<div class="panel panel-default">
			<div class="panel-body">
				<div>You don't have permission to access on this page</div>
			</div>
		</div>

		<div class="text-center">
			<a class="btn btn-default btn-logout" href="/mypage" role="button">Move to mypage</a>
			<a class="btn btn-default btn-logout" href="/logout" role="button">LOGOUT</a>
		</div>
	</div>
</body>
</html>
