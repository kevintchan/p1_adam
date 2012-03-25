public class DestNode extends NetworkNode {

  public DestNode(int inPort, int outPort) {
    super(inPort, outPort);
  }

  public void handlePacket(Packet p) {
    Packet ackPack = new Packet(p.getId(), true);
    send(ackPack);
  }
}