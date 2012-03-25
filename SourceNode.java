import java.net.*;
import java.io.*;

public class SourceNode extends NetworkNode {

  public SourceNode(int inPort, int outPort) throws UnknownHostException, IOException {
    super(inPort, outPort);
  }
  
  public void handlePacket(Packet p) {
  }

}