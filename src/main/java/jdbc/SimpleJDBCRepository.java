package jdbc;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SimpleJDBCRepository {

    private Connection connection = CustomDataSource.getInstance().getConnection();

    private static final String createUserSQL = "INSERT INTO myusers (firstname, lastname, age) VALUES (?, ?, ?);";
    private static final String updateUserSQL = "UPDATE myusers SET firstname = ?, lastname = ?, age = ? WHERE id = ?;";
    private static final String deleteUser = "DELETE FROM myusers WHERE id = ?;";
    private static final String findUserByIdSQL = "SELECT * FROM myusers WHERE id = ?;";
    private static final String findUserByNameSQL = "SELECT * FROM myusers WHERE firstname = ?;";
    private static final String findAllUserSQL = "SELECT * FROM myusers;";

    public Long createUser(User user) {
        try (PreparedStatement ps = connection.prepareStatement(createUserSQL, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setInt(3, user.getAge());
            ps.executeUpdate();
            return ps.getGeneratedKeys().getLong(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public User findUserById(Long userId) {
        try (PreparedStatement ps = connection.prepareStatement(findUserByIdSQL)) {
            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();
            return getUser(rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public User findUserByName(String userName) {
        try (PreparedStatement ps = connection.prepareStatement(findUserByNameSQL)) {
            ps.setString(1, userName);
            ResultSet rs = ps.executeQuery();
            return getUser(rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<User> findAllUser() {
        try (PreparedStatement ps = connection.prepareStatement(findAllUserSQL);
             ResultSet rs = ps.executeQuery()) {
            return getUsers(rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public User updateUser(User user) {
        try (PreparedStatement ps = connection.prepareStatement(updateUserSQL)) {
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setInt(3, user.getAge());
            ps.setLong(4, user.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    public void deleteUser(Long userId) {
        try (PreparedStatement ps = connection.prepareStatement(deleteUser)) {
            ps.setLong(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private User getUser(ResultSet rs) {
        try {
            if (rs.next()) {
                long id = rs.getLong("id");
                String firstName = rs.getString("firstname");
                String lastName = rs.getString("lastname");
                int age = rs.getInt("age");
                return new User(id, firstName, lastName, age);
            } else {
                return new User();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private List<User> getUsers(ResultSet rs) {
        List<User> users = new ArrayList<>();
        try {
            while (rs.next()) {
                long id = rs.getLong("id");
                String firstName = rs.getString("firstname");
                String lastName = rs.getString("lastname");
                int age = rs.getInt("age");
                users.add(new User(id, firstName, lastName, age));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return users;
    }
}
