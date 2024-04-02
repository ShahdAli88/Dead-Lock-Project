import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class LastTask 
{
	
    // Define Banker's Algorithm data structures
    static int[][] allocation;
    static int[] available;
    static int[][] max;
    static int[][] need;
    static boolean[] finish;
    static List<Integer> safeSequence;
    
    
	public static void main(String[] args) 
	{
		// read the CSV file line by line and then split each line using the comma as a delimiter and store the values in a List Array
		String csvFile1 = "Allocation1.csv";
		List<String[]> data1 = new ArrayList<>();
		
		String csvFile2 = "Available1.csv";
		List<String[]> data2 = new ArrayList<>();
		
		String csvFile3 = "Request1.csv";
		List<String[]> data3 = new ArrayList<>();
		
		
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile1))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                data1.add(values);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile2))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                data2.add(values);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile3))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                data3.add(values);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        

        // Initialize Banker's Algorithm data structures
        initialize(data3, data3, data3 );

        // Implement Banker's Algorithm logic here
        if (BankersAlgorithm()) {
            // Banker's Algorithm successfully executed
            // Print the result (safe sequence or deadlock detection)
            System.out.println("No Deadlock, The Safe Sequence is " + safeSequence);
        } else {
            // Deadlock detected
            System.out.println("Deadlock !");
            // List the processes that are deadlocked
            listDeadlockedProcesses();
        }
    
 }

	
	
    // Modify initialize method to use data from the files
    private static void initialize(List<String[]> data1,List<String[]> data2,List<String[]> data3) 
    {

        allocation = convertListToMatrix(data1);
        max = convertListToMatrix(data2);
        available = convertArray(data3.get(0));
        finish = new boolean[allocation.length];
        safeSequence = new ArrayList<>();

        // Calculate need matrix
        need = new int[allocation.length][allocation[0].length];
        for (int i = 0; i < allocation.length; i++) {
            for (int j = 0; j < allocation[0].length; j++) {
                need[i][j] = max[i][j] - allocation[i][j];
            }
        }
    }
    
    
 // convert a List of String arrays to a 2D array
    private static int[][] convertListToMatrix(List<String[]> data) {
        int numRows = data.size();
        int numCols = data.get(0).length;

        // Check if the first row is a header row; if so, skip it
        boolean isHeaderRow = Arrays.stream(data.get(0)).anyMatch(value -> value.equalsIgnoreCase("Process"));

        int[][] result = new int[isHeaderRow ? numRows - 1 : numRows][numCols];

        for (int i = isHeaderRow ? 1 : 0; i < numRows; i++) {
            result[i - (isHeaderRow ? 1 : 0)] = convertArray(data.get(i));
        }

        return result;
    }


    
    
    private static int[] convertArray(String[] values) {
        int[] result = new int[values.length];
        try {
            for (int i = 0; i < values.length; i++) {
                // Try parsing the value to an integer, and if it fails, set it to 0
                result[i] = Integer.parseInt(values[i]);
            }
        } catch (NumberFormatException e) {
            // Handle the case where a non-integer value is encountered
            // Print an error message or handle it in a way that makes sense for your application
            //System.err.println("Error: Non-integer value found in the CSV file. Setting it to 0.");
            Arrays.fill(result, 0); // Set all values to 0 in case of non-integer values
        }
        return result;
    }


    
    private static boolean BankersAlgorithm() {
        int[] work = Arrays.copyOf(available, available.length);
        boolean[] finishCopy = Arrays.copyOf(finish, finish.length);
        boolean deadlockDetected = false;

        while (true) {
            boolean canFinish = false;

            for (int i = 0; i < allocation.length; i++) {
                if (!finishCopy[i] && canSatisfyNeed(i, work)) {
                    // Process i can be satisfied, release resources, and mark it as finished
                    for (int j = 0; j < work.length; j++) {
                        work[j] += allocation[i][j];
                    }
                    finishCopy[i] = true;
                    canFinish = true;
                }
            }

            // If no process can finish, check for deadlock
            if (!canFinish) {
                deadlockDetected = true;
                break;
            }

            // Check if all processes are finished
            boolean allFinished = true;
            for (boolean isFinished : finishCopy) {
                if (!isFinished) {
                    allFinished = false;
                    break;
                }
            }

            if (allFinished) {
                // All processes finished without deadlock
                deadlockDetected = false;
                break;
            }
        }

        return deadlockDetected;
    }


    
    // To find the need
    private static boolean canSatisfyNeed(int processIndex, int[] work) {
        for (int i = 0; i < work.length; i++) {
            if (need[processIndex][i] > work[i]) {
                return false;
            }
        }
        return true;
    }

    
    private static void listDeadlockedProcesses() {
        List<Integer> deadlockedProcesses = new ArrayList<>();

        // Iterate through finish array to find processes that are deadlocked
        for (int i = 0; i < finish.length; i++) {
            if (!finish[i]) {
                // Process i is deadlocked
                deadlockedProcesses.add(i);
            }
        }

        // Print the list of deadlocked processes
        System.out.println("Processes that are deadlocked: " + deadlockedProcesses);
    }  
	
	
}



