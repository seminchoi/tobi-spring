package tobi.connection;

import java.sql.Connection;

public interface ConnectionMaker {
    Connection makeConnection();
}
