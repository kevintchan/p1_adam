import java.util.*;

public class ArgumentParser {

  private final static String INITIAL_AVERAGE = "-avg";
  private final static double DEFAULT_INITIAL_AVERAGE = 1000; //in milliseconds

  private final static String LAMBDA = "-l";
  private final static double DEFAULT_EXPO_DIST_LAMBDA = 5; //in packets/s

  private final static String LEARNING_RATE = "-lr";
  private final static double DEFAULT_LEARNING_RATE = .01;

  private final static String VERBOSE = "-v";
  private final static int DEFAULT_VERBOSE_LEVEL = 1;

  private final static String ITERATIONS = "-i";
  private final static int DEFAULT_ITERATIONS = 10;

  private final static int DOUBLE = 0;
  private final static int INT = 1;
  private final static int STRING = 2;

  private Map<String, Object> arguments;
  private Map<String, Integer> types;

  public ArgumentParser() {
    initializeArgumentMap();
    initializeTypeMap();
  }

  private void initializeArgumentMap() {
    arguments = new HashMap<String, Object>();
    arguments.put(INITIAL_AVERAGE, DEFAULT_INITIAL_AVERAGE);
    arguments.put(LAMBDA, DEFAULT_EXPO_DIST_LAMBDA);
    arguments.put(LEARNING_RATE, DEFAULT_LEARNING_RATE);
    arguments.put(VERBOSE, DEFAULT_VERBOSE_LEVEL);
    arguments.put(ITERATIONS, DEFAULT_ITERATIONS);
  }

  private void initializeTypeMap() {
    types = new HashMap<String, Integer>();
    types.put(INITIAL_AVERAGE, DOUBLE);
    types.put(LAMBDA, DOUBLE);
    types.put(LEARNING_RATE, DOUBLE);
    types.put(VERBOSE, INT);
    types.put(ITERATIONS, INT);
  }

  public boolean updateArguments(String[] args) {
    if (args.length % 2 != 0) {return false;}

    String key;
    Object value;
    for (int i = 0; i < args.length / 2; i++) {
      key = args[2*i];
      if (arguments.containsKey(key)) {
        String arg = args[2*i+1];
        switch(types.get(key).intValue()) {
        case DOUBLE:
          value = Double.parseDouble(arg);
        case INT:
          value = Integer.parseInt(arg);
        case STRING:
          value = arg;
        default:
          value = null;
        }
        arguments.put(key, value);
      } else {
        return false;
      }
    }
    return true;

  }

  public double getInitialAverage() {
    Double value = (Double) arguments.get(INITIAL_AVERAGE);
    return value.doubleValue();
  }

  public double getLambda() {
    Double value = (Double) arguments.get(LAMBDA);
    return value.doubleValue();
  }

  public double getLearningRate() {
     Double value = (Double) arguments.get(LEARNING_RATE);
     return value.doubleValue();
  }

  public int getVerboseLevel() {
    Integer value = (Integer) arguments.get(VERBOSE);
    return value.intValue();
  }

  public int getIterations() {
    Integer value = (Integer) arguments.get(ITERATIONS);
    return value.intValue();
  }
      

}