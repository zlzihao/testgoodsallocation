package cn.nome.saas.allocation.model.issue;

public class MinDisplaySkcData {

    private Integer Type;
    private String TypeValue;
    private String TypeName;
    private Integer Qty;

    public Integer getQty() {
        return Qty;
    }

    public void setQty(Integer qty) {
        this.Qty = qty;
    }

    public Integer getType() {
        return Type;
    }

    public void setType(Integer type) {
        Type = type;
    }

    public String getTypeValue() {
        return TypeValue;
    }

    public void setTypeValue(String typeValue) {
        this.TypeValue = typeValue;
    }

    public String getTypeName() {
        return TypeName;
    }

    public void setTypeName(String typeName) {
        this.TypeName = typeName;
    }

    public enum SkcType {
        TYPE_1(1, "小类"), TYPE_2(2, "skc");
        /**
         * 状态
         */
        private int type;
        /**
         * //状态名
         */
        private String typeName;

        public int getType() {
            return type;
        }

        public String getTypeName() {
            return typeName;
        }

        public static String getTypeName(int type) {
            for (SkcType ftype : SkcType.values()) {
                if (type == ftype.getType()) {
                    return ftype.typeName;
                }
            }
            return "";
        }
        public static int getType(String typeName) {
            for (SkcType ftype : SkcType.values()) {
                if (typeName.equals(ftype.getTypeName())) {
                    return ftype.type;
                }
            }
            return 0;
        }


        SkcType(int type, String typeName) {
            this.type = type;
            this.typeName = typeName;
        }

    }
}
