package auxiliary;

import java.io.Serializable;

public class Message implements Serializable {
    private String originalMsg;
    private String processedMsg;
    private long timeProcessed;


    public Message(String original, String processed , long time){
        originalMsg = original;
        processedMsg = processed;
        timeProcessed = time;
    }

    public String getProcessed(){
        return this.processedMsg;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(this.originalMsg);
        sb.append("-->");
        sb.append(this.processedMsg);
        sb.append(" ").append(this.timeProcessed).append(" nano-seconds");
        return sb.toString();
    }

}
