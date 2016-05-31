<%@ taglib uri="/WEB-INF/tld/c.tld" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>lucene网页测试</title>
<style type="text/css">
table.gridtable {
	font-family: verdana,arial,sans-serif;
	font-size:11px;
	color:#333333;
	border-width: 1px;
	border-color: #666666;
	border-collapse: collapse;
}
table.gridtable th {
	border-width: 1px;
	padding: 8px;
	border-style: solid; 
	border-color: #666666;
	background-color: #dedede;
}
table.gridtable td {
	border-width: 1px;
	padding: 8px;
	border-style: solid;
	border-color: #666666;
	background-color: #ffffff;
}
</style>
		<script type="text/javascript">
			function changePage(value){
				document.getElementById("changepage").value=value;
				document.getElementByIdx_x('htmlSearch').submit();
			}
		</script>
	</head>
	<body>
	<center>
	<h2><font color="blue"> 本地网页lucene索引查询+pangRank 实例测试</font></h2>
		<form method="POST" action="htmlSearch" id="htmlSearch">
			<input id="changepage" name="changepage" type="hidden">
			<input id="startparam" name="startparam" type="hidden" value="${startparam }">
			<button onclick="changePage('p')">上一页</button>
			<button onclick="changePage('n')">下一页</button>
			<input id="searchkey" name="searchkey" type="text" value="${searchkey }">
			<button onclick="search()">搜索</button><br/><br/>
			
			<table class="gridtable">
				<tr>
				    <th>搜索链接pageRank值</th>
					<th>序号</th>
					<th>标题</th>
					<th>路径</th>
					<th>描述</th>
					
				</tr>
				<c:forEach items="${dataList}" var="info" varStatus="status">
					<tr>
					    <td>${info.pr}</td>
						<td>${info.id}</td>
						<td>${info.title}</td>
						<td> <a href="${info.path}">${info.path}</a></td>
						<td width="550px">${info.description}</td>
					</tr>
				</c:forEach>
			</table>
		</form>
	</center>
	</body>
</html>
