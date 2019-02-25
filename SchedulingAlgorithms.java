import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;

/**
 *
 * @author abe
 */
public class SchedulingAlgorithms {
    
    private static final int SWITCH_COST = 3;
    private static Scanner keyboard = new Scanner(System.in);
    private static Scanner fileScanner;
    private static String fileName = "testdata1.txt";
    private static ArrayList<Process> scheduleProcesses = new ArrayList<Process>();
    
    private static void setFileName() {
        System.out.print("Enter the full desired file name: ");
        fileName = keyboard.nextLine();
        try {
            File file = new File(fileName);
            
            fileScanner = new Scanner(file);
            while(fileScanner.hasNextLine()) {
                int pid = Integer.parseInt(fileScanner.nextLine());
                int burstTime = Integer.parseInt(fileScanner.nextLine());
                int priority = Integer.parseInt(fileScanner.nextLine());
                Process newProcess = new Process(pid, burstTime, priority);
                scheduleProcesses.add(newProcess);
            }
            System.out.println("Finished loading from file");
        } catch(Exception e) {
            System.out.println("EXCEPTION ENCOUNTERED: " + e.toString());
        }
        runAlgorithms();
    }
    
    private static void firstComeFirstServe() {
        try {
            String csvFileName = "FCFS" + "-" + fileName.substring(0, fileName.length() - 4) + ".csv";
            PrintWriter pw = new PrintWriter(new File(csvFileName));
            StringBuilder sb = new StringBuilder();
            sb.append("CpuTime");
            sb.append(',');
            sb.append("PID");
            sb.append(',');
            sb.append("StartingBurstTime");
            sb.append(',');
            sb.append("EndingBurstTime");
            sb.append(',');
            sb.append("CompletionTime");
            sb.append('\n');

            sb.append("0");
            sb.append(',');
            sb.append(scheduleProcesses.get(0).getPid());
            sb.append(',');
            sb.append(scheduleProcesses.get(0).getBurstTime());
            sb.append(',');
            sb.append("0");
            sb.append(',');
            sb.append(scheduleProcesses.get(0).getBurstTime());
            sb.append('\n');
            
            int completionTime = scheduleProcesses.get(0).getBurstTime();
            int cpuTime = completionTime + SWITCH_COST;
            int runningCount = completionTime;
            
            for(int i = 1; i < scheduleProcesses.size(); i++) {
                completionTime = scheduleProcesses.get(i).getBurstTime() + cpuTime;
                runningCount += completionTime;
                
                sb.append(cpuTime);
                sb.append(',');
                sb.append(scheduleProcesses.get(i).getPid());
                sb.append(',');
                sb.append(scheduleProcesses.get(i).getBurstTime());
                sb.append(',');
                sb.append("0");
                sb.append(',');
                sb.append(completionTime);
                sb.append('\n');
                
                cpuTime = completionTime + SWITCH_COST;
            }
            
            double average = (double)runningCount / scheduleProcesses.size();

            pw.write(sb.toString());
            pw.close();

            System.out.println("Average turnaround time (completion time) was " + average);
            System.out.println("See " + csvFileName + " for results!");
        } catch(Exception e) {
            System.out.println("EXCEPTION ENCOUNTERED: " + e.toString());
        }
    }
    
    private static void shortestJobFirst() {
        ArrayList<Process> arrayList = (ArrayList<Process>) scheduleProcesses.clone();
        Collections.sort(arrayList, Process.ProcessBurstTimeComparator);
        try {
            String csvFileName = "SJF" + "-" + fileName.substring(0, fileName.length() - 4) + ".csv";
            PrintWriter pw = new PrintWriter(new File(csvFileName));
            StringBuilder sb = new StringBuilder();
            sb.append("CpuTime");
            sb.append(',');
            sb.append("PID");
            sb.append(',');
            sb.append("StartingBurstTime");
            sb.append(',');
            sb.append("EndingBurstTime");
            sb.append(',');
            sb.append("CompletionTime");
            sb.append('\n');

            sb.append("0");
            sb.append(',');
            sb.append(arrayList.get(0).getPid());
            sb.append(',');
            sb.append(arrayList.get(0).getBurstTime());
            sb.append(',');
            sb.append("0");
            sb.append(',');
            sb.append(arrayList.get(0).getBurstTime());
            sb.append('\n');
            
            int completionTime = arrayList.get(0).getBurstTime();
            int cpuTime = completionTime + SWITCH_COST;
            int runningCount = completionTime;
            
            for(int i = 1; i < arrayList.size(); i++) {
                completionTime = arrayList.get(i).getBurstTime() + cpuTime;
                runningCount += completionTime;
                
                sb.append(cpuTime);
                sb.append(',');
                sb.append(arrayList.get(i).getPid());
                sb.append(',');
                sb.append(arrayList.get(i).getBurstTime());
                sb.append(',');
                sb.append("0");
                sb.append(',');
                sb.append(completionTime);
                sb.append('\n');
                
                cpuTime = completionTime + SWITCH_COST;
            }
            
            double average = (double)runningCount / arrayList.size();

            pw.write(sb.toString());
            pw.close();

            System.out.println("Average turnaround time (completion time) was " + average);
            System.out.println("See " + csvFileName + " for results!");
        } catch(Exception e) {
            System.out.println("EXCEPTION ENCOUNTERED: " + e.toString());
        }
    }
    
    private static void roundRobin(int timeQuantum) {
        // deep copy of orignal arraylist, so original objects remain in tact
        ArrayList<Process> arrayList = new ArrayList<Process>();
        for(Process p : scheduleProcesses) {
            arrayList.add(p.clone());
        }
        try {
            String csvFileName = "round_robin" + timeQuantum + "-" + fileName.substring(0, fileName.length() - 4) + ".csv";
            PrintWriter pw = new PrintWriter(new File(csvFileName));
            StringBuilder sb = new StringBuilder();
            sb.append("CpuTime");
            sb.append(',');
            sb.append("PID");
            sb.append(',');
            sb.append("StartingBurstTime");
            sb.append(',');
            sb.append("EndingBurstTime");
            sb.append(',');
            sb.append("CompletionTime");
            sb.append('\n');
            
            int cpuTime = 0;
            int startingBurstTime, endingBurstTime, completionTime;
            int runningCount = 0;
            int numOfRows= 0;
            
            while(!arrayList.isEmpty()) {
                for(int i = 0; i < arrayList.size(); i++) {
                    startingBurstTime = arrayList.get(i).getBurstTime();
                    endingBurstTime = startingBurstTime - timeQuantum;
                    if(endingBurstTime < 0) {
                        endingBurstTime = 0;
                    }
                    completionTime = (endingBurstTime == 0) ? (cpuTime + startingBurstTime) : 0;
                    runningCount += completionTime;
                    if(completionTime != 0) {
                        numOfRows ++;
                    }

                    sb.append(cpuTime);
                    sb.append(',');
                    sb.append(arrayList.get(i).getPid());
                    sb.append(',');
                    sb.append(startingBurstTime);
                    sb.append(',');
                    sb.append(endingBurstTime);
                    sb.append(',');
                    sb.append(completionTime);
                    sb.append('\n');

                    cpuTime = (completionTime == 0) ? (cpuTime + timeQuantum + SWITCH_COST) : (completionTime + SWITCH_COST);
                    arrayList.get(i).setBurstTime(endingBurstTime); // very important line here
                    if(completionTime != 0) {
                        arrayList.remove(i);
                        i = i - 1;
                    }
                }
            }
            
            double average = (double)runningCount / numOfRows;

            pw.write(sb.toString());
            pw.close();

            System.out.println("Average turnaround time (completion time) was " + average);
            System.out.println("See " + csvFileName + " for results!");
        } catch(Exception e) {
            System.out.println("EXCEPTION ENCOUNTERED: " + e.toString());
        }
    }
    
    private static int chooseLotteryWinner(ArrayList<Process> arrayList, int prioritySum) {
        Random random = new Random();
        int lotteryNumber = random.nextInt(prioritySum);
        int numToReturn = -1;
        for(int i = 0; i < arrayList.size(); i++) {
            // choose lottery winner
            int processPriority = arrayList.get(i).getPriority();
            for(int j = 0; j < processPriority; j++) {
                int numToMatch = random.nextInt(prioritySum);
                if(numToMatch == lotteryNumber) {
                    numToReturn = i;
                    return i;
                }
            }
        }
        if(numToReturn == -1) {
            return chooseLotteryWinner(arrayList, prioritySum);
        } else {
            return numToReturn;
        }
    }
    
    private static void lottery(int timeQuantum) {
        // deep copy of orignal arraylist, so original objects remain in tact
        ArrayList<Process> arrayList = new ArrayList<Process>();
        for(Process p : scheduleProcesses) {
            arrayList.add(p.clone());
        }
        // sort by priority
        Collections.sort(arrayList, Process.ProcessPriorityComparator);
        try {
            String csvFileName = "lottery" + timeQuantum + "-" + fileName.substring(0, fileName.length() - 4) + ".csv";
            PrintWriter pw = new PrintWriter(new File(csvFileName));
            StringBuilder sb = new StringBuilder();
            sb.append("CpuTime");
            sb.append(',');
            sb.append("PID");
            sb.append(',');
            sb.append("StartingBurstTime");
            sb.append(',');
            sb.append("EndingBurstTime");
            sb.append(',');
            sb.append("CompletionTime");
            sb.append('\n');
            
            int cpuTime = 0;
            int startingBurstTime, endingBurstTime, completionTime;
            int runningCount = 0;
            int numOfRows= 0;
            
            int prioritySum = 0;
            int processNumber = -1;
            
            for(int i = 0; i < arrayList.size(); i++) {
                prioritySum += arrayList.get(i).getPriority();
            }
            
            while(!arrayList.isEmpty()) {
                processNumber = chooseLotteryWinner(arrayList, prioritySum);
                
                startingBurstTime = arrayList.get(processNumber).getBurstTime();
                endingBurstTime = startingBurstTime - timeQuantum;
                if(endingBurstTime < 0) {
                    endingBurstTime = 0;
                }
                completionTime = (endingBurstTime == 0) ? (cpuTime + startingBurstTime) : 0;
                runningCount += completionTime;
                if(completionTime != 0) {
                    numOfRows++;
                }

                sb.append(cpuTime);
                sb.append(',');
                sb.append(arrayList.get(processNumber).getPid());
                sb.append(',');
                sb.append(startingBurstTime);
                sb.append(',');
                sb.append(endingBurstTime);
                sb.append(',');
                sb.append(completionTime);
                sb.append('\n');

                cpuTime = (completionTime == 0) ? (cpuTime + timeQuantum + SWITCH_COST) : (completionTime + SWITCH_COST);
                arrayList.get(processNumber).setBurstTime(endingBurstTime); // very important line here
                if(completionTime != 0) {
                    arrayList.remove(processNumber);
                }
            }
            
            double average = (double)runningCount / numOfRows;

            pw.write(sb.toString());
            pw.close();

            System.out.println("Average turnaround time (completion time) was " + average);
            System.out.println("See " + csvFileName + " for results!");
        } catch(Exception e) {
            System.out.println("EXCEPTION ENCOUNTERED: " + e.toString());
        }
    }
    
    private static void runAlgorithms() {
        System.out.println("\nNow running scheduling algorithms...\n");
        System.out.println("First-Come-First-Serve (FCFS)");
        firstComeFirstServe();
        System.out.println("\nShortest-Job-First (SJF)");
        shortestJobFirst();
        System.out.println("\nRound-Robin with time quantum = 20");
        roundRobin(20);
        System.out.println("\nRound-Robin with time quantum = 40");
        roundRobin(40);
        System.out.println("\nLottery with time quantum = 40");
        lottery(40);
        menu();
    }

    private static void menu() {
        int selection = 0;
        
        System.out.println("\nMenu options");
        System.out.println("1. Try again with another data file to use for algorithms.");
        System.out.println("2. Exit the program.");
        
        System.out.print("Enter your selection: ");
        selection = keyboard.nextInt();
        keyboard.nextLine();
        
        switch(selection) {
            case 1: 
                scheduleProcesses.clear();
                setFileName();
                break;
            case 2:
                System.out.println("Goodbye.");
                System.exit(0);
                break;
            default:
                System.out.println("Unknown input, please try again");
                menu();
                break;
        }
    }
    
    public static void main(String[] args) {
        System.out.println("Enter the file name to use for the scheduling algorithms (ex. testdata1.txt)");
        setFileName();
    }
    
}
