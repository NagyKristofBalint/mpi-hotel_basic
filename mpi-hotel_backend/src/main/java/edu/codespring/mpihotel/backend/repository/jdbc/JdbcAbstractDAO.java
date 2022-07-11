package edu.codespring.mpihotel.backend.repository.jdbc;

import edu.codespring.mpihotel.backend.repository.RepositoryException;
import edu.codespring.mpihotel.backend.util.PropertyProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class JdbcAbstractDAO {
    private static final Logger LOG = LoggerFactory.getLogger(JdbcAbstractDAO.class);

    protected ConnectionManager connectionManager;

    public JdbcAbstractDAO() {
    }

    protected void createTable(String createTableSqlQuery, String tableName) {
        connectionManager = ConnectionManager.getInstance();
        boolean createTable = Boolean.parseBoolean(PropertyProvider.getProperty("jdbc_create_tables"));
        if (createTable) {
            LOG.info("Creating table {}", tableName);

            Connection c = null;

            try {
                c = connectionManager.getConnection();
                Statement statement = c.createStatement();

                statement.executeUpdate(createTableSqlQuery);
                LOG.info("Table {} was successfully created", tableName);
            } catch (SQLException e) {
                LOG.error("Failed to create {} table; " + e.getMessage(), tableName, e);
                throw new RepositoryException("Failed to create " + tableName + " table; " + e.getMessage(), e);
            } finally {
                if (c != null) {
                    connectionManager.returnConnection(c);
                }
            }
        }
    }
}
