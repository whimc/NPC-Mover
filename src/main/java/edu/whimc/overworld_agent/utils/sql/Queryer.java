package edu.whimc.overworld_agent.utils.sql;

import edu.whimc.overworld_agent.OverworldAgent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;

/**
 * Handles storing position data
 *
 * @author Sam
 */
public class Queryer {

    //Query for inserting skills into the database.
    private static final String QUERY_SAVE_AGENT=
            "INSERT INTO whimc_agent " +
                    "(uuid, username, time, skin_name, agent_name) " +
                    "VALUES (?, ?, ?, ?, ?)";

    private final OverworldAgent plugin;
    private final MySQLConnection sqlConnection;

    /**
     * Constructor to instantiate instance variables and connect to SQL
     * @param plugin StudentFeedback plugin instance
     * @param callback callback to signal that process completed
     */
    public Queryer(OverworldAgent plugin, Consumer<Queryer> callback) {
        this.plugin = plugin;
        this.sqlConnection = new MySQLConnection(plugin);

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            final boolean success = sqlConnection.initialize();
            Bukkit.getScheduler().runTask(plugin, () -> callback.accept(success ? this : null));
        });
    }

    /**
     * Generated a PreparedStatement for saving a new progress session.
     * @param connection MySQL Connection
     * @param player Checking progress or leaderboard to save
     * @param skinName Name of agent skin used
     * @param agentName Name of agent spawned
     * @return PreparedStatement
     * @throws SQLException
     */
    private PreparedStatement getStatement(Connection connection, Player player, String skinName, String agentName) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(QUERY_SAVE_AGENT, Statement.RETURN_GENERATED_KEYS);
        statement.setString(1, player.getUniqueId().toString());
        statement.setString(2, player.getName());
        statement.setLong(3, System.currentTimeMillis());
        statement.setString(4, skinName);
        statement.setString(5, agentName);
        return statement;
    }

    /**
     * Stores a progress command into the database and returns the obervation's ID
     * @param player Checking progress or leaderboard to save
     * @param skinName Name of agent skin used
     * @param agentName Name of agent spawned
     * @param callback    Function to call once the observation has been saved
     */
    public void storeNewAgent(Player player, String skinName, String agentName, Consumer<Integer> callback) {
        async(() -> {

            try (Connection connection = this.sqlConnection.getConnection()) {
                try (PreparedStatement statement = getStatement(connection, player, skinName, agentName)) {
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
