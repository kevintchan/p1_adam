import java.net.*;
import java.io.*;

public abstract class NetworkNode implements Runnable {

  Socket outgoing;
  Socket incoming;
  String host = "localhost";
  int inport;
  int outport;
  
  public NetworkNode(int inport, int outport) {
    this.inport = inport;
    this.outport = outport;
  }

  public void send(Packet p) throws IOException {
    DataOutputStream outStream = new DataOutputStream(outgoing.getOutputStream());
    p.writeTo(outStream);
  }

  abstract public void handlePacket(Packet p);

  //**********************BACKGROUND****************//

  public void run() {
    
    try {
      outgoing = new Socket(host, outport);
      System.out.println("Outgoing Port "+outport+" Connected ");
    } catch (IOException e) {
      System.out.println("fail_connect1" + outport);
      //do Nothing
    }

    try {
      ServerSocket serverSocket = new ServerSocket(inport);
      incoming = serverSocket.accept();
      System.out.println("Incoming Port "+inport+" Connected ");
    } catch (IOException e) {
      //do Nothing
    }
    
    while(outgoing == null) {
      try {
        outgoing = new Socket(host, outport);
        System.out.println("Outgoing Port "+outport+" Connected ");
      } catch (UnknownHostException e) {
        System.out.println("Bad things happened");
      } catch (IOException e) {
        //do nothing
      }
    }
    
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