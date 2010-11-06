<%@page pageEncoding="UTF-8" isELIgnored="false" session="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="f" uri="http://www.slim3.org/functions"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>admin ramens New</title>
</head>
<body>
<form action="/api/ramens/create" method="post" enctype="multipart/form-data">
Name:<br />
<input type="text" name="name" /><br />
Jan:<br />
<input type="text" name="jan" /><br />
BoilTime:<br />
<input type="text" name="boilTime" /><br />
Image:<br />
<input type="file" name="image" /><br />
<input type="submit" value="Create"/>
</form>
</body>
</html>
