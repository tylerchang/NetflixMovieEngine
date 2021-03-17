import java.util.Comparator;

public class PearsonComparator implements Comparator<User> {

    @Override
    public int compare(User o1, User o2) {


        //Calculations
        if(o1.tempCoefficient > o2.tempCoefficient){
            return 1;
        }
        else if(o1.tempCoefficient < o2.tempCoefficient){
            return -1;
        }
        else{
            return 0;
        }

    }
}
