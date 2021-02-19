package edu.lwtech.csd297.topten;

import java.util.*;
import org.junit.jupiter.api.*;

import edu.lwtech.csd297.topten.daos.*;
import edu.lwtech.csd297.topten.pojos.*;

import static org.junit.jupiter.api.Assertions.*;

class MemberMemoryDAOTests {

    private static final int FIRST_REC_ID = 1001;

    private DAO<Member> memberDAO;

    @BeforeEach
    void setUp() {
        memberDAO = new MemberMemoryDAO();
        memberDAO.initialize("");  // Params ignored for memory DAO
            // Note: init() adds fred, tom, and mary to the database
    }

    @AfterEach
    void tearDown() {
        memberDAO.terminate();
    }

    @Test
    void testInitialize() {
        Exception ex = assertThrows(IllegalArgumentException.class,
            () -> { memberDAO.initialize(null); }
        );
        assertTrue(ex.getMessage().contains("null"));
    }

    @Test
    void testInsert() {
        Exception ex = null;
        Member sally = new Member("Sally", "123456");

        assertEquals(3, memberDAO.size());
        int memberID = memberDAO.insert(sally);
        assertTrue(memberID > 0);
        assertEquals(4, memberDAO.size());

        ex = assertThrows(IllegalArgumentException.class,
            () -> { memberDAO.insert(null); }
        );
        assertTrue(ex.getMessage().contains("null"));

        ex = assertThrows(IllegalArgumentException.class,
            () -> { memberDAO.insert(sally); }
        );
        assertTrue(ex.getMessage().contains("already"));
    }

    @Test
    void testRetrieveByID() {
        Exception ex = null;

        Member member = memberDAO.retrieveByID(FIRST_REC_ID);
        assertEquals(FIRST_REC_ID, member.getRecID());
        member = memberDAO.retrieveByID(FIRST_REC_ID+1);
        assertEquals(FIRST_REC_ID+1, member.getRecID());

        ex = assertThrows(IllegalArgumentException.class,
            () -> { memberDAO.retrieveByID(-666); }
        );
        assertTrue(ex.getMessage().contains("negative"));
    }

    @Test
    void testRetrieveByIndex() {
        Exception ex = null;

        Member member = memberDAO.retrieveByIndex(0);
        assertEquals(FIRST_REC_ID, member.getRecID());
        member = memberDAO.retrieveByIndex(1);
        assertEquals(FIRST_REC_ID+1, member.getRecID());

        ex = assertThrows(IllegalArgumentException.class,
            () -> { memberDAO.retrieveByIndex(-666); }
        );
        assertTrue(ex.getMessage().contains("negative"));
    }

    @Test
    void testRetrieveAll() {
        List<Member> allLists = new ArrayList<>();
        allLists = memberDAO.retrieveAll();
        assertEquals(3, allLists.size());
    }

    @Test
    void testRetrieveAllIDs() {
        List<Integer> ids = memberDAO.retrieveAllIDs();
        assertEquals(3, ids.size());
    }

    @Test
    void testSearch() {
        Exception ex = null;

        List<Member> members = memberDAO.search("fred");
        assertEquals(1, members.size());
        members = memberDAO.search("jerry");
        assertEquals(0, members.size());

        ex = assertThrows(IllegalArgumentException.class,
            () -> { memberDAO.search(null); }
        );
        assertTrue(ex.getMessage().contains("null"));
    }

    @Test
    void testUpdate() {
        Exception ex = null;

        Member tom = memberDAO.search("tom").get(0);
        Member newTom = new Member(tom.getRecID(), "tom", "abcdefgh");
        assertTrue(memberDAO.update(newTom));
        tom = memberDAO.search("tom").get(0);
        assertEquals("abcdefgh", tom.getPassword());
        
        Member tim = new Member(100, "tim", "xyzxyz");
        assertFalse(memberDAO.update(tim));
        
        ex = assertThrows(IllegalArgumentException.class,
            () -> { memberDAO.update(null); }
        );
        assertTrue(ex.getMessage().contains("null"));
    }

    @Test
    void testDelete() {
        Exception ex = null;

        int fredsID = memberDAO.search("fred").get(0).getRecID();
        memberDAO.delete(fredsID);
        assertNull(memberDAO.retrieveByID(fredsID));
        memberDAO.delete(666);

        ex = assertThrows(IllegalArgumentException.class,
            () -> { memberDAO.delete(-666); }
        );
        assertTrue(ex.getMessage().contains("negative"));
    }

    @Test
    void testSize() {
        assertEquals(3, memberDAO.size());
    }

}
