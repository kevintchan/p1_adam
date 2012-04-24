public class TestCases {
  private ServiceRateFunction a;
  private ServiceRateFunction b;

  public TestCases(int testcase) {
    switch(testcase) {
    case 0:
      a = new ConstantSRF(.7);
      b = new ConstantSRF(3);
      break;
    case 1:
      a = new ConstantSRF(3);
      b = new ConstantSRF(.5);
      break;
    case 2:
      a = new SwitchingSRF(.7, 1.5, 50000);
      b = new SwitchingSRF(1.5,.7, 50000);
      break;
    case 3:
      a = new ConstantSRF(1.5);
      b = new ConstantSRF(1.5);
      break;
    }
  }

  public ServiceRateFunction getA() {
    return a;
  }

  public ServiceRateFunction getB() {
    return b;
  }

  
}
