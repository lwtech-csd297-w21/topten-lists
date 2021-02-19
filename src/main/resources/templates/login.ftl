<!DOCTYPE html>
<html>
    <head>
        <title>TopTopTenLists.com - Login</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
    </head>
    <body>
        <h1>Top Top 10 Lists</h1>
        <h2>Log in to your account</h2>

        <#if loggedIn>
            You are already logged in.<br />
            <a href="?cmd=show">See the Lists</a>
        <#else>
            <form action="?cmd=login" method="post">

                User name: <input type="text" name="username" size=60 /><br />
                Password: <input type="password" name="password" size=60 /><br />

                <input type="submit" value="Submit" />
                <input class="button" type="button" onclick="window.location.replace('/topten?cmd=home')" value="Cancel" />
            </form><br />
        </#if>
        <a href="?cmd=register">Register</a><br />
        <a href="?cmd=home">Home</a>
    </body>
</html>
