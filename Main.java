import java.io.*;
import java.util.*;

public class Main {

  static Map<String, Double> parameters;
  static double DEFAULT_INITIAL_AVERAGE = 1000; //in milliseconds
  static double DEFAULT_EXPO_DIST_LAMBDA = 5; //in packets/s
  static double DEFAULT_LEARNING_RATE = .01;
  static double DEFAULT_VERBOSE_LEVEL = 1;
  static double DEFAULT_ITERATIONS = 10;

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

    Stenographer stenographer = new Stenographer(parameters);

    Map<Integer, NetworkNode> nodes = initializeNetwork(stenographer);
    runMainLoop(nodes, parameters.get("-i"));

    try {
      Thread.sleep(1000);
    } catch (Exception e) {
    }

    stenographer.timeVsAvgDelay("tva");
    stenographer.timeVsQueueLen("tvq");

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

  public static Map<Integer, NetworkNode> initializeNetwork(Stenographer stenographer) throws IOException {

    int vLvl = (int) parameters.get("-verbose").doubleValue();
    Map<Integer, NetworkNode> nodes = new HashMap<Integer, NetworkNode>();

    /**
    int[] delayPorts = new int[2];
    delayPorts[0] = portA;
    delayPorts[1] = portB;

    int[] sourcePorts = new int[2];
    sourcePorts[0] = portS1;
    sourcePorts[1] = portS2;
    **/

    /*******tmp*********/
    int[] delayPorts = new int[1];
    delayPorts[0] = portA;
    int[] sourcePorts = new int[1];
    sourcePorts[0] = portS1;
    /*************/

    int[] destPortArray = new int[1];
    destPortArray[0] = portD;

    SourceNode s1 = new SourceNode("S1", portS1, delayPorts, vLvl,
                                   parameters.get("-lrnr8"),
                                   parameters.get("-avg"), stenographer);
    nodes.put(portS1, s1);

   
    DelayNode a = new DelayNode("A", portA, destPortArray, vLvl,
                                parameters.get("-lmbda"), stenographer);
    nodes.put(portA, a);

    DestNode d = new DestNode("D", portD, sourcePorts, vLvl);
    nodes.put(portD, d);

    s1.init();
    a.init();
    d.init();

    Thread src = new Thread(s1);
    src.start();
    Thread del = new Thread(a);
    del.start();
    Thread dest = new Thread(d);
    dest.start();

    return nodes;
  }

  public static void runMainLoop(Map<Integer, NetworkNode> nodes, double iterations) throws IOException {
    SourceNode s = (SourceNode) nodes.get(portS1);
    
    for (int i = 0; i < iterations; i++) {
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        //do nothing
      }
      System.out.println("=====================================");

      s.send(portA, System.currentTimeMillis());
    }
  }
}