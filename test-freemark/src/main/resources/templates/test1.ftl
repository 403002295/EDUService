<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
    <link rel="stylesheet" href="/plugins/normalize-css/normalize.css" />
    <link rel="stylesheet" href="/plugins/bootstrap/dist/css/bootstrap.css" />
    <link rel="stylesheet" href="/css/page-learing-index.css" />
    <link rel="stylesheet" href="/css/page-header.css" />
</head>
<body>
helle ${name}
<table> <tr>
    <td>序号</td> <td>姓名</td> <td>年龄</td> <td>钱包</td>
</tr>
<#list stus as stu>
    <tr>
        <td>${stu_index + 1}</td>
        <td>${stu.name}</td>
        <td>${stu.age}</td>
        <td>${stu.mondy}</td>
    </tr>
</#list>
</table>
</body>
</html>