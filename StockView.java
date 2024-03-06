import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

import org.nocrala.tools.texttablefmt.BorderStyle;
import org.nocrala.tools.texttablefmt.CellStyle;
import org.nocrala.tools.texttablefmt.ShownBorders;
import org.nocrala.tools.texttablefmt.Table;

public class StockView {
    static int startIndex = 0;
    static int startPage = 1;
    static CellStyle center = new CellStyle(CellStyle.HorizontalAlign.center);
    static Scanner input = new Scanner(System.in);
    void printStockDetails(StockDAO stockDAO) throws SQLException, IOException{
        while(true){
            if(startIndex>=Main.stockList.size()){            
                startIndex = Main.stockList.size()-(Main.stockList.size()%Main.limit());
            }else if(startIndex<=0){
                startIndex=0;
            }
            Table T = new Table(5,BorderStyle.UNICODE_BOX_WIDE,ShownBorders.ALL);
            T.setColumnWidth(0, 10, 20);
            T.setColumnWidth(1, 20, 30);
            T.setColumnWidth(2, 10, 20);
            T.setColumnWidth(3, 10, 20);
            T.setColumnWidth(4, 10, 20);
            T.addCell("Stock Management",center,5);
            T.addCell("ID",center);
            T.addCell("Name",center);
            T.addCell("Unit Price",center);
            T.addCell("QTY",center);
            T.addCell("Imported Date",center);
            Main.stockList.stream()
                .skip(startIndex)
                .limit(Main.limit())
                .forEach(stock -> {
                T.addCell(""+stock.getId(),center);
                T.addCell(stock.getName(),center);
                T.addCell(""+stock.getPrice(),center);
                T.addCell(""+stock.getQty(),center);
                T.addCell(stock.getDate(),center);
            });
            int endPage = Main.stockList.size()/Main.limit() + (Main.stockList.size()%Main.limit()>0?1:0);
            T.addCell("Page : "+ startPage + " of " + endPage,2);
            T.addCell("Total Record: " + stockDAO.getCount("stock_tb"),3);
            System.out.println(T.render());
    
            System.out.println("F) First\t P) Previous\t N) Next\t L) Last\t G) Goto");
            System.out.println("\n*) Display");
            System.out.println("W) Write\tR) Read\t\tU) Update\tD) Delete\tS) Search");
            System.out.println("Se) Set Row\tSa) Save\tUn) Unsaved\tBa) Backup\tRe) Restore\tE) Exit");
            System.out.println("---------------------------------");
            System.out.print("-> Choose Option: ");
            String option = input.nextLine();
            switch(option.toLowerCase()){
                case "f" -> {
                    startIndex=0;
                    startPage=1;
                    break;
                }case "n"->{
                    startIndex+=Main.limit();
                    if(startPage==endPage){
                        startPage=endPage;
                    }else if(startPage<endPage){
                        startPage++;
                    }
                    System.out.println("Next:"+startIndex);
                    //when arrayList has no remainder
                    if(Main.stockList.size()%Main.limit()==0 && startIndex==Main.stockList.size()){
                        startIndex = Main.stockList.size() - Main.limit();
                    }
                    break;
                }case "p"->{
                    startIndex-=Main.limit();
                    startPage--;
                    if(startPage==0){
                        startPage=1;
                    }
                    System.out.println("Prevous:"+startIndex);
                    break;
                }case "l" ->{
                    startPage=endPage;
                    startIndex = Main.stockList.size()-(Main.stockList.size()%Main.limit());
                    if(Main.stockList.size()%Main.limit()==0){
                        startIndex=Main.stockList.size()-Main.limit();
                    }
                    System.out.println("Last:"+startIndex);
                    break;
                }case "g" ->{
                    System.out.print("Input Page Number: ");
                    int pgNumber = input.nextInt();input.nextLine();
                    startPage=pgNumber;
                    startIndex= pgNumber == 1 ? 0 : (pgNumber-1)*Main.limit();
                    break;
                }case "w"->{
                    StockView stockView = new StockView();
                    Controller controller = new Controller(stockDAO, stockView);
                    Main.insertProduct(stockDAO,controller);
                    startIndex=0;
                    startPage=1;
                    break;
                }case "r" ->{
                    Main.read(stockDAO);
                    startIndex=0;
                    System.out.print("Press Enter to continue ...");input.nextLine();
                    startPage=1;
                    break;
                }case "d"->{
                    StockView stockView = new StockView();
                    Controller controller = new Controller(stockDAO, stockView);
                    Main.deleteProduct(controller);
                    Main.stockList.removeAll(Main.stockList);
                    stockDAO.getAll("stock_tb");
                    startIndex=0;
                    startPage=1;
                    break;
                }case "u"->{
                    StockView stockView = new StockView();
                    Controller controller = new Controller(stockDAO, stockView);
                    Main.updateProduct(stockDAO,controller);
                    Main.stockList.removeAll(Main.stockList);
                    stockDAO.getAll("stock_tb");
                    startIndex=0;
                    startPage=1;
                    break;
                }case "s"->{
                    StockView stockView = new StockView();
                    Controller controller = new Controller(stockDAO, stockView);
                    controller.searchProduct();
                }case "se" ->{
                    try {
                        Main.writer();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }case "un" ->{
                    StockView stockView = new StockView();
                    Controller controller = new Controller(stockDAO, stockView);
                    System.out.println("Press I for show UnsavedInsert or U for show unsavedUpdate");
                    String type = input.nextLine();
                    if(type.equalsIgnoreCase("i")){
                        controller.showAllUnsavedWriteData();
                    }else if(type.equalsIgnoreCase("u")){
                        controller.showAllUnsavedUpdateData();
                    }
                    
                }case "sa" ->{
                    StockView stockView = new StockView();
                    Controller controller = new Controller(stockDAO, stockView);
                    System.out.println("I for saved unsavedInsert and U for saved unsavedUpdate");
                    String type = input.nextLine();
                    if(type.equalsIgnoreCase("i")){
                        stockDAO.getAll("unsaved_write");
                        Main.insertProductToStockTB(stockDAO, controller);
                    }else if(type.equalsIgnoreCase("u")){
                        stockDAO.getAll("unsaved_update_tb");
                        Main.updateProductToStockTB(stockDAO, controller);
                    }
                    Main.stockList.removeAll(Main.stockList);
                    stockDAO.getAll("stock_tb");
                    startIndex=0;
                    startPage=1;
                    break;
                }
            }
        }
    }
    static void printSearchID(int id,String name,double price,int qty,String date){
        Table T = new Table(1,BorderStyle.UNICODE_BOX_WIDE);
        T.setColumnWidth(0, 20, 30);
        T.addCell("Product: ",center);
        T.addCell("Id: " + id);
        T.addCell("Name: " +name);
        T.addCell("Price: "+price);
        T.addCell("Qty: " +qty);
        T.addCell("Imported Date: "+date);
        System.out.println(price);
        System.out.println(T.render());
    }

    void printUnsavedWrite(StockDAO stockDAO){  
        Table T = new Table(4,BorderStyle.UNICODE_BOX_WIDE,ShownBorders.ALL);
        T.setColumnWidth(0, 10, 20);
        T.setColumnWidth(1, 20, 30);
        T.setColumnWidth(2, 10, 20);
        T.setColumnWidth(3, 10, 20);
        T.addCell("Stock Management",center,5);
        T.addCell("Name",center);
        T.addCell("Unit Price",center);
        T.addCell("QTY",center);
        T.addCell("Imported Date",center);
        if(Main.unsavedWriteList.size()==0){
            T.addCell("No Data",center,4);
        }else{
            for(StockDTO unsaved : Main.unsavedWriteList){
                T.addCell(unsaved.getName(),center);
                T.addCell(""+unsaved.getPrice(),center);
                T.addCell(""+unsaved.getQty(),center);
                T.addCell(unsaved.getDate(),center);
            }
        }
        System.out.println(T.render());
        System.out.print("Press Enter to Continue ...");input.nextLine();
    }
    void printUnsavedUpdate(StockDAO stockDAO){  
        Table T = new Table(4,BorderStyle.UNICODE_BOX_WIDE,ShownBorders.ALL);
        T.setColumnWidth(0, 10, 20);
        T.setColumnWidth(1, 20, 30);
        T.setColumnWidth(2, 10, 20);
        T.setColumnWidth(3, 10, 20);
        T.addCell("Stock Management",center,5);
        T.addCell("Name",center);
        T.addCell("Unit Price",center);
        T.addCell("QTY",center);
        T.addCell("Imported Date",center);
        if(Main.unsavedUpdateList.size()==0){
            T.addCell("No Data",center,4);
        }else{
            for(StockDTO unsaved : Main.unsavedUpdateList){
                T.addCell(unsaved.getName(),center);
                T.addCell(""+unsaved.getPrice(),center);
                T.addCell(""+unsaved.getQty(),center);
                T.addCell(unsaved.getDate(),center);
            }
        }
        System.out.println(T.render());
        System.out.print("Press Enter to Continue ...");input.nextLine();
    }
}
