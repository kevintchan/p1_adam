import java.util.*;
import java.io.*;
import java.lang.Math;

public class DelayNode extends NetworkNode {

  public double lambda;
  
  public Queue<Packet> queue;
  
  public DelayNode(int inPort, int outPort, double lambda) throws IOException {
    super(inPort, outPort);
    this.lambda = lambda;
    this.queue = new LinkedList<Packet>();
    Thread t = new Thread() {
        public void run() {
          while (true) {
            if (queue.peek() != null) {
              long dTime = calculateDelayTime();
              try {
                sleep(dTime);
              } catch (InterruptedException e) {
                //do nothing
              }
              Packet p = queue.poll();
              try {
                System.out.println("DelayNode Sending Packet:" + p.getId());
                System.out.println("Delay:" + dTime);
                System.out.println("QueueLen:" + queue.size());
                send(p);
              } catch (IOException e) {
                // do nothing
              }
            }
          }
        }
      };
    t.start();
  }
  
  public long calculateDelayTime() {
    return (long) (Math.log(1-Math.random()) * 1000 /(-1*lambda));
  }
  
  public void handlePacket(Packet p) {
    System.out.println("DelayNode Handling Packet:" + p.getId());
    queue.offer(p);
  }
}
