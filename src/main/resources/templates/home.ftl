<!DOCTYPE html>
<html>
    <head>
        <title>TopTen-Lists.com - Homepage</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
    </head>
    <body>
        <h1>TopTen Lists</h1>
        <h2>The Best TopTen Lists on the Internet!</h2>

        <a href="?cmd=show">Show me the first list!</a><br />
        <br />

        <table border="1">
            <tr>
                <th>Likes</th><th>Description</th><th>Views</th>
            </tr>
            <#list topTenLists as topTenList>
            <tr>
                <td>${topTenList.numLikes}</td>
                <td><a href="?cmd=show&index=${topTenList?index}">${topTenList.description}</a></td>
                <td>${topTenList.numViews}</td>
            </tr>
            </#list>
        </table><br />
        <br />

        <#if loggedIn>
            <a href="?cmd=add">Add a New List</a><br />
            <a href="?cmd=logout">Log Out</a>
        <#else>
            <a href="?cmd=login">Log In</a><br />
            <a href="?cmd=register">Register</a>
        </#if>
    </body>
</html>
