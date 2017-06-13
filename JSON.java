public class JSON {

	 private String type = null;
     private String room = null;
     private String name = null;
     private String msg = null;
     private int id = -1;
     private int min = -1;
     private int max = -1;

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

     public int getID() {
         return this.id;
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

     public void setID(int id) {
         this.id = id;
     }

     public void setMin(int min) {
         this.min = min;
     }

     public void setMax(int max) {
         this.max = max;
     }
}
