import java.util.ArrayList;

/**
 * Created by shaka on 22.06.2018.
 */
public class Note {
    String name;
    long time;
    ArrayList<String> messages;

    public Note(String name, long time, ArrayList<String> messages) {
        this.name = name;
        this.time = time;
        this.messages = messages;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public ArrayList<String> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<String> messages) {
        this.messages = messages;
    }
}
