import java.net.*;
import java.io.*;

public class Packet {
  int id;
  boolean ack;
  int sourcePort;
  
  // use this if you intend to use a readFrom()
  public Packet() {}

  public Packet(int id) {
    this(id, false);
  }

  public Packet(int id, boolean ack) {
    this(id, ack, 0);
  }

  public Packet(int id, boolean ack, int sourcePort) {
    this.id = id;
    this.ack = ack;
    this.sourcePort = sourcePort;
  }
  
  public void readFrom(DataInputStream in) throws IOException {
    id = in.readInt();
    ack = in.readBoolean();
    sourcePort = in.readInt();
  }
  
  public void writeTo(DataOutputStream out) throws IOException {
    out.writeInt(id);
    out.writeBoolean(ack);
    out.writeInt(sourcePort);
  }


  public int getId() {
    return id;
  }
  
  public int getSourcePort() {
    return sourcePort;
  }

}