import java.util.Date;

/**
 * Created by shaka on 09.04.2018.
 */
public class ClearThread implements Runnable {

    Date clearDate;

    @Override
    public void run() {
        Date date;
        System.out.println("Поток очистки начинает работу");
        Main m = new Main();
        clearDate = new Date();
        clearDate.setHours(1);
        clearDate.setMinutes(0);
        clearDate.setSeconds(0);
        clearDate = new Date(clearDate.getTime() + 86400000);
        while (true){
            System.out.println("Начало цыкла");
            date = new Date();


            System.out.println("Время очистки: " + clearDate.toString() );
            long timeTOwait = clearDate.getTime() - date.getTime();
            System.out.println("Время ожидания: " + timeTOwait/1000 + "c");
            try {
                Thread.sleep(timeTOwait);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Очистка очереди");
            m.clearOchered();
            System.out.println("Конец очистки очереди");
            System.out.println("Очистка бесед");
            m.clearConversation();
            System.out.println("Конец очистки бесед");

            clearDate = new Date(clearDate.getTime() + 86400000); //86400000

        }






    }


}
