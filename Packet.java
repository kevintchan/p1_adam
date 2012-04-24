import java.net.*;
import java.io.*;

public class Packet {
  int id;
  boolean ack;
  int sourcePort;
  int outPort;
  boolean probe;
  
  // use this if you intend to use a readFrom()
  public Packet() {}

  public Packet(int id) {
    this(id, false);
  }

  public Packet(int id, boolean ack) {
    this(id, ack, 0, 0, false);
  }

  public Packet(int id, boolean ack, int sourcePort, int outPort, boolean probe) {
    this.id = id;
    this.ack = ack;
    this.sourcePort = sourcePort;
    this.outPort = outPort;
  }
  
  public void readFrom(DataInputStream in) throws IOException {
    id = in.readInt();
    ack = in.readBoolean();
    sourcePort = in.readInt();
    outPort = in.readInt();
    probe = in.readBoolean();
  }
  
  public void writeTo(DataOutputStream out) throws IOException {
    out.writeInt(id);
    out.writeBoolean(ack);
    out.writeInt(sourcePort);
    out.writeInt(outPort);
    out.writeBoolean(probe);
  }


  public int getId() {
    return id;
  }
  
  public int getSourcePort() {
    return sourcePort;
  }


  public int getOutPort() {
    return outPort;
  }

  public boolean isProbe() {
    return probe;
  }

}