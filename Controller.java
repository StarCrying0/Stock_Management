import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

class Controller {
    StockDAO stockDAO;
    StockView stockView;
    Controller(StockDAO stockDAO, StockView stockView) {
        this.stockDAO = stockDAO;
        this.stockView=stockView;
    }

    void showAllData() throws SQLException, IOException{
        stockDAO.getAll("stock_tb");
        Main.keep=Main.auto();
        stockView.printStockDetails(stockDAO);
    }
    void getCount() throws SQLException{
        System.out.println(stockDAO.getCount("stock_tb"));
    }

    void insertProductToUnsavedWriteTB(StockDTO stockDTO) throws SQLException{
        stockDAO.addProduct(stockDTO, "unsaved_write");
    }
    void updateProductToUnsavedUpdate(int id,StockDTO stockDTO) throws SQLException{
        stockDAO.updateProduct(id, stockDTO,"unsaved_update_tb");
    }

    void deleteProduct(int id) throws SQLException{
        stockDAO.deleteProduct(id);
    }

    void searchProduct(){
        Main.Search();
    }
    void showAllUnsavedWriteData() throws SQLException, FileNotFoundException{
        Main.unsavedWriteList.removeAll(Main.unsavedWriteList);
        stockDAO.getAll("unsaved_write");
        stockView.printUnsavedWrite(stockDAO);
        //stockDAO.deleteUnsavedTableAfterSave("unsaved_write");
    }
    void showAllUnsavedUpdateData() throws SQLException, FileNotFoundException{
        Main.unsavedUpdateList.removeAll(Main.unsavedUpdateList);
        stockDAO.getAll("unsaved_update_tb");
        stockView.printUnsavedUpdate(stockDAO);
        //stockDAO.deleteUnsavedTableAfterSave("unsaved_write");
    }

    void savedWriteProduct(StockDTO stockDTO) throws SQLException{
        stockDAO.addProduct(stockDTO, "stock_tb");
    }
    void savedUpdateProduct(int id,StockDTO stockDTO) throws SQLException{
        stockDAO.updateProduct(id, stockDTO, "stock_tb");
    }
}
