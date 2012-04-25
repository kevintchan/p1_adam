import java.util.*;
import java.io.*;
import java.lang.Math;

public class DelayNode extends NetworkNode {

  double lambda;
  Stenographer<Integer> stenographer;
  Stenographer<Long> stenoTimes;
  Queue<Packet> queue;
  int outPort;
  ServiceRateFunction servRateFunction;
  
  public DelayNode(String name, int inPort, int[] outPorts, int vLvl, ServiceRateFunction servRateFunction , Stenographer<Integer> stenographer, Stenographer<Long> times) throws IOException {
    super(name, inPort, outPorts, vLvl);
    this.servRateFunction = servRateFunction;
    this.outPort = outPorts[0];
    this.lambda = lambda;
    this.queue = new LinkedList<Packet>();
    this.stenographer = stenographer;
    this.stenoTimes = times;
    startQueuePollingThread();
  }

  private void startQueuePollingThread() {
    Thread t = new Thread() {
        public void run() {
          while (!terminate) {
            if (queue.peek() != null) {
              long currentTime = System.currentTimeMillis();
              long dTime = calculateDelayTime(currentTime);
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
              stenographer.record(queueLen);
              stenoTimes.record(currentTime);
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
    output("Probe?"+p.isProbe(), 0);
    queue.offer(p);
  }
}
