//package cn.nome.saas.allocation.utils.old.excel;
//
//import cn.nome.platform.common.utils.DateUtil;
//import cn.nome.platform.common.utils.excel.ColumnInfo;
//import cn.nome.platform.common.utils.excel.annotation.Column;
//import com.google.common.collect.Lists;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.poi.hssf.util.CellReference;
//import org.apache.poi.ss.util.CellAddress;
//import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
//import org.apache.poi.xssf.usermodel.XSSFComment;
//
//import java.beans.IntrospectionException;
//import java.beans.PropertyDescriptor;
//import java.lang.reflect.Field;
//import java.lang.reflect.InvocationTargetException;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.function.Predicate;
//import java.util.stream.Collectors;
//
///**
// * SheetToCSV
// *
// * @author Bruce01.fan
// * @date 2019/6/13
// */
//public class SheetToCSV<T> implements XSSFSheetXMLHandler.SheetContentsHandler {
//
//    private boolean firstCellOfRow = false;
//    private T pojo;
//    private Class<T> clazz;
//    private int currentRowNumber = -1;
//    private int currentColNumber = -1;
//    private ArrayList<String> keyList = Lists.newArrayList();
//    private List<T> pojoList = Lists.newArrayList();
//
//    public List<T> getPojoList() {
//        return pojoList;
//    }
//
//    public List<T> getPojoList(Predicate<T> fitler) {
//
//        if (pojoList != null) {
//            return pojoList.stream().filter(fitler).collect(Collectors.toList());
//        }
//
//        return pojoList;
//    }
//
//    public SheetToCSV(Class<T> clazz) {
//        this.clazz = clazz;
//        cols = getColumnInfos(clazz);
//    }
//
//    List<ColumnInfo> cols;
//
//
//    @Override
//    public void startRow(int rowNum) {
//        if(rowNum!=0){
//            try {
//                pojo = clazz.newInstance();
//            } catch (InstantiationException e) {
//                e.printStackTrace();
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            }
//        }
//
//        firstCellOfRow = true;
//        currentRowNumber = rowNum;
//        currentColNumber = -1;
//    }
//
//    @Override
//    public void endRow(int rowNum) {
//        if(pojo==null){
//            return;
//        }
//
//        if (currentRowNumber!=0){
//            pojoList.add(pojo);
//        }
//
//    }
//
//    @Override
//    public void cell(String cellReference, String cellValue, XSSFComment comment) {
//
//        if (firstCellOfRow) {
//            firstCellOfRow = false;
//        } else {
//        }
//        if (cellReference == null) {
//            cellReference = new CellAddress(currentRowNumber, currentColNumber).formatAsString();
//        }
//        int thisCol = (new CellReference(cellReference)).getCol();
//
//        currentColNumber = thisCol;
//
//        if (currentRowNumber ==0){
//            keyList.add(cellValue);
//            return;
//        }
//        if (pojo == null|| StringUtils.isBlank(cellValue)) {
//            return;
//        }
//
//        String columnName = keyList.get(currentColNumber);
//
//        for (ColumnInfo column : cols) {
//            //System.out.println(columnName+"--" +column.getColumn().value());
//            if (columnName.equals(column.getColumn().value())) {
//                try {
//                    Class<?> cls = Class.forName(column.getType().getTypeName());
//
//                    Date valueDate = null;
//                    if (Date.class.isAssignableFrom(cls)) {
//                        if (cellValue.indexOf("/") > 0) {
//                            // 处理日期格式：1/5/99
//                            String[] values = cellValue.split("/");
//                            // 格式一：1/5/99
//                            if (values[0].length() == 1) {
//                                String month = values[0];
//                                String day = values[1];
//                                String year = values[2];
//                                if (year.length() == 2) {
//                                    year = "20" + year;
//                                }
//                                cellValue = year + "-" + month + "-" + day;
//                            } else if (values[0].length() == 4) {
//                                String year = values[0];
//                                String month = values[1];
//                                String day = values[2];
//                                cellValue = year + "-" + month + "-" + day;
//                            }
//                        }
//                        valueDate =  DateUtil.parse(cellValue, DateUtil.DATE_ONLY);
//                    }
//
//                    PropertyDescriptor pd = new PropertyDescriptor(column.getFieldName(), clazz);
//                    try {
//                        pd.getWriteMethod().invoke(pojo, valueDate != null ? valueDate : cellValue);
//                    } catch (IllegalAccessException e) {
//                        e.printStackTrace();
//                    } catch (InvocationTargetException e) {
//                        e.printStackTrace();
//                    }
//                } catch (IntrospectionException e) {
//                    e.printStackTrace();
//                } catch (ClassNotFoundException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }
//
//
//
//    }
//
//    private static List<ColumnInfo> getColumnInfos(Class<?> clazz) {
//        List<ColumnInfo> cols = new ArrayList<>();
//
//        for (Field field : clazz.getDeclaredFields()) {
//            Column column = field.getAnnotation(Column.class);
//            if (column != null) {
//                cols.add(getExcelCell(field, column));
//            }
//        }
//        return cols;
//    }
//
//    private static ColumnInfo getExcelCell(Field field, Column c) {
//        ColumnInfo columnInfo = new ColumnInfo();
//
//        columnInfo.setFieldName(field.getName());
//        columnInfo.setType(field.getType());
//
//        columnInfo.setColumn(c);
//        return columnInfo;
//    }
//
//    @Override
//    public void headerFooter(String text, boolean isHeader, String tagName) {
//    }
//
//}
