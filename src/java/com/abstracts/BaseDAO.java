package com.abstracts;

import com.interfaces.Repository;
import com.database.DBConnection;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public abstract class BaseDAO<T, ID> implements Repository<T, ID> {

    protected final String tableName;

    public BaseDAO(String tableName) {
        this.tableName = tableName;
    }

    /**
     * Get database connection
     * 
     * @return Database connection
     * @throws SQLException if connection fails
     */
    protected Connection getConnection() throws SQLException {
        return DBConnection.getInstance().getConnection();
    }

    /**
     * Abstract method to map ResultSet to entity
     * Must be implemented by concrete DAOs
     */
    protected abstract T mapResultSetToEntity(java.sql.ResultSet rs) throws SQLException;

    /**
     * Abstract method to get entity ID
     * Must be implemented by concrete DAOs
     */
    protected abstract ID getEntityId(T entity);

    /**
     * Abstract method to create insert SQL
     * Must be implemented by concrete DAOs
     */
    protected abstract String getInsertSQL();

    /**
     * Abstract method to create update SQL
     * Must be implemented by concrete DAOs
     */
    protected abstract String getUpdateSQL();

    /**
     * Abstract method to set parameters for insert
     * Must be implemented by concrete DAOs
     */
    protected abstract void setInsertParameters(java.sql.PreparedStatement stmt, T entity) throws SQLException;

    /**
     * Abstract method to set parameters for update
     * Must be implemented by concrete DAOs
     */
    protected abstract void setUpdateParameters(java.sql.PreparedStatement stmt, T entity) throws SQLException;

    @Override
    public T findById(ID id) {
        String sql = "SELECT * FROM " + tableName + " WHERE " + getPrimaryKeyColumn() + " = ?";

        try (Connection conn = getConnection();
                java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, id);
            java.sql.ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToEntity(rs);
            }
            return null;

        } catch (SQLException e) {
            handleSQLException("findById", e);
            return null;
        }
    }

    @Override
    public boolean existsById(ID id) {
        String sql = "SELECT 1 FROM " + tableName + " WHERE " + getPrimaryKeyColumn() + " = ?";

        try (Connection conn = getConnection();
                java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, id);
            java.sql.ResultSet rs = stmt.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            handleSQLException("existsById", e);
            return false;
        }
    }

    @Override
    public boolean deleteById(ID id) {
        String sql = "DELETE FROM " + tableName + " WHERE " + getPrimaryKeyColumn() + " = ?";

        try (Connection conn = getConnection();
                java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            handleSQLException("deleteById", e);
            return false;
        }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM " + tableName;

        try (Connection conn = getConnection();
                java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {

            java.sql.ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;

        } catch (SQLException e) {
            handleSQLException("count", e);
            return 0;
        }
    }

    /**
     * Get primary key column name
     * Can be overridden by subclasses
     */
    protected String getPrimaryKeyColumn() {
        return "id";
    }

    /**
     * Handle SQL exceptions
     * Can be overridden by subclasses for custom error handling
     */
    protected void handleSQLException(String operation, SQLException e) {
        System.err.println("SQL Error in " + getClass().getSimpleName() + "." + operation + ": " + e.getMessage());
        e.printStackTrace();
    }

    /**
     * Utility method to close resources safely
     */
    protected void closeQuietly(AutoCloseable... resources) {
        for (AutoCloseable resource : resources) {
            if (resource != null) {
                try {
                    resource.close();
                } catch (Exception e) {
                    // Log but don't throw
                    System.err.println("Error closing resource: " + e.getMessage());
                }
            }
        }
    }
}