package edu.lwtech.csd297.topten;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

import org.apache.logging.log4j.*;
import freemarker.template.*;

import edu.lwtech.csd297.topten.daos.*;
import edu.lwtech.csd297.topten.pojos.*;

@WebServlet(name = "topten", urlPatterns = {"/"}, loadOnStartup = 0)
public class TopTenListsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;        // Unused
    private static final Logger logger = LogManager.getLogger(TopTenListsServlet.class);

    private static final String SERVLET_NAME = "topten-lists";
    private static final String RESOURCES_DIR = "/WEB-INF/classes";
    private static final Configuration freeMarkerConfig = new Configuration(Configuration.getVersion());

    private DAO<Member> membersDAO = null;
    private DAO<TopTenList> listsDAO = null;

    @Override
    public void init(ServletConfig config) throws ServletException {

        super.init(config);

        logger.warn("");
        logger.warn("===========================================================");
        logger.warn("          " + SERVLET_NAME + " init() started");
        logger.warn("               http://localhost:8080/topten/servlet");
        logger.warn("===========================================================");
        logger.warn("");

        String resourcesDir = config.getServletContext().getRealPath(RESOURCES_DIR);
        logger.info("resourcesDir = {}", resourcesDir);

        logger.info("Initializing FreeMarker...");
        String templateDir = resourcesDir + "/templates";
        try {
            freeMarkerConfig.setDirectoryForTemplateLoading(new File(templateDir));
        } catch (IOException e) {
            String msg = "Template directory not found: " + templateDir;
            logger.fatal(msg, e);
            throw new UnavailableException(msg);
        }
        logger.info("Successfully initialized FreeMarker");

        logger.info("Initializing the DAOs...");
        membersDAO = new MemberMemoryDAO();
        listsDAO = new TopTenListMemoryDAO();

        if (!membersDAO.initialize(""))
            throw new UnavailableException("Unable to initialize the MembersDAO.");
        if (!listsDAO.initialize(""))
            throw new UnavailableException("Unable to initialize the ListsDAO.");
        logger.info("Successfully initialized the DAOs!");

        logger.warn("");
        logger.warn("Servlet initialization complete!");
        logger.warn("");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        long startTime = System.currentTimeMillis();

        String logInfo = request.getRemoteAddr() + " " + request.getMethod() + " " + request.getRequestURI();
        logInfo += getSanitizedQueryString(request);

        // Get the cmd parameter from the URI (defaults to 'home')
        String command = request.getParameter("cmd");
        if (command == null)
            command = "home";
        if (command != "health")                        // Don't log "health" commands
            logger.debug("IN - {}", logInfo);

        try {
            // Initialize the variables that control Freemarker's output
            // These should be changed to appropriate values inside of the corresponding case statement below
            String templateName = null;
            TopTenList list = null;
            Map<String, Object> templateDataMap = new HashMap<>();

            // Get the user's session variables (if they exist)
            int ownerID = 0;
            boolean loggedIn = false;
            HttpSession session = request.getSession(false);            // false == don't create a new session if one doesn't exist
            if (session != null) {
                ownerID = getOwnerIDFromSession(session);
                loggedIn = (ownerID > 0);
            }
            templateDataMap.put("owner", ownerID);
            templateDataMap.put("loggedIn", loggedIn);

            // Process the GET command
            switch (command) {
                case "add":
                    templateName = "add.ftl";
                    break;

                case "home":
                    List<TopTenList> topTenLists = listsDAO.retrieveAll();
                    templateDataMap.put("topTenLists", topTenLists);
                    templateName = "home.ftl";
                    break;

                case "login":
                    templateName = "login.ftl";
                    break;

                case "logout":
                    if (session != null) {
                        session.invalidate();
                    }
                    templateName = "confirm.ftl";
                    templateDataMap.put("message", "You have been successfully logged out.<br /><a href='?cmd=home'>Home</a>");
                    break;

                case "register":
                    if (session != null) {
                        session.invalidate();
                    }
                    templateName = "register.ftl";
                    break;

                case "like":
                    int id = parseInt(request.getParameter("id"));
                    if (id < 0)
                        break;

                    list = listsDAO.retrieveByID(id);
                    if (list == null)
                        break;

                    list.addLike();
                    listsDAO.update(list);
                    // FALL THRU TO case "show" !!!

                case "show":
                    int index = parseInt(request.getParameter("index"));
                    if (index < 0)
                        index = 0;

                    int nextIndex= 0;
                    int prevIndex = 0;
                    int numItems = listsDAO.retrieveAllIDs().size();
                    if (numItems > 0) {
                        nextIndex = (index + 1) % numItems;
                        prevIndex = index - 1;
                    }
                    if (prevIndex < 0)
                        prevIndex = numItems-1;

                    list = listsDAO.retrieveByIndex(index);
                    if (list == null)
                        break;

                    templateName = "show.ftl";
                    list.addView();
                    templateDataMap.put("topTenList", list);
                    templateDataMap.put("listNumber", index+1);                   // Java uses 0-based indexes.  Users want to see 1-based indexes.
                    templateDataMap.put("prevIndex", prevIndex);
                    templateDataMap.put("nextIndex", nextIndex);
                    break;

                case "health":
                    try {
                        response.sendError(HttpServletResponse.SC_OK, "OK");
                    } catch (IOException e) {
                        logger.error("IO Error sending health response: ", e);
                    }
                    return;

                default:
                    command = sanitizedString(command);
                    logger.info("Unknown GET command received: {}", command);
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
            }

            if (templateName == null) {
                // Send 404 error response
                try {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                } catch (IOException e) {
                    logger.error("Unable to send 404 response code.", e);
                }
                return;
            }

            // Have Freemarker merge the template and seond out the result
            processTemplate(response, templateName, templateDataMap);

        } catch (IOException e) {
            // Typically, this is because the connection was closed prematurely
            logger.debug("Unexpected I/O exception: ", e);
        } catch (RuntimeException e) {
            logger.error("Unexpected runtime exception: ", e);
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Oh no! Something went wrong. The appropriate authorities have been alerted.");
            } catch (IOException ex) {
                logger.error("Unable to send 500 response code.", ex);
            }
        }
        long time = System.currentTimeMillis() - startTime;
        logger.info("OUT- {} {}ms", logInfo, time);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        long startTime = System.currentTimeMillis();

        String logInfo = request.getRemoteAddr() + " " + request.getMethod() + " " + request.getRequestURI();
        logInfo += getSanitizedQueryString(request);

        // Get the cmd parameter from the URI (defaults to 'home')
        String command = request.getParameter("cmd");
        if (command == null)
            command = "home";
        logger.debug("IN - {}", logInfo);

        try {
            // Initialize the variables that control Freemarker's output
            // These should be changed to appropriate values inside of the corresponding case statement below
            String message = "";
            String username = "";
            String password = "";
            String templateName = "confirm.ftl";
            Map<String, Object> templateDataMap = new HashMap<>();

            // Get the user's session variables (if they exist)
            int ownerID = 0;
            boolean loggedIn = false;
            HttpSession session = request.getSession(false);            // false == don't create a new session if one doesn't exist
            if (session != null) {
                ownerID = getOwnerIDFromSession(session);
                loggedIn = (ownerID > 0);
            }
            templateDataMap.put("owner", ownerID);
            templateDataMap.put("loggedIn", loggedIn);

            switch (command) {
                case "create":
                    TopTenList newList = getTopTenListFromRequest(request, ownerID);

                    if (newList == null) {
                        logger.info("Create request ignored because one or more fields were empty.");
                        message = "Your new TopTenList was not created because one or more fields were empty.<br /><a href='?cmd=home'>Home</a>";
                        break;
                    }

                    if (listsDAO.insert(newList) > 0)
                        message = "Your new TopTen List has been created successfully.<br /><a href='?cmd=home'>Home</a>";
                    else
                        message = "There was a problem adding your list to the database.<br /><a href='?cmd=home'>Home</a>";
                    break;

                case "login":
                    username = request.getParameter("username");
                    password = request.getParameter("password");

                    List<Member> matchingMembers = membersDAO.search(username);
                    if (matchingMembers == null || matchingMembers.isEmpty()) {
                        message = "We do not have a member with that username on file. Please try again.<br /><a href='?cmd=login'>Log In</a>";
                        templateDataMap.put("message", message);
                        templateDataMap.put("loggedIn", false);
                        break;
                    }

                    Member member = matchingMembers.get(0);
                    if (member.getPassword().equals(password)) {
                        ownerID = member.getRecID();
                        loggedIn = true;

                        session = request.getSession(true);         // true == Create a new session for this user
                        session.setAttribute("owner", ownerID);

                        message = "You have been successfully logged in to your account.<br /><a href='?cmd=show'>Show Lists</a>";
                    } else {
                        message = "Your password did not match what we have on file. Please try again.<br /><a href='?cmd=login'>Log In</a>";
                    }

                    templateDataMap.put("loggedIn", loggedIn);
                    templateDataMap.put("message", message);
                    break;

                case "register":
                    username = request.getParameter("username");
                    password = request.getParameter("password");

                    List<Member> registeredMembers = membersDAO.search(username);
                    if (registeredMembers != null && !registeredMembers.isEmpty()) {
                        message = "That username is already registered here. Please use a different username.<br /><a href='?cmd=login'>Log In</a>";
                        templateDataMap.put("message", message);
                        break;
                    }

                    member = new Member(username, password);
                    membersDAO.insert(member);

                    message = "Welcome to the TopTen-Lists server! You are now a registered member. Please <a href='?cmd=login'>log in</a>.";
                    templateDataMap.put("message", message);
                    break;

                default:
                    command = sanitizedString(command);
                    logger.info("Unknown POST command received: {}", command);
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
            }

            templateDataMap.put("message", message);

            // Have Freemarker merge the template and seond out the result
            processTemplate(response, templateName, templateDataMap);

        } catch (IOException e) {
            // Typically, this is because the connection was closed prematurely
            logger.debug("Unexpected I/O exception: ", e);
        } catch (RuntimeException e) {
            logger.error("Unexpected runtime exception: ", e);
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Oh no! Something went wrong. The appropriate authorities have been alerted.");
            } catch (IOException ex) {
                logger.error("Unable to send 500 response code.", e);
            }
        }
        long time = System.currentTimeMillis() - startTime;
        logger.info("OUT- {} {}ms", logInfo, time);
    }

    @Override
    public void destroy() {
        listsDAO.terminate();
        membersDAO.terminate();
        logger.warn("-----------------------------------------");
        logger.warn("  " + SERVLET_NAME + " destroy() completed!");
        logger.warn("-----------------------------------------");
        logger.warn(" ");
    }

    @Override
    public String getServletInfo() {
        return "topten-lists Servlet";
    }

    // =================================================================

    private static int parseInt(String s) {
        int i = -1;
        if (s != null) {
            try {
                i = Integer.parseInt(s);
            } catch (NumberFormatException e) {
                i = -2;
            }
        }
        return i;
    }

    private static int getOwnerIDFromSession(HttpSession session) {
        int ownerID = 0;
        try {
            ownerID = (Integer)session.getAttribute("owner");
        } catch (NumberFormatException e) {
            ownerID = -1;
        }
        return ownerID;
    }

    private static void processTemplate(HttpServletResponse response, String template, Map<String, Object> model) {
        logger.debug("Processing Template: " + template);

        try (PrintWriter out = response.getWriter()) {
            Template view = freeMarkerConfig.getTemplate(template);
            view.process(model, out);
        } catch (TemplateException | MalformedTemplateNameException e) {
            logger.error("Template Error: ", e);
        } catch (IOException e) {
            logger.error("IO Error: ", e);
        }
    }

    private static TopTenList getTopTenListFromRequest(HttpServletRequest request, int owner) {

        String description = request.getParameter("description");
        if (description == null)
            return null;

        List<String> items = new ArrayList<>();
        for (int i=10; i >= 1; i--) {                       // Insert items from 10 to 1 for Freemarker's "list-as" loop
            String item = request.getParameter("item" + i);
            if (item == null || item.isEmpty())
                return null;
            items.add(item);
        }

        return new TopTenList(description, items, owner);
    }


    private String getSanitizedQueryString(HttpServletRequest request) {
        String queryString = request.getQueryString();
        if (queryString == null)
            return "";

        try { 
            queryString = URLDecoder.decode(queryString, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // Should never happen
            throw new IllegalStateException(e);
        }
        queryString = sanitizedString(queryString);
        return queryString;
    }

    private String sanitizedString(String s) {
        return s.replaceAll("[\n|\t]", "_");
    }

}
