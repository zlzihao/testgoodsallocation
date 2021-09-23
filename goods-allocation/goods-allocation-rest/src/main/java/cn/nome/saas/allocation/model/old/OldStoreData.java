package cn.nome.saas.allocation.model.old;

import cn.nome.platform.common.utils.ToString;

import java.util.Date;

public class OldStoreData extends ToString {

	private static final long serialVersionUID = 3636152291308741250L;

    private String shopid;
    private String shop_name;
    private String location;
    private float store_acreage;
    private float store_rent;
    private String store_aim;
    private String business_nature;
    private String open_quarter;
    private String store_type;
    private String business_type;
    private String site_floor;
    private int open_monma;
    private float entrance_distance;
    private String site_type;
    private float forecast_sale;
    private float target_sale;
    private float gap_sale;
    private float gap_pre;
    private Date open_shop_date;
    private String open_shop_date_string;
    private int open_shop_duration;
    private float annual_sales_amount;
    private int total_people_flow;
    private int score;

	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public String getShop_name() {
		return shop_name;
	}
	public void setShop_name(String shop_name) {
		this.shop_name = shop_name;
	}
	public String getShopid() {
		return shopid;
	}
	public void setShopid(String shopid) {
		this.shopid = shopid;
	}
	public String getOpen_shop_date_string() {
		return open_shop_date_string;
	}
	public void setOpen_shop_date_string(String open_shop_date_string) {
		this.open_shop_date_string = open_shop_date_string;
	}
	public float getAnnual_sales_amount() {
		return annual_sales_amount;
	}
	public void setAnnual_sales_amount(float annual_sales_amount) {
		this.annual_sales_amount = annual_sales_amount;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public float getStore_acreage() {
		return store_acreage;
	}
	public void setStore_acreage(float store_acreage) {
		this.store_acreage = store_acreage;
	}
	public float getStore_rent() {
		return store_rent;
	}
	public void setStore_rent(float store_rent) {
		this.store_rent = store_rent;
	}
	public String getStore_aim() {
		return store_aim;
	}
	public void setStore_aim(String store_aim) {
		this.store_aim = store_aim;
	}
	public String getBusiness_nature() {
		return business_nature;
	}
	public void setBusiness_nature(String business_nature) {
		this.business_nature = business_nature;
	}
	public String getOpen_quarter() {
		return open_quarter;
	}
	public void setOpen_quarter(String open_quarter) {
		this.open_quarter = open_quarter;
	}
	public String getStore_type() {
		return store_type;
	}
	public void setStore_type(String store_type) {
		this.store_type = store_type;
	}
	public String getBusiness_type() {
		return business_type;
	}
	public void setBusiness_type(String business_type) {
		this.business_type = business_type;
	}
	public String getSite_floor() {
		return site_floor;
	}
	public void setSite_floor(String site_floor) {
		this.site_floor = site_floor;
	}
	public int getOpen_monma() {
		return open_monma;
	}
	public void setOpen_monma(int open_monma) {
		this.open_monma = open_monma;
	}
	public float getEntrance_distance() {
		return entrance_distance;
	}
	public void setEntrance_distance(float entrance_distance) {
		this.entrance_distance = entrance_distance;
	}
	public String getSite_type() {
		return site_type;
	}
	public void setSite_type(String site_type) {
		this.site_type = site_type;
	}
	public float getForecast_sale() {
		return forecast_sale;
	}
	public void setForecast_sale(float forecast_sale) {
		this.forecast_sale = forecast_sale;
	}
	public float getTarget_sale() {
		return target_sale;
	}
	public void setTarget_sale(float target_sale) {
		this.target_sale = target_sale;
	}
	public float getGap_sale() {
		return gap_sale;
	}
	public void setGap_sale(float gap_sale) {
		this.gap_sale = gap_sale;
	}
	public float getGap_pre() {
		return gap_pre;
	}
	public void setGap_pre(float gap_pre) {
		this.gap_pre = gap_pre;
	}
	public Date getOpen_shop_date() {
		return open_shop_date;
	}
	public void setOpen_shop_date(Date open_shop_date) {
		this.open_shop_date = open_shop_date;
	}
	public int getOpen_shop_duration() {
		return open_shop_duration;
	}
	public void setOpen_shop_duration(int open_shop_duration) {
		this.open_shop_duration = open_shop_duration;
	}
	public int getTotal_people_flow() {
		return total_people_flow;
	}
	public void setTotal_people_flow(int total_people_flow) {
		this.total_people_flow = total_people_flow;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}