package cn.nome.saas.allocation.rest.admin;

import cn.nome.platform.common.exception.BusinessException;
import cn.nome.platform.common.logger.LoggerUtil;
import cn.nome.platform.common.web.controller.protocol.Result;
import cn.nome.platform.common.web.controller.protocol.ResultUtil;
import cn.nome.saas.allocation.model.allocation.TemplateInfoView;
import cn.nome.saas.allocation.service.allocation.CommonImportService;
import cn.nome.saas.allocation.service.allocation.ImportExportService;
import cn.nome.saas.allocation.service.old.allocation.impl.ImportExportService2;
import cn.nome.saas.allocation.utils.ExcelUtil;
import cn.nome.saas.allocation.utils.old.ExcelUtil2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/allocation")
public class DownloadController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Value("${download.xls.template.dir}")
    private String DOWNLOAD_TEMPLATE_DIR;

    @Value("${download.xls.template.display_category}")
    private String DISPLAY_CATEGORY_TEMPLATE_FILE_NAME;

    @Value("${download.xls.template.display_goods}")
    private String DISPLAY_GOODS_TEMPLATE_FILE_NAME;

    @Value("${download.xls.template.shop_display}")
    private String SHOP_DISPLAY_TEMPLATE_FILE_NAME;

    @Value("${download.xls.template.shop_info}")
    private String SHOP_INFO_TEMPLATE_FILE_NAME;

    @Value("${download.xls.template.forbidden_import}")
    private String FORBIDDEN_IMPORT_TEMPLATE_FILE_NAME;
    @Value("${download.xls.template.security_import}")
    private String SECURITY_IMPORT_TEMPLATE_FILE_NAME;
    @Value("${download.xls.template.new_goods_range_import}")
    private String NEW_GOODS_RANGE_IMPORT_TEMPLATE_FILE_NAME;
    @Value("${download.xls.template.new_goods_invalid_import}")
    private String NEW_GOODS_INVALID_IMPORT_TEMPLATE_FILE_NAME;
    @Value("${download.xls.template.forbidden_desc}")
    private String FORBIDDEN_DESC_TEMPLATE_FILE_NAME;
    @Value("${download.xls.template.forbidden_batch_del}")
    private String FORBIDDEN_BATCH_DEL_TEMP_FILE_NAME;
    @Value("${upload.xls.forbidden.dir}")
    private String UPLOAD_XLS_FORBIDDEN_DIR;

    @Autowired
    private ImportExportService importExportService;

    @Autowired
    private ImportExportService2 importExportService2;

    @Autowired
    private CommonImportService commonImportService;

    private final int RULE_FILE_TYPE_FORBIDDEN_APPLY = 11;
    private final int RULE_FILE_TYPE_FORBIDDEN_EXCLUDE = 12;
    private final int RULE_FILE_TYPE_SECURITY_APPLY = 21;
    private final int RULE_FILE_TYPE_SECURITY_EXCLUDE = 22;
    private final int RULE_FILE_TYPE_WHITELIST_APPLY = 31;
    private final int RULE_FILE_TYPE_WHITELIST_EXCLUDE = 32;
    private final int RULE_FILE_TYPE_SECURITY_WHITELIST_APPLY = 41;
    private final int RULE_FILE_TYPE_SECURITY_WHITELIST_EXCLUDE = 42;

    //------------------ 模版下载 -----------------------
    //门店信息模版
    @GetMapping(value = "/shopInfoTemplate")
    public String shopInfoTemplate(HttpServletRequest request, HttpServletResponse response) {
        String tempalteDir = DOWNLOAD_TEMPLATE_DIR + SHOP_INFO_TEMPLATE_FILE_NAME;
        ExcelUtil2.downloadTemplate(DOWNLOAD_TEMPLATE_DIR, SHOP_INFO_TEMPLATE_FILE_NAME, request, response);
        logger.info("{} download over", tempalteDir);
        return "886";
    }

    //门店陈列模版
    @GetMapping(value = "/shopDisplayTemplate")
    public String shopDisplayTemplate(HttpServletRequest request, HttpServletResponse response) {
        String tempalteDir = DOWNLOAD_TEMPLATE_DIR + SHOP_DISPLAY_TEMPLATE_FILE_NAME;
        ExcelUtil2.downloadTemplate(DOWNLOAD_TEMPLATE_DIR, SHOP_DISPLAY_TEMPLATE_FILE_NAME, request, response);
        logger.info("{} download over", tempalteDir);
        return "886";
    }

    //陈列分类模版
    @GetMapping(value = "/displayCategoryTemplate")
    public String displayCategoryTemplate(HttpServletRequest request, HttpServletResponse response) {
        String tempalteDir = DOWNLOAD_TEMPLATE_DIR + DISPLAY_CATEGORY_TEMPLATE_FILE_NAME;
        ExcelUtil2.downloadTemplate(DOWNLOAD_TEMPLATE_DIR, DISPLAY_CATEGORY_TEMPLATE_FILE_NAME, request, response);
        logger.info("{} download over", tempalteDir);
        return "886";
    }

    //陈列商品模版
    @GetMapping(value = "/displayGoodsTemplate")
    public String displayGoodsTemplate(HttpServletRequest request, HttpServletResponse response) {
        String tempalteDir = DOWNLOAD_TEMPLATE_DIR + DISPLAY_GOODS_TEMPLATE_FILE_NAME;
        ExcelUtil2.downloadTemplate(DOWNLOAD_TEMPLATE_DIR, DISPLAY_GOODS_TEMPLATE_FILE_NAME, request, response);
        logger.info("{} download over", tempalteDir);
        return "886";
    }

    //禁配明细导入模版
    @GetMapping(value = "/forbiddenDescTemplate")
    public void forbiddenDescTemplate(HttpServletRequest request, HttpServletResponse response) {
        ExcelUtil.downloadTemplate(DOWNLOAD_TEMPLATE_DIR, FORBIDDEN_DESC_TEMPLATE_FILE_NAME, request, response);
        logger.info("{} download over", FORBIDDEN_DESC_TEMPLATE_FILE_NAME);
    }

    //禁配明细导入模版
    @GetMapping(value = "/forbiddenBatchDelTemp")
    public void forbiddenBatchDelTemp(HttpServletRequest request, HttpServletResponse response) {
        ExcelUtil.downloadTemplate(DOWNLOAD_TEMPLATE_DIR, FORBIDDEN_BATCH_DEL_TEMP_FILE_NAME, request, response);
        logger.info("{} download over", FORBIDDEN_BATCH_DEL_TEMP_FILE_NAME);
    }

    //禁配导入模版
    @GetMapping(value = "/forbiddenImportTemp")
    public String forbiddenImportTemp(HttpServletRequest request, HttpServletResponse response) {
        ExcelUtil.downloadTemplate(DOWNLOAD_TEMPLATE_DIR, FORBIDDEN_IMPORT_TEMPLATE_FILE_NAME, request, response);
        logger.info("{} download over", FORBIDDEN_IMPORT_TEMPLATE_FILE_NAME);
        return "886";
    }

    //保底导入模版
    @GetMapping(value = "/securityImportTemp")
    public String securityImportTemp(HttpServletRequest request, HttpServletResponse response) {
        ExcelUtil.downloadTemplate(DOWNLOAD_TEMPLATE_DIR, SECURITY_IMPORT_TEMPLATE_FILE_NAME, request, response);
        logger.info("{} download over", SECURITY_IMPORT_TEMPLATE_FILE_NAME);
        return "886";
    }

    //新品商品范围导入模板
    @GetMapping(value = "/newGoodsRangeImportTemp")
    public String newGoodsIssueRangeImportTemp(HttpServletRequest request, HttpServletResponse response) {
        ExcelUtil.downloadTemplate(DOWNLOAD_TEMPLATE_DIR, NEW_GOODS_RANGE_IMPORT_TEMPLATE_FILE_NAME, request, response);
        logger.info("{} download over", NEW_GOODS_RANGE_IMPORT_TEMPLATE_FILE_NAME);
        return "886";
    }
    //新品范围失效时间导入模板
    @GetMapping(value = "/newGoodsInvalidImportTemp")
    public String newGoodsIssueInvalidImportTemp(HttpServletRequest request, HttpServletResponse response) {
        ExcelUtil.downloadTemplate(DOWNLOAD_TEMPLATE_DIR, NEW_GOODS_INVALID_IMPORT_TEMPLATE_FILE_NAME, request, response);
        logger.info("{} download over", NEW_GOODS_INVALID_IMPORT_TEMPLATE_FILE_NAME);
        return "886";
    }

    //禁配管理下载
    @GetMapping(value = "/globalConfExlExport")
    public Result forbiddenSkuTemplate(HttpServletRequest request, HttpServletResponse response,
                                       @RequestParam("ruleName") String ruleName, @RequestParam("startDate") String startDate,
                                       @RequestParam("endDate") String endDate, @RequestParam("type") Integer type) {
        String typeName;
        Boolean syncFileFlag = false;
        if (type == RULE_FILE_TYPE_FORBIDDEN_APPLY) {
            typeName = "FORBIDDEN_APPLY";
        } else if (type == RULE_FILE_TYPE_FORBIDDEN_EXCLUDE) {
            typeName = "FORBIDDEN_EXCLUDE";
        } else if (type == RULE_FILE_TYPE_SECURITY_APPLY) {
            typeName = "SECURITY_APPLY";
        } else if (type == RULE_FILE_TYPE_SECURITY_EXCLUDE) {
            typeName = "SECURITY_EXCLUDE";
        } else if (type == RULE_FILE_TYPE_WHITELIST_APPLY) {
            typeName = "WHITELIST_APPLY";
        } else if (type == RULE_FILE_TYPE_WHITELIST_EXCLUDE) {
            typeName = "WHITELIST_EXCLUDE";
        } else if (type == RULE_FILE_TYPE_SECURITY_WHITELIST_APPLY) {
            typeName = "SECURITY_APPLY";
            ruleName = ruleName.replaceAll("由“", "").replaceAll("”生成的白名单", "");
            syncFileFlag = true;
        } else if (type == RULE_FILE_TYPE_SECURITY_WHITELIST_EXCLUDE) {
            typeName = "SECURITY_EXCLUDE";
            ruleName = ruleName.replaceAll("由“", "").replaceAll("”生成的白名单", "");
            syncFileFlag = true;
        } else {
            throw new BusinessException("12000", "无下载的类型");
        }
        String tips;
        try {
            tips = ExcelUtil.downloadFile(UPLOAD_XLS_FORBIDDEN_DIR, ruleName, startDate, endDate.trim(), typeName, syncFileFlag, request, response);
//            logger.info("{} download over", FORBIDDEN_SKU_TEMPLATE_FILE_NAME);
        } catch (FileNotFoundException e) {
            throw new BusinessException("12000", "无对应文档可以导出, 请确认");
        }
        return ResultUtil.handleSuccessReturn(tips);
    }

    //--------------------- 数据导出 ----------------------------

    //陈列分类导出
    @GetMapping(value = "/exportDisplayCategory")
    public String exportDisplayCategory(HttpServletRequest request, HttpServletResponse response) throws Exception {
        importExportService.exportDisplayCategory(request, response);
        return "886";
    }

    //陈列商品导出
    @GetMapping(value = "/exportDisplayGoods")
    public String exportDisplayGoods(HttpServletRequest request, HttpServletResponse response) throws Exception {
        importExportService.exportDisplayGoods(request, response);
        return "886";
    }

    //门店陈列导出
    @GetMapping(value = "/exportShopDisplay")
    public String exportShopDisplay(HttpServletRequest request, HttpServletResponse response) throws Exception {
        importExportService.exportShopDisplay(request, response);
        return "886";
    }

    //门店信息导出
    @GetMapping(value = "/exportShopInfo")
    public String exportShopInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
        importExportService.exportShopInfo(request, response);
        return "886";
    }

    //--------------------- 数据导入 ----------------------------

    //陈列分类导入
    @PostMapping(value = "/importDisplayCategory")
    public Result importDisplayCategory(@RequestParam("file") MultipartFile file) throws Exception {
        return importExportService.importDisplayCategory(file);
    }

    //陈列商品导入
    @PostMapping(value = "/importDisplayGoods")
    public Result importDisplayGoods(@RequestParam("file") MultipartFile file) throws Exception {
        return importExportService.importDisplayGoods(file);
    }

    //店铺陈列导入
    @PostMapping(value = "/importShopDisplay")
    public Result importShopDisplay(@RequestParam("file") MultipartFile file) throws Exception {
        return importExportService.importShopDisplay(file);
    }

    //店铺信息导入
    @PostMapping(value = "/importShopInfo")
    public Result importShopInfo(@RequestParam("file") MultipartFile file) throws Exception {
        return importExportService.importShopInfo(file);
    }


    //---------------------add by laidang 公共处理 start ----------------------------
    //业务类型列表
    @GetMapping(value = "/businessList")
    public Result businessList() {
        return commonImportService.businessList();
    }

    //模板列表
    @GetMapping(value = "/templateList")
    public Result templateList(HttpServletRequest request) {
        return commonImportService.templateList(request);
    }

    //公共模板下载
    @GetMapping(value = "/downTemplate")
    public String downTemplate(HttpServletRequest request, HttpServletResponse response) {
        return commonImportService.downloadTemplate(request,response);
    }

    //公共导入处理
    @PostMapping(value = "/commonImport")
    public Result commonImport(@RequestParam("file") MultipartFile file,HttpServletRequest request) throws Exception {
        return commonImportService.importData(file,request);
    }

    //公共导出处理
    @GetMapping(value = "/commonExport")
    public String commonExport(HttpServletRequest request, HttpServletResponse response) {
        return commonImportService.commonExport(request,response);
    }
    //---------------------add by laidang 公共处理 end ----------------------------
}
