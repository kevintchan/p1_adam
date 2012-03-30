import java.net.*;
import java.io.*;

public abstract class NetworkNode implements Runnable {

  String name;
  Socket outgoing;
  Socket incoming;
  String host = "localhost";
  int inport;
  int outport;
  int verboseLevel;
  boolean establishOutConnectionFirst;;

  public NetworkNode(String name, int inport, int outport) {
    this(name, inport, outport, 0);
  }
  
  public NetworkNode(String name, int inport, int outport, int verbose) {
    this.verboseLevel = verbose;
    this.name = name;
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
          output("Outgoing Port "+outport+" Connected ", 2);
        } catch (IOException e) {
          output("Outport Connect First Failed", outport, 1000);
        }
      }
    }

    try {
      ServerSocket serverSocket = new ServerSocket(inport);
      incoming = serverSocket.accept();
      output("Incoming Port "+inport+" Connected ", 2);
    } catch (IOException e) {
      //do Nothing
    }
    
    while(outgoing == null) {
      try {
        outgoing = new Socket(host, outport);
        output("Outgoing Port "+outport+" Connected ", 2);
      } catch (IOException e) {
        output("Outgoing Connect Failed", 1000);
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

   public void output(String title, int value, int vLvl) {
    output(title+"::"+value, vLvl);
  }

   public void output(String title, double value, int vLvl) {
    output(title+"::"+value, vLvl);
  }

   public void output(String s, int vLevel) {
    if (vLevel >= verboseLevel) {
      System.out.println(name+": "+s);
    }
  }

}