import java.util.Hashtable;

public class User {

    Hashtable<Integer, Integer> movieRatings;
    Hashtable<Integer,Double> pearsonCoefficientWith;
    double tempCoefficient;
    double userAverageRating;
    double standardDeviation;

    public User(){
        movieRatings = new Hashtable<>();
        pearsonCoefficientWith = new Hashtable<>();
    }

    public void addEntry(int movieNumber, int rating){
        movieRatings.put(movieNumber, rating);
    }


}
