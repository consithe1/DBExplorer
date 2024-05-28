package snps.lccn.dsn.dbexplorer.snapchat;

public class SnapchatBlobElement {
    private String conversationId = null;
    private String sourceMedia = null;

    public SnapchatBlobElement(String convId, String srcMedia) {
        conversationId = convId;
        sourceMedia = srcMedia;
    }

    public SnapchatBlobElement() {}

    public String getConversationId() {
        return conversationId;
    }

    public String getSourceMedia() {
        return sourceMedia;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public void setSourceMedia(String sourceMedia) {
        this.sourceMedia = sourceMedia;
    }
}
