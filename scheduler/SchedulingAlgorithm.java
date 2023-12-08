// Run() is called from Scheduling.main() and is where
// the scheduling algorithm written by the user resides.
// User modification should occur within the Run() function.

import java.util.LinkedList;
import java.util.Queue;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.Map.Entry;
import java.io.*;

public class SchedulingAlgorithm {

  public static Results Run(int runtime, Vector processVector, Results result) {
    int i = 0;
    int comptime = 0;
    Integer currentProcess = 0;    
    int size = processVector.size();
    int completed = 0;
    int quantum = 200;
    int quantumTime = 0;
    String resultsFile = "Summary-Processes";
    Queue<Integer> processQueue = new LinkedList<>();
    TreeMap<Integer, Integer> blockedList = new TreeMap<>();

    result.schedulingType = "Batch (Nonpreemptive)";
    result.schedulingName = "First-Come First-Served";         

    try {
      PrintStream out = new PrintStream(new FileOutputStream(resultsFile));            

      for (int j = 0; j < size; j++) { 
        processQueue.add(j);
      }

      currentProcess = processQueue.poll();
      if (currentProcess == null) {
        result.compuTime = comptime;
        out.close();
        return result;
      }
      sProcess process = (sProcess) processVector.get(currentProcess);  
      out.println("Process: " + currentProcess + " registered... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + " " + comptime + ")");

      while (comptime < runtime) {           
        if ((quantumTime == quantum) && !(process.cpudone == process.cputime) && !(process.ioblocking == process.ionext)) {
          blockedList.put(currentProcess, comptime);
        }     
        for (Entry<Integer, Integer> blockedProcess: blockedList.entrySet()) {
          if (blockedProcess.getValue() <= comptime) {
            processQueue.add(blockedProcess.getKey());
            blockedList.remove(blockedProcess.getKey());
          }
        }

        if (currentProcess == null) {
          currentProcess = processQueue.poll();
          if (currentProcess == null) {
            comptime++;
            continue;
          }
          out.println("Process: " + currentProcess + " registered... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + " " + comptime + ")");
          process = (sProcess) processVector.get(currentProcess);  
        }

        if (process.cpudone == process.cputime) {
          completed++;
          out.println("Process: " + currentProcess + " completed... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + " " + comptime + ")");
          if (completed == size) {
            result.compuTime = comptime;
            out.close();
            return result;
          }

          quantumTime = 0;
          currentProcess = processQueue.poll();
          if (currentProcess == null) {
            comptime++;
            continue;
          }
          process = (sProcess) processVector.get(currentProcess);          
          out.println("Process: " + currentProcess + " registered... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + " " + comptime + ")");          
        }      

        if (process.ioblocking == process.ionext) {
          out.println("Process: " + currentProcess + " I/O blocked... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + " " + comptime + ")");
          process.numblocked++;
          process.ionext = 0;       
          quantumTime = 0;    
          if (process.ioblockingTime > 0) {
            blockedList.put(currentProcess, comptime + process.ioblockingTime);
          } else {
            processQueue.add(currentProcess);
          }
          currentProcess = processQueue.poll();
          if (currentProcess == null) {
            comptime++;
            continue;
          }
          process = (sProcess) processVector.get(currentProcess);
          out.println("Process: " + currentProcess + " registered... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + " " + comptime + ")");          
        }    
        
        if (quantumTime == quantum) {        
          out.println("Process: " + currentProcess + " interrupted... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + " " + comptime + ")");
          quantumTime = 0;                    
          currentProcess = processQueue.poll();
          if (currentProcess == null) {
            comptime++;
            continue;
          }
          process = (sProcess) processVector.get(currentProcess);
          out.println("Process: " + currentProcess + " registered... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + " " + comptime + ")");          
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
