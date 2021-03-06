package edu.lwtech.csd297.topten.daos;

import java.sql.*;
import java.util.*;

import org.apache.logging.log4j.*;

import edu.lwtech.csd297.topten.pojos.*;

public class MemberSqlDAO implements DAO<Member> {
    
    private static final Logger logger = LogManager.getLogger(MemberSqlDAO.class.getName());

    private Connection conn = null;

    public MemberSqlDAO() {
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

    public int insert(Member member) {
        logger.debug("Inserting " + member + "...");

        if (member.getRecID() != -1) {
            logger.error("Attempting to add previously added Member: " + member);
            return -1;
        }

        String query = "INSERT INTO Members (username, password) VALUES (?,?)";

        int recID = SQLUtils.executeSQLInsert(conn, query, "recID", member.getUsername(), member.getPassword());    
        
        logger.debug("Member successfully inserted with ID = " + recID);
        return recID;
    }

    public Member retrieveByID(int recID) {
        logger.debug("Trying to get Member with ID: " + recID);
        
        String query = "SELECT recID, username, password";
        query += " FROM Members WHERE recID=" + recID;

        List<SQLRow> rows = SQLUtils.executeSQL(conn, query);
        
        if (rows != null) {
            logger.debug("Found member!");
        } else {
            logger.debug("Did not find member.");
            return null;
        }
        
        SQLRow row = rows.get(0);
        Member member = convertRowToMember(row);
        return member;
    }
    
    public Member retrieveByIndex(int index) {
        logger.debug("Trying to get Member with index: " + index);
        
        index++;                                    // SQL uses 1-based indexes

        if (index < 1)
            return null;

        String query = "SELECT recID, username, password";
        query += " FROM Members ORDER BY recID LIMIT " + index;

        List<SQLRow> rows = SQLUtils.executeSQL(conn, query);
        
        if (rows == null) {
            logger.debug("Did not find member.");
            return null;
        }
        
        SQLRow row = rows.get(rows.size()-1);
        Member member = convertRowToMember(row);
        return member;
    }
    
    public List<Member> retrieveAll() {
        logger.debug("Getting all Members...");
        
        String query = "SELECT recID, username, password";
        query += " FROM Members ORDER BY recID";

        List<SQLRow> rows = SQLUtils.executeSQL(conn, query);
        
        if (rows == null) {
            logger.debug("No members found!");
            return null;
        }

        List<Member> members = new ArrayList<>();
        for (SQLRow row : rows) {
            Member member = convertRowToMember(row);
            members.add(member);
        }
        return members;
    }
    
    public List<Integer> retrieveAllIDs() {
        logger.debug("Getting all Member IDs...");

        String query = "SELECT recID FROM Members ORDER BY recID";

        List<SQLRow> rows = SQLUtils.executeSQL(conn, query);
        
        if (rows == null) {
            logger.debug("No members found!");
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

    public List<Member> search(String keyword) {
        logger.debug("Searching for member with '" + keyword + "'");

        String query = "SELECT recID, username, password FROM Members WHERE";
        query += " username like ?";
        query += " ORDER BY recID";

        keyword = "%" + keyword + "%";
        List<SQLRow> rows = SQLUtils.executeSQL(conn, query, keyword);
        
        if (rows == null) {
            logger.debug("No members found!");
            return null;
        }

        List<Member> members = new ArrayList<>();
        for (SQLRow row : rows) {
            Member member = convertRowToMember(row);
            members.add(member);
        }
        return members;
    }

    public boolean update(Member member) {
        throw new UnsupportedOperationException("Unable to update existing member in database.");
    }

    public void delete(int recID) {
        logger.debug("Trying to delete Member with ID: " + recID);

        String query = "DELETE FROM Members WHERE recID=" + recID;
        SQLUtils.executeSQL(conn, query);
    }
    
    public int size() {
        logger.debug("Getting the number of rows...");

        String query = "SELECT count(*) FROM Members";

        List<SQLRow> rows = SQLUtils.executeSQL(conn, query);
        if (rows == null) {
            logger.error("No members found!");
            return 0;
        }

        String value = rows.get(0).getItem();
        return Integer.parseInt(value);
    }    

    // =====================================================================

    private Member convertRowToMember(SQLRow row) {
        logger.debug("Converting " + row + " to Member...");
        int recID = Integer.parseInt(row.getItem("recID"));
        String username = row.getItem("username");
        String password = row.getItem("password");
        return new Member(recID, username, password);
    }

}
