import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

import org.nocrala.tools.texttablefmt.BorderStyle;
import org.nocrala.tools.texttablefmt.CellStyle;
import org.nocrala.tools.texttablefmt.ShownBorders;
import org.nocrala.tools.texttablefmt.Table;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main{
    static CellStyle center = new CellStyle(CellStyle.HorizontalAlign.center);
    static StockDAO stockDAO = new StockDOAImp();
    static Scanner input = new Scanner(System.in);
    static ArrayList<StockDTO> stockList = new ArrayList<>();
    static ArrayList<StockDTO> unsavedWriteList = new ArrayList<>();
    static ArrayList<StockDTO> unsavedUpdateList = new ArrayList<>();
    static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    static String date=sdf.format(new Date());
    static int keep = 0;
    static String ANSI_RED = "\u001B[31m";
    static String ANSI_GREEN = "\u001B[32m";
    static String ANSI_CYAN = "\033[0;36m";
    static String ANSI_PURPLE = "\033[0;35m";
    static String ANSI_BLUE = "\033[0;34m";
    static String ANSI_RESET = "\u001B[0m";

    static Connection connectionToDB(){
        String url = "";
        Connection con = null;
        try {
            url = "jdbc:postgresql://localhost:5432/stockdb";
            con = DriverManager.getConnection(url, "postgres", "123");

        } catch (SQLException e) {
            System.out.println(ANSI_RED+e.getMessage()+ANSI_RESET);
        }
        return con;
    }
    static int limit(){
        int data=0;
        try {
            File file = new File("limit.txt");
            Scanner input = new Scanner(file);
            while(input.hasNextInt()){
                data = input.nextInt();
            }
            input.close();
        } catch (IOException e) {
            System.out.println(ANSI_RED+e.getMessage()+ANSI_RESET);
        }
        return data;
    }
    static int readKeepTab(){
        int data=0;
        try {
            File file = new File("keeptab.txt");
            Scanner input = new Scanner(file);
            while(input.hasNextInt()){
                data = input.nextInt();
            }
            input.close();
        } catch (FileNotFoundException e) {
            System.out.println(ANSI_RED+e.getMessage()+ANSI_RESET);
        }   
        return data;
    }

    static void writer() throws IOException{
        try {
            FileWriter writer = new FileWriter("limit.txt");
            System.out.print("-> Set row: ");
            int setRow = input.nextInt();input.nextLine();
            if(setRow<=0 || setRow>stockList.size()){
                try {
                    System.out.println(ANSI_RED+"Cannot below or equal 0 or bigger than the Record: "+stockDAO.getCount("stock_tb")+ANSI_RESET);
                } catch (SQLException e) {
                    System.out.println(ANSI_RED+e.getMessage()+ANSI_RESET);
                }
                System.out.println("Set it to default 3.");
                System.out.print("\nPress Enter to continue. . .");input.nextLine();
                writer.write(String.valueOf(3));
                writer.close();
                return;
            }else{
                writer.write(String.valueOf(setRow));
                writer.close();
            }
        } catch (IOException e) {
            System.out.println(ANSI_RED+e.getMessage()+ANSI_RESET);
        }
    }
    static void writerKeepTab(int num){
        try {
            FileWriter writer = new FileWriter("keeptab.txt");
            writer.write(String.valueOf(num));
            writer.close();
        } catch (IOException e) {
            System.out.println(ANSI_RED+e.getMessage()+ANSI_RESET);
        }
    }

    static int auto(){
        int biggest = 0;
        for(StockDTO stock : stockList){
            if(stock.getId()>biggest){
                biggest = stock.getId();
            }
        }
        return biggest;
    }
    static void insertProductToUnsavedWrite(StockDAO stockDOA,Controller controller) throws IOException{
        // System.out.println("Keeph" +keeph);
        // int keep = StockView.keepTab;
        // System.out.println("Keep : "+keep);
        keep++;
        if(keep>readKeepTab()){
            writerKeepTab(keep);
        }
        keep=readKeepTab()-1;
        try {
            int temp = stockDOA.getCount("unsaved_write_tb");
            //System.out.println("ID: "+ (auto()+temp+1));
            System.out.print("-> Enter Product name: ");String name = Validation.checkName("insert");
            System.out.print("-> Enter Product price: ");double price = Validation.checkPrice("insert");
            System.out.print("-> Enter Product quantity: ");int qty = Validation.checkQty("insert");
            controller.insertProductToUnsavedWriteTB(new StockDTO((auto()+temp+1), name, price, qty, date));
        } catch (SQLException e) {
            System.out.println(ANSI_RED+e.getMessage()+ANSI_RESET);
        }
    }
    static void insertProductToStockTB(StockDAO stockDAO,Controller controller){
        try {
            if(stockDAO.getCount("unsaved_write_tb")==0){
                System.out.println("Nothing to Insert");
            }else{
                int id=0,qty=0;String name="",date=""; double price=0;
                unsavedWriteList.removeAll(unsavedWriteList);
                stockDAO.getAll("unsaved_write_tb");
                for(StockDTO unsavedWrite : unsavedWriteList){
                    id = unsavedWrite.getId();
                    name = unsavedWrite.getName();
                    price = unsavedWrite.getPrice();
                    qty = unsavedWrite.getQty();
                    date = unsavedWrite.getDate();
                    controller.savedWriteProduct(new StockDTO(id, name, price, qty, date));
                    System.out.println(ANSI_GREEN+"* New Product: "+name+" was inserted successfully"+ANSI_RESET);
                }
                stockDAO.deleteUnsavedTableAfterSave("unsaved_write_tb");
            }
        } catch (SQLException e) {
            System.out.println(ANSI_RED+e.getMessage()+ANSI_RESET);
        }
    }
    static void updateProduct(StockDAO stockDAO,Controller controller){
        boolean isFound = false,check=true;
        int id=0;
        while(check){
            System.out.print("-> Enter id you want to update: ");id=Validation.checkID("update");
            for(StockDTO stockDTO : stockList){
                if(stockDTO.getId()==id){
                    isFound = true;
                    check = false;
                    break;
                }
            }
            if(isFound==false){
                System.out.println(ANSI_RED+"Cannot find id."+ANSI_RESET);
                boolean checkAgain = true;
                while(checkAgain){
                    System.out.print("Press 1 if you want to input new ID or 0 to go back to main menu : ");
                    String choice = input.nextLine();
                    switch (choice) {
                        case "1":
                            checkAgain=false;
                            break;
                        case "0":
                            return;
                        default:
                            System.out.println(ANSI_RED+"Invalid choice. Pick again."+ANSI_RESET);
                            break;
                    }
                }
            }
        }
        System.out.print("Change Product name to : ");String name = Validation.checkName("update");
        System.out.print("Change Product price to: ");double price = Validation.checkPrice("update");
        System.out.print("Change Product qty to: ");int qty = Validation.checkQty("update");
        controller.updateProductToUnsavedUpdate(id,new StockDTO(id, name, price, qty, date));
    }

    static void updateProductToStockTB(StockDAO stockDAO,Controller controller){
        try {
            if(stockDAO.getCount("unsaved_update_tb")==0){
                System.out.println(ANSI_RED+"Nothing to Update"+ANSI_RESET);
            }else{
                int id=0,qty=0;String name="",date=""; double price=0;
                unsavedUpdateList.removeAll(unsavedUpdateList);
                stockDAO.getAll("unsaved_update_tb");
                for(StockDTO unsavedUpdate : unsavedUpdateList){
                    id = unsavedUpdate.getId();
                    name = unsavedUpdate.getName();
                    price = unsavedUpdate.getPrice();
                    qty = unsavedUpdate.getQty();
                    date = unsavedUpdate.getDate();
                    controller.savedUpdateProduct(id,new StockDTO(id, name, price, qty, date));
                    System.out.println(ANSI_RED+"* Update Product: "+id+" was update successfully"+ANSI_RESET);
                }
                stockDAO.deleteUnsavedTableAfterSave("unsaved_update_tb");
            }
        } catch (SQLException e) {
            System.out.println(ANSI_RED+e.getMessage()+ANSI_RESET);
        }
    }
    static void deleteProduct(Controller controller){
        System.out.print("-> Enter id you want to delete: ");int id=Validation.checkID("delete");
        for(StockDTO stock:stockList){
            if(stock.getId()==id){
                controller.deleteProduct(id);
                return;
            }
        }
        System.out.println(ANSI_RED+"Cant find data"+ANSI_RESET);
        System.out.print("Press Enter to continue ...");input.nextLine();
    }
    static void checkConfirmationOnDelete(int id){
        boolean check=true;
        while (check){
            System.out.print("Press Y to confirm or B for back to menu : ");
            String choice = input.nextLine();
            switch (choice.toLowerCase()) {
                case "y":
                    try {
                        stockDAO.deleteProduct(id);
                    } catch (SQLException e) {
                        System.out.println(ANSI_RED+e.getMessage()+ANSI_RESET);
                    }
                    System.out.println(ANSI_GREEN+"* Delete Product: "+id+" was delete successfully"+ANSI_RESET);
                    check=false;
                    break;
                case "b":
                    check=false;
                    break;
                default:
                    System.out.println(ANSI_RED+"\nInvalid Choice\nPlease try again..."+ANSI_RESET);
                    try {
                        stockDAO.readData(id);
                    } catch (SQLException e) {
                        System.out.println(ANSI_RED+e.getMessage()+ANSI_RESET);
                    }
            }
        }
    }

    static void read(StockDAO stockDAO){
        System.out.print("Enter id you want to read: ");int id = Validation.checkID("read");
        for(StockDTO stock:stockList){
            if(stock.getId()==id){
                try {
                    stockDAO.readData(id);
                } catch (SQLException e) {
                    System.out.println(ANSI_RED+e.getMessage()+ANSI_RESET);
                }
                return;
            }
        }
        System.out.println(ANSI_RED+"Cant find data"+ANSI_RESET);
    }

    static void search(){
        boolean checkLoop = true;
        while (checkLoop) {
            System.out.print("Enter Product you want to Search: ");
            String searchWords = input.nextLine();
            Table T = new Table(5,BorderStyle.UNICODE_BOX_WIDE,ShownBorders.ALL);
            boolean isFound=false;
            for(StockDTO stock : stockList){
                if(stock.getName().contains(searchWords)){
                    isFound=true;
                    break;
                }
            }
            if(isFound==true){
                T.setColumnWidth(0, 10, 20);
                T.setColumnWidth(1, 20, 30);
                T.setColumnWidth(2, 10, 20);
                T.setColumnWidth(3, 10, 20);
                T.setColumnWidth(4, 10, 20);
                T.addCell(ANSI_CYAN+"ID"+ANSI_RESET,center);
                T.addCell(ANSI_CYAN+"Name"+ANSI_RESET,center);
                T.addCell(ANSI_CYAN+"Unit Price"+ANSI_RESET,center);
                T.addCell(ANSI_CYAN+"QTY"+ANSI_RESET,center);
                T.addCell(ANSI_CYAN+"Imported Date"+ANSI_RESET,center);
                for(StockDTO stock : stockList){
                    if(stock.getName().contains(searchWords)){
                        T.addCell(ANSI_PURPLE+""+stock.getId(),center);
                        T.addCell(ANSI_PURPLE+stock.getName(),center);
                        T.addCell(ANSI_PURPLE+""+stock.getPrice(),center);
                        T.addCell(ANSI_PURPLE+""+stock.getQty(),center);
                        T.addCell(ANSI_PURPLE+stock.getDate(),center);
                    }
                }
                System.out.println(T.render());
            }else{
                System.out.println(ANSI_RED+"Cannot find the Product"+ANSI_RESET);
            }
            boolean check=true;
            while (check) {      
                System.out.print("Press 1 to Search again and 0 to back to main menu: ");
                String choice = input.nextLine();
                switch (choice) {
                    case "1" -> {
                        check=false;
                        break;
                    }case "0"->{
                        check=false;
                        return;
                    }default->{
                        System.out.println( ANSI_RED+"Please enter a valid number!"+ANSI_RESET);
                        break;
                    }
                }
            }
        }
    }
    public static void main(String[] args) throws SQLException, IOException {
        //System.out.println(date);
        // try {
        //     Connection con = connectionToDB();
        //     Statement sta = con.createStatement();
        //     sta.execute("Create Table stock_tb(id SERIAL PRIMARY KEY,name VARCHAR,price DECIMAL(6,2) check(price>0),qty int,imported_date DATE Default '" + date + "')");
        //     sta.executeUpdate("INSERT INTO stock_tb(name,price,qty) VALUES('Green Tea',1.99,100), ('Orange Juice',2.99,80),('Iced Coffee',4.49,50),('Ginger Beer',1.49,90),('Smoothie',3.29,70),('Lemonade',2.99,30),('Latte',3.99,60),('Strawberry Lemonade',4.49,50)");
        //     writerKeepTab(auto());
        // } catch (Exception e) {
        //     System.out.println("Error");
        // }
        StockDAO stockDAO = new StockDOAImp();
        StockView stockView = new StockView();
        Controller controller = new Controller(stockDAO, stockView);
        // System.out.println("read " +readKeepTab());
        // System.out.println(stockDAO.getCount("stock_tb"));
        // System.out.println(stockDAO.getCount("unsaved_write_tb"));
        controller.showAllData();
        // System.out.println(auto());
        // controller.getCount();
        //insertData(stockDOA,controller);
        //deleteProduct(controller);
        // read(stockDAO);
    }
}