package choongyul.android.com.retrofit2study.DataForHR;

import java.util.List;

/**
 * Created by myPC on 2017-04-24.
 */

public class RealData {
    private List<Results> results;

    private String previous;

    private int count;

    private String next;

    public List<Results> getResults ()
    {
        return results;
    }

    public void setResults (List<Results> results)
    {
        this.results = results;
    }

    public String getPrevious ()
    {
        return previous;
    }

    public void setPrevious (String previous)
    {
        this.previous = previous;
    }

    public int getCount ()
    {
        return count;
    }

    public void setCount (int count)
    {
        this.count = count;
    }

    public String getNext ()
    {
        return next;
    }

    public void setNext (String next)
    {
        this.next = next;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [results = "+results+", previous = "+previous+", count = "+count+", next = "+next+"]";
    }
}