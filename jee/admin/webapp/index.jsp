<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%><!DOCTYPE html>
<html lang="ja" ng-app="WbPortAdmin">
    <head>
        <meta charset="UTF-8">
        <title>あどみそ</title>
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.1/css/bootstrap.min.css">
        <script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.2.26/angular.min.js"></script>
        <script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.2.26/angular-resource.min.js"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/angular-ui-bootstrap/0.11.2/ui-bootstrap-tpls.min.js"></script>
        <script src="./js/app.js"></script>
    </head>
    <%= org.jsoup.Jsoup.parse(config.getServletContext().getResourceAsStream("/index.html"), "UTF-8", "").getElementsByTag("body") %>
</html>
