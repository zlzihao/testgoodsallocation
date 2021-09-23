//package cn.nome.saas.allocation.repository.old.dao;
//
//import cn.nome.saas.allocation.model.old.allocation.Shop;
//import org.apache.ibatis.annotations.Param;
//
//import javax.naming.directory.SearchResult;
//import java.util.List;
//
//public interface CrawlerMapper {
//
//    List<String> get_word_cloud(String keyword);
//    List<SearchTask> get_search_task(String keyword);
//    List<SearchTask> get_search_task_list();
//
//    List<FinishedTask> get_finished_task(String keyword);
//    List<FinishedTask> get_finished_task_list1();
//    List<FinishedTask> get_finished_task_list2();
//
//	int create_search_task(String keyword, String userid, String email);
//	int cancel_search_task(String keyword, String userid);
//
//	List<SearchResult> get_search_result(String keyword, String crawl_time, int count);
//	List<String> get_search_timestamp_for_keyword(String keyword);
//
//    List<CategoryStats> getCategoryStatsList(@Param("req") CrawlerReq req);
//    List<Shop> getDateList(@Param("req") CrawlerReq req);
//    List<PriceZone> getPriceZoneList(@Param("req") CrawlerReq req);
//    List<TopSale> getTopSaleGoods(@Param("req") CrawlerReq req);
//    List<Shop> getShopList(@Param("req") CrawlerReq req);
//}