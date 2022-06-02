package edu.whimc.overworld_agent.utils.sql.migration.schemas;

import edu.whimc.overworld_agent.utils.sql.migration.SchemaVersion;

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * Schema to create skills table in db
 */
public class Schema_1 extends SchemaVersion {

    private static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS `whimc_agent` (" +
                    "  `rowid`       INT    AUTO_INCREMENT NOT NULL," +
                    "  `uuid`        VARCHAR(36)           NOT NULL," +
                    "  `username`    VARCHAR(16)           NOT NULL," +
                    "  `time`        BIGINT                NOT NULL," +
                    "  `skin_name`           VARCHAR(36)                NOT NULL," +
                    "  `agent_name`           VARCHAR(36)                NOT NULL," +
                    "  PRIMARY KEY    (`rowid`));";

    /**
     * Constructor to specify which migrations to do
     */
    public Schema_1() {
        super(1, null);
    }

    /**
     * Method to migrate SQL
     * @param connection SQL connection
     */
    @Override
    protected void migrateRoutine(Connection connection) {
        try (PreparedStatement statement = connection.prepareStatement(CREATE_TABLE)) {
            statement.execute();
        } catch (Exception e){

        }
    }


}
