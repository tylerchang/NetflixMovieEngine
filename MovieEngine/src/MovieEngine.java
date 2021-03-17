import java.io.FileNotFoundException;
import java.util.ArrayList;

public class MovieEngine {
    public static void main(String[] args) throws FileNotFoundException {

        //RMSE REPORT:

        //I used 3 examples of users and movies and calculated the RMSE for each.

        //RMSE.USER(828444) = 0.5644008039840451
        //RMSE.USER(7) = 0.37495729291095614
        //RMSE.USER(1876520) = 0.6566812131240255

        //RMSE.MOVIE(735) = 0.5294778873067019
        //RMSE.MOVIE(200) = 0.6356493750194322
        //RMSE.MOVIE(1982) = 0.7586371308225746


        //Initializing (reading/storing) the data
        Calculations.initializeData();
        System.out.println("Initialization Complete");

        //3 example users
        User user828444 = Calculations.users.get(828444);
        User user7 = Calculations.users.get(7);
        User user1876520 = Calculations.users.get(1876520);

        //3 example movies
        int movie735 = 735;
        int movie200 = 200;
        int movie1982 = 1982;

        //Testing the 3 users

        System.out.println("Now calculating RMSE_USER(82844)....will take a few minutes");
        System.out.println("RMSE_User(828444) = " + Calculations.calculateRMSE_USER(user828444));

        System.out.println("Now calculating RMSE_USER(7)....will take a few minutes");
        System.out.println("RMSE_User(7) = " + Calculations.calculateRMSE_USER(user7));

        System.out.println("Now calculating RMSE_USER(1876520)....will take a few minutes");
        System.out.println("RMSE_User(1876520) = " + Calculations.calculateRMSE_USER(user1876520));

        //Testing the 3 movies

        System.out.println("Now calculating RMSE_MOVIE(735)....will take a few minutes");
        System.out.println("RMSE_Movie(735) = " + Calculations.calculateRMSE_MOVIE(movie735));

        System.out.println("Now calculating RMSE_MOVIE(200)....will take a few minutes");
        System.out.println("RMSE_Movie(200) = " + Calculations.calculateRMSE_MOVIE(movie200));

        System.out.println("Now calculating RMSE_MOVIE(1982)....will take a few minutes");
        System.out.println("RMSE_Movie(1982) = " + Calculations.calculateRMSE_MOVIE(movie1982));

    }
}
