import java.util.LinkedList;

public class DelayNode extends NetworkNode {

    public int lambda;

    public LinkedList queue<Packet>;

    public DelayNode(int l, ...) {
	this.lambda = l;
	this.queue = new queue();
    }

    public int delayTime() {
	return log(1-Math.random)/(-1*lambda);
    }

    
    public void send(Packet p ) {
        this.wait(delayTime());
    }




    
}
