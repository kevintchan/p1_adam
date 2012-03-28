import java.net.*;
import java.io.*;

public abstract class NetworkNode implements Runnable {

  Socket outgoing;
  Socket incoming;
  String host = "localhost";
  int inport;
  int outport;
  boolean establishOutConnectionFirst;;
  
  public NetworkNode(int inport, int outport) {
    this.inport = inport;
    this.outport = outport;
    this.establishOutConnectionFirst = false;
  }

  public void send(Packet p) throws IOException {
    DataOutputStream outStream = new DataOutputStream(outgoing.getOutputStream());
    p.writeTo(outStream);
    outStream.flush();
  }

  abstract public void handlePacket(Packet p);

  //**********************BACKGROUND****************//

  public void setEstablishOutConnectionFirst(boolean b) {
    this.establishOutConnectionFirst = b;
  }

  public void run() {

    // forces a wait on the out connection, otherwise every node
    // will listen on the in connection and the network will never
    // be established
    if (establishOutConnectionFirst) {
      while (outgoing == null) {
        try {
          outgoing = new Socket(host, outport);
          System.out.println("Outgoing Port "+outport+" Connected ");
        } catch (IOException e) {
          System.out.println("Outport Connect First Failed" + outport);
        }
      }
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
      } catch (IOException e) {
        System.out.println("Outgoing Connect Failed");
      }
    }

    // the actual while loop
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