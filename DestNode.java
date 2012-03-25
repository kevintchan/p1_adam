import java.io.*;

public class DestNode extends NetworkNode {

  public DestNode(int inPort, int outPort) throws IOException  {
    super(inPort, outPort);
  }

  public void handlePacket(Packet p) {
    System.out.println("DestNode Handling Packet:" + p.getId());
    Packet ackPack = new Packet(p.getId(), true);
    try {
      send(ackPack);
    } catch(IOException e) {
      System.out.println("handlePacket IOException");
    }
  }
}