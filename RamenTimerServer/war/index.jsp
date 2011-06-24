<%@page pageEncoding="UTF-8" isELIgnored="false" session="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="f" uri="http://www.slim3.org/functions"%>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="Content-Style-Type" content="text/css" />
<meta http-equiv="Content-Script-Type" content="text/javascript" />
<title>みんなのラーメンタイマー | みんなで作ろうラーメン茹で時間</title>
<meta name="description" content="Android向けアプリ「みんなのラーメンタイマー」で登録された商品一覧です。" />
<meta name="keywords" content="Android,アンドロイド,アプリ,みんなのラーメンタイマー,カップラーメン,インスタントラーメン,ラーメンタイマー,タイマー" />
<link href="css/screen.css" rel="stylesheet" type="text/css" media="screen" />
</head>

<body>
<div id="container">
    <div id="top"></div>
    <div id="header">
        <h1 id="logo"><a href="/"><img src="img/web_logo.png" width="552" height="72" alt="みんなのラーメンタイマー" /></a></h1>
        <h2 id="tagline">みんなで作ろうラーメン茹で時間</h2>
        <ul id="headernavi">
            <li><a href="https://market.android.com/details?id=com.androidtsubu.ramentimer" target="_blank"><img src="img/45_avail_market_logo1.png" width="117" height="45" alt="Android Market" /></a></li>
        </ul>
        <!-- /#header --></div>
    <div id="contents">
        <div id="titleArea">
            <h3>登録商品一覧（登録数${count}軒）</h3>
        </div>
        <!--
        <div id="pagerArea">
            <ul>
                <li><a href="#">＜&nbsp;前へ</a></li>
                <li><a href="#">次へ&nbsp;＞</a></li>
            </ul>
        </div>
        -->
        <div id="listArea">
            <table width="100%" cellpadding="0" cellspacing="0">
                <tr>
                    <th scope="col">商品名</th>
                    <th scope="col">コード</th>
                    <th scope="col">秒</th>
                </tr>
                <c:forEach var="ramen" items="${ramens}">
                <tr>
                    <td>${f:h(ramen.name)}</td>
                    <td>${f:h(ramen.jan)}</td>
                    <td>${f:h(ramen.boilTime)}</td>
                </tr>
                </c:forEach>
            </table>
        </div>
        <c:if test="${hasNext}">
            <div id="pagerArea">
                <a href="/?c=${c}">もっと見る</a>
            </div>
        </c:if>
        <!--
            <ul>
                <li><a href="#">＜&nbsp;前へ</a></li>
                <li><a href="#">次へ&nbsp;＞</a></li>
            </ul>
        -->
        <p class="pagetop"><a href="#top">▲このページの先頭へ</a></p>
    <!-- /#contents --></div>
    <div id="footer"> <address> Copyright&copy; <a href="https://sites.google.com/site/androidnagoyatsubu/" target="_blank">tsubu</a>. All Rights Reserved. </address>
        <!-- /#footer --></div>
    <!-- /#container --></div>
</body>
</html>
