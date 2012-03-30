import java.io.*;

public class DestNode extends NetworkNode {

  public DestNode(String name, int inPort, int outPort, int vLvl) throws IOException  {
    super(name, inPort, outPort, vLvl);
  }

  public void handlePacket(Packet p) {
    output("Handling Packet", p.getId(), 0);
    Packet ackPack = new Packet(p.getId(), true);
    try {
      send(ackPack);
    } catch(IOException e) {
      output("handlePacket IOException", 1000);
    }
  }
}