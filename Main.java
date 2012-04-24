import java.io.*;
import java.util.*;

public class Main {

  static Map<String, Double> parameters;
  static double DEFAULT_INITIAL_AVERAGE = 1000; //in milliseconds
  static double DEFAULT_EXPO_DIST_LAMBDA = 5; //in packets/s
  static double DEFAULT_LEARNING_RATE = .1;
  static double DEFAULT_VERBOSE_LEVEL = 1;
  static double DEFAULT_ITERATIONS = 10;
  static double DEFAULT_TEST = 0;

  static int portS1 = 1050;
  static int portS2 = 1051;
  static int portA = 1052;
  static int portB = 1053;
  static int portD = 1054;

  public static void main(String args[]) throws IOException{
    System.out.println("begin run");

    if (!parseArgs(args)) {
      System.out.println("Problems with your arguments");
      return;
    }

    for (String key : parameters.keySet()) {
      System.out.println(key + "::" + parameters.get(key));
    }

    Map<String, Stenographer> stenos = new HashMap<String, Stenographer>();
    Stenographer<Integer> queueLenA = new Stenographer<Integer>("QueueLength_A");
    stenos.put("A", queueLenA);
    Stenographer<Integer> queueLenB = new Stenographer<Integer>("QueueLength_B");
    stenos.put("B", queueLenB);
    Stenographer<Long> delayS1 = new Stenographer<Long>("RTT_S1");
    stenos.put("S1", delayS1);
    Stenographer<Long> delayS2 = new Stenographer<Long>("RTT_S2");
    stenos.put("S2", delayS2);
    
    Map<Integer, NetworkNode> nodes = initializeNetwork(stenos);
    runMainLoop(nodes, parameters.get("-i"));

    try {
      Thread.sleep(10000);
    } catch (Exception e) {
    }

    for(Stenographer s: stenos.values()) {
      s.write();
    }

    for (NetworkNode n : nodes.values()) {
       n.kill();
    }

    System.out.println("end run");
  }

  public static boolean parseArgs(String args[]) {
    parameters = new HashMap<String, Double>();
    parameters.put("-avg", DEFAULT_INITIAL_AVERAGE);
    parameters.put("-lmbda", DEFAULT_EXPO_DIST_LAMBDA);
    parameters.put("-lrnr8", DEFAULT_LEARNING_RATE);
    parameters.put("-verbose", DEFAULT_VERBOSE_LEVEL);
    parameters.put("-i", DEFAULT_ITERATIONS);
    parameters.put("-test", DEFAULT_TEST);

    if (args.length % 2 != 0) {return false;}

    String param;
    double value;
    for (int i = 0; i < args.length / 2; i++) {
      param = args[2*i];
      if (parameters.containsKey(param)) {
        value = Double.parseDouble(args[2*i+1]);
        parameters.put(param, value);
      } else {
        return false;
      }
    }
    return true;
  }

  public static Map<Integer, NetworkNode> initializeNetwork(Map<String, Stenographer> stenos) throws IOException {

    int vLvl = (int) parameters.get("-verbose").doubleValue();
    Map<Integer, NetworkNode> nodes = new HashMap<Integer, NetworkNode>();

    int[] delayPorts = new int[2];
    delayPorts[0] = portA;
    delayPorts[1] = portB;
    int[] sourcePorts = new int[2];
    sourcePorts[0] = portS1;
    sourcePorts[1] = portS2;
    int[] destPortArray = new int[1];
    destPortArray[0] = portD;
    SourceNode s1 = new SourceNode("S1", portS1, delayPorts, vLvl,
                                   parameters.get("-lrnr8"),
                                   parameters.get("-avg"), stenos.get("S1"), 0);
    nodes.put(portS1, s1);
    SourceNode s2 = new SourceNode("S2", portS2, delayPorts, vLvl,
                                   parameters.get("-lrnr8"),
                                   parameters.get("-avg"), stenos.get("S2"), 1);
    nodes.put(portS2, s2);

    TestCases tc = new TestCases((int) parameters.get("-test").doubleValue());
    ServiceRateFunction aSRF = tc.getA();
    System.out.println(tc.getA().get(0));
    DelayNode a = new DelayNode("A", portA, destPortArray, vLvl, aSRF, stenos.get("A"));
    nodes.put(portA, a);


    ServiceRateFunction bSRF = tc.getB();
    DelayNode b = new DelayNode("B", portB, destPortArray, vLvl, bSRF, stenos.get("B"));
    nodes.put(portB, b);
    DestNode d = new DestNode("D", portD, sourcePorts, vLvl);
    nodes.put(portD, d);

    s1.init();
    s2.init();
    a.init();
    b.init();
    d.init();

    Thread src1 = new Thread(s1);
    src1.start();
    Thread src2 = new Thread(s2);
    src2.start();
    Thread delA = new Thread(a);
    delA.start();
    Thread delB = new Thread(b);
    delB.start();
    Thread dest = new Thread(d);
    dest.start();

    return nodes;
  }

  public static void runMainLoop(Map<Integer, NetworkNode> nodes, double iterations) throws IOException {
    SourceNode s1 = (SourceNode) nodes.get(portS1);
    SourceNode s2 = (SourceNode) nodes.get(portS2);
    
    for (int i = 0; i < iterations; i++) {
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        //do nothing
      }
      System.out.println("=====================================");

      s1.send(System.currentTimeMillis());
      s2.send(System.currentTimeMillis());
    }
  }
}
