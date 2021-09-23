package cn.nome.saas.search.model.vo;

/**
 * @author chentaikuang
 */
public class ProductVo {
    private static final long serialVersionUID = 6380787358901371570L;
    private int id;
    private String name;
    private long minPrice;
    private long maxPrice;
    private String img;

    //折扣价
    private long discPrice;
    private int tagId = 0;

    private int spuStockCount = 0;

    public ProductVo(int id, String name, long maxPrice, long minPrice, String img, int spuStockCount) {
        this.id = id;
        this.name = name;
        this.maxPrice = maxPrice;
        this.minPrice = minPrice;
        this.img = img;
        this.spuStockCount = spuStockCount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(long minPrice) {
        this.minPrice = minPrice;
    }

    public long getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(long maxPrice) {
        this.maxPrice = maxPrice;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public long getDiscPrice() {
        return discPrice;
    }

    public void setDiscPrice(long discPrice) {
        this.discPrice = discPrice;
    }

    public int getTagId() {
        return tagId;
    }

    public void setTagId(int tagId) {
        this.tagId = tagId;
    }

    public int getSpuStockCount() {
        return spuStockCount;
    }

    public void setSpuStockCount(int spuStockCount) {
        this.spuStockCount = spuStockCount;
    }
}
