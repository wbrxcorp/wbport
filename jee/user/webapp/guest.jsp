<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%><!DOCTYPE html>
<html lang="ja" ng-app="WbPortGuest">
    <head>
        <meta charset="UTF-8">
        <title>WBPORT.com</title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">

        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.1/css/bootstrap.min.css">

        <script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.2.26/angular.min.js"></script>
        <script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.2.26/angular-resource.min.js"></script>
        <script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.2.26/angular-route.min.js"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/angular-i18n/1.2.10/angular-locale_ja-jp.min.js"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/angular-ui-bootstrap/0.11.2/ui-bootstrap-tpls.min.js"></script>
        <script src="./js/guest.js"></script>
    </head>
    <body class="page page-index-static">
        <div class="navbar navbar-inverse navbar-static-top">
            <div class="container">
                <div class="navbar-header">
                    <a href="/"><span class="navbar-brand">WBPORT.com</span></a>
                </div>
            </div>
        </div>

        <div class="container" style="margin-bottom:2em;">
            <div id="content" ng-view>
            </div>
        </div>
        <footer id="footer">
            <hr>
            <div class="container">
                <div class="row">
                    <div class="col-md-6">
                        <p>Copyright 2014 &copy; <a href="http://www.walbrix.com/jp/">Walbrix Corporation</a></p>
                    </div>
                    <div class="col-md-6">
                        <ul class="list-inline footer-menu">
                            <!--
                                <li><a href="#">Terms</a></li>
                                <li><a href="#">Privacy</a></li>
                            -->
                            <li><a href="http://www.walbrix.com/jp/contact.html">お問い合わせ</a></li>
                        </ul>
                    </div>
                </div>
            </div>
        </footer>
    </body>
</html>
