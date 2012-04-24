public abstract class ServiceRateFunction {
  private long startTime; // in millis
  
  public ServiceRateFunction() {
    this.startTime = System.currentTimeMillis();
  }
  
  abstract public double get(long time);
  public void setStartTime(long startTime) {
    this.startTime = startTime;
  }
  protected long normalizedTime(long time) {
    return time - this.startTime;
  }
}

class ConstantSRF extends ServiceRateFunction {
  private double constant;
  public ConstantSRF(double constant) {
    this.constant = constant;
  }
  public double get(long time) {
    return this.constant;
  }
}

class SwitchingSRF extends ServiceRateFunction {

  private double initialConstant;
  private double finalConstant;
  private long switchTime;
  
  public SwitchingSRF(double initialConstant, double finalConstant, long switchTime) {
    this.initialConstant = initialConstant;
    this.finalConstant = finalConstant;
    this.switchTime = switchTime;
  }

  public double get(long time) {
    if (normalizedTime(time) < switchTime) {
      return initialConstant;
    } else {
      return finalConstant;
    }
  }
}