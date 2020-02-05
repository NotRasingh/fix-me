public class checkSum {

    public int  GenerateCheckSum(String message, int length) {
        int sum = 0;
        int i;

        for (i = 0; i < length; i++) {
            int value = message.charAt(i);
            sum += value;
        }
        int checkSum = sum % 256;
        return(checkSum);
    }

    public String checkSumEncrypt(String message)
    {
        int checkSum = GenerateCheckSum(message, message.length());
        return (message + " " + checkSum);
    }

    public boolean checkCheckSum(String message) {
        String message1 = checkSumEncrypt(message);

        int checkSum = GenerateCheckSum(message, message.length());
        String[] splitMessage = message.split(" ");

        if (message1.contains(Integer.toString(checkSum))) {
            return true;
        }
        return false;
    }

//    public String fix_message(String message){
//        String message1 = checkSumEncrypt(message);
//
//        return message1;
//    }

    public static void main(String args[]) {
        checkSum checkSumm = new checkSum();
        String message = "sell zar 100 200 2";
        int genCheckSum = checkSumm.GenerateCheckSum(message, message.length());
        String checkSumMessage = checkSumm.checkSumEncrypt(message);
        System.out.println("This is the checkSumMessage " + checkSumMessage);
        System.out.println(checkSumm.checkCheckSum(message));


        String message1 = "buy IMB 1000 2000 2";
        int genCheckSum1 = checkSumm.GenerateCheckSum(message1, message1.length());
        String checkSumMessage1 = checkSumm.checkSumEncrypt(message1);
        System.out.println("This is the checkSumMessage " + checkSumMessage1);
        System.out.println(checkSumm.checkCheckSum(message1));
    }
}