//package cn.nome.saas.allocation.utils.old.excel;
//
//import cn.nome.platform.common.exception.BusinessException;
//import cn.nome.saas.allocation.model.rule.UploadDetailData;
//import org.apache.poi.openxml4j.opc.OPCPackage;
//import org.apache.poi.ss.usermodel.DataFormatter;
//import org.apache.poi.ss.usermodel.Workbook;
//import org.apache.poi.util.SAXHelper;
//import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
//import org.apache.poi.xssf.eventusermodel.XSSFReader;
//import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
//import org.apache.poi.xssf.model.StylesTable;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//import org.springframework.web.multipart.MultipartFile;
//import org.xml.sax.ContentHandler;
//import org.xml.sax.InputSource;
//import org.xml.sax.SAXException;
//import org.xml.sax.XMLReader;
//
//import javax.xml.parsers.ParserConfigurationException;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.List;
//import java.util.function.Predicate;
//
///**
// * XlsxProcessAbstract
// *
// * @author Bruce01.fan
// * @date 2019/6/13
// */
//public class XlsxProcessUtils {
//
//    //开始读取行数从第0行开始计算
//    private int rowIndex = -1;
//
//    /**
//     * Destination for data
//     */
//    public static <T> List<T> processAllSheet(InputStream inputStream, Integer index, Class<T> clazz, Predicate<T> filter) throws Exception {
//        OPCPackage pkg = OPCPackage.open(inputStream);
//        ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(pkg);
//        XSSFReader xssfReader = new XSSFReader(pkg);
//        StylesTable styles = xssfReader.getStylesTable();
//        SheetToCSV<T> sheetToCSV = new SheetToCSV<T>(clazz);
//        parserSheetXml(styles, strings, sheetToCSV, xssfReader.getSheet("rId"+index));
//        return sheetToCSV.getPojoList(filter);
//    }
//
//
//    /**
//     * 解析excel 转换成xml
//     *
//     * @param styles
//     * @param strings
//     * @param sheetHandler
//     * @param sheetInputStream
//     * @throws IOException
//     * @throws SAXException
//     */
//    public static void parserSheetXml(StylesTable styles, ReadOnlySharedStringsTable strings, XSSFSheetXMLHandler.SheetContentsHandler sheetHandler, InputStream sheetInputStream) throws IOException, SAXException {
//        DataFormatter formatter = new DataFormatter();
//        InputSource sheetSource = new InputSource(sheetInputStream);
//        try {
//            XMLReader sheetParser = SAXHelper.newXMLReader();
//            ContentHandler handler = new XSSFSheetXMLHandler(styles, null, strings, sheetHandler, formatter, false);
//            sheetParser.setContentHandler(handler);
//            sheetParser.parse(sheetSource);
//        } catch (ParserConfigurationException e) {
//            throw new RuntimeException("SAX parser appears to be broken - " + e);
//        }
//    }
//
//    /**
//     * 检验传入的文件是否为表格文件
//     * @param file
//     * @return
//     * @throws IOException
//     */
//    public static Workbook checkFile(MultipartFile file) throws IOException {
//        Workbook wb;
//        String fileName = file.getOriginalFilename();
//        String  extString = fileName.substring(fileName.lastIndexOf("."));
////        if (".xls".equalsIgnoreCase(extString)) {
////            InputStream is = file.getInputStream();
////            wb = new HSSFWorkbook(is);
////        } else
//        if (".xlsx".equalsIgnoreCase(extString)) {
//            InputStream is = file.getInputStream();
//            wb = new XSSFWorkbook(is);
//        } else {
//            throw new BusinessException("10001", "文件类型错误!");
//        }
//        return wb;
//    }
//
//
//    public static void main(String[] args) throws Exception {
//
//        File file = new File("/Users/fanguiming/Documents/work-folders/NOME/智能配补调/导入测试数据/禁配明细模版.xlsx");
//
//        List<UploadDetailData> list =  XlsxProcessUtils.processAllSheet(new FileInputStream(file),1, UploadDetailData.class, data->data.getShopCode()!=null);
//        System.out.println(list.size());
//
//        //Date date = DateUtil.parse("1/5/99","DD/MM/YY");
//
//        //System.out.println(date);
//
//    }
//
//}
