

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MultiThreadServer  {

    private  static ArrayList<Pair<String, Integer>> messages = new ArrayList<>();
    private static long numOfNotes = 1;

    static ExecutorService executeIt = Executors.newCachedThreadPool();

    public static void main(String[] args)  {
        Main m = new Main();

        m.filingOchered();

        Date clearDate = new Date(new Date().getTime() + 86400000); //
        clearDate.setHours(1);
        clearDate.setMinutes(0);
        clearDate.setSeconds(0);
        System.out.println("Время очистки: " + clearDate.toString() );
        long timeToWait = clearDate.getTime() - new Date().getTime();
        System.out.println("до запуска " + timeToWait/1000 + "c" );
        Timer timer = new Timer();
        timer.schedule(new ClearTask(), timeToWait, 86400000);

        try (ServerSocket server = new ServerSocket(3346);
             BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            System.out.println("Server socket created, command console reader for listen to server commands");

            while (true) {
                Socket client = server.accept();

                executeIt.execute(new MonoThreadClientHandler(client));
                System.out.print("Connection accepted.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static ArrayList<Pair<String, Integer>> getMessages() {
        return messages;
    }


    public static synchronized Pair<String, Integer> getMessages(Pair<String, Integer> pair, int ID){
        messages.add(0,pair);

        Pair<String, Integer> note = null;

        boolean find = false;
        for(int i=messages.size()-1;i>=0;i--){
            note = messages.get(i);
            if(note.getValue() == ID){
                continue;
            }else{
                find = true;
                messages.remove(i);
                break;
            }

        }
        if(!find){
            return null;
        }
        return note;
    }


    public static void setMessages(ArrayList<Pair<String, Integer>> messages) {
        MultiThreadServer.messages = messages;
    }

    public static long getNumOfNotes() {
        return numOfNotes;
    }

    public static void setNumOfNotes(long numOfNotes) {
        MultiThreadServer.numOfNotes = numOfNotes;
    }

    public static synchronized String newNote(){
        long n = getNumOfNotes();
        setNumOfNotes(n+1);
        return Long.toString(n+1);
    }
}