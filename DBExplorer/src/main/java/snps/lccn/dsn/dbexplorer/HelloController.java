package snps.lccn.dsn.dbexplorer;

import com.opencsv.CSVWriter;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import snps.lccn.dsn.dbexplorer.database.DatabaseRequestManager;
import snps.lccn.dsn.dbexplorer.snapchat.SnapchatBlobElement;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class HelloController {
    @FXML
    private Label welcomeText;
    private static final Logger logger = LogManager.getLogger(HelloController.class);

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");

        DatabaseRequestManager requestManager = new DatabaseRequestManager();

        logger.info("Starting POC DBExplorer ...");

        // get the name of the files in the input folder
        String inputFolderName = "C:\\Users\\LCCN\\Desktop\\LOCAL\\databases_explorer\\resources_lucie\\medias";
        File inputFolder = new File(inputFolderName);
        File[] filesInInputFolder = inputFolder.listFiles();
        if (filesInInputFolder != null) {

            // create a csv file
            File file = new File("C:\\Users\\LCCN\\Desktop\\LOCAL\\databases_explorer\\results.csv");

            try {
                FileWriter outputFile = new FileWriter(file);
                CSVWriter writer = new CSVWriter(outputFile);

                // adding header to csv
                String[] header = {"filename", "source_snapchat", "conversation_id", "participant_ids"};
                writer.writeNext(header);

                for (File inputMediaFile : filesInInputFolder) {
                    logger.debug(String.format("Input folder filename: %s", inputMediaFile.getName()));
                    ArrayList<String> senderIds = new ArrayList<>();
                    SnapchatBlobElement blobElement = requestManager.getConversationIdFromCacheControllerDB(inputMediaFile.getName());

                    if (blobElement.getConversationId() != null) {
                        // get the senderIds from the database arroyo.db
                        senderIds = requestManager.getSenderIdsFromArroyoDB(blobElement.getConversationId());
                    }

                    // add a new line in the csv result with the elements found
                    // filename;source;conversation_id;participants_ids
                    String[] newLine = new String[header.length];
                    newLine[0] = inputMediaFile.getName();

                    if (blobElement.getSourceMedia() != null) {
                        newLine[1] = blobElement.getSourceMedia();
                    } else {
                        newLine[1] = "Not in databases.";
                    }

                    if (blobElement.getConversationId() != null) {
                        newLine[2] = blobElement.getConversationId();
                        StringBuilder sendersString = new StringBuilder();
                        for (String senderId : senderIds) {
                            sendersString.append(senderId).append(";");
                        }
                        newLine[3] = sendersString.toString();
                    } else {
                        newLine[2] = "";
                        newLine[3] = "";
                    }

                    writer.writeNext(newLine);
                }

                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}