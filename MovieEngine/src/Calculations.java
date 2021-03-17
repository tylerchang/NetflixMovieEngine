import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Calculations {

    static Hashtable <Integer, User> users = new Hashtable<>();

    //Prints all the users
    public static void printUsers(){
        Set<Integer> userKeys = users.keySet();

        for(int u : userKeys){
            System.out.print("User: " + u);

            Set<Integer> m = users.get(u).movieRatings.keySet();

            for(int j : m){

                System.out.print(" Movie: " + j);
                System.out.print(" Rating: " + users.get(u).movieRatings.get(j));

            }
            //System.out.println(users.get(u).pearsonCoefficientWith);
            System.out.println();
        }
    }

    //Reads and stores the data
    public static void initializeData() throws FileNotFoundException {

        File sampleFolder = new File("moviedata/");
        File [] movies = sampleFolder.listFiles();

        System.out.println("Currently initializing data....please give it a moment :)");

        for(int i = 0; i< movies.length; i++){

            Scanner scanner = new Scanner(movies[i]);
            if(scanner.hasNextLine()){
                String line = scanner.nextLine();
                int movieNumber = Integer.parseInt(line.substring(0,line.length()-1));


                while(scanner.hasNext()){
                    String s = scanner.next();
                    int commaIndex = s.indexOf(",");

                    int userId = Integer.parseInt(s.substring(0,commaIndex));
                    int rating = Integer.parseInt(s.substring(commaIndex+1, commaIndex+2));

                    if(users.containsKey(userId)){
                        users.get(userId).addEntry(movieNumber, rating);
                    }else{
                        User user = new User();
                        user.addEntry(movieNumber, rating);
                        users.put(userId, user);
                    }
                }

            }


            scanner.close();

        }

        Set<Integer> userKeys = users.keySet();
        for(int u: userKeys){
            calculateUserAverageRating(users.get(u));
            calculateStandardDeviation(users.get(u));
        }
    }

    //Calculates the RMSE given a movie
    public static double calculateRMSE_MOVIE(int movie){

        double numeratorSum = 0;
        int numberOfRatings = 0;

        Set<Integer> userKeys = users.keySet();

        for(int user: userKeys){
            if(users.get(user).movieRatings.containsKey(movie)){
                numeratorSum += Math.pow(calculatePredictedRating(users.get(user), movie) - users.get(user).movieRatings.get(movie), 2);
                numberOfRatings++;
            }

        }

        return Math.sqrt(numeratorSum/numberOfRatings);

    }

    //Calculates the RMSE given a user
    public static double calculateRMSE_USER(User user_i){
        double numeratorSum = 0;
        int numberOfMovies = 0;

        Set<Integer> movies = user_i.movieRatings.keySet();

        for(int movie: movies){
            numeratorSum += Math.pow(calculatePredictedRating(user_i, movie) - user_i.movieRatings.get(movie), 2);
            numberOfMovies++;
        }

        return Math.sqrt(numeratorSum/numberOfMovies);

    }

    //Predicts the rating of a movie for a user
    public static double calculatePredictedRating(User user_i, int movie){

        //For this example, we will be setting k = 100 since that's what they did in the white paper
        int k = 100;
        double numeratorSum = 0;
        double denominatorSum = 0;
        ArrayList<User> kNearestNeighbors = getKNearestNeighbors(user_i, movie, k);

        //If there are no similar users, return the average rating of the user
        if(kNearestNeighbors.size() < 1){
            return user_i.userAverageRating;
        }

        for(int i = 0; i<kNearestNeighbors.size(); i++){
            numeratorSum += kNearestNeighbors.get(i).tempCoefficient * (kNearestNeighbors.get(i).movieRatings.get(movie) - kNearestNeighbors.get(i).userAverageRating);
            denominatorSum += Math.abs(kNearestNeighbors.get(i).tempCoefficient);
        }

        //To avoid dividing by 0
        if(denominatorSum==0){
            return user_i.userAverageRating;
        }

        return (numeratorSum/denominatorSum) + user_i.userAverageRating;

    }

    //Gets the K nearest neighbors of user who are most similar to user based on p coefficient.
    public static ArrayList<User> getKNearestNeighbors(User user_i, int movie, int k){

        //List of users who have also watched the input movie
        ArrayList <User> usersWhoAlsoRated = new ArrayList<>();

        //Loop through users, if user has rated the movie and the user is not the input, add the user to the list
        Set<Integer> userKeys = users.keySet();
        for(int user: userKeys){
            if(users.get(user).movieRatings.containsKey(movie) && users.get(user) != user_i){
                usersWhoAlsoRated.add(users.get(user));
                //store the Pearson Coefficient relative to the input user for these users only
                users.get(user).tempCoefficient = getPearsonCoefficient(user_i, users.get(user));
            }
        }

        //Sort by coefficient in descending order
        Collections.sort(usersWhoAlsoRated, new PearsonComparator());
        Collections.reverse(usersWhoAlsoRated);

        //if the list size is less than or equal to K, we will return the whole thing to maximize accuracy. If it is greater than K, we will only return the top K values.
        if(usersWhoAlsoRated.size() <= k){
            return usersWhoAlsoRated;
        }else{
            ArrayList<User> topKUsers = new ArrayList<>();

            for(int i = 0; i<k; i++){
                topKUsers.add(usersWhoAlsoRated.get(i));
            }

            return topKUsers;
        }

    }

    //Calculates the Pearson Correlation Coefficient between two users
    public static double getPearsonCoefficient(User user_i, User user_j) {

        ArrayList<Integer> commonMovies = getCommonMovies(user_i, user_j);

        double expectationMatrix = getExpectationMatrix(user_i, user_j);

        double standardDeviation_i = user_i.standardDeviation;

        double standardDeviation_j = user_j.standardDeviation;

        //If the users have no common movies or if one of the standard deviations is 0, assume that there is nothing in common and return 0
        if(commonMovies.size()==0 || standardDeviation_i == 0 || standardDeviation_j==0){
            return 0;
        }

        return (expectationMatrix) / (standardDeviation_i * standardDeviation_j);
    }

    //Calculates the expectation matrix between two users
    public static double getExpectationMatrix(User user_i, User user_j) {

        double expectationMatrix = 0;

        ArrayList<Integer> commonMovies = getCommonMovies(user_i, user_j);

        //Iterate through commonMovies, for each movie, calculate E and add to sum
        for(int i = 0; i<commonMovies.size(); i++){
            int movie = commonMovies.get(i);
            expectationMatrix += (user_i.movieRatings.get(movie) - user_i.userAverageRating) * (user_j.movieRatings.get(movie) - user_j.userAverageRating);
        }

        return expectationMatrix/(commonMovies.size());

    }

    //Gets the list of movies that both users have rated
    public static ArrayList<Integer> getCommonMovies(User user_i, User user_j){

        ArrayList<Integer> commonMovies = new ArrayList<Integer>();

        Hashtable<Integer,Integer> user_i_list = user_i.movieRatings;
        Set<Integer> movies = user_i_list.keySet();
        for(int movie : movies){
            if(user_j.movieRatings.containsKey(movie)){
                commonMovies.add(movie);
            }
        }

        return commonMovies;
    }

    //Calculates and stores the average rating of each user into the user object. Only called once during data initialization
    public static void calculateUserAverageRating(User user_i) {
        //Loop through all the movies that user has watched, sum up all the ratings and divide by number of ratings

        double sum = 0;
        double numberOfMovies = 0;

        Set<Integer> keys = user_i.movieRatings.keySet();

        for(int key: keys){
            sum+= user_i.movieRatings.get(key);
            numberOfMovies++;
        }

        user_i.userAverageRating =  sum/numberOfMovies;
    }

    //Calculates the standard deviation (sigma) of a user. Only called once during initialization.
    public static void calculateStandardDeviation(User user_i) {

        double sum = 0;
        double numberOfRatings = 0;

        Set<Integer> movieKeys = user_i.movieRatings.keySet();

        for(int movie : movieKeys){
            int rating = user_i.movieRatings.get(movie);
            double averageRating = user_i.userAverageRating;
            double difference = rating - averageRating;
            sum += Math.pow(difference, 2);
            numberOfRatings++;
        }

        //Setting the variable in the user
        user_i.standardDeviation =  Math.sqrt(sum/numberOfRatings);

    }

}
