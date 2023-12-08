public class Process {
  public int arrivalTime;
  public int cpuTime;
  public int ioBlocking;
  public int ioBlockingTime;
  public int cpuDone;
  public int ioNext;
  public int numBlocked;

  public Process (int arrivalTime, int cpuTime, int ioBlocking, int ioBlockingTime, int cpuDone, int ioNext, int numBlocked) {
    this.arrivalTime = arrivalTime;
    this.cpuTime = cpuTime;
    this.ioBlocking = ioBlocking;
    this.ioBlockingTime = ioBlockingTime;
    this.cpuDone = cpuDone;
    this.ioNext = ioNext;
    this.numBlocked = numBlocked;
  } 	
}
