import java.net.*;
import java.io.*;

public class Packet {
  int id;
  
  // use this if you intend to use a readFrom()
  public Packet() {}

  public Packet(int id, String payload) {
    this.id = id;
  }

  public void readFrom(DataInputStream in) throws IOException {
    id = in.readInt();
  }
  
  public void writeTo(DataOutputStream out) throws IOException {
    out.writeInt(id);
  }

}