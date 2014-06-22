//var exec = require("child_process").exec;
var solr_server_host = "211.149.175.138";//211.149.175.138
//var solr_server_host = "10.22.22.40";
var solr_server_port = 8983;
//var solr_server_port = 80;
var applicationJson = "application/json; charset=utf-8";
var UserAgent = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.154 Safari/537.36";
var http = require("http");
var redis = require("redis");
var client = redis.createClient(6379,'211.149.175.138');
//var client = redis.createClient(6379,'10.22.23.63');
client.on('error', function (err) {
    console.log('Error ' + err);
});

//返回的response的头部内容
var headContentObject = {
            "Content-Type": applicationJson,
            "Access-Control-Allow-Origin":"*",
            'Access-Control-Allow-Methods': 'GET',
            'Access-Control-Allow-Headers': 'X-Requested-With,content-type'};

/**
 * 所有分类
 * @param query
 * @param response
 */
function category(query,response) {
    var key ='smile@cates@all';
    client.hgetall(key, function(err, res) {
        console.log(res);
        var items = [];
        for (i in res) {
            items.push(JSON.parse(res[i]));
        }
        //console.log(items);
        response.writeHead(200, headContentObject);
        response.write(addData(JSON.stringify(items)));
        response.end();
    });
}
/**
 * 首页活动推荐
 * @param query
 * @param response
 */
function recommendactivity(query,response) {
    var key ='smile@recommend@index';
    client.get(key, function(err, res) {
        // console.log(res);
        var recommendindex = JSON.parse(res);
        if(recommendindex){
            var result = '['; /*'[{"id":"1","p":"/activity/1.jpg"},' +
                '{"id":"2","p":"/activity/2.jpg"},' +
                '{"id":"3","p":"/activity/3.jpg"}]';*/
            for(var i=0;i<recommendindex.length;i++){
                if(i > 0){
                    result += ',';
                }
                result += '{\"id\":';
                result += JSON.stringify(recommendindex[i].id);
                result += ',\"p\":';
                result += JSON.stringify(recommendindex[i].p);
                result += ',\"url\":';
                result += JSON.stringify(recommendindex[i].url);
                result += '}';
            }
            result += ']';
            response.writeHead(200, headContentObject);
            response.write(addData(result));
            response.end();
        }
    });
}

/**
 * 首页活动及品牌推荐
 * @param query
 * @param response
 */
function index(query,response) {
    var key ='smile@recommend@index';
    client.get(key, function(err, res) {
        var result2 = '[';
        var recommendindex = JSON.parse(res);
        if(recommendindex){
            for(var i=0;i<recommendindex.length;i++){
                if(i > 0){
                    result2 += ',';
                }
                result2 += '{\"id\":';
                result2 += JSON.stringify(recommendindex[i].id);
                result2 += ',\"p\":';
                result2 += JSON.stringify(recommendindex[i].p);
                result2 += ',\"url\":';
                result2 += JSON.stringify(recommendindex[i].url?recommendindex[i].url:null);
                result2 += '}';
            }
        }
        result2 += ']';

        var result = '{"activity":'+result2+'';
        result += ',"brands":';
        key ='smile@brands@recommend';
        var param = '';
        var cid = query.cid;
        if(cid!=null && cid!=''){
            key = 'smile@brands@recommend&cates@' + cid;
        }
        console.log('key='+key);
        client.get(key, function(err, res) {
            //console.log(res);
            if(res){
                var memberfilter = new Array();
                memberfilter[0] = "id";
                memberfilter[1] = "n";
				memberfilter[2] = "d";
                memberfilter[3] = "p";
                memberfilter[4] = "pp";
				memberfilter[5] = "lp";
                memberfilter[6] = "bs";

                var recommendindex = JSON.parse(res);
                result += '[';
                for(var i=0;i<recommendindex.length;i++){
                    if(i>0){
                        result += ',';
                    }
                    /*result += '{\"id\":';
                    result += recommendindex[i].id;
                    result += ',\"name\":';
                    result += JSON.stringify(recommendindex[i].name ? recommendindex[i].name : null);
					result += ',\"img_url\":';
                    result += JSON.stringify(recommendindex[i].img_url?recommendindex[i].img_url:null);*/
                    result += '{\"brand\":';
                    result += JSON.stringify(recommendindex[i].brand);
                    result += ',\"gs\":';
                    result += JSON.stringify(recommendindex[i].gs,memberfilter);
                    result += '}';
                }
                result += ']';
            }
            result += '}';
            response.writeHead(200, headContentObject);
            response.write(addData(result));
            response.end();
        });
    });
}

/**
 * 首页活动及品牌推荐
 * @param query
 * @param response
 */
function indexsingle(query,response) {
    var key ='smile@recommend@index';
    client.get(key, function(err, res) {
        var result2 = '[';
        var recommendindex = JSON.parse(res);
        if(recommendindex){
            for(var i=0;i<recommendindex.length;i++){
                if(i > 0){
                    result2 += ',';
                }
                result2 += '{\"id\":';
                result2 += JSON.stringify(recommendindex[i].id);
                result2 += ',\"p\":';
                result2 += JSON.stringify(recommendindex[i].p);
                result2 += ',\"url\":';
                result2 += JSON.stringify(recommendindex[i].url?recommendindex[i].url:null);
                result2 += '}';
            }
        }
        result2 += ']';

        var result = '{"activitys":'+result2+'';
        result += ',"brands":';
        key ='smile@brands@all';
        console.log('key='+key);
        client.hvals(key, function(err, res) {
            console.log(res);
            if(res){
                result += '[';
                for(var i=0;i<res.length;i++){
                    if(i>0){
                        result += ',';
                    }
                    result += res[i];
                }
                result += ']';
            }
            result += '}';
            response.writeHead(200, headContentObject);
            response.write(addData(result));
            response.end();
        });
    });
}

/**
 * 首页品牌推荐商品
 * @param query
 * @param response
 */
function recommendbrand(query,response) {
    var key ='smile@brands@recommend';
    var param = '';
    var cid = query.cid;
    if(cid!=null && cid!=''){
        key = 'smile@brands@recommend&cates@' + cid;
    }
    //console.log('key='+key);
    client.get(key, function(err, res) {
        //console.log(res);
        var result = '';
        if(res){
            var memberfilter = new Array();
            memberfilter[0] = "id";
            memberfilter[1] = "n";
			memberfilter[2] = "d";
            memberfilter[3] = "p";
            memberfilter[4] = "pp";
			memberfilter[5] = "lp";
            memberfilter[6] = "bs";

            var recommendindex = JSON.parse(res);
            result = '[';
            for(var i=0;i<recommendindex.length;i++){
                if(i>0){
                    result += ',';
                }
                result += '{\"brand\":';
                result += JSON.stringify(recommendindex[i].brand);
                result += ',\"gs\":';
                result += JSON.stringify(recommendindex[i].gs,memberfilter);
                result += '}';
            }
            result += ']';
        }
        response.writeHead(200, headContentObject);
        response.write(addData(result));
        response.end();
    });
}

/**
 * 查看指定品牌的商品
 * @param query
 * @param response
 */
function brand(query,response) {
    var param = '';
    var seqorderType = query.seqorderType;
    if(seqorderType!=null && seqorderType!=''){
        if(seqorderType=='time'){
            param += "&sort=st+desc";
        } else if(seqorderType=='discount'){
            param += "&sort=sd+desc";
        } else if(seqorderType=='sales'){
            param += "&sort=ss+desc";
        } else if(seqorderType=='monthsales'){
            param += "&sort=sy+desc";
        } else if(seqorderType=='price'){
            param += "&sort=sj+asc";
        } else {
            param += "&sort=ss+desc";
        }
    } else {
        param += "&sort=ss+desc";
    }

    var bcparam = '';
    var bid = query.bid;
    if(bid==null || bid==''){
        response.writeHead(200, headContentObject);
        response.write( '{"data":null,"code":120001}');
        response.end();
    } else {
        bcparam += 'q=bid%3A'+bid;
    }

    var cid = query.cid;
    if(cid!=null && cid!=''){
        if(bcparam==''){
            bcparam += 'q=cid%3A'+cid;
        } else {
            bcparam += '+AND+cid%3A'+cid;
        }
    }

    var urlPath = "/solr/smile_product/select?"+bcparam+param;

    //console.log("Got urlPath: " + urlPath);
    var options = {
        host: solr_server_host,
        port: solr_server_port,
        path: urlPath,
        headers:{
            "Content-Type": applicationJson,
            "User-Agent": UserAgent
        }
    };
    var req = http.request(options, function(res) {
        //console.log("res======== " + res);
        res.setEncoding('utf8');
        try{
            var tmp = '';
            res.on('data', function (chunk) {
                tmp += chunk;
            });
            res.on('end',function() {
                var resss = JSON.parse(tmp);
                if(resss && resss.response && resss.response.docs){
                    var docs = resss.response.docs;
                    var result =  '[';
                    for(var i=0;i<docs.length;i++){
                        if(i > 0){
                            result += ',';
                        }
                        result += '{\"id\":';
                        result += docs[i].id;
                        result += ',\"n\":';
                        result += JSON.stringify(docs[i].n);
						result += ',\"d\":';
                        result += JSON.stringify(docs[i].d);
                        result += ',\"bs\":';
                        result += JSON.stringify(docs[i].bs);
                        result += ',\"p\":';
                        result += JSON.stringify(docs[i].p);
                        result += ',\"pp\":';
                        result += JSON.stringify(docs[i].pp?docs[i].pp:0);
						result += ',\"lp\":';
                        result += JSON.stringify(docs[i].lp?docs[i].lp:0);
                        result += ',\"sn\":';
                        result += JSON.stringify(docs[i].sn?docs[i].sn:0);
                        result += ',\"zsn\":';
                        result += JSON.stringify(docs[i].zsn?docs[i].zsn:0);
                        result += ',\"seq\":';
                        if(seqorderType=='discount'){
                            result += JSON.stringify(docs[i].sd);
                        } else if(seqorderType=='time'){
                            result += JSON.stringify(docs[i].st);
                        } else if(seqorderType=='sales'){
                            result += JSON.stringify(docs[i].ss);
                        } else if(seqorderType=='monthsales'){
                            result += JSON.stringify(docs[i].sy);
                        } else if(seqorderType=='price'){
                            result += JSON.stringify(docs[i].sj);
                        } else {
                            result += JSON.stringify(docs[i].ss);
                        }
                        result += '}';
                    }

                    result += ']';
                } else {
                    result = "[]";
                }
                response.writeHead(200, headContentObject);
                response.write(addData(result));
                response.end();
            });
        } catch (ee){
            console.log(ee.message);
        }
    });
    req.on('error', function(e) {
        console.log("Got error: " + e.message);
        response.writeHead(404, headContentObject);
        response.write(e.message);
        response.end();
    });
    req.end();
}
/**
 * 查看指定品牌的商品更多
 * @param query
 * @param response
 */
function brandmore(query,response) {
    var param = '';
    var seqorderType = query.seqorderType;
    if(seqorderType!=null && seqorderType!=''){
        if(seqorderType=='time'){
            param += "&sort=st+desc";
        } else if(seqorderType=='discount'){
            param += "&sort=sd+desc";
        } else if(seqorderType=='sales'){
            param += "&sort=ss+desc";
        } else if(seqorderType=='monthsales'){
            param += "&sort=sy+desc";
        } else if(seqorderType=='price'){
            param += "&sort=sj+asc";
        } else {
            param += "&sort=ss+desc";
        }
    } else {
        param += "&sort=ss+desc";
    }

    var seqorderValue = query.seqorderValue;
    if(seqorderValue!=null && seqorderValue!=''){
        if(seqorderType=='time'){
            param += "&fq=st%3A%5B*+"+(seqorderValue-1)+"%5D";
        } else if(seqorderType=='discount'){
            param += "&fq=sd%3A%5B*+"+(seqorderValue-1)+"%5D";
        } else if(seqorderType=='sales'){
            param += "&fq=ss%3A%5B*+"+(seqorderValue-1)+"%5D";
        } else if(seqorderType=='monthsales'){
            param += "&fq=sy%3A%5B*+"+(seqorderValue-1)+"%5D";
        } else if(seqorderType=='price'){
            param += "&fq=sj%3A%5B"+(seqorderValue-(-1))+"+*%5D";
        } else {
            param += "&fq=ss%3A%5B*+"+(seqorderValue-1)+"%5D";
        }
    } else {
        //param += "&fq=ss%3A%5B*+"+(seqorderValue-1)+"%5D";
    }

    var bcparam ='';
    var bid = query.bid;
    if(bid==null || bid==''){
        response.writeHead(200, headContentObject);
        response.write('Bid can not empty!');
        response.end();
    } else {
        bcparam += 'q=bid%3A'+bid;
    }

    var cid = query.cid;
    if(cid!=null && cid!=''){
        bcparam += '+cid%3A'+cid;
    }

    var urlPath = "/solr/smile_product/select?"+bcparam+param;

    console.log("Got urlPath: " + urlPath);
    var options = {
        host: solr_server_host,
        port: solr_server_port,
        path: urlPath,
        headers:{
            "Content-Type": applicationJson,
            "User-Agent": UserAgent
        }
    };
    var req = http.request(options, function(res) {
        res.setEncoding('utf8');
        try{
            var tmp = '';
            res.on('data', function (chunk) {
                tmp += chunk;
            });
            res.on('end',function() {
                var resss = JSON.parse(tmp);
                if(resss && resss.response && resss.response.docs){
                    var docs = resss.response.docs;
                    var result =  '[';
                    for(var i=0;i<docs.length;i++){
                        if(i > 0){
                            result += ',';
                        }
                        result += '{\"id\":';
                        result += docs[i].id;
                        result += ',\"n\":';
                        result += JSON.stringify(docs[i].n);
						result += ',\"d\":';
                        result += JSON.stringify(docs[i].d);
                        result += ',\"bs\":';
                        result += JSON.stringify(docs[i].bs);
                        result += ',\"p\":';
                        result += JSON.stringify(docs[i].p);
                        result += ',\"pp\":';
                        result += JSON.stringify(docs[i].pp?docs[i].pp:null);
						result += ',\"lp\":';
                        result += JSON.stringify(docs[i].lp?docs[i].lp:null);
                        result += ',\"sn\":';
                        result += JSON.stringify(docs[i].sn?docs[i].sn:0);
                        result += ',\"zsn\":';
                        result += JSON.stringify(docs[i].zsn?docs[i].zsn:0);
                        result += ',\"seq\":';
                        if(seqorderType=='discount'){
                            result += JSON.stringify(docs[i].sd);
                        } else if(seqorderType=='time'){
                            result += JSON.stringify(docs[i].st);
                        } else if(seqorderType=='sales'){
                            result += JSON.stringify(docs[i].ss);
                        } else if(seqorderType=='monthsales'){
                            result += JSON.stringify(docs[i].sy);
                        } else if(seqorderType=='price'){
                            result += JSON.stringify(docs[i].sj);
                        } else {
                            result += JSON.stringify(docs[i].ss);
                        }
                        result += '}';
                    }

                    result += ']';
                } else {
                    result = "[]";
                }
                //console.log("result: " + result);
                response.writeHead(200, headContentObject);
                response.write(addData(result));
                response.end();
            });
        } catch (ee){
            console.log(ee.message);
        }
    });
    req.on('error', function(e) {
        console.log("Got error: " + e.message);
        response.writeHead(404, headContentObject);
        response.write(e.message);
        response.end();
    });
    req.end();
}
function sorttype(query,response){
    var result = '[';

    result += '{';
    result += '"n":"总销量","v":"sales"';
    result += '}';

    result += ',';
    result += '{';
    result += '"n":"月销量","v":"monthsales"';
    result += '}';

    result += ',';
    result += '{';
    result += '"n":"折扣","v":"discount"';
    result += '}';

    result += ',';
    result += '{';
    result += '"n":"价格","v":"price"';
    result += '}';

    result += ']';

    response.writeHead(200, headContentObject);
    response.write(addData(result));
    response.end();
}
/**
 * 所有排行
 * @param query
 * @param response
 */
function sort(query,response) {
    var result = '[';

    result += '{';
    result += '"n":"总销量排行","u":"/smile/node/ranking/sales"';
    result += '}';

    result += ',';
    result += '{';
    result += '"n":"月销量排行","u":"/smile/node/ranking/monthsales"';
    result += '}';

    result += ',';
    result += '{';
    result += '"n":"折扣排行","u":"/smile/node/ranking/discount"';
    result += '}';

    result += ',';
    result += '{';
    result += '"n":"价格排行","u":"/smile/node/ranking/price"';
    result += '}';
    result += ']';

    response.writeHead(200, headContentObject);
    response.write(addData(result));
    response.end();
}

/**
 * 总销量排行
 * @param query
 * @param response
 */
function rankingsales(query,response) {
    var urlPath = "/solr/smile_product/select?q=*%3A*&sort=ss+desc&rows=100";
    var options = {
        host: solr_server_host,
        port: solr_server_port,
        path: urlPath,
        headers:{
            "Content-Type": applicationJson,
            "User-Agent": UserAgent
        }
    };
    var req = http.request(options, function(res) {
        res.setEncoding('utf8');
        try{
            var tmp = '';
            res.on('data', function (chunk) {
                tmp += chunk;
            });
            res.on('end',function() {
                var resss = JSON.parse(tmp);
                if(resss && resss.response && resss.response.docs){
                    var docs = resss.response.docs;
                    var result =  '[';
                    for(var i=0;i<docs.length;i++){
                        if(i > 0){
                            result += ',';
                        }
                        result += '{\"id\":';
                        result += docs[i].id;
                        result += ',\"n\":';
                        result += JSON.stringify(docs[i].n);
                        result += ',\"d\":';
                        result += JSON.stringify(docs[i].d);
                        result += ',\"bs\":';
                        result += JSON.stringify(docs[i].bs);
                        result += ',\"p\":';
                        result += JSON.stringify(docs[i].p);
                        result += ',\"sn\":';
                        result += JSON.stringify(docs[i].sn?docs[i].sn:0);
                        result += ',\"pp\":';
                        result += JSON.stringify(docs[i].pp?docs[i].pp:null);
                        result += ',\"lp\":';
                        result += JSON.stringify(docs[i].lp?docs[i].lp:null);
                        result += ',\"zsn\":';
                        result += JSON.stringify(docs[i].zsn?docs[i].zsn:0);
                        result += '}';
                    }

                    result += ']';
                } else {
                    result = "[]";
                }
                response.writeHead(200, headContentObject);
                response.write(addData(result));
                response.end();
            });
        } catch (ee){
            console.log(ee.message);
        }
    });
    req.on('error', function(e) {
        console.log("Got error: " + e.message);
        response.writeHead(404, headContentObject);
        response.write(e.message);
        response.end();
    });
    req.end();
}

/**
 * 月销量排行
 * @param query
 * @param response
 */
function rankingmonthsales(query,response) {
    var urlPath = "/solr/smile_product/select?q=*%3A*&sort=sy+desc&rows=100";
    var options = {
        host: solr_server_host,
        port: solr_server_port,
        path: urlPath,
        headers:{
            "Content-Type": applicationJson,
            "User-Agent": UserAgent
        }
    };
    var req = http.request(options, function(res) {
        res.setEncoding('utf8');
        try{
            var tmp = '';
            res.on('data', function (chunk) {
                tmp += chunk;
            });
            res.on('end',function() {
                var resss = JSON.parse(tmp);
                if(resss && resss.response && resss.response.docs){
                    var docs = resss.response.docs;
                    var result =  '[';
                    for(var i=0;i<docs.length;i++){
                        if(i > 0){
                            result += ',';
                        }
                        result += '{\"id\":';
                        result += docs[i].id;
                        result += ',\"n\":';
                        result += JSON.stringify(docs[i].n);
                        result += ',\"d\":';
                        result += JSON.stringify(docs[i].d);
                        result += ',\"bs\":';
                        result += JSON.stringify(docs[i].bs);
                        result += ',\"p\":';
                        result += JSON.stringify(docs[i].p);
                        result += ',\"sn\":';
                        result += JSON.stringify(docs[i].sn?docs[i].sn:0);
                        result += ',\"pp\":';
                        result += JSON.stringify(docs[i].pp?docs[i].pp:null);
                        result += ',\"lp\":';
                        result += JSON.stringify(docs[i].lp?docs[i].lp:null);
                        result += ',\"zsn\":';
                        result += JSON.stringify(docs[i].zsn?docs[i].zsn:0);
                        result += '}';
                    }

                    result += ']';
                } else {
                    result = "[]";
                }
                response.writeHead(200, headContentObject);
                response.write(addData(result));
                response.end();
            });
        } catch (ee){
            console.log(ee.message);
        }
    });
    req.on('error', function(e) {
        console.log("Got error: " + e.message);
        response.writeHead(404, headContentObject);
        response.write(e.message);
        response.end();
    });
    req.end();
}
/**
 * 折扣排行
 * @param query
 * @param response
 */
function rankingdiscount(query,response) {
    var urlPath = "/solr/smile_product/select?q=*%3A*&sort=sd+desc&rows=100";
    var options = {
        host: solr_server_host,
        port: solr_server_port,
        path: urlPath,
        headers:{
            "Content-Type": applicationJson,
            "User-Agent": UserAgent
        }
    };
    var req = http.request(options, function(res) {
        res.setEncoding('utf8');
        try{
            var tmp = '';
            res.on('data', function (chunk) {
                tmp += chunk;
            });
            res.on('end',function() {
                var resss = JSON.parse(tmp);
                if(resss && resss.response && resss.response.docs){
                    var docs = resss.response.docs;
                    var result =  '[';
                    for(var i=0;i<docs.length;i++){
                        if(i > 0){
                            result += ',';
                        }
                        result += '{\"id\":';
                        result += docs[i].id;
                        result += ',\"n\":';
                        result += JSON.stringify(docs[i].n);
						result += ',\"d\":';
                        result += JSON.stringify(docs[i].d);
                        result += ',\"bs\":';
                        result += JSON.stringify(docs[i].bs);
                        result += ',\"p\":';
                        result += JSON.stringify(docs[i].p);
                        result += ',\"pp\":';
                        result += JSON.stringify(docs[i].pp?docs[i].pp:null);
						result += ',\"lp\":';
                        result += JSON.stringify(docs[i].lp?docs[i].lp:null);
                        result += ',\"zsn\":';
                        result += JSON.stringify(docs[i].zsn?docs[i].zsn:0);
                        result += '}';
                    }

                    result += ']';
                } else {
                    result = "[]";
                }
                response.writeHead(200, headContentObject);
                response.write(addData(result));
                response.end();
            });
        } catch (ee){
            console.log(ee.message);
        }
    });
    req.on('error', function(e) {
        console.log("Got error: " + e.message);
        response.writeHead(404, headContentObject);
        response.write(e.message);
        response.end();
    });
    req.end();
}

/**
 * 价格排行
 * @param query
 * @param response
 */
function rankingprice(query,response) {
    var urlPath = "/solr/smile_product/select?q=*%3A*&sort=sj+asc&rows=100";
    var options = {
        host: solr_server_host,
        port: solr_server_port,
        path: urlPath,
        headers:{
            "Content-Type": applicationJson,
            "User-Agent": UserAgent
        }
    };
    var req = http.request(options, function(res) {
        res.setEncoding('utf8');
        try{
            var tmp = '';
            res.on('data', function (chunk) {
                tmp += chunk;
            });
            res.on('end',function() {
                var resss = JSON.parse(tmp);
                if(resss && resss.response && resss.response.docs){
                    var docs = resss.response.docs;
                    var result =  '[';
                    for(var i=0;i<docs.length;i++){
                        if(i > 0){
                            result += ',';
                        }
                        result += '{\"id\":';
                        result += docs[i].id;
                        result += ',\"n\":';
                        result += JSON.stringify(docs[i].n);
                        result += ',\"d\":';
                        result += JSON.stringify(docs[i].d);
                        result += ',\"bs\":';
                        result += JSON.stringify(docs[i].bs);
                        result += ',\"p\":';
                        result += JSON.stringify(docs[i].p);
                        result += ',\"sn\":';
                        result += JSON.stringify(docs[i].sn?docs[i].sn:0);
                        result += ',\"pp\":';
                        result += JSON.stringify(docs[i].pp?docs[i].pp:null);
                        result += ',\"lp\":';
                        result += JSON.stringify(docs[i].lp?docs[i].lp:null);
                        result += ',\"zsn\":';
                        result += JSON.stringify(docs[i].zsn?docs[i].zsn:0);
                        result += '}';
                    }

                    result += ']';
                } else {
                    result = "[]";
                }
                response.writeHead(200, headContentObject);
                response.write(addData(result));
                response.end();
            });
        } catch (ee){
            console.log(ee.message);
        }
    });
    req.on('error', function(e) {
        console.log("Got error: " + e.message);
        response.writeHead(404, headContentObject);
        response.write(e.message);
        response.end();
    });
    req.end();
}

/**
 * 搜索商品
 * @param query
 * @param response
 */
function search(query,response) {
    var param = '';
    var seqorderType = query.seqorderType;
    if(seqorderType!=null && seqorderType!=''){
        if(seqorderType=='time'){
            param += "&sort=st+desc";
        } else if(seqorderType=='discount'){
            param += "&sort=sd+desc";
        } else if(seqorderType=='sales'){
            param += "&sort=ss+desc";
        } else if(seqorderType=='monthsales'){
            param += "&sort=sy+desc";
        } else if(seqorderType=='price'){
            param += "&sort=sj+asc";
        } else {
            param += "&sort=ss+desc";
        }
    } else {
        param += "&sort=ss+desc";
    }
    //param += "&rows=5";
    var keywords = query.keywords;
    if(keywords==null || keywords==''){
        keywords = '*';
    }
    var urlPath = "/solr/smile_product/select?q=n%3A"+encodeURIComponent(keywords)+"+OR+d%3A"+encodeURIComponent(keywords)+"+OR+be%3A"+encodeURIComponent(keywords)+param;

    console.log("Got urlPath: " + urlPath);
    var options = {
        host: solr_server_host,
        port: solr_server_port,
        path: urlPath,
        headers:{
            "Content-Type": applicationJson,
            "User-Agent": UserAgent
        }
    };
    var req = http.request(options, function(res) {
       // console.log("res======== " + res);
        res.setEncoding('utf8');
        try{
            var tmp = '';
            res.on('data', function (chunk) {
                tmp += chunk;
            });
            console.log("tmp======== " + tmp);
            res.on('end',function() {
                var resss = JSON.parse(tmp);
                if(resss && resss.response && resss.response.docs){
                    var docs = resss.response.docs;
                    var result =  '[';
                    for(var i=0;i<docs.length;i++){
                        if(i > 0){
                            result += ',';
                        }
                        result += '{\"id\":';
                        result += docs[i].id;
                        result += ',\"n\":';
                        result += JSON.stringify(docs[i].n);
						result += ',\"d\":';
                        result += JSON.stringify(docs[i].d);
                        result += ',\"p\":';
                        result += JSON.stringify(docs[i].p);
                        result += ',\"pp\":';
                        result += JSON.stringify(docs[i].pp?docs[i].pp:null);
						result += ',\"lp\":';
                        result += JSON.stringify(docs[i].lp?docs[i].lp:null);
                        result += ',\"sn\":';
                        result += JSON.stringify(docs[i].sn?docs[i].sn:0);
                        result += ',\"zsn\":';
                        result += JSON.stringify(docs[i].zsn?docs[i].zsn:0);
                        result += ',\"seq\":';
                        if(seqorderType=='discount'){
                            result += JSON.stringify(docs[i].sd);
                        } else if(seqorderType=='time'){
                            result += JSON.stringify(docs[i].st);
                        } else if(seqorderType=='sales'){
                            result += JSON.stringify(docs[i].ss);
                        } else if(seqorderType=='monthsales'){
                            result += JSON.stringify(docs[i].sy);
                        } else if(seqorderType=='price'){
                            result += JSON.stringify(docs[i].sj);
                        } else {
                            result += JSON.stringify(docs[i].ss);
                        }
                        result += ',\"bs\":';
                        result += JSON.stringify(docs[i].bs);
                        result += '}';
                    }

                    result += ']';
                } else {
                    result = "[]";
                }
                response.writeHead(200, headContentObject);
                response.write(addData(result));
                response.end();
            });
        } catch (ee){
            console.log(ee.message);
        }
    });
    req.on('error', function(e) {
        console.log("Got error: " + e.message);
        response.writeHead(404, headContentObject);
        response.write(e.message);
        response.end();
    });
    req.end();
}
/**
 * 搜索商品更多
 * @param query
 * @param response
 */
function searchmore(query,response) {
    var param = '';
    var seqorderType = query.seqorderType;
    if(seqorderType!=null && seqorderType!=''){
        if(seqorderType=='time'){
            param += "&sort=st+desc";
        } else if(seqorderType=='discount'){
            param += "&sort=sd+desc";
        } else if(seqorderType=='sales'){
            param += "&sort=ss+desc";
        } else if(seqorderType=='monthsales'){
            param += "&sort=sy+desc";
        } else if(seqorderType=='price'){
            param += "&sort=sj+asc";
        } else {
            param += "&sort=ss+desc";
        }
    } else {
        param += "&sort=ss+desc";
    }

    var seqorderValue = query.seqorderValue;
    if(seqorderValue!=null && seqorderValue!=''){
        if(seqorderType=='time'){
            param += "&fq=st%3A%5B*+"+(seqorderValue-1)+"%5D";
        } else if(seqorderType=='discount'){
            param += "&fq=sd%3A%5B*+"+(seqorderValue-1)+"%5D";
        } else if(seqorderType=='sales'){
            param += "&fq=ss%3A%5B*+"+(seqorderValue-1)+"%5D";
        } else if(seqorderType=='monthsales'){
            param += "&fq=sy%3A%5B*+"+(seqorderValue-1)+"%5D";
        } else if(seqorderType=='price'){
            param += "&fq=sj%3A%5B"+(seqorderValue-(-1))+"+*%5D";
        } else {
            param += "&fq=ss%3A%5B*+"+(seqorderValue-1)+"%5D";
        }
    } else {
        //param += "&fq=ss%3A%5B*+"+(seqorderValue-1)+"%5D";
    }
    //param += "&rows=5";
    var keywords = query.keywords;
    if(keywords==null || keywords==''){
        keywords = '*';
    }
    var urlPath = "/solr/smile_product/select?q=n%3A"+encodeURIComponent(keywords)+"+OR+d%3A"+encodeURIComponent(keywords)+"+OR+be%3A"+encodeURIComponent(keywords)+param;

    //console.log("Got urlPath: " + urlPath);
    var options = {
        host: solr_server_host,
        port: solr_server_port,
        path: urlPath,
        headers:{
            "Content-Type": applicationJson,
            "User-Agent": UserAgent
        }
    };
    var req = http.request(options, function(res) {
        res.setEncoding('utf8');
        try{
            var tmp = '';
            res.on('data', function (chunk) {
                tmp += chunk;
            });
            res.on('end',function() {
                var resss = JSON.parse(tmp);
                if(resss && resss.response && resss.response.docs){
                    var docs = resss.response.docs;
                    var result =  '[';
                    for(var i=0;i<docs.length;i++){
                        if(i > 0){
                            result += ',';
                        }
                        result += '{\"id\":';
                        result += docs[i].id;
                        result += ',\"n\":';
                        result += JSON.stringify(docs[i].n);
						result += ',\"d\":';
                        result += JSON.stringify(docs[i].d);
                        result += ',\"p\":';
                        result += JSON.stringify(docs[i].p);
                        result += ',\"pp\":';
                        result += JSON.stringify(docs[i].pp?docs[i].pp:null);
						result += ',\"lp\":';
                        result += JSON.stringify(docs[i].lp?docs[i].lp:null);
                        result += ',\"sn\":';
                        result += JSON.stringify(docs[i].sn?docs[i].sn:0);
                        result += ',\"zsn\":';
                        result += JSON.stringify(docs[i].zsn?docs[i].zsn:0);
                        result += ',\"seq\":';
                        if(seqorderType=='discount'){
                            result += JSON.stringify(docs[i].sd);
                        } else if(seqorderType=='time'){
                            result += JSON.stringify(docs[i].st);
                        } else if(seqorderType=='sales'){
                            result += JSON.stringify(docs[i].ss);
                        } else if(seqorderType=='monthsales'){
                            result += JSON.stringify(docs[i].sy);
                        } else if(seqorderType=='price'){
                            result += JSON.stringify(docs[i].sj);
                        } else {
                            result += JSON.stringify(docs[i].ss);
                        }
                        result += ',\"bs\":';
                        result += JSON.stringify(docs[i].bs);
                        result += '}';
                    }

                    result += ']';
                } else {
                    result = "[]";
                }
                //console.log("result: " + result);
                response.writeHead(200, headContentObject);
                response.write(addData(result));
                response.end();
            });
        } catch (ee){
            console.log(ee.message);
        }
    });
    req.on('error', function(e) {
        console.log("Got error: " + e.message);
        response.writeHead(404, headContentObject);
        response.write(e.message);
        response.end();
    });
    req.end();
}

/**
 * 商品详情
 * @param query
 * @param response
 */
function goodsdetail(query,response) {
    var bs = query.bs;
    if(bs==null || bs==''){
        response.writeHead(200, headContentObject);
        response.write( '{"code":111112}');
        response.end();
    }
    var urlPath = "/solr/smile_product/select?&sort=sf+desc&q=bs%3A"+encodeURIComponent(bs);

    //console.log("Got urlPath: " + urlPath);
    var options = {
        host: solr_server_host,
        port: solr_server_port,
        path: urlPath,
        headers:{
            "Content-Type": applicationJson,
            "User-Agent": UserAgent
        }
    };
    var req = http.request(options, function(res) {
        //console.log("res======== " + res);
        res.setEncoding('utf8');
        try{
            var tmp = '';
            res.on('data', function (chunk) {
                tmp += chunk;
            });
            //console.log("tmp======== " + tmp);
            res.on('end',function() {
                var resss = JSON.parse(tmp);
                if(resss && resss.response && resss.response.docs){
                    var docs = resss.response.docs;
                    var result = '[';
                    for(var i=0;i<docs.length;i++){
                        if(i > 0){
                            result += ',';
                        }
                        result += '{\"id\":';
                        result += docs[i].id;
                        result += ',\"n\":';
                        result += JSON.stringify(docs[i].n);
						result += ',\"d\":';
                        result += JSON.stringify(docs[i].d);
                        result += ',\"p\":';
                        result += JSON.stringify(docs[i].bp);
                        result += ',\"bp\":';
                        result += JSON.stringify(docs[i].imgs);
                        result += ',\"pp\":';
                        result += JSON.stringify(docs[i].pp?docs[i].pp:0);
                        result += ',\"lp\":';
                        result += JSON.stringify(docs[i].lp?docs[i].lp:0);
                        /*result += ',\"sp\":';
                        result += JSON.stringify(docs[i].sp?docs[i].sp:0);*/
                        result += ',\"be\":';
                        result += JSON.stringify(docs[i].ce);

                        /*if(i==0){
                            result += ',\"c\":"黄色",\"ci\":"/color/00000.jpg"';
                        } else if(i==1){
                            result += ',\"c\":"黑色",\"ci\":"/color/00001.jpg"';
                        } else if(i==2){
                            result += ',\"c\":"中性粉色",\"ci\":"/color/00002.jpg"';
                        } else if(i==3){
                            result += ',\"c\":"大红色",\"ci\":"/color/00003.jpg"';
                        } else if(i==4){
                            result += ',\"c\":"黄褐色",\"ci\":"/color/00004.jpg"';
                        } else if(i==5){
                            result += ',\"c\":"米白色",\"ci\":"/color/00005.jpg"';
                        } else if(i==6){
                            result += ',\"c\":"矢车菊色",\"ci\":"/color/00006.jpg"';
                        } else if(i==7){
                            result += ',\"c\":"海军蓝",\"ci\":"/color/00007.jpg"';
                        }*/

                        result += ',\"c\":';
                        result += JSON.stringify(docs[i].c?docs[i].c:null);
                        result += ',\"ci\":';
                        result += JSON.stringify(docs[i].ci?docs[i].ci:null);

                        result += ',\"zsn\":';
                        result += JSON.stringify(docs[i].zsn?docs[i].zsn:0);
                        result += ',\"sn\":';
                        result += JSON.stringify(docs[i].sn?docs[i].sn:0);
                        result += ',\"bs\":';
                        result += JSON.stringify(docs[i].bs);
                        result += '}';
                    }

                    result += ']';
                } else {
                    result = "[]";
                }
                response.writeHead(200, headContentObject);
                response.write(addData(result));
                response.end();
            });
        } catch (ee){
            console.log(ee.message);
        }
    });
    req.on('error', function(e) {
        console.log("Got error: " + e.message);
        response.writeHead(404, headContentObject);
        response.write(e.message);
        response.end();
    });
    req.end();
}

exports.index = index;
exports.indexsingle = indexsingle;
exports.recommendbrand = recommendbrand;
exports.category = category;
exports.recommendactivity = recommendactivity;
exports.brand = brand;
exports.brandmore = brandmore;
exports.sorttype = sorttype;
exports.sort = sort;
exports.rankingdiscount = rankingdiscount;
exports.rankingsales = rankingsales;
exports.rankingprice = rankingprice;
exports.rankingmonthsales = rankingmonthsales;
exports.search = search;
exports.searchmore = searchmore;
exports.goodsdetail = goodsdetail;

function addData(tmp){
    if(tmp==''){
        tmp = null;
    }
    return '{"data":'+tmp+',"code":"200000"}';
}