package edu.whimc.overworld_agent.utils.sql.migration.schemas;
import edu.whimc.overworld_agent.utils.sql.migration.SchemaVersion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Schema_5 extends SchemaVersion {
    private static final String CREATE_INTERACTION =
            "CREATE TABLE IF NOT EXISTS `whimc_dialogue_interaction` (" +
                    "  `rowid`       INT    AUTO_INCREMENT NOT NULL," +
                    "  `uuid`        VARCHAR(36)           NOT NULL," +
                    "  `username`    VARCHAR(16)           NOT NULL," +
                    "  `world`    VARCHAR(36)           NOT NULL," +
                    "  `time`        BIGINT                NOT NULL," +
                    "  `interaction`    TEXT           NOT NULL," +
                    "  PRIMARY KEY    (`rowid`));";
    /**
     * Constructor to specify which migrations to do
     */
    public Schema_5() {
        super(5, new Schema_6());
    }
    @Override
    protected void migrateRoutine(Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(CREATE_INTERACTION)) {
            statement.execute();
        }

    }
}