package uz.developers.service;

import uz.developers.model.Result;
import uz.developers.model.User;

import java.sql.*;

public class DatabaseService {

    private String url = "jdbc:postgresql://localhost:5432/relearn";
    private String dbUser = "postgres";
    private String dbPassword = "1234";












    //register user


    public Result registerUser(User user) {
        Connection connection = null;
        CallableStatement callableStatement = null;
        int count = 0;

        try {
            Class.forName("org.postgresql.Driver");

            connection = DriverManager.getConnection(url, dbUser, dbPassword);
            String checkUsernameQuery = "select count(*) from users where username='" + user.getUsername() + "'";
            PreparedStatement preparedStatement = connection.prepareStatement(checkUsernameQuery);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                count = resultSet.getInt(1);
            }
            if (count > 0) {
                return new Result("Username already exist", false);
            }

            String checkPasswordQuery = "select count(*) from users where password='" + user.getPassword() + "'";
            PreparedStatement preparedStatement1 = connection.prepareStatement(checkPasswordQuery);
            ResultSet resultSet1 = preparedStatement1.executeQuery();
            while (resultSet1.next()) {
                count = resultSet1.getInt(1);
            }
            if (count > 0) {
                return new Result("Password is already exist",false);
            }


            String sql = "CALL insert_users(?, ?, ?, ?)";
            callableStatement = connection.prepareCall(sql);
            callableStatement.setString(1, user.getFirstname());
            callableStatement.setString(2, user.getLastname());
            callableStatement.setString(3, user.getUsername());
            callableStatement.setString(4, user.getPassword());
            callableStatement.execute();
            return new Result("Successfully registered",true);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return new Result("Error in server", false);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    //login user

    public User login(String username, String password){
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url,dbUser,dbPassword);
            String query = "select * from users where username=? and password=?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                username = resultSet.getString(2);
                String firstname = resultSet.getString(3);
                String lastname = resultSet.getString(4);
                password = resultSet.getString(5);
                User user = new User(
                        id,
                        firstname,
                        lastname,
                        password,
                        username);
                return user;
            }
            return null;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return null;
    }



    public void deleteUser(int userId) {
        try {
            Connection connection = DriverManager.getConnection(url, dbUser, dbPassword);
            String query = "delete from users where id =?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, userId);
            preparedStatement.executeUpdate();
            System.out.println("User is deleted");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
