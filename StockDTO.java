    class StockDTO {
        private int id;
        private String name;
        private double price;
        private int qty;
        private String date;
        
        StockDTO(int id,String name,double price,int qty,String date){
            this.id=id;
            this.name = name;
            this.price = price;
            this.qty=qty;
            this.date=date;
        }

        int getId(){
            return id;
        }
        String getName(){
            return name;
        }
        double getPrice(){
            return price;
        }
        int getQty(){
            return qty;
        }
        String getDate(){
            return date;
        }

        public String toString(){
            return "Id: " + getId() + "\tname: " + getName() + "\tprice: " + getPrice() + "\tqty: " + getQty() + "\tDate: " + getDate();
        }
    }
