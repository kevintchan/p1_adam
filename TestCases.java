public class TestCases {
  private ServiceRateFunction a;
  private ServiceRateFunction b;

  public TestCases(int testcase) {
    switch(testcase) {
    case 0:
      a = new ConstantSRF(.3);
      b = new ConstantSRF(2);
    case 1:
      a = new ConstantSRF(2);
      b = new ConstantSRF(1);
    case 2:
      a = new SwitchingSRF(1,2, 10000);
      b = new SwitchingSRF(2,1, 10000);
    }
  }

  public ServiceRateFunction getA() {
    return a;
  }

  public ServiceRateFunction getB() {
    return b;
  }

  
}
