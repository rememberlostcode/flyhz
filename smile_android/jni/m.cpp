#include "com_holding_smile_tools_TbUtil.h"
#include "curl/curl.h"
#include <string>
#include <sstream>
#include <android/log.h> // 这个是输出LOG所用到的函数所在的路径
#include <stdio.h>
#include <string.h>
#include <regex.h>
#include <stdlib.h>

#define LOG_TAG    "PLOG" // 这个是自定义的LOG的标识
#undef LOG // 取消默认的LOG

#define LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__) // 定义LOG类型
#define LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__) // 定义LOG类型
#define LOGW(...)  __android_log_print(ANDROID_LOG_WARN,LOG_TAG,__VA_ARGS__) // 定义LOG类型
#define LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__) // 定义LOG类型
#define LOGF(...)  __android_log_print(ANDROID_LOG_FATAL,LOG_TAG,__VA_ARGS__) // 定义LOG类型

bool isInit = false;
std::string P_SERVER_NAME = "http://zuir2.zju.edu.cn/X";
std::string cookieString = "";

std::string noLogin = "100001"; //判断是否未登录的字符串
std::string logined = "200000"; //判断是否登录成功的字符串
int loginMaxNum = 3;//重新登录最大尝试次数

#define SUBSLEN 10              /* 匹配子串的数量 */
#define EBUFLEN 128             /* 错误消息buffer长度 */

/**
 * 正则获取cookie
 */
int regexCookie(char src[], char matched[]) {
	size_t len;
	regex_t re; /* 存储编译好的正则表达式，正则表达式在使用之前要经过编译 */
	regmatch_t subs[SUBSLEN]; /* 存储匹配到的字符串位置 */

	char errbuf[EBUFLEN]; /* 存储错误消息 */
	char pattern[] = "JSESSIONID=(.*);"; /* pattern字符串"Set-Cookie(.*)/(.*)[ ]" */
	int err, i;

	/* 编译正则表达式 */
	err = regcomp(&re, pattern, REG_EXTENDED);

	if (err) {
		len = regerror(err, &re, errbuf, sizeof(errbuf));
		LOGD("error: regcomp: %s\n", errbuf);
		return 1;
	}
	/* 执行模式匹配 */
	err = regexec(&re, src, (size_t) SUBSLEN, subs, 0);

	if (err == REG_NOMATCH) { /* 没有匹配成功 */
		LOGD("Sorry, no match ...\n");
		return 0;
	} else if (err) { /* 其它错误 */
		len = regerror(err, &re, errbuf, sizeof(errbuf));
		LOGD("error: regexec: %s\n", errbuf);
		return 1;
	} else {
		/* 如果不是REG_NOMATCH并且没有其它错误，则模式匹配上 */
		for (i = 0; i <= re.re_nsub; i++) {
			len = subs[i].rm_eo - subs[i].rm_so;
			if (i == 0) {
				memcpy(matched, src + subs[i].rm_so, len);
				//			memcpy(matched, src + subs[i].rm_so + 12, len); //12是偏移量，"Set-Cookie:"和一个空格
				matched[len] = '\0';
			}
		}
	}
	/* 用完了别忘了释放 */
	regfree(&re);
	return 0;
}

/**
 * jstring转换成std::string
 */
static std::string JstringToString(JNIEnv* env, jstring jstr) {
	char* rtn = NULL;
	jclass clsstring = env->FindClass("java/lang/String");
	jstring strencode = env->NewStringUTF("UTF-8");
	jmethodID mid = env->GetMethodID(clsstring, "getBytes",
			"(Ljava/lang/String;)[B");
	jbyteArray barr = (jbyteArray) env->CallObjectMethod(jstr, mid, strencode);
	jsize alen = env->GetArrayLength(barr);
	jbyte* ba = env->GetByteArrayElements(barr, JNI_FALSE);
	if (alen > 0) {
		rtn = (char*) malloc(alen + 1);
		memcpy(rtn, ba, alen);
		rtn[alen] = 0;
	}
	env->ReleaseByteArrayElements(barr, ba, 0);

	std::string stemp(rtn);
	free(rtn);
	return stemp;
}

/**
 * 调用java的自动登录方法
 */
static std::string UseJavaStaticMethodAutoLogin(JNIEnv* env) {
	jstring jstr = NULL;

	jclass cls = env->FindClass("com/example/begining/service/SubmitService");
	jmethodID mid = env->GetStaticMethodID(cls, "autoLoginForC",
			"()Ljava/lang/String;");
	jstr = (jstring) env->CallStaticObjectMethod(cls, mid);

	std::string res = JstringToString(env, jstr);
	env->DeleteLocalRef(jstr);
	env->DeleteLocalRef(cls);
	return res;
}

/**
 * 基础类型转换成std::string
 */
template<class T>
static std::string ToString(T value) {
	std::string str;
	std::stringstream ss;
	ss << value;
	ss >> str;
	return str;
}
/**
 * 回调函数,写入内容到content
 */
static size_t PResponse(void *data, size_t size, size_t nmemb,
		std::string &content) {
//	LOGD("PResponse size: %d\n", size);
	size_t sizes = size * nmemb;
//	LOGD("PResponse sizes: %d,strlen:%d\n", sizes,strlen((char*)data));
//	LOGD("PResponse data: %s\n", (char* )data);
	content.append((char*) data, sizes);
	return sizes;
}
struct WriteThis {
	const char *readptr;
	long sizeleft;
};

static size_t read_callback(void *ptr, size_t size, size_t nmemb, void *userp) {
	struct WriteThis *pooh = (struct WriteThis *) userp;

	if (size * nmemb < 1)
		return 0;

	if (pooh->sizeleft) {
		*(char *) ptr = pooh->readptr[0]; /* copy one single byte */
		pooh->readptr++; /* advance pointer */
		pooh->sizeleft--; /* less data left */
		return 1; /* we return 1 byte at a time! */
	}

	return 0; /* no more data left to deliver */
}

/**
 * http get方式连接
 */
static std::string connect_get(JNIEnv *env, std::string url) {
//	LOGD("this url is get!\n");
//	LOGD("url : %s \n", url.c_str());
//	LOGD("cookie: %s\n", cookieString.c_str());
	int retcode = 0;
	CURL* curl;
	CURLcode res;
	std::string header;
	std::string content;

	int reNum = 0;
	reDoing: curl = curl_easy_init();

	if (cookieString.empty()) {
		//cookie是空的，就从头部写入header以便获取cookie
		curl_easy_setopt(curl, CURLOPT_WRITEHEADER, &header);
		curl_easy_setopt(curl, CURLOPT_HEADERFUNCTION, PResponse);
	}

	curl_easy_setopt(curl, CURLOPT_URL, url.c_str());
	curl_easy_setopt(curl, CURLOPT_TIMEOUT, 60L);

	curl_easy_setopt(curl, CURLOPT_WRITEDATA, &content);
	curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, PResponse);
	curl_easy_setopt(curl, CURLOPT_COOKIE, cookieString.c_str());

	res = curl_easy_perform(curl);

	//cookie是空的就从header中找出cookie内容
	if (cookieString.empty()) {
//		LOGD("header: %s\n", header.c_str());
		//存储匹配到的字符串
		char matched[header.size()];
		//源字符串
		char src[header.size() + 1];
		//把头部数据拷贝到源字符串
		strcpy(src, header.c_str());
		//获取cookie
		regexCookie(src, matched);
		//cookie保存
		cookieString.assign(matched);
	}

	if (0 != res) {
		LOGD("curl error: %d !\n", res);
	} else {
		curl_easy_getinfo(curl, CURLINFO_RESPONSE_CODE, &retcode);
//		LOGD("CURL HTTP STATUS:%d", retcode);
	}
	//释放内存
	curl_easy_cleanup(curl);
	if (200 == retcode) {
//		LOGD("curl content = %s \n", content.c_str());
	} else {
		LOGD("curl error: %d\n", res);
		content =
				"{\"code\": 100002,\"validate\":{\"option\":null,\"message\": \"网络异常\"},\"data\": null}";
	}

	//判断是否已经登录失效
	if (strstr(content.c_str(), noLogin.c_str())) {
		LOGD("Login lapsed！\n");
		loginAgain: reNum++;
		std::string strLogin = UseJavaStaticMethodAutoLogin(env);
		if (strstr(strLogin.c_str(), logined.c_str())) {		//登录成功
			LOGD("Successful re-login！\n");
			LOGD("Just restart request！\n");
			if (reNum < loginMaxNum) {
				content.clear();
				goto reDoing;
			} else {
				LOGD(
						"Log on again more times than the maximum number of attempts！\n");
			}
		} else {
			LOGD("Login failed again！\n");
			if (reNum < loginMaxNum) {
				strLogin.clear();
				goto loginAgain;
			} else {
				LOGD(
						"Log on again more times than the maximum number of attempts！\n");
			}
		}
	}
	return content;
}

/**
 * http post方式连接
 */
static std::string connect_post(JNIEnv *env, std::string url,
		std::string postFields) {
//	LOGD("this url is post!");
//	LOGD("url : %s \n", url.c_str());
//	LOGD("postFields: %s\n", postFields.c_str());
//	LOGD("cookie: %s\n", cookieString.c_str());
	int retcode = 0;
	CURL* curl;
	CURLcode res;
	std::string header;
	std::string content;
	struct curl_slist *headerlist = NULL;
	struct WriteThis pooh;

	int reNum = 0;
	reDoing: std::string tmpCookie("Cookie: ");
	tmpCookie.append(cookieString.c_str());
	headerlist = curl_slist_append(headerlist, tmpCookie.c_str());

	curl = curl_easy_init();
	curl_easy_setopt(curl, CURLOPT_URL, url.c_str());
	curl_easy_setopt(curl, CURLOPT_TIMEOUT, 60L);
	curl_easy_setopt(curl, CURLOPT_WRITEHEADER, &header);
	curl_easy_setopt(curl, CURLOPT_HEADERFUNCTION, PResponse);
	curl_easy_setopt(curl, CURLOPT_WRITEDATA, &content);
	curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, PResponse);

	pooh.readptr = postFields.c_str();
	pooh.sizeleft = (long) strlen(postFields.c_str());

	curl_easy_setopt(curl, CURLOPT_POST, 1L);
	curl_easy_setopt(curl, CURLOPT_READFUNCTION, read_callback);
	curl_easy_setopt(curl, CURLOPT_READDATA, &pooh);
	curl_easy_setopt(curl, CURLOPT_POSTFIELDSIZE, pooh.sizeleft);
	curl_easy_setopt(curl, CURLOPT_HTTPHEADER, headerlist);

	res = curl_easy_perform(curl);

	if (0 != res) {
		LOGD("curl error: %d !\n", res);
	} else {
		curl_easy_getinfo(curl, CURLINFO_RESPONSE_CODE, &retcode);
		LOGD("CURL HTTP STATUS:%d", retcode);
		//cookie是空的就从header中找出cookie内容
		if (cookieString.empty() && !header.empty()) {
//			LOGD("header: %s\n", header.c_str());
			//存储匹配到的字符串
			char matched[header.size()];
			//源字符串
			char src[header.size() + 1];
			//把头部数据拷贝到源字符串
			strcpy(src, header.c_str());
			//获取cookie
			regexCookie(src, matched);
			//cookie保存
			cookieString.assign(matched);
		}
	}
	//释放内存
	curl_slist_free_all(headerlist);
	curl_easy_cleanup(curl);
	if (200 == retcode) {
//		LOGD("curl content = %s \n", content.c_str());
	} else {
		LOGD("curl error: %d\n", res);
		content =
				"{\"code\": 100002,\"validate\":{\"option\":null,\"message\": \"网络异常\"},\"data\": null}";
	}

	//判断是否已经登录失效
	if (strstr(content.c_str(), noLogin.c_str())) {
		LOGD("Login lapsed！\n");
		loginAgain: reNum++;
		std::string strLogin = UseJavaStaticMethodAutoLogin(env);
		if (strstr(strLogin.c_str(), logined.c_str())) {		//登录成功
			LOGD("Successful re-login！\n");
			LOGD("Just restart request！\n");
			if (reNum < loginMaxNum) {
				content.clear();
				goto reDoing;
			} else {
				LOGD(
						"Log on again more times than the maximum number of attempts！\n");
			}
		} else {
			LOGD("Login failed again！\n");
			if (reNum < loginMaxNum) {
				strLogin.clear();
				goto loginAgain;
			} else {
				LOGD(
						"Log on again more times than the maximum number of attempts！\n");
			}
		}
	}
	return content;
}

/**
 * http get方式连接(无cookie)
 */
static std::string connect_get_without_cookie(JNIEnv *env, std::string url) {
//	LOGD("this url is get!\n");
//	LOGD("url : %s \n", url.c_str());
	int retcode = 0;
	CURL* curl;
	CURLcode res;
	std::string content;

	int reNum = 0;
	reDoing: curl = curl_easy_init();

	curl_easy_setopt(curl, CURLOPT_URL, url.c_str());
	curl_easy_setopt(curl, CURLOPT_TIMEOUT, 60L);

	curl_easy_setopt(curl, CURLOPT_WRITEDATA, &content);
	curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, PResponse);

	res = curl_easy_perform(curl);

	if (0 != res) {
		LOGD("curl error: %d !\n", res);
	} else {
		curl_easy_getinfo(curl, CURLINFO_RESPONSE_CODE, &retcode);
		LOGD("CURL HTTP STATUS:%d", retcode);
	}
	//释放内存
	curl_easy_cleanup(curl);
	if (200 == retcode) {
//		LOGD("curl content = %s \n", content.c_str());
	} else {
		LOGD("curl error: %d\n", res);
		content =
				"{\"code\": 100002,\"validate\":{\"option\":null,\"message\": \"网络异常\"},\"data\": null}";
	}
	return content;
}

/**
 * http post方式连接(无cookie)
 */
static std::string connect_post(JNIEnv *env, std::string url) {
//	LOGD("this url is post!");
//	LOGD("url : %s \n", url.c_str());
	int retcode = 0;
	CURL* curl;
	CURLcode res;
	std::string header;
	std::string content;
	struct WriteThis pooh;

	curl = curl_easy_init();
	curl_easy_setopt(curl, CURLOPT_URL, url.c_str());
	curl_easy_setopt(curl, CURLOPT_TIMEOUT, 60L);
	curl_easy_setopt(curl, CURLOPT_WRITEHEADER, &header);
	curl_easy_setopt(curl, CURLOPT_HEADERFUNCTION, PResponse);
	curl_easy_setopt(curl, CURLOPT_WRITEDATA, &content);
	curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, PResponse);

	curl_easy_setopt(curl, CURLOPT_POST, 1L);
	curl_easy_setopt(curl, CURLOPT_READFUNCTION, read_callback);
	curl_easy_setopt(curl, CURLOPT_READDATA, &pooh);
	curl_easy_setopt(curl, CURLOPT_POSTFIELDSIZE, pooh.sizeleft);

	res = curl_easy_perform(curl);

	if (0 != res) {
		LOGD("curl error: %d !\n", res);
	} else {
		curl_easy_getinfo(curl, CURLINFO_RESPONSE_CODE, &retcode);
		LOGD("CURL HTTP STATUS:%d", retcode);
	}
	//释放内存
	curl_easy_cleanup(curl);
	if (200 == retcode) {
//		LOGD("curl content = %s", content.c_str());
	} else {
		LOGD("curl error: %d\n", res);
		content =
				"{\"code\": 100002,\"validate\":{\"option\":null,\"message\": \"网络异常\"},\"data\": null}";
	}

	return content;
}

/**
 * 初始化
 */
JNIEXPORT void JNICALL Java_com_holding_smile_tools_TbUtil_init__(
		JNIEnv *env, jclass jc) {
	if (!isInit) {
		LOGE("p init start...\n");
		curl_global_init( CURL_GLOBAL_ALL);
		LOGE("p init end...\n");
	} else {
		LOGE("curl had inited!\n");
	}

}

/**
 * 初始化
 */
JNIEXPORT void JNICALL Java_com_holding_smile_tools_TbUtil_init__Ljava_lang_String_2(
		JNIEnv *env, jclass jc, jstring url) {
	if (!isInit) {
		LOGE("p init start...\n");
		std::string surl = JstringToString(env, url);
		LOGE("Server url: %s\n", surl.c_str());
		P_SERVER_NAME.assign(surl.c_str());
		curl_global_init( CURL_GLOBAL_ALL);

		env->DeleteLocalRef(url);
		LOGE("p init end...\n");
	} else {
		LOGE("curl had inited!\n");
	}
}

/*
 * 销毁
 */
JNIEXPORT void JNICALL Java_com_holding_smile_tools_TbUtil_cleanup(
		JNIEnv *env, jclass jc) {
	curl_global_cleanup();
}

jstring stoJstring(JNIEnv* env, const char* pat) {
	jclass strClass = env->FindClass("java/lang/String");
	jmethodID ctorID = env->GetMethodID(strClass, "<init>",
			"([BLjava/lang/String;)V");
	jbyteArray bytes = env->NewByteArray(strlen(pat));
	env->SetByteArrayRegion(bytes, 0, strlen(pat), (jbyte*) pat);
	jstring encoding = env->NewStringUTF("utf-8");
	return (jstring) env->NewObject(strClass, ctorID, bytes, encoding);
}

/*
 * Class:     com_holding_smile_tools_TbUtil
 * Method:    cshTb
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_holding_smile_tools_TbUtil_cshTb(JNIEnv *env,
		jclass cls) {
	LOGE("p cshTb start...\n");
	jfieldID fid = NULL;
	jfieldID tbFid = NULL;
	jobject webView = NULL;
	jstring jstr = NULL;
	jclass webClazz = NULL;
	jclass tbClazz = NULL;


	fid = env->GetStaticFieldID(cls, "webView", "Landroid/webkit/WebView;");
	//LOGE("p cshTb 11111...\n");
	webView = env->GetStaticObjectField(cls,fid);
	//LOGE("p cshTb 22222...\n");
	webClazz = env->FindClass("android/webkit/WebView");
	//LOGE("p cshTb 33333...\n");
	jmethodID mid = env->GetMethodID(webClazz, "loadUrl", "(Ljava/lang/String;)V");
	//LOGE("p cshTb 44444...\n");

	//获取淘宝店铺地址的url的url
	tbClazz = env->FindClass("com/holding/smile/activity/MyApplication");
	//LOGE("p cshTb 55555...\n");
	tbFid = env->GetStaticFieldID(tbClazz, "tb_url", "Ljava/lang/String;");
	//LOGE("p cshTb 66666...\n");
	jstr = (jstring)(env->GetStaticObjectField(tbClazz,tbFid));
	if(jstr == NULL){
//		LOGE("p cshTb tb_url is null.\n");
	} else {
		std::string url = JstringToString(env, jstr);
		if (url.size() == 0) {
//			LOGE("p cshTb url is null.\n");
		} else {
			//LOGE("p cshTb 77777...%s\n", url.c_str());
			std::string tbd_url = connect_get_without_cookie(env, url);
			//LOGE("p cshTb 88888...\n");
			env->CallVoidMethod(webView, mid, stoJstring(env, tbd_url.c_str()));
			//LOGE("p cshTb 99999...\n");
		}
	}
	env->DeleteLocalRef(webView);
	env->DeleteLocalRef(jstr);
	env->DeleteLocalRef(webClazz);
	env->DeleteLocalRef(tbClazz);
	LOGE("p cshTb end...\n");
}

