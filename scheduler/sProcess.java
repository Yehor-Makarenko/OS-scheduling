public class sProcess {
  public int arrivalTime = (int) (Math.random() * 100);
  public int cputime;
  public int ioblocking;
  public int ioblockingTime = 10;
  public int cpudone;
  public int ionext;
  public int numblocked;

  public sProcess (int cputime, int ioblocking, int cpudone, int ionext, int numblocked) {
    this.cputime = cputime;
    this.ioblocking = ioblocking;
    this.cpudone = cpudone;
    this.ionext = ionext;
    this.numblocked = numblocked;
  } 	
}
