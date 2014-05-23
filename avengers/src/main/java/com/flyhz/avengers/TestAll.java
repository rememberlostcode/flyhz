
package com.flyhz.avengers;

import java.util.ArrayList;
import java.util.List;

import com.flyhz.avengers.domains.ResultFilter;
import com.flyhz.avengers.domains.coach.template.impl.coach.ResultFilterImpl;
import com.flyhz.avengers.framework.config.xml.XDomain;
import com.flyhz.avengers.framework.config.xml.XDomains;
import com.flyhz.avengers.framework.util.image.Image;

public class TestAll {

	public static List<String>	list		= null;
	public static List<String>	newList		= null;
	public static List<String>	productList	= new ArrayList<String>();
	public static ResultFilter	rf			= new ResultFilterImpl();

	// public static Filter filter = new CoachUrlFilterImpl();

	public static void main(String[] args) {

		// XDomains avengers =
		// AvengersDataUtil.getDataByXmlFileName("avengers.xml");
		XDomains avengers = null;
		List<XDomain> domains = avengers.getDomain();

		for (int k = 0; k < domains.size(); k++) {
			XDomain domain = domains.get(k);

			List<String> waitFilterUrls = new ArrayList<String>();
			waitFilterUrls.add("http://www.coach.com/online/handbags/Home-10551-10051-en/");
			waitFilterUrls.add("http://www.coach.com/online/handbags/-newatcoach_allnewatcoach-us-us-5000000000000000002-en?t1Id=5000000000000000001&t2Id=5000000000000000002&navCatId=7100000000000000568&LOC=HN1");
			waitFilterUrls.add("http://www.coach.com/online/handbags/genWCM-10551-10051-en-/Coach_US/StaticPage/women_landing?navCatId=5000000000000258802&LOC=HN1");
			waitFilterUrls.add("http://www.coach.com/online/handbags/genWCM-10551-10051-en-/Coach_US/StaticPage/men_landing?navCatId=82&LOC=HN1");
			waitFilterUrls.add("http://www.coach.com/online/handbags/-handbags_features_newarrivals_1-us-us-5000000000000015027-en?navCatId=7100000000000000590&navCatId=7100000000000000569&LOC=HN1");
			waitFilterUrls.add("http://www.coach.com/online/handbags/-shoes_newarrivals-us-us-23762-en?t1Id=105&t2Id=23762&navCatId=7100000000000000613&navCatId=7100000000000000570&LOC=HN1");
			waitFilterUrls.add("http://www.coach.com/online/handbags/-handbags_features_newarrivals_1-us-us-5000000000000015027-en?navCatId=7100000000000000590&LOC=HN2");
			waitFilterUrls.add("http://www.coach.com/online/handbags/-handbags_silhouettes_satchels-us-us-5000000000000015033-en?t1Id=62&t2Id=5000000000000015033&navCatId=7100000000000000594&LOC=HN2");
			waitFilterUrls.add("http://www.coach.com/online/handbags/-handbags_feature_madison-us-us-5000000000000049554-en?t1Id=62&t2Id=5000000000000049554&navCatId=7100000000000000604&LOC=HN2");
			// waitFilterUrls.add("http://www.coach.com/online/handbags/Product-bleecker_preston_satchel_in_pebbled_leather-10551-10051-30144-en?cs=svckc&catId=5000000000000015027&navCatId=7100000000000000590");
			waitFilterUrls.add("http://www.coach.com/online/handbags/Product-bleecker_triple_zip_crossbody_in_edgepaint_leather-10551-10051-51636-en?cs=svcmo&catId=5000000000000015027&navCatId=7100000000000000590");
			// waitFilterUrls.add("http://www.coach.com/online/handbags/Product-bleecker_sullivan_hobo_in_pebbled_leather-10551-10051-31623-en?cs=gdckm&catId=5000000000000015027&navCatId=7100000000000000590");
			// waitFilterUrls.add("http://www.coach.com/online/handbags/Product-madison_hobo_in_leather-10551-10051-27858-en?cs=licht");
			waitFilterUrls.add("http://s7d2.scene7.com/is/image/Coach/31623_a4?$pd_altsq$");
			waitFilterUrls.add("http://s7d2.scene7.com/is/image/Coach/31623_a3?$pd_altsq$");
			waitFilterUrls.add("http://s7d2.scene7.com/is/image/Coach/31623_gdckm_a0?$pd_main$");
			waitFilterUrls.add("http://s7d2.scene7.com/is/image/Coach/31623_m2?$pd_vertical$");
			waitFilterUrls.add("http://s7d2.scene7.com/is/image/Coach/31623_m1?$pd_vertical$");

			// newList = filter.filterValidUrl(domain, waitFilterUrls);

			// com.flyhz.avengers.framework.Pattern coachTemplate = null;
			for (int i = 0; i < newList.size(); i++) {
				// String className =
				// AvengersDataUtil.getUrlParserName(newList.get(i), domain);
				// if (StringUtils.isNotBlank(className)) {
				// try {
				// coachTemplate =
				// (com.flyhz.avengers.framework.Pattern) Class.forName(
				// className).newInstance();
				// coachTemplate.doPattern(newList.get(i));
				// } catch (InstantiationException e) {
				// e.printStackTrace();
				// } catch (IllegalAccessException e) {
				// e.printStackTrace();
				// } catch (ClassNotFoundException e) {
				// e.printStackTrace();
				// }
				// break;
				// }
			}

			rf.startDownloadThread();
			for (int i = 0; i < productList.size(); i++) {
				String[] imgs = productList.get(i).split(",");
				Image image = new Image(imgs[0].replace("[", "").replace("\"", ""));
				rf.addImage(image);
			}
		}
	}
}
