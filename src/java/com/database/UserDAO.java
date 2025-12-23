package com.database;

import com.models.User;
import com.abstracts.BaseDAO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * User Data Access Object
 * Extends BaseDAO to demonstrate inheritance and abstract class usage
 */
public class UserDAO extends BaseDAO<User, Integer> {
    
    public UserDAO() {
        super("users");
    }
    
    /**
     * Create new user
     */
    public User createUser(String username, String email, String password, String fullName) {
        User user = new User(username, email, password, fullName);
        return save(user);
    }
    
    /**
     * Find user by username
     */
    public User findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToEntity(rs);
            }
            return null;
            
        } catch (SQLException e) {
            handleSQLException("findByUsername", e);
            return null;
        }
    }
    
    /**
     * Find user by email
     */
    public User findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToEntity(rs);
            }
            return null;
            
        } catch (SQLException e) {
            handleSQLException("findByEmail", e);
            return null;
        }
    }
    
    /**
     * Find user by username or email
     */
    public User findByUsernameOrEmail(String usernameOrEmail) {
        String sql = "SELECT * FROM users WHERE username = ? OR email = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, usernameOrEmail);
            stmt.setString(2, usernameOrEmail);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToEntity(rs);
            }
            return null;
            
        } catch (SQLException e) {
            handleSQLException("findByUsernameOrEmail", e);
            return null;
        }
    }
    
    /**
     * Check if username exists
     */
    public boolean usernameExists(String username) {
        String sql = "SELECT 1 FROM users WHERE username = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
            
        } catch (SQLException e) {
            handleSQLException("usernameExists", e);
            return false;
        }
    }
    
    /**
     * Check if email exists
     */
    public boolean emailExists(String email) {
        String sql = "SELECT 1 FROM users WHERE email = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
            
        } catch (SQLException e) {
            handleSQLException("emailExists", e);
            return false;
        }
    }
    
    // Abstract method implementations from BaseDAO
    @Override
    public User save(User user) {
        String sql = getInsertSQL();
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            setInsertParameters(stmt, user);
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                // For SQLite, get the last inserted row ID using last_insert_rowid()
                try (PreparedStatement idStmt = conn.prepareStatement("SELECT last_insert_rowid()")) {
                    ResultSet rs = idStmt.executeQuery();
                    if (rs.next()) {
                        user.setUserId(rs.getInt(1));
                    }
                }
                return user;
            }
            return null;
            
        } catch (SQLException e) {
            handleSQLException("save", e);
            return null;
        }
    }
    
    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM " + tableName;
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                users.add(mapResultSetToEntity(rs));
            }
            
        } catch (SQLException e) {
            handleSQLException("findAll", e);
        }
        
        return users;
    }
    
    @Override
    public boolean update(User user) {
        String sql = getUpdateSQL();
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            setUpdateParameters(stmt, user);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            handleSQLException("update", e);
            return false;
        }
    }
    
    @Override
    protected User mapResultSetToEntity(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setFullName(rs.getString("full_name"));
        
        Timestamp timestamp = rs.getTimestamp("created_at");
        if (timestamp != null) {
            user.setCreatedAt(timestamp.toLocalDateTime());
        }
        
        return user;
    }
    
    @Override
    protected Integer getEntityId(User user) {
        return user.getUserId();
    }
    
    @Override
    protected String getInsertSQL() {
        return "INSERT INTO users (username, email, password, full_name) VALUES (?, ?, ?, ?)";
    }
    
    @Override
    protected String getUpdateSQL() {
        return "UPDATE users SET username = ?, email = ?, password = ?, full_name = ? WHERE user_id = ?";
    }
    
    @Override
    protected void setInsertParameters(PreparedStatement stmt, User user) throws SQLException {
        stmt.setString(1, user.getUsername());
        stmt.setString(2, user.getEmail());
        stmt.setString(3, user.getPassword());
        stmt.setString(4, user.getFullName());
    }
    
    @Override
    protected void setUpdateParameters(PreparedStatement stmt, User user) throws SQLException {
        stmt.setString(1, user.getUsername());
        stmt.setString(2, user.getEmail());
        stmt.setString(3, user.getPassword());
        stmt.setString(4, user.getFullName());
        stmt.setInt(5, user.getUserId());
    }
    
    @Override
    protected String getPrimaryKeyColumn() {
        return "user_id";
    }
}