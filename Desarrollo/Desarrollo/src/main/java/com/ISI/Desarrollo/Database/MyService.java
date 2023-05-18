package com.ISI.Desarrollo.Database;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Service
public class MyService {
    private String url;
    private String user;
    private String password;

    public MyService(@Value("${spring.datasource.url}") String url,
                     @Value("${spring.datasource.username}") String user,
                     @Value("${spring.datasource.password}") String password) {
        this.url = url;
        this.user = user;
        this.password = password;

    }
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}