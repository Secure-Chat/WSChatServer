package github.io.darena3.chitchat;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

import org.java_websocket.WebSocket;

/**
 * A class to represent a chatroom. A Chatroom contains various data structures such as a list of nicknames
 * of all clients currently connected and a HashMap of connections to allow nicknames to be assigned to specific
 * connections.
 * @author David Arena
 *
 */
public class Chatroom {
	private String name;
    private String password;
    public List<String> names = new ArrayList<>();
    public List<String> messages = new ArrayList<>();
    public HashMap<WebSocket, String> conns = new HashMap<>();
    private int currID = 1;

    Chatroom(String name) {
        this.name = name;
    }
    
    Chatroom(String name, String password) {
        this.name = name;
        this.password = password;
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
