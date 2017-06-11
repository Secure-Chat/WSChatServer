import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

import org.java_websocket.WebSocket;

public class Chatroom {
	private String name;
    private String password;
    public List<String> names = new ArrayList<>();
    public List<String> messages = new ArrayList<>();
    public HashMap<WebSocket, String> conns = new HashMap<>();
    private int currID = 1;

    Chatroom(String name) {
        this.name = name;
        names.add("SERVER");
    }
    
    Chatroom(String name, String password) {
        this.name = name;
        this.password = password;
        names.add("SERVER");
    }

    public String getName() {
        return this.name;
    }

    public String getPassword() {
        return this.password;
    }

    public int getCurrID() {
        return this.currID;
    }
    
    public void incID() {
    	this.currID++;
    }
}
