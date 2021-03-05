<!DOCTYPE html>
<html>
    <head>
        <title>TopTen-List.com - ${topTenList.description}</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link rel="stylesheet" href="/static/topten.css">
    </head>
    <body>
        <table>
            <thead>
                <tr>
                    <th scope="col" colspan=2>${topTenList.description}</th>
                </tr>
                <tr>
                    <th scope="col">#</th><th scope="col">Item</th>
                </tr>
            </thead>
            <tbody>
                <#assign count = 10>
                <#list topTenList.items as item>
                <tr>
                    <td class="item-count">${count}</td>
                    <td class="item-description">${item}</td>
                    <#assign count = count - 1>
                </tr>
                </#list>
            </tbody>
        </table>

        <p>
        <a href="?cmd=show&index=${prevIndex}"><img src="/static/previous.png" width=30/></a> &nbsp;
        Likes: ${topTenList.numLikes}
            <input type="button" value="Like" onclick="location.href='?cmd=like&index=${listNumber-1}&id=${topTenList.recID?c}';" /> &nbsp;
        <a href="?cmd=show&index=${nextIndex}"><img src="/static/next.png" width=30 /></a>
        </p>

        <hr />

        <#if loggedIn>
            <a href="?cmd=add">Add a New List</a><br />
            <a href="?cmd=logout">Log Out</a><br />
        <#else>
            <a href="?cmd=login">Log In</a><br />
            <a href="?cmd=register">Register</a><br />
        </#if>
        <a href="?cmd=home">Home</a>
    </body>
</html>
