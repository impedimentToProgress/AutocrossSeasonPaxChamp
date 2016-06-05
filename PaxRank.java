import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

class PaxRank
{
    private static HashMap<String, Driver> drivers = new HashMap<String, Driver>(200);

    public static void main(String args[])throws Exception
    {
        if(args.length != 2 && args.length != 3)
        {
            System.out.println("Usage: java PaxRank pax.csv countedEvents [driverLists.txt]");
            return;
        }

	int countedEvents = Integer.parseInt(args[1]);
	String resultsFileName = args[0];
	boolean restrictDrivers = args.length == 3;

        System.out.println("Welcome to PaxRank");
        System.out.println("Processing " + resultsFileName);
        System.out.println("Counting " + countedEvents + " events");
        processFile(resultsFileName);
        System.out.println("Found " + drivers.size() + " drivers");

	if(restrictDrivers)
	{
	    String driversFileName = args[2];
	    System.out.println("Limiting the drivers to those in the file: " + driversFileName);
	    ArrayList<String> printDrivers = createDriverListFromFile(driversFileName);
	    removeDriversNotInList(drivers, printDrivers); 
	}

        printResults(Integer.parseInt(args[1]));

	for(String name : drivers.keySet())
	{
	    for(String otherName : drivers.keySet())
	    {
		double RATIO = .8;
		String common = lcs(name, otherName);
		int longName = Math.max(name.length(), otherName.length());

		if(!name.equals(otherName) && common.length() > (RATIO * longName)) 
		{
		    System.out.println(name + ", " + otherName + ", " + (float)lcs(name, otherName).length()/longName);
		}
	    }
	}
        //printAverageResults();
    }

    // Parses driver names and results from the passed file
    // then adds them to the global driver/results list
    private static void processFile(String filename)throws Exception
    {
        BufferedReader in = new BufferedReader(new FileReader(filename));
        String line;
        
        while((line = in.readLine()) != null)
        {
            /* Line: "driver name", pax time, pax score */
            String [] parts = line.split(",");

            if(parts.length != 3)
            {
                System.out.println("ERROR: line: " + line + " : is malformed");
                System.exit(-1);
            }
            
            String name = parts[0].trim();
	    // Remove the leading and trailing quotes
	    name = name.substring(1, name.length() - 1);
            double result = Double.parseDouble(parts[2].trim());

            if(drivers.containsKey(name))
            {
                drivers.get(name).addResult(result);
            }
            else
            {
                drivers.put(name, new Driver(name, result));
            }
        }
    }
     
    // Create a list of driver names from the passed file and return the list
    // All names are converted to lower case
    private static ArrayList<String> createDriverListFromFile(String filename)throws Exception
    {
	ArrayList<String> list = new ArrayList<String>();
        BufferedReader in = new BufferedReader(new FileReader(filename));
        String line;
        
        while((line = in.readLine()) != null)
        {
	    list.add(line.trim().toLowerCase());
        }

	return list;
    }

    // Remove all drivers from the first passed list not in the second passed list
    private static void removeDriversNotInList(HashMap<String, Driver> results, ArrayList<String> names)
    {
	for(Iterator<String> driverNames = results.keySet().iterator(); driverNames.hasNext(); )
	{
	    String driver = driverNames.next();

	    if(!names.contains(driver.toLowerCase()))
	    {
		driverNames.remove();
	    }
	}
    }

    private static void printResults(int n)
    {
        int driver;
        Driver [] driverList = new Driver[drivers.size()];
        
        // Make it easy to traverse the set of results and order the list from
        // best to worst
        drivers.values().toArray(driverList);        
        Driver.setN(n);
        Arrays.sort(driverList);

        System.out.printf(String.format("Rank %-20s: %6s %6s %10s %6s\n", "Driver", "Score", "Diff", "From First", "Events"));

        int scorePrev = 0;
        int scoreBest = driverList[0].bestNResultsAdd();
        /* Print the results from best to worst */
        for(driver = 0; driver < driverList.length; ++driver)
        {
            String name = driverList[driver].name;
            int score = driverList[driver].bestNResultsAdd();
	    int events = driverList[driver].getNumResults();

            scorePrev = scorePrev == 0 ? score : scorePrev;
            System.out.printf(String.format("%4d %-20s: %6d %6d %10d %6d\n", (driver+1), name, score, (scorePrev-score), (scoreBest-score), events));
            scorePrev = score;
        }
    }

    private static void printAverageResults()
    {
        int driver;
        Driver [] driverList = new Driver[drivers.size()];
        
        // Make it easy to traverse the set of results and order the list from
        // best to worst
        drivers.values().toArray(driverList);
        Arrays.sort(driverList);

        System.out.printf(String.format("Rank %-20s: %6s\n", "Driver", "Avg"));
        /* Print the results from best to worst */
        for(driver = 0; driver < driverList.length; ++driver)
        {
            String name = driverList[driver].name;
            name = name.substring(1, name.length()-1); /* Remove quotes */
            int score = driverList[driver].averageResult();
            System.out.printf(String.format("%4d %-20s: %6d\n", (driver+1), name, score));
        }
    }

    private static String lcs(String a, String b) {
	int[][] lengths = new int[a.length()+1][b.length()+1];
 
	// row 0 and column 0 are initialized to 0 already
 
	for (int i = 0; i < a.length(); i++)
	    for (int j = 0; j < b.length(); j++)
		if (a.charAt(i) == b.charAt(j))
		    lengths[i+1][j+1] = lengths[i][j] + 1;
		else
                lengths[i+1][j+1] =
                    Math.max(lengths[i+1][j], lengths[i][j+1]);
 
	// read the substring out from the matrix
	StringBuffer sb = new StringBuffer();
	for (int x = a.length(), y = b.length();
	     x != 0 && y != 0; ) {
	    if (lengths[x][y] == lengths[x-1][y])
		x--;
	    else if (lengths[x][y] == lengths[x][y-1])
		y--;
	    else {
		assert a.charAt(x-1) == b.charAt(y-1);
		sb.append(a.charAt(x-1));
		x--;
		y--;
	    }
	}
 
	return sb.reverse().toString();
    }
}
