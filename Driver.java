import java.util.ArrayList;

class Driver implements Comparable<Driver>
{
    public String name;
    private ArrayList<Double> results;
    private static int resultsThatCount;

    public Driver(String name)
    {
        this.name = name;
        this.results = new ArrayList<Double>(10);
    }

    public Driver(String name, double result)
    {
        this.name = name;
        this.results = new ArrayList<Double>(10);
        addResult(new Double(result));
    }

    public void addResult(double result)
    {
        int index;

        for(index = 0; index < results.size(); ++index)
        {
            if(results.get(index) < result)
            {
                break;
            }
        }

        results.add(index, new Double(result));
    }

    public int bestNResultsAdd(int n)
    {
        int sum = 0;

        n = n > results.size() ? results.size() : n;
        while(n > 0 )
        {
            sum += results.get(n-1).intValue();
            --n;
        }

        return sum;
    }

    public int bestNResultsAvg(int n)
    {
        int sum = 0;
        int count = n;

        n = n > results.size() ? results.size() : n;
        sum = bestNResultsAdd(n);
        return sum/count;
    }

    public int bestNResultsAdd()
    {
        return bestNResultsAdd(resultsThatCount);
    }

    public int bestNResultsAvg()
    {
        return bestNResultsAvg(resultsThatCount);
    }

    public int averageResult()
    {
        return bestNResultsAvg(results.size());
    }

    public static void setN(int n)
    {
        resultsThatCount = n;
    }

    public static int getN()
    {
        return resultsThatCount;
    }

    public int getNumResults()
    {
	return results.size();
    }

    public int compareTo(Driver o)
    {
        return -1*(bestNResultsAdd() - o.bestNResultsAdd());
        //return -1*(averageResult() - o.averageResult());
        //return name.compareTo(o.name);
    }
}
