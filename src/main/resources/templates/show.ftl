<!DOCTYPE html>
<html>
    <head>
        <title>TopTen-List.com - ${topTenList.description}</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
    </head>
    <body>
        <h1>TopTen List #${listNumber}</h1>
        <h2>${topTenList.description}</h2>

        <h3>Likes: ${topTenList.numLikes}</h3>

        <ol start=10 reversed>
        <#list topTenList.items as item>
            <li>${item}</li>
        </#list>
        </ol>

        <a href="?cmd=like&index=${listNumber-1}&id=${topTenList.recID?c}">Like</a> &nbsp; &nbsp;
        <a href="?cmd=show&index=${prevIndex}">Previous</a> &nbsp; &nbsp;
        <a href="?cmd=show&index=${nextIndex}">Next</a><br/>
        <br />

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
