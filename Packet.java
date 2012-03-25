import java.net.*;
import java.io.*;

public class Packet {
  int id;
  boolean ack;
  
  // use this if you intend to use a readFrom()
  public Packet() {}

  public Packet(int id) {
    this(id, false);
  }

  public Packet(int id, boolean ack) {
    this.id = id;
    this.ack = ack;
  }

  public void readFrom(DataInputStream in) throws IOException {
    id = in.readInt();
    ack = in.readBoolean();
  }
  
  public void writeTo(DataOutputStream out) throws IOException {
    out.writeInt(id);
    out.writeBoolean(ack);
  }


  public int getId() {
    return id;
  }

}