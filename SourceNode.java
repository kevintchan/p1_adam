import java.util.*;
import java.net.*;
import java.io.*;

public class SourceNode extends NetworkNode {

    public Map<Integer, Long> startTimes;
    public double average;
    public double learningRate;
    public int IdCounter;

  public SourceNode(String name, int inPort, int outPort, int vLvl, double learningRate, double average) throws IOException {
    super(name, inPort, outPort, vLvl);
	this.startTimes = new HashMap<Integer, Long>();
	this.IdCounter = 0;
	this.average = average;
	this.learningRate = learningRate;
    }

    public void send(long startTime) throws IOException {
      
	Packet p = new Packet(IdCounter, false);
	startTimes.put(IdCounter, startTime);
        output("Sending Packet", IdCounter, 0);
	super.send(p);
	IdCounter++;
    }
    
    public void handlePacket(Packet p) {
      int id = p.getId();
      output("Handling Packet", id, 0);
      long diff = System.currentTimeMillis() - startTimes.get(id).longValue();
	if (diff <= 2*average) {
          average = (1 - learningRate)*average +  learningRate*diff;
          output("RTT", diff, 1);
          output("Avg", average, 1);
	} else {
          output("PACKET DROPPED", id, 1);
        }
    }
}
