// This file contains the main() function for the Scheduling
// simulation.  Init() initializes most of the variables by
// reading from a provided file.  SchedulingAlgorithm.Run() is
// called from main() to run the simulation.  Summary-Results
// is where the summary results are written, and Summary-Processes
// is where the process scheduling summary is written.

// Created by Alexander Reeder, 2001 January 06

import java.io.*;
import java.util.*;

public class Scheduling {
  private static int processnum = 5;
  private static int executionTime = 1000;
  private static int executionStandardDev = 100;  
  private static int blockTime = 100;
  private static int blockStandardDev = 10;
  private static int maxRunTime = 1000;
  private static int quantum = 200;
  private static ArrayList<Process> processVector = new ArrayList<>();
  private static Results result = new Results("null","null",0);
  private static String resultsFile = "Summary-Results";

  private static void init(String file) {
    File f = new File(file);
    String line;    
    int arrivalTime = 0;
    int cpuTime = 0;
    int ioTime = 0;
    int ioBlocking = 0;
    double x = 0.0;

    try {   
      BufferedReader in = new BufferedReader(new FileReader(f));
    
      while ((line = in.readLine()) != null) {
        if (line.startsWith("numprocess")) {
          StringTokenizer st = new StringTokenizer(line);
          st.nextToken();
          processnum = Common.s2i(st.nextToken());
        }
        if (line.startsWith("run_time_average")) {
          StringTokenizer st = new StringTokenizer(line);
          st.nextToken();
          executionTime = Common.s2i(st.nextToken());
        }
        if (line.startsWith("run_time_stddev")) {
          StringTokenizer st = new StringTokenizer(line);
          st.nextToken();
          executionStandardDev = Common.s2i(st.nextToken());
        }
        if (line.startsWith("block_time_average")) {
          StringTokenizer st = new StringTokenizer(line);
          st.nextToken();
          blockTime = Common.s2i(st.nextToken());
        }
        if (line.startsWith("block_time_stddev")) {
          StringTokenizer st = new StringTokenizer(line);
          st.nextToken();
          blockStandardDev = Common.s2i(st.nextToken());
        }
        if (line.startsWith("quantum")) {
          StringTokenizer st = new StringTokenizer(line);
          st.nextToken();
          quantum = Common.s2i(st.nextToken());
        }
        if (line.startsWith("process")) {
          StringTokenizer st = new StringTokenizer(line);
          st.nextToken();
          arrivalTime = Common.s2i(st.nextToken());
          ioBlocking = Common.s2i(st.nextToken());
          x = Common.R1();
          while (x == -1.0) {
            x = Common.R1();
          }
          x = x * executionStandardDev;
          cpuTime = (int) x + executionTime;
          x = Common.R1();
          while (x == -1.0) {
            x = Common.R1();
          }
          x = x * blockStandardDev;
          ioTime = (int) x + blockTime;


          processVector.add(new Process(arrivalTime, cpuTime, ioBlocking, ioTime, 0, 0, 0));          
        }
        if (line.startsWith("runtime")) {
          StringTokenizer st = new StringTokenizer(line);
          st.nextToken();
          maxRunTime = Common.s2i(st.nextToken());
        }
      }
      in.close();
    } catch (IOException e) { }
  }

  public static void main(String[] args) {
    int i = 0;

    if (args.length != 1) {
      System.out.println("Usage: 'java Scheduling <INIT FILE>'");
      System.exit(-1);
    }
    File f = new File(args[0]);
    if (!(f.exists())) {
      System.out.println("Scheduling: error, file '" + f.getName() + "' does not exist.");
      System.exit(-1);
    }  
    if (!(f.canRead())) {
      System.out.println("Scheduling: error, read of " + f.getName() + " failed.");
      System.exit(-1);
    }
    System.out.println("Working...");
    init(args[0]);
    if (processVector.size() < processnum) {
      i = 0;
      while (processVector.size() < processnum) {       
        double X = Common.R1();
        while (X == -1.0) {
          X = Common.R1();
        }
        X = X * executionStandardDev;
        int cputime = (int) X + executionTime;
        X = Common.R1();
          while (X == -1.0) {
            X = Common.R1();
          }
          X = X * blockStandardDev;
          int iotime = (int) X + blockTime;
        processVector.add(new Process(i * 100, cputime, i * 100, iotime, 0,0,0));          
        i++;
      }
    }
    result = SchedulingAlgorithm.run(maxRunTime, quantum, processVector, result);    
    PrintStream out = null;
    try {
      out = new PrintStream(new FileOutputStream(resultsFile));
      out.println("Scheduling Type: " + result.schedulingType);
      out.println("Scheduling Name: " + result.schedulingName);
      out.println("Simulation Run Time: " + result.compTime);
      out.println("Execution Time: " + executionTime);
      out.println("Standard Deviation For Execution Time: " + executionStandardDev);
      out.println("IO Blocking Time: " + blockTime);
      out.println("Standard Deviation For IO Blocking Time: " + blockStandardDev);
      out.println("Quantum: " + quantum);
      out.println("\nProcess #\tArrival time\tCPU Time\tIO Blocking\tIO Blocking Time\tCPU Completed\tCPU Blocked");
      for (i = 0; i < processVector.size(); i++) {
        Process process = (Process) processVector.get(i);
        out.print(Integer.toString(i));
        out.print("\t\t\t\t\t");
        out.print(Integer.toString(process.arrivalTime));
        out.print(" (ms)\t\t");
        out.print(Integer.toString(process.cpuTime));
        out.print(" (ms)\t\t");
        out.print(Integer.toString(process.ioBlocking));
        out.print(" (ms)\t\t"); 
        out.print(Integer.toString(process.ioBlockingTime));
        out.print(" (ms)\t\t");
        out.print(Integer.toString(process.cpuDone));
        out.print(" (ms)\t\t");
        out.println(process.numBlocked + " times");
      }
      out.close();
    } catch (IOException e) { }
    System.out.println("Completed.");
  }
}

