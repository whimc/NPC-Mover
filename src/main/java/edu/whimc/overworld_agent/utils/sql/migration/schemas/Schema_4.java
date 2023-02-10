package edu.whimc.overworld_agent.utils.sql.migration.schemas;

import edu.whimc.overworld_agent.utils.sql.migration.SchemaVersion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Schema_4 extends SchemaVersion {
    private static final String CREATE_TAGS =
            "CREATE TABLE IF NOT EXISTS `whimc_tags` (" +
                    "  `rowid`       INT    AUTO_INCREMENT NOT NULL," +
                    "  `uuid`        VARCHAR(36)           NOT NULL," +
                    "  `username`    VARCHAR(16)           NOT NULL," +
                    "  `world`    VARCHAR(36)           NOT NULL," +
                    "  `x`           DOUBLE                NOT NULL," +
                    "  `y`           DOUBLE                NOT NULL," +
                    "  `z`           DOUBLE                NOT NULL," +
                    "  `time`        BIGINT                NOT NULL," +
                    "  `tag`    TEXT           NOT NULL," +
                    "  `active`      BOOLEAN               NOT NULL," +
                    "  `expiration`  BIGINT                        ," +
                    "  PRIMARY KEY    (`rowid`));";
    /**
     * Constructor to specify which migrations to do
     */
    public Schema_4() {
        super(4, new Schema_5());
    }
    @Override
    protected void migrateRoutine(Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(CREATE_TAGS)) {
            statement.execute();
        }

    }
}