package edu.lwtech.csd297.topten;

import java.util.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import edu.lwtech.csd297.topten.pojos.*;

class TopTenListTests {

    TopTenList romanList;
    TopTenList planetsList;
    TopTenList starWarsList;

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

        description = "Top 10 Favorite Planets";
        items = Arrays.asList(
            "Hollywood",
            "Neptune",
            "Uranus",
            "Venus",
            "Mercury",
            "Earth",
            "Mars",
            "Jupiter",
            "Saturn",
            "Pluto!!!"
        );
        planetsList = new TopTenList(description, items, owner);

        description = "Top 10 Favorite Star Wars Movies";
        items = Arrays.asList(
            "III: Revenge of the Sith",
            "II: Attack of the Clones",
            "VIII: The Last Jedi",
            "IX: The Rise of Skywalker",
            "VI: Return of the Jedi",
            "I: The Phantom Menace",
            "VII: The Force Awakens",
            "The Mandelorian Compilation",
            "IV: A New Hope",
            "V: The Empire Strikes Back"
        );
        starWarsList = new TopTenList(description, items, owner);
    }

    @Test
    void testConstructor() {
        Exception ex = null;
        List<String> emptyList = new ArrayList<>();
        List<String> fullList = new ArrayList<>();
        for (int i=0; i < 10; i++) {
            fullList.add("Item " + i);
        }

        ex = assertThrows(IllegalArgumentException.class,
            () -> { new TopTenList(-666, "description", fullList, false, 1, 2, 3); }
        );
        assertTrue(ex.getMessage().contains("recID"));
        ex = assertThrows(IllegalArgumentException.class,
            () -> { new TopTenList(123, null, fullList, false, 1, 2, 3); }
        );
        assertTrue(ex.getMessage().contains("description is null"));
        ex = assertThrows(IllegalArgumentException.class,
            () -> { new TopTenList(123, "", fullList, false, 1, 2, 3); }
        );
        assertTrue(ex.getMessage().contains("description is empty"));
        ex = assertThrows(IllegalArgumentException.class,
            () -> { new TopTenList(123, "description", null, false, 1, 2, 3); }
        );
        assertTrue(ex.getMessage().contains("item list is null"));
        ex = assertThrows(IllegalArgumentException.class,
            () -> { new TopTenList(123, "description", emptyList, false, 1, 2, 3); }
        );
        assertTrue(ex.getMessage().contains("less than 10 items"));
        ex = assertThrows(IllegalArgumentException.class,
            () -> { new TopTenList(123, "description", fullList, false, -1, 2, 3); }
        );
        assertTrue(ex.getMessage().contains("ownerID"));
        ex = assertThrows(IllegalArgumentException.class,
            () -> { new TopTenList(123, "description", fullList, false, 1, -2, 3); }
        );
        assertTrue(ex.getMessage().contains("numViews"));
        ex = assertThrows(IllegalArgumentException.class,
            () -> { new TopTenList(123, "description", fullList, false, 1, 2, -3); }
        );
        assertTrue(ex.getMessage().contains("numLikes"));
    }


    @Test
    void testGetId() {
        assertEquals(-1, romanList.getRecID());
        assertEquals(-1, planetsList.getRecID());
        assertEquals(-1, starWarsList.getRecID());
    }

    @Test
    void testSetRecID() {
        Exception ex = null;

        romanList.setRecID(123);
        assertEquals(123, romanList.getRecID());

        ex = assertThrows(IllegalArgumentException.class,
            () -> { romanList.setRecID(-666); }
        );
        assertTrue(ex.getMessage().contains("negative"));

        ex = assertThrows(IllegalArgumentException.class,
            () -> { romanList.setRecID(666); }
        );
        assertTrue(ex.getMessage().contains("already been added"));

    }

    @Test
    void testGetDescription() {
        assertEquals("Top 10 Favorite Roman Numerals", romanList.getDescription());
        assertEquals("Top 10 Favorite Planets", planetsList.getDescription());
        assertEquals("Top 10 Favorite Star Wars Movies", starWarsList.getDescription());
    }

    @Test
    void getItems() {
        List<String> items = null;
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
        assertEquals(items, romanList.getItems());
    }

    @Test
    void testGetOwnerID() {
        assertEquals(1, romanList.getOwnerID());
    }

    @Test
    void testPublish() {
        assertFalse(romanList.isPublished());
        assertFalse(planetsList.isPublished());
        starWarsList.publish(666);
        assertFalse(starWarsList.isPublished());
        starWarsList.publish(1);
        assertTrue(starWarsList.isPublished());
    }

    @Test
    void testGetNumViews() {
        assertEquals(0, romanList.getNumViews());
        romanList.addView();
        assertEquals(1, romanList.getNumViews());
    }

    @Test
    void testGetNumLikes() {
        assertEquals(0, romanList.getNumLikes());
        romanList.addLike();
        assertEquals(1, romanList.getNumLikes());
    }
    
    @Test
    void testToString() {
        assertTrue(romanList.toString().contains("Top 10"));
        assertTrue(romanList.toString().contains("Draft"));
        assertTrue(planetsList.toString().contains("Top 10"));
        assertTrue(planetsList.toString().contains("Draft"));
        starWarsList.publish(1);
        assertTrue(starWarsList.toString().contains("Top 10"));
        assertTrue(starWarsList.toString().contains("Published"));
    }

}
