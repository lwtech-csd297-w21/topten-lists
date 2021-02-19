package edu.lwtech.csd297.topten;

import java.util.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import edu.lwtech.csd297.topten.daos.*;
import edu.lwtech.csd297.topten.pojos.*;

class TopTenListMemoryDAOTests {

    private static final int FIRST_REC_ID = 1001;

    private DAO<TopTenList> topTenListDAO;

    private TopTenList romanList;

    @BeforeEach
    void setUp() {
        String description;
        List<String> items;
        int owner = 1;

        description = "Top 10 Favorite Roman Numerals";
        items = Arrays.asList(
            "X",
            "IX",
            "VIII",
            "VII",
            "VI",
            "V",
            "IV",
            "III",
            "II",
            "I"
        );
        romanList = new TopTenList(description, items, owner);

        topTenListDAO = new TopTenListMemoryDAO();
        topTenListDAO.initialize("");  // Params ignored for memory DAO
    }

    @AfterEach
    void tearDown() {
        topTenListDAO.terminate();
    }

    @Test
    void testInitialize() {
        Exception ex = assertThrows(IllegalArgumentException.class,
            () -> { topTenListDAO.initialize(null); }
        );
        assertTrue(ex.getMessage().contains("null"));
    }

    @Test
    void testInsert() {
        Exception ex = null;

        assertEquals(3, topTenListDAO.size());
        int listID = topTenListDAO.insert(romanList);        // Add a second copy of the roman list
        assertTrue(listID > 0);
        assertEquals(4, topTenListDAO.size());

        ex = assertThrows(IllegalArgumentException.class,
            () -> { topTenListDAO.insert(null); }
        );
        assertTrue(ex.getMessage().contains("null"));

        ex = assertThrows(IllegalArgumentException.class,
            () -> { topTenListDAO.insert(romanList); }
        );
        assertTrue(ex.getMessage().contains("already"));

    }

    @Test
    void testRetrieveByID() {
        Exception ex = null;

        TopTenList list = topTenListDAO.retrieveByID(FIRST_REC_ID);
        assertEquals(1001, list.getRecID());
        list = topTenListDAO.retrieveByID(FIRST_REC_ID+1);
        assertEquals(1002, list.getRecID());

        ex = assertThrows(IllegalArgumentException.class,
            () -> { topTenListDAO.retrieveByID(-666); }
        );
        assertTrue(ex.getMessage().contains("negative"));
    }

    @Test
    void testRetrieveByIndex() {
        Exception ex = null;

        TopTenList list = topTenListDAO.retrieveByIndex(0);
        assertEquals(FIRST_REC_ID, list.getRecID());
        list = topTenListDAO.retrieveByIndex(1);
        assertEquals(FIRST_REC_ID+1, list.getRecID());

        ex = assertThrows(IllegalArgumentException.class,
            () -> { topTenListDAO.retrieveByIndex(-666); }
        );
        assertTrue(ex.getMessage().contains("negative"));
    }

    @Test
    void testRetrieveAll() {
        List<TopTenList> allLists = new ArrayList<>();
        allLists = topTenListDAO.retrieveAll();
        assertEquals(3, allLists.size());
    }

    @Test
    void testRetrieveAllIDs() {
        List<Integer> ids = topTenListDAO.retrieveAllIDs();
        assertEquals(3, ids.size());
    }

    @Test
    void testSearch() {
        Exception ex = null;

        List<TopTenList> lists = topTenListDAO.search("Roman");
        assertEquals(1, lists.size());
        lists = topTenListDAO.search("Flatworms");
        assertEquals(0, lists.size());

        ex = assertThrows(IllegalArgumentException.class,
            () -> { topTenListDAO.search(null); }
        );
        assertTrue(ex.getMessage().contains("null"));
    }

    @Test
    void testUpdate() {
        Exception ex = null;

        TopTenList list = topTenListDAO.retrieveByID(FIRST_REC_ID);
        list.publish(1);
        topTenListDAO.update(list);
        list = topTenListDAO.retrieveByID(FIRST_REC_ID);
        assertTrue(list.isPublished());

        assertFalse(topTenListDAO.update(romanList));
        
        ex = assertThrows(IllegalArgumentException.class,
            () -> { topTenListDAO.update(null); }
        );
        assertTrue(ex.getMessage().contains("null"));
    }

    @Test
    void testDelete() {
        Exception ex = null;

        int romanID = topTenListDAO.search("Roman").get(0).getRecID();
        topTenListDAO.delete(romanID);
        assertNull(topTenListDAO.retrieveByID(romanID));
        topTenListDAO.delete(666);

        ex = assertThrows(IllegalArgumentException.class,
            () -> { topTenListDAO.delete(-666); }
        );
        assertTrue(ex.getMessage().contains("negative"));
    }

}
