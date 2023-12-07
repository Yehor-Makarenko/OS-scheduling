// Run() is called from Scheduling.main() and is where
// the scheduling algorithm written by the user resides.
// User modification should occur within the Run() function.

import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;
import java.io.*;

public class SchedulingAlgorithm {

  public static Results Run(int runtime, Vector processVector, Results result) {
    int i = 0;
    int comptime = 0;
    int currentProcess = 0;    
    int size = processVector.size();
    int completed = 0;
    int quantum = 200;
    int quantumTime = 0;
    String resultsFile = "Summary-Processes";
    Queue<Integer> processQueue = new LinkedList<>();

    result.schedulingType = "Batch (Nonpreemptive)";
    result.schedulingName = "First-Come First-Served";         

    try {
      PrintStream out = new PrintStream(new FileOutputStream(resultsFile));            

      for (int j = 0; j < size; j++) { 
        processQueue.add(j);
      }

      sProcess process = (sProcess) processVector.get(processQueue.poll());
      out.println("Process: " + currentProcess + " registered... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + " " + comptime + ")");

      while (comptime < runtime) {
        if (process.cpudone == process.cputime) {
          completed++;
          out.println("Process: " + currentProcess + " completed... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + " " + comptime + ")");
          if (completed == size) {
            result.compuTime = comptime;
            out.close();
            return result;
          }

          currentProcess = processQueue.poll();
          process = (sProcess) processVector.get(currentProcess);          
          out.println("Process: " + currentProcess + " registered... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + " " + comptime + ")");
          quantumTime = 0;
        }      

        while (process.ioblocking == process.ionext) {
          out.println("Process: " + currentProcess + " I/O blocked... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + " " + comptime + ")");
          process.numblocked++;
          process.ionext = 0;           
          processQueue.add(currentProcess);
          currentProcess = processQueue.poll();
          process = (sProcess) processVector.get(currentProcess);
          out.println("Process: " + currentProcess + " registered... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + " " + comptime + ")");
          quantumTime = 0;
        }    
        
        if (quantumTime == quantum) {        
          out.println("Process: " + currentProcess + " interrupted... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + " " + comptime + ")");
          processQueue.add(currentProcess);
          currentProcess = processQueue.poll();
          process = (sProcess) processVector.get(currentProcess);
          out.println("Process: " + currentProcess + " registered... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + " " + comptime + ")");
          quantumTime = 0;
        }

        process.cpudone++;       
        if (process.ioblocking > 0) {
          process.ionext++;
        }
        quantumTime++;
        comptime++;
      }
      out.close();
    } catch (IOException e) { /* Handle exceptions */ }
    result.compuTime = comptime;
    return result;
  }
}
