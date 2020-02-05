import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

public class Market {

    private static ArrayList<Instruments> instrumentList = new ArrayList<Instruments>();
    private static HashMap<String, Instruments> insList = new HashMap<String, Instruments>();

    public static void main(String[] args) throws UnknownHostException, IOException {


        Instruments weThinkCOde = new Instruments(1, "wethinkcode", 100, 69, "STOCK");
        Instruments Vans = new Instruments(2, "Vans", 100, 420, "STOCK");
        Instruments Apple = new Instruments(3, "Apple", 100, 666, "STOCK");
        Instruments AllStar = new Instruments(4, "AllStar", 100, 786, "STOCK");
        Instruments Loverlab = new Instruments(5, "Loverlab", 100, 333, "STOCK");
        Instruments usdjpy = new Instruments(6, "usdjpy", 655, 12, "CURRENCY");


        //instrumentList.add(weThinkCOde);
        //instrumentList.add(usdjpy);
        insList.put(weThinkCOde.getName(), weThinkCOde);
        insList.put(Vans.getName(), Vans);
        insList.put(Apple.getName(), Apple);
        insList.put(AllStar.getName(), AllStar);
        insList.put(Loverlab.getName(), Loverlab);
        insList.put(usdjpy.getName(), usdjpy);
        final Scanner scn = new Scanner(System.in);

        // getting localhost ip
        InetAddress ip = InetAddress.getByName("localhost");

        // establish the connection
        Socket s = new Socket(ip, 5001);

        // obtaining input and out streams
        final DataInputStream dis = new DataInputStream(s.getInputStream());
        final DataOutputStream dos = new DataOutputStream(s.getOutputStream());

        // sendMessage thread
        //initial instrument list message
        String instrumentList = "List of Instruments";
        for (Map.Entry<String, Instruments> instrument : insList.entrySet()) {
            instrumentList += instrument.getValue().printDetails();
        }
        try {
            dos.writeUTF(instrumentList);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Thread sendMessage = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {

                    //forward instrument list
                    // read the message to deliver.
                    //   String msg = scn.nextLine();

                    try {
                        // write on the output stream
                        dos.writeUTF("#Broker");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        // readMessage thread
        Thread readMessage = new Thread(new Runnable() {
            @Override
            public void run() {

                while (true) {
                    try {
                        // read the message sent to this client
                        String msg = dis.readUTF();
//                        //check if its initial message from server
                        if (msg.length() > 6) {
                            //validate && execute  Order
                            String[] fixedOrder = validateOrder(msg);
                            String resMsg = executeOrder(fixedOrder);
                            //run checksum here
                            dos.writeUTF(resMsg);
                        }
//

                        System.out.println(msg);
                    } catch (IOException e) {

                        e.printStackTrace();
                    }
                }
            }
        });

        //   sendMessage.start();
        readMessage.start();
    }

    //validate order
    public static String[] validateOrder(String msg) {
        //check FIX notation
        String[] message = msg.split(" ");
        return message;
    }

    //execute order
    //hashmap shit
    public static String executeOrder(String[] order) {
        String response = "";


        //id=
        String id = order[0];
        String myID = order[1];
        //35= new order
        String neworder = order[2].substring(order[2].indexOf("=") + 1); //always D
        //54= buy or sell
        String buysell = order[3].substring(order[3].indexOf("=") + 1);
        ; //1 or 2
        //55= instrumentID
        String instrumentID = order[4].substring(order[4].indexOf("=") + 1);
        //38= quantity
        int qty = Integer.parseInt(order[5].substring(order[5].indexOf("=") + 1));
        //40= order type,always 1
        String orderType = order[6].substring(order[6].indexOf("=") + 1);
        //44= price
        String price = order[7].substring(order[7].indexOf("=") + 1);
        //checksum
        String checksum = order[8];

        if (buysell.equals("1")) {
            //buy order
            //check availability
            if (insList.get(instrumentID).getQty() >= qty) {

                //actual buy
                int buyResult = insList.get(instrumentID).buy(qty, Integer.parseInt(price));
                String res = myID + " " + id + " Buy successful!";

                if (buyResult == 0) {
                   String  instrumentList =  myID + " " + id + " Order Rejected, Incorrect Price.\n List of Instruments:\n";
                    for (Map.Entry<String, Instruments> instrument : insList.entrySet()) {
                        instrumentList += instrument.getValue().printDetails();
                    }
                    return instrumentList;
                }


                Iterator<Map.Entry<String, Instruments>> it = insList.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, Instruments> pair = it.next();
                    res += pair.getValue().printDetails();
                }
                return res + " " + GenerateCheckSum(res);
            } else {
                String res = "";
                Iterator<Map.Entry<String, Instruments>> it = insList.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, Instruments> pair = it.next();
                    res += pair.getValue().printDetails()  + GenerateCheckSum(res);
                }
                // STOP HARD CODING THE MARKET ID
                String  instrumentList =  myID + " " + id + " Instrument quantity not available.\n List of Instruments:\n";
                for (Map.Entry<String, Instruments> instrument : insList.entrySet()) {
                    instrumentList += instrument.getValue().printDetails();
                }
                return instrumentList;
            }
        } else if (buysell.equals("2")) {
            //sell order
            int sellResult = insList.get(instrumentID).sell(qty, Integer.parseInt(price));
            String res = myID + " " + id + " Sell successful!";

            if (sellResult == 0) {
                String  instrumentList =  myID + " " + id + " Order Rejected, Incorrect Price.\n List of Instruments:\n";
                for (Map.Entry<String, Instruments> instrument : insList.entrySet()) {
                    instrumentList += instrument.getValue().printDetails();
                }
                return instrumentList;
            }

            Iterator<Map.Entry<String, Instruments>> it = insList.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Instruments> pair = it.next();
                res += pair.getValue().printDetails();
            }
            return res + " " + GenerateCheckSum(res);
        }

        return response;
    }


    public static int GenerateCheckSum(String message) {
        int sum = 0;
        int i;
        int length = message.length();

        for (i = 0; i < length; i++) {
            int value = message.charAt(i);
            sum += value;
        }
        int checkSum = sum % 256;
        return (checkSum);
    }


}
