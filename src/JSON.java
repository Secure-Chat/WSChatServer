package github.io.darena3.chitchat;
/**
 * A JSON wrapper class to be used with handling messages. Using Google's Gson library, messages are converted
 * into JSON objects and back into Strings to be sent back to all clients.
 * @author David Arena
 *
 */
public class JSON {

	 private String type = null;
     private String room = null;
     private String name = null;
     private String msg = null;
     private String timestamp = null;
     private String min = null;
     private String max = null;

     public JSON(String type) {
         this.type = type;
     }

     public String getType() {
         return this.type;
     }

     public String getRoom() {
         return this.room;
     }

     public String getName() {
         return this.name;
     }

     public String getMsg() {
         return this.msg;
     }

     public String getTimestamp() {
         return this.timestamp;
     }
     
     public void setType(String type) {
    	 this.type = type;
     }

     public void setRoom(String room) {
         this.room = room;
     }

     public void setName(String name) {
         this.name = name;
     }

     public void setMsg(String msg) {
         this.msg = msg;
     }

     public void setTimestamp(String id) {
         this.timestamp = id;
     }

     public void setMin(String min) {
         this.min = min;
     }

     public void setMax(String max) {
         this.max = max;
     }
}
