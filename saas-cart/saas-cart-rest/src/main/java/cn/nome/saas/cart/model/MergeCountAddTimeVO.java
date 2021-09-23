package cn.nome.saas.cart.model;

/**
 * @author chentaikuang
 */
public class MergeCountAddTimeVO {
    private int count;
    private long addTime;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public long getAddTime() {
        return addTime;
    }

    public void setAddTime(long addTime) {
        this.addTime = addTime;
    }

    public MergeCountAddTimeVO(int count, long addTime) {
        this.count = count;
        this.addTime = addTime;
    }
}
