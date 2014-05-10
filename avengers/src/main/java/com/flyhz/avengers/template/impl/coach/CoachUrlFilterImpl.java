
package com.flyhz.avengers.template.impl.coach;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.flyhz.avengers.framework.UrlFilter;
import com.flyhz.avengers.framework.xml.Domain.Templates.Template;
import com.flyhz.avengers.template.impl.BaseUrlFilter;

public class CoachUrlFilterImpl extends BaseUrlFilter implements UrlFilter {
	@Override
	public Set<String> filterValidUrl(List<String> black, List<Template> templates,
			List<String> waitFilterUrls) {
		if (waitFilterUrls != null && !waitFilterUrls.isEmpty()) {
			Set<String> filterUrlsSet = rmDuplicate(waitFilterUrls);
			filterUrlsSet = checkWhiteUrls(templates, filterUrlsSet);
			filterUrlsSet = checkBlackUrls(black, filterUrlsSet);
			return filterUrlsSet;
		}
		return null;
	}

	public static void main(String[] args) {
		Template template = new Template();
		template.setUrl("^http://www.coach.com/online/handbags/-handbags_features_newarrivals_1-us-us-5000000000000015027-en?");
		Template template1 = new Template();
		template1.setUrl("^http://www.coach.com/online/handbags/-handbags_feature_madison-us-us-5000000000000049554-en?");
		Template template2 = new Template();
		template2.setUrl("^http://www.coach.com/online/handbags/Product-");
		Template template3 = new Template();
		template3.setUrl("^http://s7d2.scene7.com/is/image/Coach/");
		List<Template> templates = new ArrayList<Template>();
		templates.add(template);
		templates.add(template1);
		templates.add(template2);
		templates.add(template3);

		List<String> blackUrls = new ArrayList<String>();
		blackUrls.add("^http://www.coach.com/online/handbags/-newatcoach_allnewatcoach-us-us-5000000000000000002-en?");
		blackUrls.add("^http://www.coach.com/online/handbags/genWCM-10551-10051-en-/Coach_US/StaticPage/women_landing?");
		blackUrls.add("^http://www.coach.com/online/handbags/genWCM-10551-10051-en-/Coach_US/StaticPage/men_landing?");
		blackUrls.add("^http://s7d2.scene7.com/is/image/Coach/");

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
		waitFilterUrls.add("http://www.coach.com/online/handbags/Product-bleecker_preston_satchel_in_pebbled_leather-10551-10051-30144-en?cs=svckc&catId=5000000000000015027&navCatId=7100000000000000590");
		waitFilterUrls.add("http://www.coach.com/online/handbags/Product-bleecker_triple_zip_crossbody_in_edgepaint_leather-10551-10051-51636-en?cs=svcmo&catId=5000000000000015027&navCatId=7100000000000000590");
		waitFilterUrls.add("http://www.coach.com/online/handbags/Product-bleecker_sullivan_hobo_in_pebbled_leather-10551-10051-31623-en?cs=gdckm&catId=5000000000000015027&navCatId=7100000000000000590");
		waitFilterUrls.add("http://www.coach.com/online/handbags/Product-madison_hobo_in_leather-10551-10051-27858-en?cs=licht");
		waitFilterUrls.add("http://s7d2.scene7.com/is/image/Coach/31623_a4?$pd_altsq$");
		waitFilterUrls.add("http://s7d2.scene7.com/is/image/Coach/31623_a3?$pd_altsq$");
		waitFilterUrls.add("http://s7d2.scene7.com/is/image/Coach/31623_gdckm_a0?$pd_main$");
		waitFilterUrls.add("http://s7d2.scene7.com/is/image/Coach/31623_m2?$pd_vertical$");
		waitFilterUrls.add("http://s7d2.scene7.com/is/image/Coach/31623_m1?$pd_vertical$");

		UrlFilter urlFilter = new CoachUrlFilterImpl();
		urlFilter.filterValidUrl(blackUrls, templates, waitFilterUrls);
	}
}
