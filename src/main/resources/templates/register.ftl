<!DOCTYPE html>
<html>
    <head>
        <title>TopTopTenLists.com - Register</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
    </head>
    <body>
        <h1>Top Top-10 Lists</h1>
        <h2>Create your account:</h2>

        <form action="?cmd=register" method="post">

            User name: <input type="text" name="username" size=60 /><br />
            Password: <input type="text" name="password" size=60 /><br />

            <input type="submit" value="Submit" />
            <input class="button" type="button" onclick="window.location.replace('/topten?cmd=home')" value="Cancel" />
        </form><br />
        <a href="?cmd=login">Already registered? Log in</a><br />
        <a href="?cmd=home">Home</a>
    </body>
</html>
