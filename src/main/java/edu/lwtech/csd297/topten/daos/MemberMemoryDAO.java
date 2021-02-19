package edu.lwtech.csd297.topten.daos;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.*;

import edu.lwtech.csd297.topten.pojos.*;

// Memory-based DAO class - stores objects in a List.  No persistance.

public class MemberMemoryDAO implements DAO<Member> {

    private static final Logger logger = LogManager.getLogger(MemberMemoryDAO.class.getName());

    private AtomicInteger nextMemberRecID;
    private List<Member> memberDB;          // Our "database" table for Members

    // ----------------------------------------------------------------

    public MemberMemoryDAO() {
        this.nextMemberRecID = new AtomicInteger(1000);
        this.memberDB = new ArrayList<>();
    }

    // ----------------------------------------------------------------

    public boolean initialize(String initParams) {
        if (initParams == null)
            throw new IllegalArgumentException("init: initParams cannot be null");
        logger.debug("Initializing Member Memory DAO with: '{}'", initParams);

        addDemoMemberData();
        return true;
    }

    public void terminate() {
        logger.debug("Terminating Member Memory DAO...");
        memberDB = null;
    }

    public int insert(Member member) {
        if (member == null)
            throw new IllegalArgumentException("insert: member cannot be null");
        if (member.getRecID() != -1)
            throw new IllegalArgumentException("insert: member is already in database (recID != -1)");
        logger.debug("Inserting " + member + "...");

        member.setRecID(nextMemberRecID.incrementAndGet());
        memberDB.add(member);

        logger.debug("Member successfully inserted!");
        return member.getRecID();
    }

    public void delete(int id) {
        if (id < 0)
            throw new IllegalArgumentException("delete: id cannot be negative");
        logger.debug("Trying to delete member with ID: {} ...", id);

        Member memberFound = null;
        for (Member member : memberDB) {
            if (member.getRecID() == id) {
                memberFound = member;
                break;
            }
        }
        if (memberFound != null) {
            memberDB.remove(memberFound);
            logger.debug("Successfully deleted member with ID: {}", id);
        } else {
            logger.debug("Unable to delete member with ID: {}. Member not found.", id);
        }
    }

    public Member retrieveByID(int id) {
        if (id < 0)
            throw new IllegalArgumentException("retrieveByID: id cannot be negative");
        logger.debug("Getting member with ID: {} ...", id);

        Member memberFound = null;
        for (Member member : memberDB) {
            if (member.getRecID() == id) {
                memberFound = member;
                break;
            }
        }
        return memberFound;
    }

    public Member retrieveByIndex(int index) {
        // Note: indexes are zero-based
        if (index < 0)
            throw new IllegalArgumentException("retrieveByIndex: index cannot be negative");
        logger.debug("Getting member with index: {} ...", index);

        return memberDB.get(index);
    }

    public List<Member> retrieveAll() {
        logger.debug("Getting all items...");
        return new ArrayList<>(memberDB);               // Return copy of Member colleciton
    }

    public List<Integer> retrieveAllIDs() {
        logger.debug("Getting all member IDs...");

        List<Integer> memberIDs = new ArrayList<>();
        for (Member member : memberDB) {
            memberIDs.add(member.getRecID());
        }
        return memberIDs;
    }

    public List<Member> search(String keyword) {
        if (keyword == null)
            throw new IllegalArgumentException("search: keyword cannot be null");
        logger.debug("Searching for members with username containing: '{}'", keyword);

        keyword = keyword.toLowerCase();
        List<Member> membersFound = new ArrayList<>();
        for (Member member : memberDB) {
            if (member.getUsername().toLowerCase().contains(keyword)) {
                membersFound.add(member);
                break;
            }
        }
        logger.debug("Found {} members with the keyword '{}'!", membersFound.size(), keyword);
        return membersFound;
    }

    public int size() {
        return memberDB.size();
    }

    public boolean update(Member member) {
        if (member == null)
            throw new IllegalArgumentException("update: member cannot be null");
        logger.debug("Trying to update member: {} ...", member.getRecID());

        for (int i = 0; i < memberDB.size(); i++) {
            if (memberDB.get(i).getRecID() == member.getRecID()) {
                memberDB.set(i, member);
                logger.debug("Successfully updated member: {} !", member.getRecID());
                return true;
            }
        }
        logger.debug("Unable to update member: {}.  RecID not found.", member);
        return false;
    }

    // =================================================================

    private void addDemoMemberData() {
        logger.debug("Creating demo Members...");

        Member member;
        member = new Member("fred@lwtech.edu", "fred");
        insert(member);
        member = new Member("tom@lwtech.edu", "tom");
        insert(member);
        member = new Member("mary@lwtech.edu", "mary");
        insert(member);

        logger.debug("{} members inserted!", size());
    }

}
