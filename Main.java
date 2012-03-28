import java.io.*;
import java.util.*;

public class Main {

  static Map<String, Double> parameters;
  static double DEFAULT_INITIAL_AVERAGE = 3000; //in milliseconds
  static double DEFAULT_EXPO_DIST_LAMBDA = 5; //in seconds/packet
  static double DEFAULT_LEARNING_RATE = .01;

  public static void main(String args[]) throws IOException{
    System.out.println("begin run");

    if (!parseArgs(args)) {
      System.out.println("Problems with your arguments");
      return;
    }
    for (String key : parameters.keySet()) {
      System.out.println(key + "::" + parameters.get(key));
    }

    SourceNode s = initializeNetwork();
    runMainLoop(s);

    System.out.println("end run");
  }

  public static boolean parseArgs(String args[]) {
    parameters = new HashMap<String, Double>();
    parameters.put("-avg", DEFAULT_INITIAL_AVERAGE);
    parameters.put("-lmbda", DEFAULT_EXPO_DIST_LAMBDA);
    parameters.put("-lrnr8", DEFAULT_LEARNING_RATE);

    if (args.length % 2 != 0) {
      System.out.println("hi");
      return false;
    }
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

  public static SourceNode initializeNetwork() throws IOException {
    int portSA = 1050;
    int portAD = 1051;
    int portDA = 1052;

    SourceNode s = new SourceNode(1052, 1050,
                                  parameters.get("-lrnr8"),
                                  parameters.get("-avg"));
    DelayNode a = new DelayNode(1050, 1051, parameters.get("-lmbda"));
    DestNode d = new DestNode(1051, 1052);
    d.setEstablishOutConnectionFirst(true);
    Thread src = new Thread(s);
    src.start();
    Thread del = new Thread(a);
    del.start();
    Thread dest = new Thread(d);
    dest.start();
    return s;
  }

  public static void runMainLoop(SourceNode s) throws IOException {
    while (true) {
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        //do nothing
      }
      s.send(System.currentTimeMillis());
    }
  }
}