package cn.nome.saas.sdc.bigData.repository.entity;

/**
 * @author hejiongyu@nome.com
 */
public class DwsViewWarehouseInfoDO {
    private String stockCode;
    private String stockName;
    private String address;

    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
