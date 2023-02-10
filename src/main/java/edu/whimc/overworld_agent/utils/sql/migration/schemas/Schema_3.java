package edu.whimc.overworld_agent.utils.sql.migration.schemas;

import edu.whimc.overworld_agent.utils.sql.migration.SchemaVersion;
import org.bukkit.ChatColor;

import java.sql.*;

public class Schema_3 extends SchemaVersion {


    private static final String ADD_CATEGORY =
            "ALTER TABLE whimc_dialog_science ADD COLUMN agent_response TEXT;";


    public Schema_3() {
        super(3, new Schema_4());
    }

    @Override
    protected void migrateRoutine(Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(ADD_CATEGORY)) {
            statement.execute();
        }
    }

}
