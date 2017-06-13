package github.io.darena3.chitchat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Collection;

import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import com.google.gson.*;

import java.util.ArrayList;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.Handler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.SimpleFormatter;
import java.util.logging.XMLFormatter;
import java.io.UnsupportedEncodingException;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.BindException;


/**
 * An implementation of a WebSocketServer. Keeps track of several concurrent chatrooms. Implements
 * the methods of the WebSocketServer interface's defined behaviors. Processes incoming messages as JSON
 * strings and sends back JSON strings.
 * 
 * TODO implement files so chatrooms can store messages and message #IDs
 */
public class SChatServer extends WebSocketServer {
	public List<String> names = new ArrayList<>(); //chatroom names
	public List<Chatroom> rooms = new ArrayList<>(); //chatrooms themselves
	
	public SChatServer(int port) throws UnknownHostException {
		super(new InetSocketAddress(port));
	}

	public SChatServer(InetSocketAddress address) {
		super(address);
	}

	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		//this.sendToAll("new connection: " + handshake.getResourceDescriptor());
		System.out.println(conn.getRemoteSocketAddress().getAddress().getHostAddress() + " entered the server!");
	}

	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		for (Chatroom c : rooms) {
			System.out.println("User \"" + c.conns.get(conn) + "\" has left the room \"" + c.getName() + "\".");
			c.names.remove(c.conns.get(conn));
			c.conns.remove(conn);
		}
		System.out.println(conn.getRemoteSocketAddress().getAddress().getHostAddress() + " has left the server.");
	}

	@Override
	public void onMessage(WebSocket conn, String message) {
		process(conn, message, this);
	}

	@Override
	public void onFragment(WebSocket conn, Framedata fragment) {
		System.out.println("received fragment: " + fragment);
	}

	public static void main(String[] args) throws InterruptedException, IOException {
		WebSocketImpl.DEBUG = false;
		int port = 6789;
		
		SChatServer s = new SChatServer(port);
		s.start();
		System.out.println("SecureChatServer started on port: " + s.getPort());

		BufferedReader sysin = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			String in = sysin.readLine();
			String toSend = in;
			if (in.indexOf(" ") != -1 && in.substring(in.indexOf(" ")).indexOf(" ") != -1) {
				int id = Integer.parseInt(in.substring(0, in.indexOf(" ")));
				String room = in.substring(in.indexOf(" ") + 1, in.substring(in.indexOf(" ") + 1).indexOf(" ") + in.indexOf(" ") + 1);
				String msg = in.substring(in.indexOf(" ") + 1).substring(room.length() + 1);
				toSend = "{\"type\":\"message\", \"room\":\"" + room + "\", \"id\":" + id + ", \"name\":\"SERVER\", \"msg\":\"" + msg + "\"}";
			}
			s.sendToAll(toSend);
			System.out.println(toSend);
			if (in.equals("exit")) {
				s.stop();
				break;
			}
		}
	}

	@Override
	public void onError(WebSocket conn, Exception ex) {
		ex.printStackTrace();
		if (conn != null) {
			// some errors like port binding failed may not be assignable to a
			// specific websocket
		}
	}

	@Override
	public void onStart() {
		System.out.println("Server started!");
	}

	/**
	 * Sends <var>text</var> to all currently connected WebSocket clients.
	 * 
	 * @param text
	 *            The String to send across the network.
	 * @throws InterruptedException
	 *             When socket related I/O errors occur.
	 */
	public void sendToAll(String text) {
		Collection<WebSocket> con = connections();
		synchronized (con) {
			for (WebSocket c : con) {
				c.send(text);
			}
		}
	}
	
	/**
	 * Evaluates the given message as a JSON string and processes it based on its given type. Makes sure the user has a valid
	 * nickname before connecting or sending messages.
	 * @param conn
	 * @param message
	 * @param s
	 */
	public void process(WebSocket conn, String message, SChatServer s) {
		JSON obj = new Gson().fromJson(message, JSON.class);
		String type = obj.getType();
		switch(type) {
			case "connect":
				if (findRoom(obj)) {
					Chatroom myRoom = rooms.get(names.indexOf(obj.getRoom()));
					if (verifyNickname(obj.getName(), myRoom.names)) {
						myRoom.names.add(obj.getName());
						myRoom.conns.put(conn, obj.getName());
						System.out.println(conn.getRemoteSocketAddress().getAddress().getHostAddress() + " connected with username \"" + obj.getName() + "\" to room \"" + obj.getRoom() + "\".");
					}
					else {
						obj.setType("message");
						obj.setID(myRoom.getCurrID());
						myRoom.incID();
						obj.setName("SERVER");
						obj.setMsg("That nickname is already in use or is blank.");
						conn.send(new Gson().toJson(obj));
						System.out.println("Bad nickname from user " + conn.getRemoteSocketAddress().getAddress().getHostAddress());
					}
				}
				break;
			case "message":
				if (findRoom(obj)) {
					Chatroom myRoom = rooms.get(names.indexOf(obj.getRoom()));
					String myName = myRoom.conns.get(conn);
					if (myName != null && !myName.equalsIgnoreCase("SERVER")) {
						obj.setName(myName);
						obj.setID(myRoom.getCurrID());
						myRoom.incID();
						s.sendToAll(new Gson().toJson(obj));
						System.out.println("Message received from user \"" + obj.getName() + "\" in room \"" + obj.getRoom() + "\".");
					}
					else {
						obj.setID(myRoom.getCurrID());
						myRoom.incID();
						obj.setName("SERVER");
						obj.setMsg("That user's nickname is already in use or is blank.");
						conn.send(new Gson().toJson(obj));
						System.out.println("Bad nickname from user " + conn.getRemoteSocketAddress().getAddress().getHostAddress());
					}
				}
				break;
		}
	}
	
	/**
	 * Makes sure the given nickname is not equivalent to any other nickname in use
	 * in the given list, is not blank, or is not a reserved nickname.
	 * @param name
	 * @param nicknames
	 * @return true if the nickname meets all necessary conditions, false otherwise
	 */
	public boolean verifyNickname(String name, List<String> nicknames) {
		if (name == null || name.equalsIgnoreCase("SERVER")) return false;
		for (String s : nicknames) {
			if (name.equals(s))
				return false;
		}
		return true;
	}
	
	/**
	 * Attempts to find a room to which the JSON message received should be assigned. If the
	 * room with the given name does not yet exist, it is created.
	 * @param obj
	 * @return false if the JSON object received did not specify a room, true otherwise
	 */
	public boolean findRoom(JSON obj) {
		if (obj.getRoom() == null) return false;
		for (String s : names) {
			if (obj.getRoom().equals(s))
				return true;
		}
		names.add(obj.getRoom());
		rooms.add(new Chatroom(obj.getRoom()));
		return true;
	}
}
