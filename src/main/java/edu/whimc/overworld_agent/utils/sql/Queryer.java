package edu.whimc.overworld_agent.utils.sql;


import edu.whimc.overworld_agent.OverworldAgent;
import edu.whimc.overworld_agent.dialoguetemplate.Interaction;
import edu.whimc.overworld_agent.dialoguetemplate.Tag;
import edu.whimc.overworld_agent.dialoguetemplate.models.BuildTemplate;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;

/**
 * Handles storing agent data
 *
 * @author Sam
 */
public class Queryer {

    /**
     * Query for inserting an observation into the database.
     */
    private static final String QUERY_SAVE_NPC =
            "INSERT INTO whimc_agents " +
                    "(time, uuid, username, command, agent_name, agent_skin) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
    /**
     * Query for inserting a progress entry into the database.
     */
    private static final String QUERY_SAVE_SCIENCE_INQUIRY =
            "INSERT INTO whimc_dialog_science " +
                    "(uuid, username, world, time, science_inquiry, agent_response) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
    /**
     * Query for inserting a progress entry into the database.
     */
    private static final String QUERY_SAVE_TAG =
            "INSERT INTO whimc_tags " +
                    "(uuid, username, world, x, y, z, time, tag, active, expiration) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    /**
     * Query for inserting a progress entry into the database.
     */
    private static final String QUERY_SAVE_INTERACTION =
            "INSERT INTO whimc_dialogue_interaction" +
                    "(uuid, username, world, time, interaction, x, y, z) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    /**
     * Query for inserting a build interaction entry into the database.
     */
    private static final String QUERY_SAVE_BUILD_INTERACTION =
            "INSERT INTO whimc_dialogue_builder_interaction" +
                    "(uuid, username, world, x, y, z, time, interaction, build_id) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    /**
     * Query for inserting a template entry into the database.
     */
    private static final String QUERY_SAVE_TEMPLATE =
            "INSERT INTO whimc_build_templates" +
                    "(uuid, username, template_name, start_time, end_time) " +
                    "VALUES (?, ?, ?, ?, ?)";
    //Query for getting build template during session from the database.
    private static final String QUERY_GET_BUILD_TEMPLATE =
            "SELECT * FROM whimc_build_templates "+
                    "WHERE rowid = ?;";

    //Query for getting science tool use during session from the database.
    private static final String QUERY_GET_SESSION_CONVERSATION =
            "SELECT * FROM whimc_dialog_science "+
                    "WHERE uuid=? AND time > ?;";
    private static final String QUERY_MAKE_EXPIRED_INACTIVE =
            "UPDATE whimc_tags " +
                    "SET active=0 " +
                    "WHERE ? > expiration";
    /**
     * Query for making an observation inactive.
     */
    private static final String QUERY_MAKE_TAG_INACTIVE =
            "UPDATE whimc_tags " +
                    "SET active=0 " +
                    "WHERE rowid=? AND active=1";
    private final OverworldAgent plugin;
    private final MySQLConnection sqlConnection;

    public Queryer(OverworldAgent plugin, Consumer<Queryer> callback) {
        this.plugin = plugin;
        this.sqlConnection = new MySQLConnection(plugin);

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            final boolean success = sqlConnection.initialize();
            Bukkit.getScheduler().runTask(plugin, () -> callback.accept(success ? this : null));
        });
    }

    /**
     * Generated a PreparedStatement for saving a new npc.
     *
     * @param connection MySQL Connection
     * @param player player who spawned the agent
     * @param command used to spawn agent
     * @param agentName name of the agent
     * @param agentSkin skin of the agent
     * @return PreparedStatement
     * @throws SQLException
     */
    private PreparedStatement getStatement(Connection connection, Player player, String command, String agentName, String agentSkin) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(QUERY_SAVE_NPC, Statement.RETURN_GENERATED_KEYS);

        statement.setLong(1, System.currentTimeMillis());
        statement.setString(2, player.getUniqueId().toString());
        statement.setString(3, player.getName());
        statement.setString(4, command);
        statement.setString(5, agentName);
        statement.setString(6, agentSkin);
        return statement;
    }


    /**
     * Stores an observation into the database and returns the obervation's ID
     * @param player player who spawned the agent
     * @param command used to spawn agent
     * @param agentName name of the agent
     * @param agentSkin skin of the agent
     * @param callback    Function to call once the observation has been saved
     */
    public void storeNewAgent(Player player, String command, String agentName, String agentSkin, Consumer<Integer> callback) {
        async(() -> {

            try (Connection connection = this.sqlConnection.getConnection()) {
                try (PreparedStatement statement = getStatement(connection, player, command, agentName, agentSkin)) {
                    String query = statement.toString().substring(statement.toString().indexOf(" ") + 1);
                    statement.executeUpdate();

                    try (ResultSet idRes = statement.getGeneratedKeys()) {
                        idRes.next();
                        int id = idRes.getInt(1);

                        sync(callback, id);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
    /**
     * Generated a PreparedStatement for saving a new progress session.
     * @param connection MySQL Connection
     * @param player player using command
     * @param inquiry student inquiry
     * @param response agent response to player
     * @return PreparedStatement
     * @throws SQLException
     */
    private PreparedStatement insertScienceInquiry(Connection connection, Player player, String inquiry, String response) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(QUERY_SAVE_SCIENCE_INQUIRY, Statement.RETURN_GENERATED_KEYS);
        statement.setString(1, player.getUniqueId().toString());
        statement.setString(2, player.getName());
        statement.setString(3, player.getWorld().getName());
        statement.setLong(4, System.currentTimeMillis());
        statement.setString(5, inquiry);
        statement.setString(6, response);
        return statement;
    }

    /**
     * Stores a progress command into the database and returns the obervation's ID
     * @param player player using agent
     * @param inquiry student inquiry to agent
     * @param response agent response to player
     * @param callback    Function to call once the observation has been saved
     */
    public void storeNewScienceInquiry(Player player, String inquiry, String response, Consumer<Integer> callback) {
        async(() -> {

            try (Connection connection = this.sqlConnection.getConnection()) {
                try (PreparedStatement statement = insertScienceInquiry(connection, player, inquiry, response)) {
                    String query = statement.toString().substring(statement.toString().indexOf(" ") + 1);
                    statement.executeUpdate();

                    try (ResultSet idRes = statement.getGeneratedKeys()) {
                        idRes.next();
                        int id = idRes.getInt(1);

                        sync(callback, id);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Generated a PreparedStatement for saving a new progress session.
     * @param connection MySQL Connection
     * @param tag player tag put into overworld
     * @return PreparedStatement
     * @throws SQLException
     */
    private PreparedStatement insertTag(Connection connection, Tag tag) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(QUERY_SAVE_TAG, Statement.RETURN_GENERATED_KEYS);
        statement.setString(1, tag.getPlayer().getUniqueId().toString());
        statement.setString(2, tag.getPlayer().getName());
        statement.setString(3, tag.getPlayer().getWorld().getName());
        statement.setDouble(4, tag.getHoloLocation().getX());
        statement.setDouble(5, tag.getHoloLocation().getY());
        statement.setDouble(6, tag.getHoloLocation().getZ());
        statement.setLong(7, tag.getTagTime().getTime());
        statement.setString(8, tag.getTag());
        statement.setBoolean(9, tag.getActive());
        statement.setLong(10, tag.getExpiration().getTime());
        return statement;
    }

    /**
     * Stores a progress command into the database and returns the obervation's ID
     * @param tag tag player placed
     * @param callback    Function to call once the observation has been saved
     */
    public void storeNewTag(Tag tag, Consumer<Integer> callback) {
        async(() -> {

            try (Connection connection = this.sqlConnection.getConnection()) {
                try (PreparedStatement statement = insertTag(connection, tag)) {
                    statement.executeUpdate();

                    try (ResultSet idRes = statement.getGeneratedKeys()) {
                        idRes.next();
                        int id = idRes.getInt(1);

                        sync(callback, id);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Generated a PreparedStatement for saving a new progress session.
     * @param connection MySQL Connection
     * @param interaction player interaction with agent
     * @return PreparedStatement
     * @throws SQLException
     */
    private PreparedStatement insertInteraction(Connection connection, Interaction interaction) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(QUERY_SAVE_INTERACTION, Statement.RETURN_GENERATED_KEYS);
        statement.setString(1, interaction.getPlayer().getUniqueId().toString());
        statement.setString(2, interaction.getPlayer().getName());
        statement.setString(3, interaction.getPlayer().getWorld().getName());
        statement.setLong(4, interaction.getTime().getTime());
        statement.setString(5, interaction.getInteraction());
        statement.setDouble(6, interaction.getLocation().getX());
        statement.setDouble(7, interaction.getLocation().getY());
        statement.setDouble(8, interaction.getLocation().getZ());
        return statement;
    }

    /**
     * Stores a progress command into the database and returns the obervation's ID
     * @param interaction player interaction with agent
     * @param callback    Function to call once the observation has been saved
     */
    public void storeNewInteraction(Interaction interaction, Consumer<Integer> callback) {
        async(() -> {

            try (Connection connection = this.sqlConnection.getConnection()) {
                try (PreparedStatement statement = insertInteraction(connection, interaction)) {
                    statement.executeUpdate();

                    try (ResultSet idRes = statement.getGeneratedKeys()) {
                        idRes.next();
                        int id = idRes.getInt(1);

                        sync(callback, id);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }


    /**
     * Method to get skills for a player
     * @param player Player to get the skills for
     * @param callback callback to signify process completion
     */
    public void getSessionConversation(Player player, Long sessionStart, Consumer callback){
        HashMap<String, List<String>> conversation = new HashMap<>();
        async(() -> {
            try (Connection connection = this.sqlConnection.getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement(QUERY_GET_SESSION_CONVERSATION)) {
                    statement.setString(1, player.getUniqueId().toString());
                    statement.setLong(2, sessionStart);
                    ResultSet results = statement.executeQuery();
                    while (results.next()) {
                        String world = results.getString("world");
                        String input = results.getString("science_inquiry");
                        String response = results.getString("agent_response");
                        if(!conversation.containsKey(world)){
                            conversation.put(world,new ArrayList<>());
                        }
                        conversation.get(world).add(input);
                        conversation.get(world).add(response);
                    }
                    sync(callback,conversation);
                }
            } catch (SQLException exc) {
                exc.printStackTrace();
            }
        });
    }

    /**
     * Generated a PreparedStatement for saving a new progress session.
     * @param connection MySQL Connection
     * @param interaction player interaction with agent
     * @return PreparedStatement
     * @throws SQLException
     */
    private PreparedStatement insertBuildInteraction(Connection connection, Interaction interaction, int buildID) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(QUERY_SAVE_BUILD_INTERACTION, Statement.RETURN_GENERATED_KEYS);
        statement.setString(1, interaction.getPlayer().getUniqueId().toString());
        statement.setString(2, interaction.getPlayer().getName());
        statement.setString(3, interaction.getPlayer().getWorld().getName());
        statement.setDouble(4, interaction.getLocation().getX());
        statement.setDouble(5, interaction.getLocation().getY());
        statement.setDouble(6, interaction.getLocation().getZ());
        statement.setLong(7, interaction.getTime().getTime());
        statement.setString(8, interaction.getInteraction());
        statement.setInt(9, buildID);
        return statement;
    }

    /**
     * Stores a progress command into the database and returns the obervation's ID
     * @param interaction player interaction with agent
     * @param callback    Function to call once the observation has been saved
     */
    public void storeNewBuildInteraction(Interaction interaction, int buildID, Consumer<Integer> callback) {
        async(() -> {

            try (Connection connection = this.sqlConnection.getConnection()) {
                try (PreparedStatement statement = insertBuildInteraction(connection, interaction, buildID)) {
                    statement.executeUpdate();

                    try (ResultSet idRes = statement.getGeneratedKeys()) {
                        idRes.next();
                        int id = idRes.getInt(1);

                        sync(callback, id);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Generated a PreparedStatement for saving a new progress session.
     * @param connection MySQL Connection
     * @param bt player build template
     * @return PreparedStatement
     * @throws SQLException
     */
    private PreparedStatement insertTemplate(Connection connection, BuildTemplate bt) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(QUERY_SAVE_TEMPLATE, Statement.RETURN_GENERATED_KEYS);
        statement.setString(1, bt.getPlayer().getUniqueId().toString());
        statement.setString(2, bt.getPlayer().getName());
        statement.setString(3, bt.getName());
        statement.setLong(4, bt.getStartTime().getTime());
        statement.setLong(5, bt.getEndTime().getTime());
        return statement;
    }

    /**
     * Stores a progress command into the database and returns the obervation's ID
     * @param bt player build template
     * @param callback    Function to call once the observation has been saved
     */
    public void storeNewTemplate(BuildTemplate bt, Consumer<Integer> callback) {
        async(() -> {
            try (Connection connection = this.sqlConnection.getConnection()) {
                try (PreparedStatement statement = insertTemplate(connection, bt)) {
                    statement.executeUpdate();

                    try (ResultSet idRes = statement.getGeneratedKeys()) {
                        idRes.next();
                        int id = idRes.getInt(1);

                        sync(callback, id);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }


    /**
     * Method to get skills for a player
     * @param buildID id of template to retrieve
     * @param sender sender of command
     * @param callback callback to signify process completion
     */
    public void getBuildTemplate(int buildID, Player sender, Consumer callback){
        async(() -> {
            BuildTemplate template = null;
            try (Connection connection = this.sqlConnection.getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement(QUERY_GET_BUILD_TEMPLATE)) {
                    statement.setInt(1, buildID);
                    ResultSet results = statement.executeQuery();
                    while (results.next()) {
                        String user = results.getString("username");
                        Player creator = Bukkit.getPlayer(user);
                        String templateName = results.getString("template_name");
                        Timestamp startTime = new Timestamp(results.getLong("start_time"));
                        Timestamp endTime = new Timestamp(results.getLong("end_time"));
                        template = new BuildTemplate(plugin, sender, templateName, startTime, endTime, creator);
                    }
                    sync(callback,template);
                }
            } catch (SQLException exc) {
                exc.printStackTrace();
            }
        });
    }

    /**
     * Makes an observation inactive in the database.
     *
     * @param id Id of the observation
     */
    public void makeSingleTagInactive(int id, Runnable callback) {
        async(() -> {
            try (Connection connection = this.sqlConnection.getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement(QUERY_MAKE_TAG_INACTIVE)) {
                    statement.setInt(1, id);
                    statement.executeUpdate();
                    sync(callback);
                }
            } catch (SQLException exc) {
                exc.printStackTrace();
            }
        });
    }

    public void makeExpiredTagsInactive(Consumer<Integer> callback) {
        async(() -> {
            try (Connection connection = this.sqlConnection.getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement(QUERY_MAKE_EXPIRED_INACTIVE)) {
                    statement.setLong(1, System.currentTimeMillis());
                    sync(callback, statement.executeUpdate());
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private <T> void sync(Consumer<T> cons, T val) {
        Bukkit.getScheduler().runTask(this.plugin, () -> cons.accept(val));
    }

    private void sync(Runnable runnable) {
        Bukkit.getScheduler().runTask(this.plugin, runnable);
    }

    private void async(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, runnable);
    }


}
