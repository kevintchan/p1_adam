public class SourceNode extends NetworkNode {

    public HashMap<int, long> startTime;
    public long average;
    public long parameter;
    public int counter;


    public SourceNode(int inPort, int outPort, long parameter) {
	this.startTime = new HashMap<int, long>();
	this.counter = 0;
	this.average = 0;
	this.parameter = parameter;
	super(inPort, outPort);

    }

    public send(long startTime) {
	packet p = new packet(IdCounter, false);
	startTime.add(IdCounter, startTime);
	super.send(p);
	IdCounter++;
    }
    
    public void handlePacket(Packet p) {
	int id = p.getId();
	long diff = gettimefrommain - StartTime.get(id);
	if (diff =< 2*average) {
	    average = (1 - parameter)*average - paramter*diff;
	}
    }

    public
	
        
  
    
}
