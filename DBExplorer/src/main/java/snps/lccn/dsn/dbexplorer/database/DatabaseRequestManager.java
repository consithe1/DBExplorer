package snps.lccn.dsn.dbexplorer.database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import snps.lccn.dsn.dbexplorer.snapchat.SnapchatBlobElement;
import snps.lccn.dsn.dbexplorer.snapchat.SnapchatBlobParser;


import java.sql.*;
import java.util.ArrayList;

public class DatabaseRequestManager {
    private static final Logger logger = LogManager.getLogger(DatabaseRequestManager.class);
    private Connection conn;

    public DatabaseRequestManager() {
    }

    public SnapchatBlobElement getConversationIdFromCacheControllerDB(String filename) throws SQLException {
        SnapchatBlobElement blobElement = new SnapchatBlobElement();
        // TODO change with PreparedStatements
        // select a shorter string from the full filename
        String filenameShort = filename.substring(0, Math.min(20, filename.length()));

        // initialise sql elements
        ResultSet resultSet;
        String selectStatementString = "SELECT * FROM CACHE_FILE_METADATA WHERE CACHE_KEY LIKE \"%" + filenameShort + "%\"";
        logger.debug(String.format("Statement to execute: %s", selectStatementString));

        // connect to the cache_controller database
        this.connect("C:\\Users\\LCCN\\Desktop\\LOCAL\\databases_explorer\\resources_lucie\\databases\\cache_controller.db");
        Statement selectStatement = this.conn.createStatement();
        resultSet = selectStatement.executeQuery(selectStatementString);
        while (resultSet.next()) {
            // get the blob containing elements to analyse
            byte[] data = resultSet.getBytes("CONTENT_RETRIEVAL_METADATA");
            // parse the blob content and try to extract data from it
            SnapchatBlobParser parser = new SnapchatBlobParser();
            blobElement = parser.parse(data);
        }
        this.disconnect();

        return blobElement;
    }

    public ArrayList<String> getSenderIdsFromArroyoDB(String conversationId) throws SQLException {
        // TODO change with PreparedStatements
        ArrayList<String> senderIds = new ArrayList<>();
        ResultSet rs;

        String stmtString = String.format("select distinct sender_id from conversation_message where client_conversation_id=\"%s\"", conversationId);
        logger.debug(stmtString);

        this.connect("C:\\Users\\LCCN\\Desktop\\LOCAL\\databases_explorer\\resources_lucie\\databases\\arroyo.db");
        Statement stmt = this.conn.createStatement();
        rs = stmt.executeQuery(stmtString);
        while (rs.next()) {
            String senderId = rs.getString("sender_id");
            senderIds.add(senderId);
        }
        this.disconnect();

        return senderIds;
    }

    public void connect(String databasePath) {
        this.conn = null;

        try {
            Class.forName("org.sqlite.JDBC");
            this.conn = DriverManager.getConnection("jdbc:sqlite:" + databasePath);
            logger.debug("Connected to database");
        }
        catch (ClassNotFoundException e) {
            logger.error(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void disconnect() {
        try {
            if (this.conn != null) {
                this.conn.close();
                logger.debug("Connection to database closed");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}