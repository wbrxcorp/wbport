<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%><!DOCTYPE html>
<html lang="ja" ng-app="WbPort">
    <head>
        <meta charset="UTF-8">
        <title>WBPORT.com</title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">

        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.1/css/bootstrap.min.css">
        <link rel="stylesheet" href="./css/style.css">

        <script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.2.26/angular.min.js"></script>
        <script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.2.26/angular-resource.min.js"></script>
        <script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.2.26/angular-sanitize.min.js"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/angular-i18n/1.2.10/angular-locale_ja-jp.min.js"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/angular-ui-bootstrap/0.11.2/ui-bootstrap-tpls.min.js"></script>
        <script src="./js/app.js"></script>
    </head>
    <%= org.jsoup.Jsoup.parse(config.getServletContext().getResourceAsStream(org.springframework.web.context.support.WebApplicationContextUtils.getWebApplicationContext(application).getBean("auth").equals("auth")? "/index.html" : "/login.html"), "UTF-8", "").getElementsByTag("body") %>
</html>
