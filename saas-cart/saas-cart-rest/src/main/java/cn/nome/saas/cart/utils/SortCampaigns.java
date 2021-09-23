package cn.nome.saas.cart.utils;

import java.util.Comparator;

import cn.nome.saas.cart.feign.CampaignModel;

/**
 * 满n减优惠活动排序
 *
 * @author chentaikuang
 */
public class SortCampaigns implements Comparator<CampaignModel> {

	/**
	 * 规则： 1）满n(SubType)减一(CampaignType)，满n越小则优先返回,减CampaignType越大则优先返回
	 * 2）开始时间比较比较晚，或结束时间比较早，则优先返回
	 *
	 * @param o1
	 * @param o2
	 * @return
	 */
	@Override
	public int compare(CampaignModel o1, CampaignModel o2) {

		if (!o1.getSubType().equals(o2.getSubType())) {
			return o1.getSubType() - o2.getSubType();
		}

		if (!o1.getCampaignType().equals(o2.getCampaignType())) {
			return o2.getCampaignType() - o1.getCampaignType();
		}

		if (o1.getStartTime().after(o2.getStartTime()) || o1.getEndTime().before(o2.getEndTime())) {
			return 1;
		}
		return 0;
	}

//	public static void main(String[] args) {
//		CampaignModel o1 = new CampaignModel();
//		o1.setCampaignType(2);
//		o1.setSubType(2);
//		o1.setStartTime(new Date());
//		o1.setEndTime(new Date());
//
//		CampaignModel o2 = new CampaignModel();
//		o2.setCampaignType(5);
//		o2.setSubType(2);
//		o2.setStartTime(new Date());
//		o2.setEndTime(new Date());
//
//		CampaignModel o4 = new CampaignModel();
//		o4.setCampaignType(1);
//		o4.setSubType(2);
//		o4.setName("dddd");
//		o4.setStartTime(new Date());
//		o4.setEndTime(new Date());
//		
//		try {
//			Thread.currentThread().sleep(2221);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		CampaignModel o3 = new CampaignModel();
//		o3.setCampaignType(1);
//		o3.setSubType(2);
//		o3.setName("aa");
//		o3.setStartTime(new Date());
//		o3.setEndTime(new Date());
//		
//
//		List<CampaignModel> lists = new ArrayList<>();
//		lists.add(o1);
//		lists.add(o2);
//		lists.add(o3);
//		lists.add(o4);
//
//		Collections.sort(lists, new SortCampaigns());
//
//		System.err.println(lists);
//	}
}
