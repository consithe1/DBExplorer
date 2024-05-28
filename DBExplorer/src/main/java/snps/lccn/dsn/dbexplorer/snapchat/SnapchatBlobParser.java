package snps.lccn.dsn.dbexplorer.snapchat;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.ArrayList;
import java.util.Arrays;

public class SnapchatBlobParser {

    private static final Logger logger = LogManager.getLogger(SnapchatBlobParser.class);
    public SnapchatBlobParser() {

    }

    public SnapchatBlobElement parse(byte[] data) {

        SnapchatBlobElement blobElement = new SnapchatBlobElement();

        logger.debug(String.format("Blob decoded: %s", new String(data)));
        ArrayList<Integer> indexesDC2 = new ArrayList<>();
        ArrayList<Integer> indexesSUB = new ArrayList<>();
        // find the indexes of DC2 and SUB ASCII instructions
        for (int i = 0; i < data.length; i++) {
            byte byteInstr = data[i];
            if (byteInstr == 18) {
                indexesDC2.add(i);
            } else {
                if (byteInstr == 26) {
                    indexesSUB.add(i);
                }
            }
        }

        logger.debug(String.format("Indexes of DC2 ASCII Control instructions: %s", indexesDC2));
        logger.debug(String.format("Indexes of SUB ASCII Control instructions: %s", indexesSUB));

        if (!indexesDC2.isEmpty() && !indexesSUB.isEmpty()) {
            // take the first index of SUB instruction
            // find the last DC2 instruction index < to index SUB
            int indexSUB = indexesSUB.getFirst();
            int indexDC2 = -1;
            for (int j = 0; j < indexesDC2.size() - 1; j++) {
                if ((indexesDC2.get(j) < indexSUB) && (indexesDC2.get(j+1) > indexSUB)) {
                    indexDC2 = indexesDC2.get(j);
                }
            }
            byte[] source = Arrays.copyOfRange(data, indexDC2+2, indexSUB);
            String sourceMedia = new String(source);
            logger.info(String.format("Media's source: %s", sourceMedia));

            blobElement.setSourceMedia(sourceMedia);

            if (sourceMedia.equals("Chat")) {
                String conversationIdFull = new String(Arrays.copyOfRange(data, indexDC2-47, indexDC2));
                String[] conversationIDFullElements = conversationIdFull.split(":");
                blobElement.setConversationId(conversationIDFullElements[1]);
            }
        } else {
            blobElement.setSourceMedia("Can't be decoded.");
        }

        return blobElement;
    }
}
