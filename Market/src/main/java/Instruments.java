public class Instruments {

    private int id;
    private String name;
    private double qty;
    private double price;
    private String instrumentType;

    public Instruments(int id, String name, double qty, double price, String instrumentType) {
        this.id = id;
        this.name = name;
        this.qty = qty;
        this.price = price;
        this.instrumentType = instrumentType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getQty() {
        return qty;
    }

    public void setQty(double qty) {
        this.qty = qty;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getInstrumentType() {
        return instrumentType;
    }

    public void setInstrumentType(String instrumentType) {
        this.instrumentType = instrumentType;
    }

    public int buy(int qty, int price){
        if(price == this.price) {
            this.qty -= qty;
            this.price += (qty / 2);
            return 1;
        }else{
            return 0;
        }
    }

    public int sell(int qty, int price) {
        if (price == this.price) {
            this.qty += qty;
            this.price -= (qty / 2);
            return 1;
        } else {
            return 0;
        }
    }

    public String printDetails(){
        String insString = "\nID: " + this.id + " | Name: " + this.name+ " | Price " + this.price + " | Qty: " + this.qty + "\n";
        return insString;
    }






}
