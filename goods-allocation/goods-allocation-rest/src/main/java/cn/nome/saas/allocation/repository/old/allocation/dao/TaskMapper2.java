package cn.nome.saas.allocation.repository.old.allocation.dao;

import cn.nome.platform.common.utils.Page;
import cn.nome.saas.allocation.model.old.allocation.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TaskMapper2 {

	int create_task(String task_name, int task_type, int clothing_period, int commodity_period, String run_time,
                    String user_id, String user_name, int right_now, int area_type, String in_shop_ids, String out_shop_ids,
                    int allocation_type, int days);

	List<Task> get_task_list(@Param("page") Page page);

	int select_task_num();

	List<Task> get_running_task_list();

	List<TaskStore2> get_task_store_list2(int task_id, String area_code, int price_threshold, int quantity_threshold,
										  String year, String season);

	List<TaskStore> get_task_input_store_list(int task_id, String area_code, int price_threshold,
											  int quantity_threshold, String year, String season);

	List<TaskStore> get_task_output_store_list(int task_id, String area_code, int price_threshold,
                                               int quantity_threshold, String year, String season);

	List<TaskStoreCommodity> get_task_input_store_commodity_list(int task_id, List<String> list, String year,
                                                                 String season, @Param("page") Page page);

	List<TaskStoreCommodity> get_task_output_store_commodity_list(int task_id, List<String> list, String year,
																  String season, @Param("page") Page page);

	int select_task_input_store_commodity_num(int task_id, List<String> list, String year, String season);

	int select_task_output_store_commodity_num(int task_id, List<String> list, String year, String season);

	List<TaskStoreCommodity> get_task_store_pair_commodity_list(int task_id, String inshop_id, String outshop_id,
                                                                String year, String season);

	List<Task> get_task(int task_id);

	void cancel_task(int task_id);

	List<Task> get_need_to_run_task_list();

	Task get_task_by_id(int task_id);

	int update_running_task(int task_id);

	int update_finish_task(int task_id);

	int create_task_progress(@Param("task_id") int task_id, @Param("progress") int progress,
                             @Param("minute") int minute, @Param("message") String message);

	List<DBArea> get_area_list();

	List<Store> get_store_list();

	List<String> getShopIds(List<String> list);
}