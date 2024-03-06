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
    
    static Connection connectionToDB() throws SQLException{
        String url = "jdbc:postgresql://localhost:5432/stockdb";
        Connection con = DriverManager.getConnection(url, "postgres", "123");
        return con;
    }
    static int limit() throws FileNotFoundException{
        File file = new File("limit.txt");
        Scanner input = new Scanner(file);
        int data=0;
        while(input.hasNextInt()){
            data = input.nextInt();
        }
        input.close();
        
        return data;
    }
    static int readKeepTab() throws FileNotFoundException{
        File file = new File("keeptab.txt");
        Scanner input = new Scanner(file);
        int data=0;
        while(input.hasNextInt()){
            data = input.nextInt();
        }
        input.close();
        
        return data;
    }

    static void writer() throws IOException{
        FileWriter writer = new FileWriter("limit.txt");
        System.out.print("-> Set row: ");
        int setRow = input.nextInt();input.nextLine();
        writer.write(String.valueOf(setRow));
        writer.close();
    }
    static void writerKeepTab(int num) throws IOException{
        FileWriter writer = new FileWriter("keeptab.txt");
        writer.write(String.valueOf(num));
        writer.close();
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
    static void insertProduct(StockDAO stockDOA,Controller controller) throws SQLException, IOException{
        // System.out.println("Keeph" +keeph);
        // int keep = StockView.keepTab;
        // System.out.println("Keep : "+keep);
        keep++;
        System.out.println("keep:"+keep);
        if(keep>readKeepTab()){
            writerKeepTab(keep);
        }
        keep=readKeepTab()-1;
        
        int temp = stockDOA.getCount("unsaved_write");
        System.out.println("ID: "+ (auto()+temp+1));
        System.out.print("Product name: ");String name = input.nextLine();
        System.out.print("Enter Product price: ");double price = input.nextDouble();input.nextLine();
        System.out.print("Enter quantity: ");int qty = input.nextInt();input.nextLine();
        controller.insertProductToUnsavedWriteTB(new StockDTO((auto()+temp+1), name, price, qty, date));
    }
    static void insertProductToStockTB(StockDAO stockDAO,Controller controller) throws SQLException{
        int id=0,qty=0;String name="",date=""; double price=0;
        unsavedWriteList.removeAll(unsavedWriteList);
        stockDAO.getAll("unsaved_write");
        for(StockDTO unsavedWrite : unsavedWriteList){
            System.out.println(unsavedWrite.getId());
            id = unsavedWrite.getId();
            name = unsavedWrite.getName();
            price = unsavedWrite.getPrice();
            qty = unsavedWrite.getQty();
            date = unsavedWrite.getDate();
            controller.savedWriteProduct(new StockDTO(id, name, price, qty, date));
            System.out.println("Name: "+unsavedWrite.getName());
        }
        stockDAO.deleteUnsavedTableAfterSave("unsaved_write");
    }
    static void updateProduct(StockDAO stockDAO,Controller controller) throws SQLException{
        System.out.println("Enter id you want to update: ");int id=input.nextInt();input.nextLine();
        System.out.print("Product name: ");String name = input.nextLine();
        System.out.print("Enter Product price: ");double price = input.nextDouble();input.nextLine();
        System.out.print("Enter quantity: ");int qty = input.nextInt();input.nextLine();
        controller.updateProductToUnsavedUpdate(id,new StockDTO(id, name, price, qty, date));
    }

    static void updateProductToStockTB(StockDAO stockDAO,Controller controller) throws SQLException{
        int id=0,qty=0;String name="",date=""; double price=0;
        unsavedUpdateList.removeAll(unsavedUpdateList);
        stockDAO.getAll("unsaved_update_tb");
        for(StockDTO unsavedUpdate : unsavedUpdateList){
            System.out.println(unsavedUpdate.getId());
            id = unsavedUpdate.getId();
            name = unsavedUpdate.getName();
            price = unsavedUpdate.getPrice();
            qty = unsavedUpdate.getQty();
            date = unsavedUpdate.getDate();
            controller.savedUpdateProduct(id,new StockDTO(id, name, price, qty, date));
            System.out.println("Name: "+unsavedUpdate.getName());
        }
        stockDAO.deleteUnsavedTableAfterSave("unsaved_update_tb");
    }
    static void deleteProduct(Controller controller) throws SQLException{
        System.out.print("Enter id you want to delete: ");int id=input.nextInt();input.nextLine();
        controller.deleteProduct(id);
    }
    static void read(StockDAO stockDAO) throws SQLException{
        System.out.print("Enter id: ");int id = input.nextInt();input.nextLine();
        stockDAO.readData(id);
    }

    static void Search(){
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
            T.addCell("ID",center);
            T.addCell("Name",center);
            T.addCell("Unit Price",center);
            T.addCell("QTY",center);
            T.addCell("Imported Date",center);
            for(StockDTO stock : stockList){
                if(stock.getName().contains(searchWords)){
                    T.addCell(""+stock.getId(),center);
                    T.addCell(stock.getName(),center);
                    T.addCell(""+stock.getPrice(),center);
                    T.addCell(""+stock.getQty(),center);
                    T.addCell(stock.getDate(),center);
                }
            }
            System.out.println(T.render());
        }else{
            System.out.println("Cannot find the Product");
        }
        while (true) {      
            System.out.print("Press 1 to Search again and 0 to back: ");
            String choice = input.nextLine();
            switch (choice) {
                case "1" -> {
                    Search();
                    return;
                }case "0"->{
                    return;
                }default->{
                    System.out.println( "Please enter a valid number!");
                    Search();
                    continue;
                }
            }
        }
    }
    public static void main(String[] args) throws SQLException, IOException {
        System.out.println(date);
        // try {
        //     Connection con = connectionToDB();
        //     Statement sta = con.createStatement();
        //     sta.execute("Create Table unsaved_update_tb(id SERIAL PRIMARY KEY,name VARCHAR,price DECIMAL(6,2) check(price>0),qty int,imported_date DATE Default '" + date + "')");
        //     //sta.executeUpdate("INSERT INTO stock_tb(name,price,qty) VALUES('Green Tea',1.99,100), ('Orange Juice',2.99,80),('Iced Coffee',4.49,50),('Ginger Beer',1.49,90),('Smoothie',3.29,70),('Lemonade',2.99,30),('Latte',3.99,60),('Strawberry Lemonade',4.49,50)");
        //     writerKeepTab(auto());
        // } catch (Exception e) {
        //     System.out.println("Error");
        // }
        StockDAO stockDAO = new StockDOAImp();
        StockView stockView = new StockView();
        Controller controller = new Controller(stockDAO, stockView);
        System.out.println("read " +readKeepTab());
        System.out.println(stockDAO.getCount("Stock_tb"));
        System.out.println(stockDAO.getCount("unsaved_write"));
        controller.showAllData();
        System.out.println(auto());
        // controller.getCount();
        //insertData(stockDOA,controller);
        //deleteProduct(controller);
        // read(stockDAO);
    }
}