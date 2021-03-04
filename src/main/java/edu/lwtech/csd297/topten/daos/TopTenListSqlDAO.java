package edu.lwtech.csd297.topten.daos;

import java.sql.*;
import java.util.*;

import org.apache.logging.log4j.*;

import edu.lwtech.csd297.topten.pojos.*;

public class TopTenListSqlDAO implements DAO<TopTenList> {
    
    private static final Logger logger = LogManager.getLogger(TopTenListSqlDAO.class.getName());

    private Connection conn = null;

    public TopTenListSqlDAO() {
        this.conn = null;                                   // conn must be created during init()
    }

    public boolean initialize(String initParams) {
        logger.info("Connecting to the database...");

        conn = SQLUtils.connect(initParams);
        if (conn == null) {
            logger.error("Unable to connect to SQL Database: " + initParams);
            return false;
        }
        logger.info("...connected!");

        return true;
    }

    public void terminate() {
        SQLUtils.disconnect(conn);
        conn = null;
    }

    public int insert(TopTenList list) {
        logger.debug("Inserting " + list + "...");

        if (list.getRecID() != -1) {
            logger.error("Attempting to add previously added TopTenList: " + list);
            return -1;
        }

        String query = "INSERT INTO TopTenLists";
        query += " (description, item1, item2, item3, item4, item5, item6, item7, item8, item9, item10, isPublished, ownerID, numViews, numLikes)";
        query += " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        String description = list.getDescription();
        List<String> items = list.getItems();
        String isPublished = list.isPublished() ? "Y" : "N";
        String ownerID = "" + list.getOwnerID();
        String numViews = "" + list.getNumViews();
        String numLikes = "" + list.getNumLikes();

        int recID = SQLUtils.executeSQLInsert(conn, query, "recID", description,
            items.get(0), items.get(1), items.get(2), items.get(3), items.get(4),
            items.get(5), items.get(6), items.get(7), items.get(8), items.get(9),
            isPublished, ownerID, numViews, numLikes);    
        
        logger.debug("TopTenList successfully inserted with recID = " + recID);
        return recID;
    }

    public void delete(int recID) {
        logger.debug("Trying to delete TopTenList with ID: " + recID);

        String query = "DELETE FROM TopTenLists WHERE recID=" + recID;
        SQLUtils.executeSQL(conn, query);
    }
    
    public TopTenList retrieveByID(int recID) {
        logger.debug("Trying to get TopTenList with ID: " + recID);
        
        String query = "SELECT recID, description,";
        query += " item1, item2, item3, item4, item5, item6, item7, item8, item9, item10, isPublished, ownerID, numViews, numLikes";
        query += " FROM TopTenLists WHERE recID=" + recID;

        List<SQLRow> rows = SQLUtils.executeSQL(conn, query);
        
        if (rows != null) {
            logger.debug("Found list!");
        } else {
            logger.debug("Did not find list.");
            return null;
        }
        
        SQLRow row = rows.get(0);
        TopTenList toptenlist = convertRowToList(row);
        return toptenlist;
    }
    
    public TopTenList retrieveByIndex(int index) {
        logger.debug("Trying to get TopTenList with index: " + index);
        
        index++;                                    // SQL uses 1-based indexes

        if (index < 1)
            return null;

        String query = "SELECT recID, description,";
        query += " item1, item2, item3, item4, item5, item6, item7, item8, item9, item10, isPublished, ownerID, numViews, numLikes";
        query += " FROM TopTenLists ORDER BY recID LIMIT " + index;

        List<SQLRow> rows = SQLUtils.executeSQL(conn, query);
        
        if (rows == null) {
            logger.debug("Did not find list.");
            return null;
        }
        
        SQLRow row = rows.get(rows.size()-1);
        TopTenList toptenlist = convertRowToList(row);
        return toptenlist;
    }
    
    public List<TopTenList> retrieveAll() {
        logger.debug("Getting all TopTenLists...");
        
        String query = "SELECT recID, description,";
        query += " item1, item2, item3, item4, item5, item6, item7, item8, item9, item10, isPublished, ownerID, numViews, numLikes";
        query += " FROM TopTenLists ORDER BY recID";

        List<SQLRow> rows = SQLUtils.executeSQL(conn, query);
        
        if (rows == null) {
            logger.debug("No lists found!");
            return null;
        }

        List<TopTenList> lists = new ArrayList<>();
        for (SQLRow row : rows) {
            TopTenList toptenlist = convertRowToList(row);
            lists.add(toptenlist);
        }
        return lists;
    }
    
    public List<Integer> retrieveAllIDs() {
        logger.debug("Getting List IDs...");

        String query = "SELECT recID FROM TopTenLists ORDER BY recID";

        List<SQLRow> rows = SQLUtils.executeSQL(conn, query);
        
        if (rows == null) {
            logger.debug("No lists found!");
            return null;
        }
        
        List<Integer> recIDs = new ArrayList<>();
        for (SQLRow row : rows) {
            String value = row.getItem("recID");
            int i = Integer.parseInt(value);
            recIDs.add(i);
        }
        return recIDs;
    }

    public List<TopTenList> search(String keyword) {
        logger.debug("Searching for list with '" + keyword + "'");

        String query = "SELECT recID FROM TopTenLists WHERE ";
        for (int i=1; i <= 10; i++) {
            query += "item" + i + " like '%" + keyword + "%' OR ";
        }        
        query += " ORDER BY recID";

        List<SQLRow> rows = SQLUtils.executeSQL(conn, query);
        
        if (rows == null) {
            logger.debug("No lists found!");
            return null;
        }

        List<TopTenList> lists = new ArrayList<>();
        for (SQLRow row : rows) {
            TopTenList toptenlist = convertRowToList(row);
            lists.add(toptenlist);
        }
        return lists;
    }

    public boolean update(TopTenList list) {
        logger.debug("Updating views and likes for list #" + list.getRecID());

        String query = "UPDATE TopTenLists " + 
                "SET numViews='" + list.getNumViews() + "', numLikes='" + list.getNumLikes() + "' " +
                "WHERE recID='" + list.getRecID() + "'";

        List<SQLRow> rows = SQLUtils.executeSQL(conn, query);

        return (rows != null);
    }

    public int size() {
        logger.debug("Getting the number of rows...");

        String query = "SELECT count(*) FROM TopTenLists";

        List<SQLRow> rows = SQLUtils.executeSQL(conn, query);
        if (rows == null) {
            logger.error("No lists found!");
            return 0;
        }

        String value = rows.get(0).getItem();
        return Integer.parseInt(value);
    }

    // =====================================================================

    private TopTenList convertRowToList(SQLRow row) {
        List<String> items = new ArrayList<>();
        int recID = Integer.parseInt(row.getItem("recID"));
        String description = row.getItem("description");
        for (int i=10; i > 0; i--)                              // Insert items going from #10 down to #1 so that they print out correctly
            items.add(row.getItem("item"+i));
        boolean isPublished = row.getItem("isPublished").equalsIgnoreCase("Y");
        int ownerID = Integer.parseInt(row.getItem("ownerID"));
        int numViews = Integer.parseInt(row.getItem("numViews"));
        int numLikes = Integer.parseInt(row.getItem("numLikes"));
        return new TopTenList(recID, description, items, isPublished, ownerID, numViews, numLikes);
    }

}
