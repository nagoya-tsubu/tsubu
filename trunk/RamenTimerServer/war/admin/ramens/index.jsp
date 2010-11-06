<%@page pageEncoding="UTF-8" isELIgnored="false" session="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="f" uri="http://www.slim3.org/functions"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>admin ramens Index</title>
</head>
<body>

<p>
<a href="${f:url('new')}">New</a>
</p>

<table>
<c:if test="${fn:length(ramens) > 0}">
<thead>
<tr><th>Name</th><th>Jan</th><th>BoilTime</th><th>Image</th><th>JSON</th></tr>
</thead>
</c:if>
<tbody>
<c:forEach var="ramen" items="${ramens}">
<tr>
<td>${f:h(ramen.name)}</td>
<td>${f:h(ramen.jan)}</td>
<td>${f:h(ramen.boilTime)}</td>
<td><a href="${f:h(ramen.imageUrl)}">Show</a></td>
<td><a href="/api/ramens/show?jan=${f:h(ramen.jan)}">Show</a></td>
</tr>
</c:forEach>
</tbody>
</table>
</body>
</html>
