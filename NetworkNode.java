import java.net.*;
import java.io.*;
import java.util.*;

public abstract class NetworkNode implements Runnable {

  String name;
  ServerSocket serverSocket;
  int port;
  Map<Integer, Socket> outgoingSockets;
  String host = "localhost";
  int verboseLevel;
  boolean terminate;
  int[] outgoingPorts;
  Queue<Packet> queue;

  public NetworkNode(String name, int port, int[] outgoing) throws IOException {
    this(name, port, outgoing, 0);
  }
  
  public NetworkNode(String name, int port, int[] outgoing, int verbose) throws IOException {
    this.verboseLevel = verbose;
    this.name = name;
    this.terminate = false;
    this.port = port;
    this.serverSocket = new ServerSocket(port);
    this.queue = new LinkedList<Packet>();
    this.outgoingPorts = outgoing;
    outgoingSockets = new HashMap<Integer, Socket>();
    startListeningThread();
  }

  public void send(int port, Packet p) throws IOException {
    Socket outSocket = outgoingSockets.get(port);
    DataOutputStream outStream = new DataOutputStream(outSocket.getOutputStream());
    p.writeTo(outStream);
    outStream.flush();
  }

  abstract public void handlePacket(Packet p);

  public void kill() throws IOException {
    terminate = true;
    for (Socket outgoing : outgoingSockets.values()) {
      outgoing.close();
    }
    serverSocket.close();
    output("killed", 0);
  }
  //**********************BACKGROUND****************//

  public void init() throws IOException{
    for (int i = 0; i < outgoingPorts.length; i++) {
      int outPort = outgoingPorts[i];
      Socket s = null;
      while (s == null) {
        output("roll", 0);
        try {
          s = new Socket(host, outPort);
        } catch (IOException e) {
          //doNothing
        }
      }
      output(port+"::"+outPort, 0);
      outgoingSockets.put(outPort, s);
    }
  }

  public void run() {
    while (!terminate) {
      try {
        Packet p = getNextPacket();
        if (!terminate) {
          handlePacket(p);
        }
      } catch (IOException e) {
        //(IOException)
      }
    }
    output(name+": terminated", 2);
  }

  private void startListeningThread() {
    Thread t = new Thread() {
        Set<Socket> incomingSockets = new HashSet<Socket>();
        public void run() {
          try {
            while (!serverSocket.isClosed()) {
              Socket incoming = serverSocket.accept();
              incomingSockets.add(incoming);
              Listener l = new Listener(queue, incoming);
              Thread workerThread = new Thread(l);
              workerThread.start();
            }
          } catch (IOException e) {
            output("Listener Terminated", 0);
            try {
              for (Socket incomingSocket : incomingSockets) {
                incomingSocket.close();
              }
            } catch (IOException ioe) {
              //(IOException)
            }
          }
        }
      };
    t.start();
  }

  private Packet getNextPacket() throws IOException {
    while (queue.peek() == null && !terminate) {}
    Packet p = queue.poll();
    return p;
  }

  public String getName() {
    return name;
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

class Listener implements Runnable {
  Queue<Packet> queue;
  Socket incoming;
  DataInputStream inStream;
  
  public Listener(Queue<Packet> queue, Socket incoming) throws IOException {
    this.queue = queue;
    this.incoming = incoming;
    this.inStream = new DataInputStream(incoming.getInputStream());
  }

  public void run() {
    try {
      while (!incoming.isClosed()) {
        Packet p = new Packet();
        p.readFrom(inStream);
        queue.offer(p);
      }
    } catch (IOException e) {
      //IOException 
    }
  }
}