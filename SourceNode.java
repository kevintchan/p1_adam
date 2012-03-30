import java.util.*;
import java.net.*;
import java.io.*;

public class SourceNode extends NetworkNode {

  Map<Integer, Long> startTimes;
  double average;
  double learningRate;
  int IdCounter;
  Stenographer stenographer;

  public SourceNode(String name, int inPort, int outPort, int vLvl, double learningRate, double average, Stenographer stenographer) throws IOException {
    super(name, inPort, outPort, vLvl);
	this.startTimes = new HashMap<Integer, Long>();
	this.IdCounter = 0;
	this.average = average;
	this.learningRate = learningRate;
        this.stenographer = stenographer;
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
          stenographer.record(id, "RTT", diff);
          stenographer.record(id, "Avg", average);
	} else {
          output("PACKET DROPPED", id, 1);
        }
    }
}
