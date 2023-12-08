// Run() is called from Scheduling.main() and is where
// the scheduling algorithm written by the user resides.
// User modification should occur within the Run() function.

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.io.*;

public class SchedulingAlgorithm {

  public static Results run(int runTime, int quantum, ArrayList<Process> processVector, Results result) {
    int compTime = 0;
    Integer currentProcess = null;    
    int size = processVector.size();
    int completed = 0;
    int quantumTime = 0;
    String resultsFile = "Summary-Processes";
    Queue<Integer> processQueue = new LinkedList<>();
    TreeMap<Integer, Integer> blockedList = new TreeMap<>();
    Process process = null;

    result.schedulingType = "Preemptive";
    result.schedulingName = "Round-robin";         

    try {
      if (size == 0) {
        result.compTime = compTime;        
        return result;
      }
      PrintStream out = new PrintStream(new FileOutputStream(resultsFile));           

      while (compTime < runTime) {           
        for (int j = 0; j < size; j++) {
          if (compTime == processVector.get(j).arrivalTime) {
            blockedList.put(j, compTime);
          }
        }
        if ((quantumTime == quantum) && !(process.cpuDone == process.cpuTime) && !(process.ioBlocking == process.ioNext)) {
          blockedList.put(currentProcess, compTime);
        }     

        Iterator<Entry<Integer, Integer>> iterator = blockedList.entrySet().iterator();
        while (iterator.hasNext()) {
          Entry<Integer, Integer> blockedProcess = iterator.next();
          if (blockedProcess.getValue() <= compTime) {
            processQueue.add(blockedProcess.getKey());
            iterator.remove();
          }
        }

        if (currentProcess == null) {
          currentProcess = processQueue.poll();
          if (currentProcess == null) {
            compTime++;
            continue;
          }
          process = (Process) processVector.get(currentProcess);  
          out.println("Process: " + currentProcess + " registered... (" + process.cpuTime + " " + process.ioBlocking + " " + process.cpuDone + " " + compTime + ")");
        }

        if (process.cpuDone == process.cpuTime) {
          completed++;
          out.println("Process: " + currentProcess + " completed... (" + process.cpuTime + " " + process.ioBlocking + " " + process.cpuDone + " " + compTime + ")");
          if (completed == size) {
            result.compTime = compTime;
            out.close();
            return result;
          }

          quantumTime = 0;
          currentProcess = processQueue.poll();
          if (currentProcess == null) {
            compTime++;
            continue;
          }
          process = processVector.get(currentProcess);          
          out.println("Process: " + currentProcess + " registered... (" + process.cpuTime + " " + process.ioBlocking + " " + process.cpuDone + " " + compTime + ")");          
        }      

        if (process.ioBlocking == process.ioNext) {
          out.println("Process: " + currentProcess + " I/O blocked... (" + process.cpuTime + " " + process.ioBlocking + " " + process.cpuDone + " " + compTime + ")");
          process.numBlocked++;
          process.ioNext = 0;       
          quantumTime = 0;    
          if (process.ioBlockingTime > 0) {
            blockedList.put(currentProcess, compTime + process.ioBlockingTime);
          } else {
            processQueue.add(currentProcess);
          }
          currentProcess = processQueue.poll();
          if (currentProcess == null) {
            compTime++;
            continue;
          }
          process = processVector.get(currentProcess);
          out.println("Process: " + currentProcess + " registered... (" + process.cpuTime + " " + process.ioBlocking + " " + process.cpuDone + " " + compTime + ")");          
        }    
        
        if (quantumTime == quantum) {        
          out.println("Process: " + currentProcess + " interrupted... (" + process.cpuTime + " " + process.ioBlocking + " " + process.cpuDone + " " + compTime + ")");
          quantumTime = 0;                    
          currentProcess = processQueue.poll();
          if (currentProcess == null) {
            compTime++;
            continue;
          }
          process = processVector.get(currentProcess);
          out.println("Process: " + currentProcess + " registered... (" + process.cpuTime + " " + process.ioBlocking + " " + process.cpuDone + " " + compTime + ")");          
        }

        process.cpuDone++;       
        if (process.ioBlocking > 0) {
          process.ioNext++;
        }
        quantumTime++;
        compTime++;
      }
      out.close();
    } catch (IOException e) { }
    result.compTime = compTime;
    return result;
  }
}
