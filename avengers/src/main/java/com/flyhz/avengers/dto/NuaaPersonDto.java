
package com.flyhz.avengers.dto;

import org.apache.commons.lang3.StringUtils;

public class NuaaPersonDto {
	private String	name;				// 姓名
	private String	sex;				// 姓别
	private String	userImg;			// 照片
	private String	jobTitle;			// 职称
	private String	position;			// 职务
	private String	educationDegree;	// 学历学位
	private String	mentorCategory;	// 导师类别
	private String	college;			// 所在学院
	private String	profession;		// 专业
	private String	researchDirection;	// 研究方向
	private String	socialAppointments; // 社会兼职
	private String	honorary;			// 荣誉称号
	private String	scientificResearch; // 科研情况
	private String	researchResults;	// 研究成果
	private String	academicExperience; // 学术经历
	private String	courseLinks;		// 所在学院
	private String	other;				// 其它

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getUserImg() {
		return userImg;
	}

	public void setUserImg(String userImg) {
		this.userImg = userImg;
	}

	public String getJobTitle() {
		return jobTitle;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getEducationDegree() {
		return educationDegree;
	}

	public void setEducationDegree(String educationDegree) {
		this.educationDegree = educationDegree;
	}

	public String getMentorCategory() {
		return mentorCategory;
	}

	public void setMentorCategory(String mentorCategory) {
		this.mentorCategory = mentorCategory;
	}

	public String getCollege() {
		return college;
	}

	public void setCollege(String college) {
		this.college = college;
	}

	public String getProfession() {
		return profession;
	}

	public void setProfession(String profession) {
		this.profession = profession;
	}

	public String getResearchDirection() {
		return researchDirection;
	}

	public void setResearchDirection(String researchDirection) {
		this.researchDirection = researchDirection;
	}

	public String getSocialAppointments() {
		return socialAppointments;
	}

	public void setSocialAppointments(String socialAppointments) {
		this.socialAppointments = socialAppointments;
	}

	public String getHonorary() {
		return honorary;
	}

	public void setHonorary(String honorary) {
		this.honorary = honorary;
	}

	public String getScientificResearch() {
		return scientificResearch;
	}

	public void setScientificResearch(String scientificResearch) {
		this.scientificResearch = scientificResearch;
	}

	public String getResearchResults() {
		return researchResults;
	}

	public void setResearchResults(String researchResults) {
		this.researchResults = researchResults;
	}

	public String getAcademicExperience() {
		return academicExperience;
	}

	public void setAcademicExperience(String academicExperience) {
		this.academicExperience = academicExperience;
	}

	public String getCourseLinks() {
		return courseLinks;
	}

	public void setCourseLinks(String courseLinks) {
		this.courseLinks = courseLinks;
	}

	public String getOther() {
		return other;
	}

	public void setOther(String other) {
		this.other = other;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		if (StringUtils.isNotBlank(this.name)) {
			sb.append("\"name\":\"");
			sb.append(this.name);
			sb.append("\"");
		}
		if (StringUtils.isNotBlank(this.userImg)) {
			if (sb.length() > 1)
				sb.append(",");
			sb.append("\"userImg\":\"");
			sb.append(this.userImg);
			sb.append("\"");
		}
		if (StringUtils.isNotBlank(this.sex)) {
			if (sb.length() > 1)
				sb.append(",");
			sb.append("\"sex\":\"");
			sb.append(this.sex);
			sb.append("\"");
		}
		if (StringUtils.isNotBlank(this.jobTitle)) {
			if (sb.length() > 1)
				sb.append(",");
			sb.append("\"jobTitle\":\"");
			sb.append(this.jobTitle);
			sb.append("\"");
		}
		if (StringUtils.isNotBlank(this.position)) {
			if (sb.length() > 1)
				sb.append(",");
			sb.append("\"position\":\"");
			sb.append(this.position);
			sb.append("\"");
		}
		if (StringUtils.isNotBlank(this.educationDegree)) {
			if (sb.length() > 1)
				sb.append(",");
			sb.append("\"educationDegree\":\"");
			sb.append(this.educationDegree);
			sb.append("\"");
		}
		if (StringUtils.isNotBlank(this.mentorCategory)) {
			if (sb.length() > 1)
				sb.append(",");
			sb.append("\"mentorCategory\":\"");
			sb.append(this.mentorCategory);
			sb.append("\"");
		}

		if (StringUtils.isNotBlank(this.college)) {
			if (sb.length() > 1)
				sb.append(",");
			sb.append("\"college\":\"");
			sb.append(this.college);
			sb.append("\"");
		}
		if (StringUtils.isNotBlank(this.profession)) {
			if (sb.length() > 1)
				sb.append(",");
			sb.append("\"profession\":\"");
			sb.append(this.profession);
			sb.append("\"");
		}
		if (StringUtils.isNotBlank(this.researchDirection)) {
			if (sb.length() > 1)
				sb.append(",");
			sb.append("\"researchDirection\":\"");
			sb.append(this.researchDirection);
			sb.append("\"");
		}
		if (StringUtils.isNotBlank(this.socialAppointments)) {
			if (sb.length() > 1)
				sb.append(",");
			sb.append("\"socialAppointments\":\"");
			sb.append(this.socialAppointments);
			sb.append("\"");
		}
		if (StringUtils.isNotBlank(this.honorary)) {
			if (sb.length() > 1)
				sb.append(",");
			sb.append("\"honorary\":\"");
			sb.append(this.honorary);
			sb.append("\"");
		}
		if (StringUtils.isNotBlank(this.scientificResearch)) {
			if (sb.length() > 1)
				sb.append(",");
			sb.append("\"scientificResearch\":\"");
			sb.append(this.scientificResearch);
			sb.append("\"");
		}
		if (StringUtils.isNotBlank(this.researchResults)) {
			if (sb.length() > 1)
				sb.append(",");
			sb.append("\"researchResults\":\"");
			sb.append(this.researchResults);
			sb.append("\"");
		}
		if (StringUtils.isNotBlank(this.academicExperience)) {
			if (sb.length() > 1)
				sb.append(",");
			sb.append("\"academicExperience\":\"");
			sb.append(this.academicExperience);
			sb.append("\"");
		}
		if (StringUtils.isNotBlank(this.courseLinks)) {
			if (sb.length() > 1)
				sb.append(",");
			sb.append("\"courseLinks\":\"");
			sb.append(this.courseLinks);
			sb.append("\"");
		}
		if (StringUtils.isNotBlank(this.other)) {
			if (sb.length() > 1)
				sb.append(",");
			sb.append("\"other\":\"");
			sb.append(this.other);
			sb.append("\"");
		}
		sb.append("}");

		return sb.toString();
	}

}
