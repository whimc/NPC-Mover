package edu.whimc.overworld_agent.utils.sql.migration.schemas;

import edu.whimc.overworld_agent.utils.sql.migration.SchemaVersion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Schema_1 extends SchemaVersion {

    private static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS `whimc_agents` (" +
                    "  `rowid`       INT    AUTO_INCREMENT NOT NULL," +
                    "  `time`        BIGINT                NOT NULL," +
                    "  `uuid`        VARCHAR(36)           NOT NULL," +
                    "  `username`    VARCHAR(16)           NOT NULL," +
                    "  `command`       VARCHAR(64)           NOT NULL," +
                    "  `agent_name`           VARCHAR(36)                NOT NULL," +
                    "  `agent_skin`          VARCHAR(36)                NOT NULL," +
                    "  PRIMARY KEY    (`rowid`)," +
                    "  INDEX uuid     (`uuid`)," +
                    "  INDEX username (`username`));";

    public Schema_1() {
        super(1, null);
    }

    @Override
    protected void migrateRoutine(Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(CREATE_TABLE)) {
            statement.execute();
        }
    }


}
