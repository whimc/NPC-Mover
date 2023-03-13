package edu.whimc.overworld_agent.utils.sql.migration.schemas;

import edu.whimc.overworld_agent.utils.sql.migration.SchemaVersion;
import org.bukkit.ChatColor;

import java.sql.*;

public class Schema_6 extends SchemaVersion {


    private static final String ADD_CATEGORY =
            "ALTER TABLE whimc_dialogue_interaction ADD COLUMN x DOUBLE, " +
                    "ADD COLUMN y DOUBLE," +
                    "ADD COLUMN z DOUBLE;";


    public Schema_6() {
        super(6, new Schema_7());
    }

    @Override
    protected void migrateRoutine(Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(ADD_CATEGORY)) {
            statement.execute();
        }
    }

}
