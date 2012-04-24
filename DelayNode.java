import java.util.*;
import java.io.*;
import java.lang.Math;

public class DelayNode extends NetworkNode {

  double lambda;
  Stenographer stenographer;
  Queue<Packet> queue;
  int outPort;
  ServiceRateFunction servRateFunction;
  
  public DelayNode(String name, int inPort, int[] outPorts, int vLvl, ServiceRateFunction servRateFunction , Stenographer stenographer) throws IOException {
    super(name, inPort, outPorts, vLvl);
    this.servRateFunction = servRateFunction;
    this.outPort = outPorts[0];
    this.lambda = lambda;
    this.queue = new LinkedList<Packet>();
    this.stenographer = stenographer;
    startQueuePollingThread();
  }

  private void startQueuePollingThread() {
    Thread t = new Thread() {
        public void run() {
          while (!terminate) {
            if (queue.peek() != null) {
              long dTime = calculateDelayTime(System.currentTimeMillis());
              try {
                sleep(dTime);
              } catch (InterruptedException e) {
                //do nothing
              }
              Packet p = queue.poll();
              int queueLen = queue.size();
              try {
                output("Sending Packet", p.getId(), 0);
                output("Delay", dTime, 1);
                output("QueueLen", queueLen, 1);
                send(p);
              } catch (IOException e) {
                // do nothing
              }
              stenographer.record(p.getId(), "QueueLen", queueLen);
              stenographer.record(p.getId(), "Delay", dTime);
            }
          }
          output("Polling Thread Terminated", 0);
        }
      };
    t.start();
  }

  private void send(Packet p) throws IOException {
    super.send(outPort, p);
  }
  
  public long calculateDelayTime(long time) {
    double servicingRate = servRateFunction.get(time);
    return (long) (Math.log(1-Math.random()) * 1000 /(-1*servicingRate));
  }
  
  public void handlePacket(Packet p) {
    output("Handling Packet", p.getId(), 0);
    queue.offer(p);
  }
}
