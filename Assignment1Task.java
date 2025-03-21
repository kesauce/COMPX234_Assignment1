import java.util.ArrayList;

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
                // Simulate printer taking some time to print the document
                printerSleep();
                // Grab the request at the head of the queue and print it
                // Write code here
                
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
                // machine sleeps for a random amount of time
                machineSleep();
                // machine wakes up and sends a print request
                // Write code here
                printRequest(machineID);
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
