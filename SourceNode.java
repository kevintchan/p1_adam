import java.util.*;
import java.net.*;
import java.io.*;

public class SourceNode extends NetworkNode {

    Map<Integer, Long> startTimes;
    double learningRate;
    int idCounter;
    double activeAverage;
    double probeAverage;
    int activePort;
    int probePort;
    Stenographer<Long> stenographer;
    String currentPath;
    boolean probing;
    int probenum;

    public SourceNode(String name, int port, int outPorts[], int vLvl, double learningRate, double average, Stenographer<Long> stenographer) throws IOException {
	super(name, port, outPorts, vLvl);
	this.startTimes = new HashMap<Integer, Long>();
	this.idCounter = 0;
	this.probeAverage = average;
	this.activeAverage = average;
	this.learningRate = learningRate;
	this.stenographer = stenographer;
	this.activePort = outPorts[0];
	this.probePort = outPorts[1];
	this.currentPath = "A";
	this.probing = false;
    }

    public void send(long startTime) throws IOException {
	Packet p = new Packet(idCounter, false, port, activePort, false);
	startTimes.put(hash(idCounter, activePort), startTime);
	output("Sending Packet on " + currentPath, idCounter, 0);
	super.send(activePort, p);
	if (idCounter % 40 == 0) {
	    probing = true;
	    probenum = 0;
        }
        if (probing) {
          probe(startTime);
	}
        
	idCounter++;
	if (idCounter > 0 && (idCounter + 1) % 40 == 0) {
	    System.out.println("swapping time");
	    System.out.println(probeAverage);
	    System.out.println(activeAverage);
	    if (swapCriterionMet()) {
		swapPathes();
	    }
	}
    }

    public void probe(long startTime) throws IOException {
	if (idCounter % 5 == 0) {
          Packet p = new Packet(idCounter, false, port, probePort, true);
          startTimes.put(hash(idCounter, probePort), startTime);
          output("Sending Probe Packet", idCounter, 1);
          output(""+p.isProbe(), 0);
          super.send(probePort, p);
          probenum += 1;
	}
	if (probenum == 5) {
	    probing = false;
            probenum = 0;
	}
    }
  
    public void handlePacket(Packet p) {
      int id = p.getId();
      int outPort = p.getOutPort();
      int hash = hash(id, outPort);
      output("Handling Packet", hash, 0);
      if (startTimes.containsKey(hash)) {
        
        long diff = System.currentTimeMillis() - startTimes.get(hash).longValue();
        output("RTT", diff, 1);
        
        double average;
        if (!isProbe(p)) {
          average = activeAverage;
          output("Active Packet", hash, 0);
          stenographer.record(diff);
        } else {
          average = probeAverage;
          output("Probe Packet", hash, 0);
        }
        
        if (diff <= 2*average) {
          average = (1 - learningRate)*average +  learningRate*diff;
        } else {
          output("PACKET DROPPED", id, 1);
        }
        
        if (!isProbe(p)) {
          activeAverage = average;
          output("Active AVG", activeAverage, 1);
        } else {
          probeAverage = average;
          output("Probe AVG", probeAverage, 1);
        }
      }
    }

    private boolean swapCriterionMet() {
	return activeAverage > probeAverage;
    }

    private void swapPathes() {
	int tmpPort = probePort;
	probePort = activePort;
	activePort = tmpPort;

	double tmpAvg = probeAverage;
	probeAverage = activeAverage;
	activeAverage = tmpAvg;

	if (currentPath.equals("A")) {
	    currentPath = "B";
	} else {
	    currentPath = "A";
	}
	startTimes.clear();
    }
  
    private int hash(int idCount, int port) {
	return idCount * 10000 + port;
    }
    private boolean isProbe(Packet p) {
      return p.isProbe();
      //	int hash = hash(p.getId(), p.getOutPort());
      //	return p.isProbe() && (0 == (hash - probePort) % 10000);
    }
    
}
