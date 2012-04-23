import java.io.*;

public class DestNode extends NetworkNode {

  public DestNode(String name, int inPort, int[] outPorts, int vLvl) throws IOException  {
    super(name, inPort, outPorts, vLvl);
  }

  public void handlePacket(Packet p) {
    output("Handling Packet", p.getId(), 0);
    int outPort = p.getSourcePort();
    output(""+outPort, 0);
    Packet ackPack = new Packet(p.getId(), true);
    try {
      send(outPort, ackPack);
    } catch(IOException e) {
      output("handlePacket IOException", 1000);
    }
  }
}