import java.util.*;
import java.net.*;
import java.io.*;

public class SourceNode extends NetworkNode {

    public Map<Integer, Long> startTimes;
    public double average;
    public double learningRate;
    public int IdCounter;

  public SourceNode(int inPort, int outPort, double learningRate, double average) throws IOException {
	super(inPort, outPort);
	this.startTimes = new HashMap<Integer, Long>();
	this.IdCounter = 0;
	this.average = average;
	this.learningRate = learningRate;
    }

    public void send(long startTime) throws IOException {
      System.out.println("=====================================");
	Packet p = new Packet(IdCounter, false);
	startTimes.put(IdCounter, startTime);
	super.send(p);
	IdCounter++;
    }
    
    public void handlePacket(Packet p) {
      int id = p.getId();
      System.out.println("SourceNode Handling Packet" + id);
      long diff = System.currentTimeMillis() - startTimes.get(id).longValue();
	if (diff <= 2*average) {
          average = (1 - learningRate)*average +  learningRate*diff;
          System.out.println("RTT: " + diff);
          System.out.println("Avg: " + average);
	} else {
          System.out.println("DROPPED");
        }
    }
}
