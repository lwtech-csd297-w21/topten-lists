<!DOCTYPE html>
<html>
    <head>
        <title>Add New TopTen List</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
    </head>
    <body>

        <h1>Add New TopTen List</h1>

        <#if loggedIn>
            <form action="?cmd=create" method="post">

                Description: <input type="text" name="description" size=60></input><br />
                <br />
                Item 10: <input type="text" name="item10" size=40></input><br />
                Item 9: <input type="text" name="item9" size=40></input><br />
                Item 8: <input type="text" name="item8" size=40></input><br />
                Item 7: <input type="text" name="item7" size=40></input><br />
                Item 6: <input type="text" name="item6" size=40></input><br />
                Item 5: <input type="text" name="item5" size=40></input><br />
                Item 4: <input type="text" name="item4" size=40></input><br />
                Item 3: <input type="text" name="item3" size=40></input><br />
                Item 2: <input type="text" name="item2" size=40></input><br />
                Item 1: <input type="text" name="item1" size=40></input><br />

                <input type="submit" value="Submit" />
                <input class="button" type="button" onclick="window.location.replace('/topten')" value="Cancel" />
            </form>
        <#else>
            Please log in to your account before trying to create a new TopTen List.</br>
            <a href="?cmd=login">Log In</a>
        </#if>

    </body>
</html>
