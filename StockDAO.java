import java.sql.*;
import java.sql.SQLException;

interface StockDAO {
    void getAll(String table) throws SQLException;
    int getCount(String table) throws SQLException;
    void addProduct(StockDTO stockDTO,String table) throws SQLException;
    void updateProduct(int id,StockDTO stockDTO,String table) throws SQLException;
    void deleteProduct(int id) throws SQLException;
    void readData(int id) throws SQLException;
    void deleteUnsavedTableAfterSave(String table) throws SQLException;
}

class StockDOAImp implements StockDAO{
    public void getAll(String table) throws SQLException{
        Connection con = Main.connectionToDB();
        PreparedStatement ps = con.prepareStatement("SELECT * FROM "+table+" ORDER BY id ASC");
        ResultSet rs = ps.executeQuery();

        StockDTO stockDTO = null;
        while(rs.next()){
            int id = rs.getInt("id");
            String  name = rs.getString("name");
            double price = rs.getDouble("price");
            int qty = rs.getInt("qty");
            String date = rs.getString("imported_date");

            stockDTO = new StockDTO(id, name, price, qty, date);
            if(table.equals("stock_tb")){
                Main.stockList.add(stockDTO);
            }else if(table.equals("unsaved_write")){
                Main.unsavedWriteList.add(stockDTO);
            }else if(table.equals("unsaved_update_tb")){
                Main.unsavedUpdateList.add(stockDTO);
            }

        }
        con.close();
        ps.close();
    }
    public int getCount(String table) throws SQLException {
        Connection con = Main.connectionToDB();
        PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM "+table);
        ResultSet rs = ps.executeQuery();
        int count = 0;
        while(rs.next()) {
            count = rs.getInt(1);
        }
        con.close();
        ps.close();
        return count;
    }
    public void addProduct(StockDTO stockDTO,String table) throws SQLException{
        Connection con = Main.connectionToDB();
        String sql;
        PreparedStatement ps = null;
        if(table.equals("unsaved_write")){
            ps = con.prepareStatement("INSERT INTO "+table+"(id,name,price,qty) VALUES(?,?,?,?)");
            ps.setInt(1,stockDTO.getId());
            ps.setString(2,stockDTO.getName());
            ps.setDouble(3,stockDTO.getPrice());
            ps.setInt(4,stockDTO.getQty());
        }else if(table.equals("stock_tb")){
            ps = con.prepareStatement("INSERT INTO "+table+"(name,price,qty) VALUES(?,?,?)");
            ps.setString(1,stockDTO.getName());
            ps.setDouble(2,stockDTO.getPrice());
            ps.setInt(3,stockDTO.getQty());
        }
        
        ps.executeUpdate();
        con.close();
        ps.close();
    }
    public void updateProduct(int id , StockDTO stockDTO,String table) throws SQLException{
        Connection con = Main.connectionToDB();
        PreparedStatement ps=null;
        if(table.equals("stock_tb")){
            String sql = "UPDATE "+table+" SET name = ?,price = ?, qty=? WHERE id=?";
            ps = con.prepareStatement(sql);
            ps.setString(1,stockDTO.getName());
            ps.setDouble(2,stockDTO.getPrice());
            ps.setInt(3,stockDTO.getQty());
            ps.setInt(4, id);
        }else if(table.equals("unsaved_update_tb")){
            ps = con.prepareStatement("INSERT INTO "+table+"(id,name,price,qty) VALUES(?,?,?,?)");
            ps.setInt(1,stockDTO.getId());
            ps.setString(2,stockDTO.getName());
            ps.setDouble(3,stockDTO.getPrice());
            ps.setInt(4,stockDTO.getQty());
        }
        ps.executeUpdate();
        con.close();
        ps.close();
    }

    public void deleteProduct(int id) throws SQLException{
        Connection con = Main.connectionToDB();
        String sql = "DELETE FROM stock_tb WHERE id=?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
        con.close();
        ps.close();
    }

    public void readData(int id) throws SQLException{
        Connection con = Main.connectionToDB();
        PreparedStatement ps = con.prepareStatement("SELECT * FROM stock_tb where id = ?");
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            int oid = rs.getInt("id");
            String name= rs.getString("name");
            double price= rs.getDouble("price");
            int qty = rs.getInt("qty");
            String date= rs.getString("imported_date");
            StockView.printSearchID(oid, name, price, qty, date);
        }
        con.close();
        ps.close();
    }
    public void deleteUnsavedTableAfterSave(String table) throws SQLException{
        Connection con = Main.connectionToDB();
        PreparedStatement ps = con.prepareStatement("DELETE FROM "+table);
        ps.executeUpdate();
        con.close();
        ps.close();
    }
}
