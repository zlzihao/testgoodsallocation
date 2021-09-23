package cn.nome.saas.allocation.model.old.issue;

import java.math.BigDecimal;

/**
 * @author chentaikuang
 */
public class IssueOutStockRemainDo {

    private int ID;
    private int TaskId;
    private String MatCode;
    private String SizeID;
    private BigDecimal StockQty;

    private int Status = 0;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getTaskId() {
        return TaskId;
    }

    public void setTaskId(int taskId) {
        TaskId = taskId;
    }

    public String getMatCode() {
        return MatCode;
    }

    public void setMatCode(String matCode) {
        MatCode = matCode;
    }

    public String getSizeID() {
        return SizeID;
    }

    public void setSizeID(String sizeID) {
        SizeID = sizeID;
    }

    public BigDecimal getStockQty() {
        return StockQty;
    }

    public void setStockQty(BigDecimal stockQty) {
        StockQty = stockQty;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("IssueOutStockRemainDo{");
        sb.append("ID=").append(ID);
        sb.append(", TaskId=").append(TaskId);
        sb.append(", MatCode='").append(MatCode).append('\'');
        sb.append(", SizeID='").append(SizeID).append('\'');
        sb.append(", StockQty=").append(StockQty);
        sb.append(", Status=").append(Status);
        sb.append('}');
        return sb.toString();
    }
}
