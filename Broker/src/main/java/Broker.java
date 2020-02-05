
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;


public class Broker {

    private static ArrayList<String> List_of_instruments = new ArrayList<String>();;
    private static ArrayList<String> IdList= new ArrayList<String>();
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_BLUE = "\u001B[34m";

//    List_of_instuments =

    public static void Market_list(String message)
    {
        String[] split;
        String[] split2;
        int i = 1;
        if (message.contains("Name"))
        {
            split = message.split("\n");
            while (i < split.length)
            {
                if (split[i].contains("Name")) {
                    StringTokenizer st = new StringTokenizer(split[i], "|");
                    String ID = st.nextToken();
                    String name = st.nextToken();
               List_of_instruments.add(name.substring(name.indexOf(":") + 2, name.length() - 1));
               System.out.println(name.substring(name.indexOf(":") + 2, name.length() - 1));

                }
                i++;
            }
        }
    }
    public static void Market_id(String message)
    {
        if (message.contains("Market ID"))
        {
            IdList.add(message.substring(message.length() - 6, message.length()));
        }
    }

    public static String fix_notation(String message)
    {
        String[] split_input = message.split(" ");
        String x = null;
        if (split_input[1].equals("buy"))
        {
            x = "1";
        }
        else if (split_input[1].equals("sell"))
        {
            x = "2";
        }
        message = split_input[0] + " 35=D 54="+x+" 55="+split_input[2] + " 38="+split_input[3] + " 40=1 44="+split_input[4] + " " + GenerateCheckSum(message);
//        System.out.println("FIXED MESSAGE: " + message);
        return message;
    }

    public static String validate_input() {
        final Scanner scn = new Scanner(System.in);
        String final_string = "";
//        List_of_instuments = new ArrayList<String>();
//        List_of_instuments.add("zar");
//        List_of_instuments.add("IBM");
//        List_of_instuments.add("USD");
//        List_of_instuments.add("MAC");
//        List_of_instuments.add("wethinkcode");
//        IdList = new ArrayList<String>();
//        IdList.add("100000");
//        IdList.add("100001");
//        IdList.add("100002");
//        IdList.add("100003");
        // Market id input
        while (true)
        {
            System.out.println(ANSI_GREEN + "Enter Market ID?" + ANSI_RESET);
            String msg = scn.nextLine();
            if (IdList.contains(msg))
            {
                final_string += msg;
                break ;
            }
            System.out.println(ANSI_RED + "Enter Valid Market ID" + ANSI_RESET);
        }
        // Buying or selling
        while (true)
        {
            System.out.println(ANSI_GREEN + "Buy or Sell?" + ANSI_RESET);
            String msg1 = scn.nextLine();
            if (msg1.toLowerCase().equals("buy") || msg1.toLowerCase().equals("sell"))
            {
                final_string += " " + msg1;
                break ;
            }
            System.out.println(ANSI_RED + "State Whether Buy or Sell" + ANSI_RESET);
        }
        // Instrument list
        while (true)
        {
            System.out.println(ANSI_GREEN + "Enter Instrument?" + ANSI_RESET);
            String msg2 = scn.nextLine();
            if (List_of_instruments.contains(msg2))
            {
                final_string += " " + msg2;
                break ;
            }
            System.out.println(ANSI_RED + "Enter Valid instrument" + ANSI_RESET);
        }
        // Quantity
        while (true)
        {
            System.out.println(ANSI_GREEN + "Enter Quantity?" + ANSI_RESET);
            int x = 1;
            String msg3 = scn.nextLine();
            for (int i = 0; i < msg3.length(); i++) {
                if (!Character.isDigit(msg3.charAt(i))) {
                    System.out.println(ANSI_RED + "Invalid Quantity" + ANSI_RESET);
                    x = 0;
                }
            }
            if (x == 1)
            {
                final_string += " " + msg3;
                break ;
            }
            //System.out.println("Invalid Quantity");
        }
        // Price
        while (true)
        {
            System.out.println(ANSI_GREEN + "Enter Price?" + ANSI_RESET);
            int x = 1;
            String msg4 = scn.nextLine();
            for (int i = 0; i < msg4.length(); i++) {
                if (!Character.isDigit(msg4.charAt(i))) {
                    System.out.println(ANSI_RED + "Invalid Price" + ANSI_RESET);
                    x = 0;
                }
            }
            if (x == 1) {
                final_string += " " + msg4;
                break;
            }
            //System.out.println("Invalid Quantity");
        }
        return final_string;
    }

    public static int GenerateCheckSum(String message) {
        int sum = 0;
        int length;
        int i;

        length = message.length();
        for (i = 0; i < length; i++) {
            int value = message.charAt(i);
            sum += value;
        }
        int checkSum = sum % 256;
        return (checkSum);
    }

    public static String checkSumEncrypt(String message) {
        int checkSum = GenerateCheckSum(message);
        return (message + " " + checkSum);
    }


    public static void main(String[] args) throws UnknownHostException, IOException {

        final Scanner scn = new Scanner(System.in);
        final int id;
        // getting localhost ip
        InetAddress ip = InetAddress.getByName("localhost");

        // establish the connection
        Socket s = new Socket(ip, 5000);

        // obtaining input and out streams
        final DataInputStream dis = new DataInputStream(s.getInputStream());
        id = Integer.parseInt(dis.readUTF());
        System.out.println(id);
        final DataOutputStream dos = new DataOutputStream(s.getOutputStream());
//        int i = 0;
        
        while(true) {
            String string = dis.readUTF();
            Market_id(string);
            Market_list(string);
            if (string.equals("end")){
                break;
            }
            System.out.println(string);
        }


        // sendMessage thread
        ExecutorService executor = Executors.newFixedThreadPool(1);

        executor.submit(new Runnable() {
            @Override
            public void run() {
                while (true) {

                    // read the message to deliver.
                    //String msg = scn.nextLine();
                    String msg = validate_input();
                    msg = checkSumEncrypt(msg);
                    msg = fix_notation(msg);
                    try {
                        // write on the output stream
                        dos.writeUTF(id + " " + msg);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        // read the message sent to this client
                        String response = dis.readUTF();
                        System.out.println(response);
                    } catch (IOException e) {

                        e.printStackTrace();
                    }
                }
            }
        });
//        Thread sendMessage = new Thread(new Runnable() {
//            @Override
//            public void run() {
//            }
//        });

        // readMessage thread
//        Thread readMessage = new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//                while (true) {
//                    try {
//                        // read the message sent to this client
//                        String msg = dis.readUTF();
//                        System.out.println(msg);
//                    } catch (IOException e) {
//
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });

//        sendMessage.start();
//        readMessage.start();
    }

}
