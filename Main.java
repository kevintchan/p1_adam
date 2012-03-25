import java.io.*;

public class Main {

  public static void main(String args[]) throws IOException{
    System.out.println("running");
    SourceNode s = initializeNetwork();
    runMainLoop(s);

    
  }

  public static SourceNode initializeNetwork() throws IOException {
    int portSA = 1050;
    int portAD = 1051;
    int portDA = 1052;
    int lambda = 5;
    double parameter = .01;

    SourceNode s = new SourceNode(1052, 1050, parameter);
    DelayNode a = new DelayNode(1050, 1051, lambda);
    DestNode d = new DestNode(1051, 1052);
    
    Thread src = new Thread(s);
    src.start();
    Thread del = new Thread(a);
    del.start();
    try {
      Thread.sleep(500);
    } catch (InterruptedException e) {
      //do nothing
    }
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