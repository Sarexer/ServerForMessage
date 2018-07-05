
import java.sql.*;
import java.util.*;
import java.util.Date;

/**
 * Created by shaka on 22.11.2017.
 */
public class Main {
    private static final String USERNAME = "sarexer";
    private static final String PASSWORD = "415590shaka905541";
    private static final String URL = "jdbc:mysql://185.43.4.38:3306/registration?useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
    public  Statement statement;


    public Main() {

        try {
            Driver driver = new com.mysql.cj.jdbc.Driver();
            DriverManager.registerDriver(driver);
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);

            statement = connection.createStatement();

        } catch (SQLException ex) {
            System.out.println("Ошибка при подключении");
        }
    }

    public void clearOchered(){
        String getOchered = "select number,time from notes.ochered";
        String clearOchered = "delete from notes.ochered where number=\'";
        String dropTable = "drop table notes.";

        Date currentTime = new Date();
        ArrayList<String> notesTODelete = new ArrayList<>();
        try {
            ResultSet resultSet = statement.executeQuery(getOchered);
            while (resultSet.next()){
                String number = resultSet.getString("number");
                long timeConv = resultSet.getLong("time");
                if((currentTime.getTime() - timeConv) > 86400000){  //86400000
                    notesTODelete.add(number);
                }

            }
            String divider = "insert into registration.logs (action,time) values (\"          \",\"          \")";
            deleteMessages(notesTODelete);

            statement.execute(divider);
            statement.execute(divider);
            statement.execute(divider);
            for (String number : notesTODelete) {
                String log = "insert into registration.logs (action,time) values (\" Из очереди удалена " + number + "\",\"" + new Date().toString() + "\")";
                statement.execute(log);
                statement.execute(clearOchered + number + "\'");
                statement.execute(dropTable + number);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void clearConversation(){
        String getConversations = "select number,time,sum from notes.conv";
        String clearConversation = "delete from notes.conv where number=\'";
        String dropTable = "drop table notes.";
        ArrayList<String> notesToDelete = new ArrayList<>();

        try {
            ResultSet resultSet = statement.executeQuery(getConversations);
            String number;
            Date currentTime = new Date();
            long timeConv;
            int sum;
            while(resultSet.next()){
                number = resultSet.getString("number");
                timeConv = resultSet.getLong("time");
                sum = resultSet.getInt("sum");

                if(sum < 10){
                    if((currentTime.getTime() - timeConv) >= 86400000){ //86400000
                        notesToDelete.add(number);
                    }
                }else if(sum >= 10){
                    if((currentTime.getTime() - timeConv) >= 2592000000L){
                        notesToDelete.add(number);

                    }

                }
            }

            for (String note : notesToDelete) {
                String log = "insert into registration.logs (action,time) values (\" Из бесед удалена " + note + "\",\"" + new Date().toString() + "\")";
                statement.execute(clearConversation + note + "\'");
                statement.execute(dropTable + note);
                System.out.println("Записка удалена" + note);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public synchronized void deleteMessages(ArrayList<String> notes){
        ArrayList<Pair<String, Integer>> messages = MultiThreadServer.getMessages();
        for(int i =0;i<messages.size();i++){
            Pair<String, Integer> message = messages.get(i);
            String note = message.getKey();

            if(notes.contains(note)){
                messages.remove(message);
                i--;
            }
        }

    }

    public void filingOchered() {
        String selectNotes = "select number,ID from notes.ochered";
        ArrayList<Pair<String, Integer>> ochered = MultiThreadServer.getMessages();
        Pair<String,Integer> p = null;
        try {
            ResultSet resultSet = statement.executeQuery(selectNotes);
            while (resultSet.next()){
                String s = resultSet.getString("number");
                int l = resultSet.getInt("ID");
                p = new Pair<>(s, l);

                ochered.add(p);
            }

            if(ochered.size() != 0){


                Pair<String, Integer> p1 =  ochered.get(ochered.size()-1);

                int numberOfLastNote = Integer.parseInt(p1.getKey().substring(4));
                MultiThreadServer.setNumOfNotes(numberOfLastNote);
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    public void deleteNote(String note){
        String del = "drop table notes." + note;
        String delFromConv = "delete from notes.conv where number=" + "\'" + note + "\'";

        try {
            statement.execute(del);
            statement.execute(delFromConv);
        } catch (SQLException e) {
            e.printStackTrace();
        }



    }

    public void returnNoteDB(String note, int idSobesednika){
        Pair<String,Integer> p = new Pair<>(note, idSobesednika);
        returnNoteServer(p);


    }

    public synchronized void returnNoteServer(Pair<String,Integer> p){
        ArrayList<Pair<String, Integer>> messages = MultiThreadServer.getMessages();
        messages.add(0,p);
    }

    public ArrayList<String> loadConv(String note, int count){
        String load = "select message,ID from notes." +note;

        ArrayList<String> conv = new ArrayList<>();
        try {
            ResultSet resultSet = statement.executeQuery(load);
            resultSet.next();

            conv.add(String.valueOf(resultSet.getInt("ID")));
            resultSet.next();
            conv.add(String.valueOf(resultSet.getInt("ID")));
            conv.add(note);
            resultSet.beforeFirst();
            for(int i =0;i<count;i++){
                resultSet.next();
            }
            while(resultSet.next()){
                conv.add(resultSet.getString("message"));
            }
            resultSet.last();
            int ID = resultSet.getInt("ID");
            String getToken = "select token from registration.users where ID=\'" + ID + "\'";
            resultSet = statement.executeQuery(getToken);
            resultSet.next();
            conv.add(0, resultSet.getString("token"));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return conv;
    }
    //исправить обновление времени нового сообщения!!!!!!!!!!!!!!!!!!!!!!!!
    //исправить обновление времени нового сообщения!!!!!!!!!!!!!!!!!!!!!!!!
    //исправить обновление времени нового сообщения!!!!!!!!!!!!!!!!!!!!!!!!
    //исправить обновление времени нового сообщения!!!!!!!!!!!!!!!!!!!!!!!!

    public int newMsg(String message, long IDs, String note, long ID){
        Date currentTime = new Date();
        String newmsg = "update notes.conv set ID=" + IDs + " where number=\""+note +"\"";
        String updateSum = "update notes.conv set sum = sum + 1 where number=\"" + note + "\"";
        String updateTime = "update notes.conv set time = " + currentTime.getTime() + " where number = \"" + note + "\"";
        String addMsg = "insert into notes. " + note + " (message,ID) values (\"" + message + "\",\"" + ID + "\")";

        try {
            statement.execute(addMsg);
            statement.execute(updateSum);
            statement.execute(newmsg);
            statement.execute(updateTime);
        } catch (SQLException e) {
            e.printStackTrace();
            return 1;
        }

        return 0;

    }
    public void newConv(String message, long IDs, String note, long ID){
        Date currentTime = new Date();
        String addMsg = "insert into notes. " + note + " (message,ID) values (\"" + message + "\",\"" + ID + "\")";
        String addConv = "insert into notes.conv (number, ID, time, sum) values (\"" + note + "\",\"" + IDs + "\", " + currentTime.getTime() + "," + 2 +")";
        String removeNoteFromQueue = "delete from notes.ochered where number=\"" + note +"\"";

        try{
            statement.executeUpdate(removeNoteFromQueue);
            statement.executeUpdate(addMsg);
            statement.executeUpdate(addConv);

        }catch (SQLException ex){
            ex.printStackTrace();
        }
    }

    public ArrayList<String> updateNotes(int ID){
        String update = "select number from notes.conv where ID=" + ID;
        ArrayList<String> notes = new ArrayList<>();
        try {
            ResultSet resultSet = statement.executeQuery(update);
            while(resultSet.next()){
                notes.add(resultSet.getString("number"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notes;
    }
    public ArrayList<ArrayList<String>> updateMsg(ArrayList<String> notes){
        ArrayList<ArrayList<String>> firstMsg = new ArrayList<>();
        for(int i = 0; i<notes.size();i++){
            String note = notes.get(i);
            String update = "select message from notes." + note;
            String gettime = "SELECT time FROM notes.conv where number = \'"+note+"\'";

            try {
                ResultSet resultSet = statement.executeQuery(update);
                resultSet.last();
                ArrayList<String> n = new ArrayList<>();
                n.add(note);
                n.add(resultSet.getString("message"));
                resultSet.previous();
                n.add(resultSet.getString("message"));
                resultSet.close();
                ResultSet resulttime = statement.executeQuery(gettime);
                resulttime.next();
                n.add(Long.toString(resulttime.getLong("time")));
                firstMsg.add(n);

            } catch (SQLException e) {
                e.printStackTrace();
            }

        }


        return firstMsg;
    }
    public ArrayList<String> newTable(String tableName, int ID, String msg){

        Date currentTime = new Date();
        String newtable = "create table notes." +
                            tableName +
                            " (" +
                            "message longtext not null," +
                            "ID int not null)";
        String insertMessage = "insert into notes. " + tableName + " (message,ID) values (\"" + msg + "\",\"" + ID + "\")";
        String insertToQueue = "insert into notes.ochered (number, ID, time) values (\"" + tableName + "\",\"" + ID + "\", " + currentTime.getTime() + ")";








        try {
            statement.executeUpdate(newtable);
            statement.executeUpdate(insertMessage);
            statement.executeUpdate(insertToQueue);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        Pair<String, Integer> p = new Pair<>(tableName, ID);



        Pair<String, Integer> note = MultiThreadServer.getMessages(p, ID);

        if(note == null){
            return null;
        }
        //String removeFromQueue = "delete from notes.ochered where number=\"" + note.getKey() +"\"";

        String selectMessages = "select message,ID from notes." + note.getKey();
        String getToken = "select token from registration.users where ID=\'" + note.getValue() + "\'";
        ArrayList<String> list = new ArrayList<>(2);

        try {
            //statement.executeUpdate(removeFromQueue);
            ResultSet resultSet = statement.executeQuery(selectMessages);
            resultSet.next();
            String messages = resultSet.getString("message");
            long lastID = resultSet.getLong("ID");
            ResultSet resultSet1 = statement.executeQuery(getToken);
            resultSet1.next();
            String token = resultSet1.getString("token");

            list.add(messages);
            list.add(String.valueOf(lastID));
            list.add(note.getKey());
            list.add(token);

        } catch (Exception e) {
            e.printStackTrace();
        }


        return list;
    }


    public int entr(String table, String login, String pass) {
        String check = "SELECT login, pass FROM " + table + " WHERE login=\"" + login + "\"";

        try{
            ResultSet res = statement.executeQuery(check);
            res.next();
            String dataBaseLogin = res.getString("login");
            if(dataBaseLogin != null){
                String password = res.getString("pass");
                if(password.equals(pass)){
                    return 0;
                }else{
                    return 1;
                }
            }else{
                return 1;
            }
        }catch (SQLException ex){
            ex.printStackTrace();
        }
        return 1;
    }
    public int insert(String table, String login, String pass) {

        String str = "insert into " + table + " (login,pass) values (\"" + login + "\",\"" + pass + "\")";
        String check = "SELECT login FROM " + table + " WHERE login=\"" + login + "\"";

        try{
            ResultSet res = statement.executeQuery(check);
            ArrayList<String> logins = new ArrayList<>();
            while(res.next()){
                logins.add(res.getString("login"));
            }
            if(logins.size() == 0){
                statement.executeUpdate(str);
                return 0;
            }else{
                return 1;
            }
        }catch (SQLException ex){
            ex.printStackTrace();
        }
        return 0;
    }

}
