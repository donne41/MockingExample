package com.example.payment;

import java.sql.Connection;
import java.sql.PreparedStatement;

public interface DatabaseRepo extends Connection {
    void executeUpdate(PreparedStatement sql);
}
