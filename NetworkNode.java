import java.net.*;
import java.io.*;

public abstract class NetworkNode implements Runnable {

  Socket outgoing;
  Socket incoming;

  public NetworkNode(int inport, int outport) throws UnknownHostException, IOException {
    String host = "localhost";
    outgoing = new Socket(host, inport);
    incoming = new Socket(host, outport);
  }

  public void send(Packet p) throws IOException {
    DataOutputStream outStream = new DataOutputStream(outgoing.getOutputStream());
    p.writeTo(outStream);
  }

  abstract public void handlePacket(Packet p);

  //**********************BACKGROUND****************//

  public void run() {
    while (true) {
      try {
        Packet p = getNextPacket();
        handlePacket(p);
      } catch (IOException e) {
        //(TODO):kchan
      }
    }
  }

  private Packet getNextPacket() throws IOException {
    DataInputStream inStream = new DataInputStream(incoming.getInputStream());
    Packet p = new Packet();
    p.readFrom(inStream);
    return p;
  }

}