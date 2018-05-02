import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class MonoThreadClientHandler implements Runnable {

    private static Socket clientDialog;

    public MonoThreadClientHandler(Socket client) {
        MonoThreadClientHandler.clientDialog = client;
    }

    @Override
    public void run() {

        try {
            DataOutputStream out = new DataOutputStream(clientDialog.getOutputStream());

            DataInputStream in = new DataInputStream(clientDialog.getInputStream());
            System.out.println("DataInputStream created");

            System.out.println("DataOutputStream  created");

            System.out.println("Server reading from channel");


            String entry = in.readUTF();

            String[] strings = entry.split("/");

            String action = strings[0];

            Main m = new Main();
            ArrayList<String> list;

            switch (action){
                case "del":
                    String n = strings[1];
                    m.deleteNote(n);
                    break;
                case "new":
                    int ID = Integer.parseInt(strings[1]);
                    String message = strings[2];
                    list = m.newTable("note" + MultiThreadServer.newNote(), ID, message);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(baos);
                    oos.writeObject(list);
                    baos.writeTo(out);

                    oos.close();
                    break;
                case "mess":
                    String note = strings[1];
                    long IDs = Long.parseLong(strings[2]);
                    long IDm = Long.parseLong(strings[3]);
                    String msg = strings[4];
                    m.newConv(msg, IDs, note, IDm);

                    break;

                case "upd":
                    int id = Integer.parseInt(strings[1]);

                    ArrayList<String> notes = m.updateNotes(id);
                    ArrayList<String> messages = m.updateMsg(notes);
                    ArrayList<ArrayList<String>> l = new ArrayList<>();
                    l.add(notes);
                    l.add(messages);
                    ByteArrayOutputStream BAOS = new ByteArrayOutputStream();
                    ObjectOutputStream OOS = new ObjectOutputStream(BAOS);
                    OOS.writeObject(l);
                    BAOS.writeTo(out);
                    BAOS.flush();

                    OOS.close();
                    BAOS.close();
                    break;
                case "load":
                    String Note = strings[1];

                    ArrayList<String> conv = m.loadConv(Note);
                    out.writeUTF(Integer.toString(conv.size()));
                    ByteArrayOutputStream BAOs = new ByteArrayOutputStream();
                    ObjectOutputStream OOs = new ObjectOutputStream(BAOs);
                    OOs.writeObject(conv);
                    BAOs.writeTo(out);
                    BAOs.flush();
                    OOs.close();
                    BAOs.close();
                    break;
                case "nmess":
                    String note1 = strings[1];
                    long Ids = Long.parseLong(strings[2]);
                    long Idm = Long.parseLong(strings[3]);
                    String msg1 = strings[4];
                    int res =  m.newMsg(msg1, Ids, note1, Idm);
                    if(res == 1){
                        out.writeUTF("1");
                    }else if(res == 0){
                        out.writeUTF("0");
                    }
                    break;
                case "rtn":
                    String numberNote = strings[1];
                    int idS = Integer.parseInt(strings[2]);
                    m.returnNoteDB(numberNote, idS);
                    break;



            }





            // освобождаем буфер сетевых сообщений
            out.flush();

            in.close();
            out.close();

            // потом закрываем сокет общения с клиентом в нити моносервера
            clientDialog.close();

            System.out.println("Closing connections & channels - DONE.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}