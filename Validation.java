import java.util.Scanner;
import java.util.regex.*;

class Validation {
    static Scanner input = new Scanner(System.in);

    static int checkID(String type){
        boolean check = true;
        int id=0;
        while(check){
            if(!input.hasNextInt()){
                System.out.println(Main.ANSI_RED+"ID dont allow letter or digit"+Main.ANSI_RESET);
                System.out.print("-> Enter ID you want to "+type+": ");
                input.next();
            }else{
                id = input.nextInt();input.nextLine();
                check=false;
            }
        }
        return id;
    }
    static String checkName(String type){
        Pattern checkNamePattern = Pattern .compile("^[^\\s][a-zA-Z0-9\\s]+$");
        boolean check=true;
        String name = input.nextLine();
        while(check){
            Matcher matchName = checkNamePattern.matcher(name); 
            if(!matchName.matches()){
                System.out.println(Main.ANSI_RED+"Name allow only number and letters"+Main.ANSI_RESET);
                System.out.print(type.equals("insert")?"-> Enter product name: ":"Change Product name to : ");
                name = input.nextLine();
            }else{
                for(StockDTO  stock : Main.stockList){
                    if (stock.getName().equalsIgnoreCase(name)) {
                        System.out.println(Main.ANSI_RED+"\nThis name is already in use, please enter another one."+Main.ANSI_RESET);
                        System.out.print(type.equals("insert")?"-> Enter product name: ":"Change Product name to : ");
                        name = input.nextLine();
                    }else{
                        check=false;
                    }
                }
            }
        }
        return name;
    }
    static double checkPrice(String type){
        boolean check = true;
        double price=0;
        while(check){
            if(!input.hasNextDouble()){
                System.out.println(Main.ANSI_RED+"Price only allow numbers"+Main.ANSI_RESET);
                System.out.print(type.equals("insert")?"-> Enter Product Price: ":"Change Product price to: ");
                input.next();
            }else{
                price = input.nextDouble();input.nextLine();
                if(price<=0){
                    System.out.println(Main.ANSI_RED+"Price cannot be 0 or less than 0"+Main.ANSI_RESET);
                    System.out.print(type.equals("insert")?"-> Enter Product Price: ":"Change Product price to: ");
                }else{
                    check=false;
                }
            }
        }
        return price;
    }
    static int checkQty(String type){
        boolean check = true;
        int qty=0;
        while(check){
            if(!input.hasNextInt()){
                System.out.println(Main.ANSI_RED+"QTY only allow numbers and no digit"+Main.ANSI_RESET);
                System.out.print(type.equals("insert")?"-> Enter Product Quantity: ":"Change Product qty to: ");
                input.next();
            }else{
                qty = input.nextInt();input.nextLine();
                check=false;
            }
        }
        return qty;
    }
}
