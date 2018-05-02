import java.util.Date;

/**
 * Created by shaka on 09.04.2018.
 */
public class ClearThread implements Runnable {

    Date clearDate;

    @Override
    public void run() {
        Date date;

        Main m = new Main();
        while (true){
            date = new Date();
            clearDate = new Date(date.getTime() + 86400000);
            clearDate.setHours(2);
            clearDate.setMinutes(0);
            clearDate.setSeconds(0);
            long timeTOwait = clearDate.getTime() - date.getTime();
            try {
                Thread.sleep(timeTOwait);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            m.clearOchered();

            m.clearConversation();




        }






    }


}
