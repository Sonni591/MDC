<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="d" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>


<%@ page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreService" %>

<%
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
%>


<html>
	<head>
		<title>Geek Quest</title>
	</head>

	<body>
		<c:choose>
		<c:when test="${user != null}">
		
		<p>Hello, ${fn:escapeXml(user.email)}!
		You can <a href="${logoutUrl}">sign out</a>. </p>

		<form action="/savecharacter" method="get" >    
		    <div>Name: <input name="name" type="text" value="${player_name}"></div>
		    <div>Character class:  
		     <select name="charclass" size="1">
		      <option  ${selected1} >hobbit</option>
		      <option  ${selected2} >dwarf</option>
		      <option  ${selected3} >mage</option>
		      <option  ${selected4} >elf</option>
		    </select></div>
		    <div>Health status:  <input name="health" type="text" value="${player_health}" readonly="readonly"></div>
		    <div>Score:  <input name="score" type="text" value="${player_score}" readonly="readonly"></div>
		    <div>Missions:	
		    	<table>
		    		<td>
	    			<table border = "1">
	    			<thead>
	    					<tr>
	    						<td>Description</td>
	    						
    						</tr>
					</thead>
						<d:forEach  items ="${mission_description}"  var = "mission_desc">
							<tr>
								<td>${mission_desc}</td>
							</tr>
						</d:forEach>
						
					</table>
					</td>
					<td>
					<table border = "1">
	    			<thead>
	    					<tr>
	    						
	    						<td>Accomplished</td>
    						</tr>
					</thead>
						
						<d:forEach  items ="${mission_isAccomplished}"  var = "mission_isAcc">
							<tr>
								<td>${mission_isAcc}</td>
							</tr>
						</d:forEach>
					</table>
					</td>
					</table>
		    </div>
			
			<!-- <div><input name="save" type="button" value="save btn"></div> -->
			<div><input name="save" type="submit" value="save submit"></div>
		</form>

		<img src="${imageUrl}">

	    <form action="<%= blobstoreService.createUploadUrl("/saveimage") %>" method="post" enctype="multipart/form-data">
	        <input type="file" name="myFile">
	        <input type="submit" value="Submit Image">
	    </form>

		</c:when> <c:otherwise>
		<p>Hello!
		<a href="${loginUrl}">Please sign in</a> </p>

		</c:otherwise>

		</c:choose>


	</body>
</html>