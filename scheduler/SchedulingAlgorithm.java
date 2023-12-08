// Run() is called from Scheduling.main() and is where
// the scheduling algorithm written by the user resides.
// User modification should occur within the Run() function.

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
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
    Integer currentProcess = null;    
    int size = processVector.size();
    int completed = 0;
    int quantum = 200;
    int quantumTime = 0;
    String resultsFile = "Summary-Processes";
    Queue<Integer> processQueue = new LinkedList<>();
    TreeMap<Integer, Integer> blockedList = new TreeMap<>();
    sProcess process = null;

    result.schedulingType = "Preemptive";
    result.schedulingName = "Round-robin";         

    try {
      if (size == 0) {
        result.compuTime = comptime;        
        return result;
      }
      PrintStream out = new PrintStream(new FileOutputStream(resultsFile));                  

      while (comptime < runtime) {           
        for (int j = 0; j < size; j++) {
          if (comptime == ((sProcess) processVector.get(j)).arrivalTime) {
            blockedList.put(j, comptime);
          }
        }
        if ((quantumTime == quantum) && !(process.cpudone == process.cputime) && !(process.ioblocking == process.ionext)) {
          blockedList.put(currentProcess, comptime);
        }     

        Iterator<Entry<Integer, Integer>> iterator = blockedList.entrySet().iterator();
        while (iterator.hasNext()) {
          Entry<Integer, Integer> blockedProcess = iterator.next();
          if (blockedProcess.getValue() <= comptime) {
            processQueue.add(blockedProcess.getKey());
            iterator.remove();
          }
        }

        if (currentProcess == null) {
          currentProcess = processQueue.poll();
          if (currentProcess == null) {
            comptime++;
            continue;
          }
          process = (sProcess) processVector.get(currentProcess);  
          out.println("Process: " + currentProcess + " registered... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + " " + comptime + ")");
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
