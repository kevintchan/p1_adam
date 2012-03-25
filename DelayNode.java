import java.util.*;
import java.lang.Math;

public class DelayNode extends NetworkNode {

  public int lambda;
  
  public Queue<Packet> queue;
  
  public DelayNode(int l, int inPort, int outPort) {
    super(inPort, outPort);
    this.lambda = l;
    this.queue = new LinkedList<Packet>();
    Thread t = new Thread() {
        public void run() {
          while (queue.peek() != null) {
            long dTime = calculateDelayTime();
            try {
              sleep(dTime);
            } catch (InterruptedException e) {
              //do nothing
            }
            Packet p = queue.poll();
            send(p);
          }
        }
      };
    t.start();
  }
  
  public long calculateDelayTime() {
    return (long) Math.log(1-Math.random())/(-1*lambda);
  }
  
  public void handlePacket(Packet p) {
    queue.offer(p);
  }
}
