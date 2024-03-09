import java.sql.SQLException;

class Controller {
    StockDAO stockDAO;
    StockView stockView;
    Controller(StockDAO stockDAO, StockView stockView) {
        this.stockDAO = stockDAO;
        this.stockView=stockView;
    }

    void showAllData() {
        try {
            stockDAO.getAll("stock_tb");
        } catch (SQLException e) {
            System.out.println(Main.ANSI_RED+e.getMessage()+Main.ANSI_RESET);
        }
        Main.keep=Main.auto();
        stockView.printStockDetails(stockDAO);
    }
    void getCount(){
        try {
            System.out.println(stockDAO.getCount("stock_tb"));
        } catch (SQLException e) {
            System.out.println(Main.ANSI_RED+e.getMessage()+Main.ANSI_RESET);
        }
    }

    void insertProductToUnsavedWriteTB(StockDTO stockDTO){
        try {
            stockDAO.addProduct(stockDTO, "unsaved_write_tb");
        } catch (SQLException e) {
            System.out.println(Main.ANSI_RED+e.getMessage()+Main.ANSI_RESET);
        }
    }
    void updateProductToUnsavedUpdate(int id,StockDTO stockDTO){
        try {
            stockDAO.updateProduct(id, stockDTO,"unsaved_update_tb");
        } catch (SQLException e) {
            System.out.println(Main.ANSI_RED+e.getMessage()+Main.ANSI_RESET);
        }
    }

    void deleteProduct(int id){
        try {
            stockDAO.readData(id);
        } catch (SQLException e) {
            System.out.println(Main.ANSI_RED+e.getMessage()+Main.ANSI_RESET);
        }
        Main.checkConfirmationOnDelete(id);
    }

    void searchProduct(){
        Main.search();
    }
    void showAllUnsavedWriteData(){
        Main.unsavedWriteList.removeAll(Main.unsavedWriteList);
        try {
            stockDAO.getAll("unsaved_write_tb");
        } catch (SQLException e) {
            System.out.println(Main.ANSI_RED+e.getMessage()+Main.ANSI_RESET);
        }
        stockView.printUnsavedWrite(stockDAO);
        //stockDAO.deleteUnsavedTableAfterSave("unsaved_write_tb");
    }
    void showAllUnsavedUpdateData(){
        Main.unsavedUpdateList.removeAll(Main.unsavedUpdateList);
        try {
            stockDAO.getAll("unsaved_update_tb");
        } catch (SQLException e) {
            System.out.println(Main.ANSI_RED+e.getMessage()+Main.ANSI_RESET);
        }
        stockView.printUnsavedUpdate(stockDAO);
        //stockDAO.deleteUnsavedTableAfterSave("unsaved_write_tb");
    }

    void savedWriteProduct(StockDTO stockDTO){
        try {
            stockDAO.addProduct(stockDTO, "stock_tb");
        } catch (SQLException e) {
            System.out.println(Main.ANSI_RED+e.getMessage()+Main.ANSI_RESET);
        }
    }
    void savedUpdateProduct(int id,StockDTO stockDTO){
        try {
            stockDAO.updateProduct(id, stockDTO, "stock_tb");
        } catch (SQLException e) {
            System.out.println(Main.ANSI_RED+e.getMessage()+Main.ANSI_RESET);
        }
    }
}
