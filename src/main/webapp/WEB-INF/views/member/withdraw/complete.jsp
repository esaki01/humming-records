<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0, minimum-scale=1.0">
<title>Humming | 退会完了</title>
<link rel="stylesheet" href="../../../../css/style-withdraw.css" />
<link rel="stylesheet" href="../../../../css/header.css" />
<link rel="stylesheet" href="../../../../css/bootstrap.min.css" />
</head>
<body style="background-color: whitesmoke;">
    <div class="container-fluid">
        <%@ include file="../../common/header.jsp"%>
        <div class="wrapper">
            <div class="panel panel-default">
                <div class="panel-heading">
                    <b>退会の手続き</b>
                </div>
                <div class="panel-body withdraw">
                    <div id="title">
                        退会手続きが完了しました。<br>ご利用ありがとうございました。
                    </div>
                    <div id="back">
                        <a href="/" class="btn btn-default">Humming トップへ</a>
                    </div>
                </div>
            </div>
        </div>
        <%@ include file="../../common/footer.jsp"%>
    </div>
    <script src="../../js/jquery-3.1.1.min.js"></script>
    <script src="../../js/bootstrap.min.js"></script>
</body>
</html>