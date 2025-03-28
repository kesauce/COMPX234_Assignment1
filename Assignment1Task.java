import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

class Assignment1 {

    // Simulation Initialisation
    private static int NUM_MACHINES = 50; // Number of machines in the system that issue print requests
    private static int NUM_PRINTERS = 5; // Number of printers in the system that print requests
    private static int SIMULATION_TIME = 30;
    private static int MAX_PRINTER_SLEEP = 3;
    private static int MAX_MACHINE_SLEEP = 5;
    private static boolean sim_active = true;

    // Create an empty list of print requests
    printList list = new printList();

    // Machine semaphore starts at 5 to represent the 5 available spaces for printing documents
    private static Semaphore machineSemaphore = new Semaphore(5);

    // Printer semaphore starts at 0 to represent the 0 jobs that are currently printing
    private static Semaphore printerSemaphore = new Semaphore(0);

    // Mutex lock to ensure that there are no clashes between 2 printers and 1 job or 2 jobs and 1 printer
    private static ReentrantLock mutexLock = new ReentrantLock();

    public void startSimulation() {

        // ArrayList to keep for machine and printer threads
        ArrayList<Thread> mThreads = new ArrayList<Thread>();
        ArrayList<Thread> pThreads = new ArrayList<Thread>();

        // Create Machine and Printer threads
        // Create a thread for each machine and put it in the mThreads array list
        for(int i = 0; i < NUM_MACHINES; i++){
            machineThread newMThread = new machineThread(i);
            mThreads.add(newMThread);
        }

        // Create a thread for each printer and put it in the pThreads array list
        for (int i = 0; i < NUM_PRINTERS; i++) {

            printerThread newPThread = new printerThread(i);
            pThreads.add(newPThread);

        }

        // Start all the threads
        // Goes through each arraylist and start the thread
        for (Thread machine : mThreads) {
            machine.start();
        }

        for (Thread printer : pThreads){
            printer.start();
        }

        // Let the simulation run for some time
        sleep(SIMULATION_TIME);

        // Finish simulation
        sim_active = false;

        // Wait until all printer threads finish by using the join function
        // Join the machine and printer threads
        for (Thread machine : mThreads){
            try{
                machine.join();
            }
            catch(InterruptedException e){
                e.printStackTrace();
            }
        }

        for (Thread printer : pThreads){
            try{
                printer.join();
            }
            catch(InterruptedException e){
                e.printStackTrace();
            }
        }



    }

    // Printer class
    public class printerThread extends Thread {
        private int printerID;

        public printerThread(int id) {
            printerID = id;
        }

        public void run() {
            while (sim_active) {
                // Grab the request at the head of the queue and print it
                try{
                    //Acquire a permit from the machines to print a document and lock the lock so that nothing can interrupt the process
                    printerSemaphore.acquire();
                    mutexLock.lock();

                    // Simulate printer taking some time to print the document and print the document
                    printerSleep();
                    printDox(printerID);

                    //Send a permit to the machines to announce that a printer is available
                    machineSemaphore.release();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                finally{
                    //Unlock the lock again so that others can send requests
                    mutexLock.unlock();
                }
            }
        }

        public void printerSleep() {
            int sleepSeconds = 1 + (int) (Math.random() * MAX_PRINTER_SLEEP);
            // sleep(sleepSeconds*1000);
            try {
                sleep(sleepSeconds * 1000);
            } catch (InterruptedException ex) {
                System.out.println("Sleep Interrupted");
            }
        }

        public void printDox(int printerID) {
            System.out.println("Printer ID:" + printerID + " : now available");
            // print from the queue
            list.queuePrint(list, printerID);
        }

    }

    // Machine class
    public class machineThread extends Thread {
        private int machineID;

        public machineThread(int id) {
            machineID = id;
        }

        public void run() {
            while (sim_active) {
                try{
                    //Acquire a permit from the printer to print a document and lock the lock so that nothing can interrupt the process
                    machineSemaphore.acquire();
                    mutexLock.lock();

                    // Machine sleeps for a random amount of time and sends a print request
                    machineSleep();
                    printRequest(machineID);

                    //Send a permit to the machines to announce that a document is done
                    printerSemaphore.release();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                finally{
                    //Unlocks the mutex lock for other use
                    mutexLock.unlock();
                }
            }
        }

        // machine sleeps for a random amount of time
        public void machineSleep() {
            int sleepSeconds = 1 + (int) (Math.random() * MAX_MACHINE_SLEEP);

            try {
                sleep(sleepSeconds * 1000);
            } catch (InterruptedException ex) {
                System.out.println("Sleep Interrupted");
            }
        }

        public void printRequest(int id) {
            System.out.println("Machine " + id + " Sent a print request");
            // Build a print document
            printDoc doc = new printDoc("My name is machine " + id, id);
            // Insert it in print queue
            list = list.queueInsert(list, doc);
        }
    }

    private static void sleep(int s) {
        try {
            Thread.sleep(s * 1000);
        } catch (InterruptedException ex) {
            System.out.println("Sleep Interrupted");
        }
    }
}
