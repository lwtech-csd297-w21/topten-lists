package edu.lwtech.csd297.topten.daos;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.*;

import edu.lwtech.csd297.topten.pojos.*;

// Memory-based DAO class - stores objects in a List.  No persistance.

public class TopTenListMemoryDAO implements DAO<TopTenList> {

    private static final Logger logger = LogManager.getLogger(TopTenListMemoryDAO.class.getName());

    private AtomicInteger nextListRecID;
    private List<TopTenList> topTenListDB;      // Our "database" table for TopTenLists

    // ----------------------------------------------------------------

    public TopTenListMemoryDAO() {
        this.nextListRecID = new AtomicInteger(1000);
        this.topTenListDB = new ArrayList<>();
    }

    // ----------------------------------------------------------------

    public boolean initialize(String initParams) {
        if (initParams == null)
            throw new IllegalArgumentException("init: initParams cannot be null");
        logger.debug("Initializing TopTenList Memory DAO with: '{}'", initParams);

        addDemoTopTenListData();
        return true;
    }

    public void terminate() {
        logger.debug("Terminating TopTenList Memory DAO...");
        topTenListDB = null;
    }

    public int insert(TopTenList list) {
        if (list == null)
            throw new IllegalArgumentException("insert: list cannot be null");
        if (list.getRecID() != -1)
            throw new IllegalArgumentException("insert: list is already in database (recID != -1)");
        logger.debug("Inserting " + list + "...");

        list.setRecID(nextListRecID.incrementAndGet());
        topTenListDB.add(list);

        logger.debug("Item successfully inserted!");
        return list.getRecID();
    }

    public TopTenList retrieveByID(int id) {
        if (id < 0)
            throw new IllegalArgumentException("retrieveByID: id cannot be negative");
        logger.debug("Getting list with ID: {} ...", id);

        TopTenList listFound = null;
        for (TopTenList list : topTenListDB) {
            if (list.getRecID() == id) {
                listFound = list;
                break;
            }
        }
        return listFound;
    }

    public TopTenList retrieveByIndex(int index) {
        // Note: indexes are zero-based
        if (index < 0)
            throw new IllegalArgumentException("retrieveByIndex: index cannot be negative");
        logger.debug("Getting list with index: {} ...", index);

        return topTenListDB.get(index);
    }

    public List<TopTenList> retrieveAll() {
        logger.debug("Getting all lists ...");
        return new ArrayList<>(topTenListDB);       // Return copy of List colleciton
    }

    public List<Integer> retrieveAllIDs() {
        logger.debug("Getting list IDs...");

        List<Integer> listIDs = new ArrayList<>();
        for (TopTenList list : topTenListDB) {
            listIDs.add(list.getRecID());
        }
        return listIDs;
    }

    public List<TopTenList> search(String keyword) {
        if (keyword == null)
            throw new IllegalArgumentException("search: keyword cannot be null");
        logger.debug("Searching for lists containing: '{}'", keyword);

        keyword = keyword.toLowerCase();
        List<TopTenList> listsFound = new ArrayList<>();
        for (TopTenList list : topTenListDB) {
            if (list.getDescription().toLowerCase().contains(keyword)) {
                listsFound.add(list);
                break;
            }
        }
        logger.debug("Found {} lists with the keyword '{}'!", listsFound.size(), keyword);
        return listsFound;
    }

    public int size() {
        return topTenListDB.size();
    }

    public boolean update(TopTenList list) {
        if (list == null)
            throw new IllegalArgumentException("update: list cannot be null");
        logger.debug("Trying to update list: {} ...", list.getRecID());

        for (int i = 0; i < topTenListDB.size(); i++) {
            if (topTenListDB.get(i).getRecID() == list.getRecID()) {
                topTenListDB.set(i, list);
                logger.debug("Successfully updated list: {} !", list.getRecID());
                return true;
            }
        }
        logger.debug("Unable to update list: {}.  RecID not found.", list.getRecID());
        return false;
    }

    public void delete(int id) {
        if (id < 0)
            throw new IllegalArgumentException("delete: id cannot be negative");
        logger.debug("Trying to delete list with ID: {} ...", id);

        TopTenList listFound = null;
        for (TopTenList list : topTenListDB) {
            if (list.getRecID() == id) {
                listFound = list;
                break;
            }
        }
        if (listFound != null) {
            topTenListDB.remove(listFound);
            logger.debug("Successfully deleted list with ID: {}", id);
        } else {
            logger.debug("Unable to delete list with ID: {}. List not found.", id);
        }
    }

    // =================================================================

    private void addDemoTopTenListData() {
        logger.debug("Creating demo TopTenLists...");

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
        insert(new TopTenList(description, items, owner));

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
        insert(new TopTenList(description, items, owner));

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
        insert(new TopTenList(description, items, owner));

        logger.info(size() + " lists inserted");
    }

}
