package edu.whimc.overworld_agent.utils.sql;


import edu.whimc.overworld_agent.OverworldAgent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.*;
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
