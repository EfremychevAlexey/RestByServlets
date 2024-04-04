package restByServlets.util;


import restByServlets.db.ConnectionManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DBInit {
    private static String filePath = "src/main/resources/sql/init.sql";

    public static void init(ConnectionManager connectionManager) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();

        try (Connection connection = connectionManager.getConnection();
             Statement statement = connection.createStatement()) {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));

            while(reader.ready()){
                stringBuilder.append(reader.readLine());
                stringBuilder.append("\n");
            }

            statement.execute(stringBuilder.toString());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
