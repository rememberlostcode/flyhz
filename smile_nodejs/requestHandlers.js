//var exec = require("child_process").exec;
var solr_server_host = "211.149.175.138";
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
        console.log(items);
        response.writeHead(200, {
            "Content-Type": applicationJson,
            "Access-Control-Allow-Origin":"*",
            'Access-Control-Allow-Methods': 'GET',
            'Access-Control-Allow-Headers': 'X-Requested-With,content-type'});
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
            var result = '[{"id":"1","p":"/activity/1.jpg"},' +
                '{"id":"2","p":"/activity/2.jpg"},' +
                '{"id":"3","p":"/activity/3.jpg"},' +
                '{"id":"4","p":"/activity/4.jpg"},' +
                '{"id":"5","p":"/activity/5.jpg"},' +
                '{"id":"6","p":"/activity/6.jpg"}]';
            /*for(var i=0;i<recommendindex.length;i++){
             if(i > 0){
             result += ',';
             }
             result += '{\"id\":';
             result += JSON.stringify(recommendindex[i].id);
             result += ',\"n\":';
             result += JSON.stringify(recommendindex[i].n);
             result += ',\"p\":';
             result += JSON.stringify(recommendindex[i].p);
             result += '}';
             }
             result += ']';*/
            response.writeHead(200, {
                "Content-Type": applicationJson,
                "Access-Control-Allow-Origin":"*",
                'Access-Control-Allow-Methods': 'GET',
                'Access-Control-Allow-Headers': 'X-Requested-With,content-type'});
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
        var result2 = '[{"id":"1","p":"/activity/1.jpg"},' +
            '{"id":"2","p":"/activity/2.jpg"},' +
            '{"id":"3","p":"/activity/3.jpg"},' +
            '{"id":"4","p":"/activity/4.jpg"},' +
            '{"id":"5","p":"/activity/5.jpg"},' +
            '{"id":"6","p":"/activity/6.jpg"}]';


        var result = '{"activity":'+result2+'';
        result += ',"brand":';
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
                memberfilter[2] = "p";
                memberfilter[3] = "pp";

                var recommendindex = JSON.parse(res);
                result += '[';
                for(var i=0;i<recommendindex.length;i++){
                    if(i>0){
                        result += ',';
                    }
                    result += '{\"id\":';
                    result += JSON.stringify(recommendindex[i].id);
                    result += ',\"n\":';
                    result += JSON.stringify(recommendindex[i].n);
                    result += ',\"gs\":';
                    result += JSON.stringify(recommendindex[i].gs,memberfilter);
                    result += '}';
                }
                result += ']';
            }
            result += '}';
            response.writeHead(200, {
                "Content-Type": applicationJson,
                "Access-Control-Allow-Origin":"*",
                'Access-Control-Allow-Methods': 'GET',
                'Access-Control-Allow-Headers': 'X-Requested-With,content-type'});
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
    console.log('key='+key);
    client.get(key, function(err, res) {
        //console.log(res);
        var result = '';
        if(res){
            var memberfilter = new Array();
            memberfilter[0] = "id";
            memberfilter[1] = "n";
            memberfilter[2] = "p";
            memberfilter[3] = "pp";

            var recommendindex = JSON.parse(res);
            result = '[';
            for(var i=0;i<recommendindex.length;i++){
                if(i>0){
                    result += ',';
                }
                result += '{\"id\":';
                result += JSON.stringify(recommendindex[i].id);
                result += ',\"n\":';
                result += JSON.stringify(recommendindex[i].n);
                result += ',\"gs\":';
                result += JSON.stringify(recommendindex[i].gs,memberfilter);
                result += '}';
            }
            result += ']';
        }
        response.writeHead(200, {
            "Content-Type": applicationJson,
            "Access-Control-Allow-Origin":"*",
            'Access-Control-Allow-Methods': 'GET',
            'Access-Control-Allow-Headers': 'X-Requested-With,content-type'});
        response.write(addData(result));
        response.end();
    });
}

function getRecommendBrand(query) {
    var key ='smile@brands@recommend';
    var param = '';
    var cid = query.cid;
    if(cid!=null && cid!=''){
        key = 'smile@brands@recommend&cates@' + cid;
    }
    console.log('key='+key);
    client.get(key, function(err, res) {
        //console.log(res);
        var result = '';
        if(res){
            var memberfilter = new Array();
            memberfilter[0] = "id";
            memberfilter[1] = "n";
            memberfilter[2] = "p";
            memberfilter[3] = "pp";

            var recommendindex = JSON.parse(res);
            result = '[';
            for(var i=0;i<recommendindex.length;i++){
                if(i>0){
                    result += ',';
                }
                result += '{\"id\":';
                result += JSON.stringify(recommendindex[i].id);
                result += ',\"n\":';
                result += JSON.stringify(recommendindex[i].n);
                result += ',\"gs\":';
                result += JSON.stringify(recommendindex[i].gs,memberfilter);
                result += '}';
            }
            result += ']';
        }
        return result;
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
        } else {
            param += "&sort=ss+desc";
        }
    } else {
        param += "&sort=ss+desc";
    }

    var bcparam = '';
    var bid = query.bid;
    if(bid==null || bid==''){
        response.writeHead(200, {
            "Content-Type": applicationJson,
            "Access-Control-Allow-Origin":"*",
            'Access-Control-Allow-Methods': 'GET',
            'Access-Control-Allow-Headers': 'X-Requested-With,content-type'});
        response.write('Bid can not empty!');
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
                        result += JSON.stringify(docs[i].id);
                        result += ',\"n\":';
                        result += JSON.stringify(docs[i].n);
                        result += ',\"p\":';
                        result += docs[i].p;
//                        result += ',\"t\":';
//                        result += JSON.stringify(docs[i].t).substring(0,17).replace('T',' ')+'\"';
//                        result += ',\"d\":';
//                        result += JSON.stringify(docs[i].d);
//                        result += ',\"lp\":';
//                        result += JSON.stringify(docs[i].lp?docs[i].lp:null);
//                        result += ',\"sp\":';
//                        result += JSON.stringify(docs[i].sp?docs[i].sp:null);

                        result += ',\"pp\":';
                        result += JSON.stringify(docs[i].pp?docs[i].pp:null);
                        result += ',\"seq\":';
                        if(seqorderType=='discount'){
                            result += JSON.stringify(docs[i].sd);
                        } else if(seqorderType=='time'){
                            result += JSON.stringify(docs[i].st);
                        } else if(seqorderType=='sales'){
                            result += JSON.stringify(docs[i].ss);
                            result += ',\"sn\":';
                            result += JSON.stringify(docs[i].sn?docs[i].sn:0);
                        } else {
                            result += JSON.stringify(docs[i].ss);
                            result += ',\"sn\":';
                            result += JSON.stringify(docs[i].sn?docs[i].sn:0);
                        }
                        result += '}';
                    }

                    result += ']';
                } else {
                    result = "[]";
                }
                response.writeHead(200, {
                    "Content-Type": applicationJson,
                    "Access-Control-Allow-Origin":"*",
                    'Access-Control-Allow-Methods': 'GET',
                    'Access-Control-Allow-Headers': 'X-Requested-With,content-type'});
                response.write(addData(result));
                response.end();
            });
        } catch (ee){
            console.log(ee.message);
        }
    });
    req.on('error', function(e) {
        console.log("Got error: " + e.message);
        response.writeHead(404, {
            "Content-Type": applicationJson,
            "Access-Control-Allow-Origin":"*",
            'Access-Control-Allow-Methods': 'GET',
            'Access-Control-Allow-Headers': 'X-Requested-With,content-type'});
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
        } else {
            param += "&fq=ss%3A%5B*+"+(seqorderValue-1)+"%5D";
        }
    } else {
        //param += "&fq=ss%3A%5B*+"+(seqorderValue-1)+"%5D";
    }

    var bcparam ='';
    var bid = query.bid;
    if(bid==null || bid==''){
        response.writeHead(200, {
            "Content-Type": applicationJson,
            "Access-Control-Allow-Origin":"*",
            'Access-Control-Allow-Methods': 'GET',
            'Access-Control-Allow-Headers': 'X-Requested-With,content-type'});
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
                        result += JSON.stringify(docs[i].id);
                        result += ',\"n\":';
                        result += JSON.stringify(docs[i].n);
                        result += ',\"p\":';
                        result += docs[i].p;
//                        result += ',\"t\":';
//                        result += JSON.stringify(docs[i].t).substring(0,17).replace('T',' ')+'\"';
//                        result += ',\"d\":';
//                        result += JSON.stringify(docs[i].d);
//                        result += ',\"lp\":';
//                        result += JSON.stringify(docs[i].lp?docs[i].lp:null);
//                        result += ',\"sp\":';
//                        result += JSON.stringify(docs[i].sp?docs[i].sp:null);

                        result += ',\"pp\":';
                        result += JSON.stringify(docs[i].pp?docs[i].pp:null);
                        result += ',\"seq\":';
                        if(seqorderType=='discount'){
                            result += JSON.stringify(docs[i].sd);
                        } else if(seqorderType=='time'){
                            result += JSON.stringify(docs[i].st);
                        } else if(seqorderType=='sales'){
                            result += JSON.stringify(docs[i].ss);
                            result += ',\"sn\":';
                            result += JSON.stringify(docs[i].sn?docs[i].sn:0);
                        } else {
                            result += JSON.stringify(docs[i].ss);
                            result += ',\"sn\":';
                            result += JSON.stringify(docs[i].sn?docs[i].sn:0);
                        }
                        result += '}';
                    }

                    result += ']';
                } else {
                    result = "[]";
                }
                //console.log("result: " + result);
                response.writeHead(200, {
                    "Content-Type": applicationJson,
                    "Access-Control-Allow-Origin":"*",
                    'Access-Control-Allow-Methods': 'GET',
                    'Access-Control-Allow-Headers': 'X-Requested-With,content-type'});
                response.write(addData(result));
                response.end();
            });
        } catch (ee){
            console.log(ee.message);
        }
    });
    req.on('error', function(e) {
        console.log("Got error: " + e.message);
        response.writeHead(404, {
            "Content-Type": applicationJson,
            "Access-Control-Allow-Origin":"*",
            'Access-Control-Allow-Methods': 'GET',
            'Access-Control-Allow-Headers': 'X-Requested-With,content-type'});
        response.write(e.message);
        response.end();
    });
    req.end();
}
function sorttype(query,response){
    var result = '[';

    result += '{';
    result += '"n":"销售量","v":"sales"';
    result += '}';

    result += ',';
    result += '{';
    result += '"n":"折扣","v":"discount"';
    result += '}';

    result += ']';

    response.writeHead(200, {
        "Content-Type": applicationJson,
        "Access-Control-Allow-Origin":"*",
        'Access-Control-Allow-Methods': 'GET',
        'Access-Control-Allow-Headers': 'X-Requested-With,content-type'});
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
    result += '"n":"折扣排行","u":"http://'+solr_server_host+'/smile/node/ranking/discount"';
    result += '}';

    result += ',';
    result += '{';
    result += '"n":"销量排行","u":"http://'+solr_server_host+'/smile/node/ranking/sales"';
    result += '}';

    result += ']';

    response.writeHead(200, {
        "Content-Type": applicationJson,
        "Access-Control-Allow-Origin":"*",
        'Access-Control-Allow-Methods': 'GET',
        'Access-Control-Allow-Headers': 'X-Requested-With,content-type'});
    response.write(addData(result));
    response.end();
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
                        result += JSON.stringify(docs[i].id);
                        result += ',\"n\":';
                        result += JSON.stringify(docs[i].n);
                        result += ',\"p\":';
                        result += docs[i].p;
//                        result += ',\"t\":';
//                        result += JSON.stringify(docs[i].t).substring(0,17).replace('T',' ')+'\"';
//                        result += ',\"d\":';
//                        result += JSON.stringify(docs[i].d);
//                        result += ',\"lp\":';
//                        result += JSON.stringify(docs[i].lp?docs[i].lp:null);
                        result += ',\"pp\":';
                        result += JSON.stringify(docs[i].pp?docs[i].pp:null);
//                        result += ',\"sp\":';
//                        result += JSON.stringify(docs[i].sp?docs[i].sp:null);
                        result += ',\"seq\":';
                        result += JSON.stringify(docs[i].sd);
                        result += '}';
                    }

                    result += ']';
                } else {
                    result = "[]";
                }
                response.writeHead(200, {
                    "Content-Type": applicationJson,
                    "Access-Control-Allow-Origin":"*",
                    'Access-Control-Allow-Methods': 'GET',
                    'Access-Control-Allow-Headers': 'X-Requested-With,content-type'});
                response.write(addData(result));
                response.end();
            });
        } catch (ee){
            console.log(ee.message);
        }
    });
    req.on('error', function(e) {
        console.log("Got error: " + e.message);
        response.writeHead(404, {
            "Content-Type": applicationJson,
            "Access-Control-Allow-Origin":"*",
            'Access-Control-Allow-Methods': 'GET',
            'Access-Control-Allow-Headers': 'X-Requested-With,content-type'});
        response.write(e.message);
        response.end();
    });
    req.end();
}

/**
 * 销量排行
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
                        result += JSON.stringify(docs[i].id);
                        result += ',\"n\":';
                        result += JSON.stringify(docs[i].n);
                        result += ',\"p\":';
                        result += docs[i].p;
                        result += ',\"sn\":';
                        result += JSON.stringify(docs[i].sn?docs[i].sn:0);
//                        result += ',\"t\":';
//                        result += JSON.stringify(docs[i].t).substring(0,17).replace('T',' ')+'\"';
//                        result += ',\"d\":';
//                        result += JSON.stringify(docs[i].d);
//                        result += ',\"lp\":';
//                        result += JSON.stringify(docs[i].lp?docs[i].lp:null);
                        result += ',\"pp\":';
                        result += JSON.stringify(docs[i].pp?docs[i].pp:null);
//                        result += ',\"sp\":';
//                        result += JSON.stringify(docs[i].sp?docs[i].sp:null);
                        result += ',\"seq\":';
                        result += JSON.stringify(docs[i].ss);
                        result += '}';
                    }

                    result += ']';
                } else {
                    result = "[]";
                }
                response.writeHead(200, {
                    "Content-Type": applicationJson,
                    "Access-Control-Allow-Origin":"*",
                    'Access-Control-Allow-Methods': 'GET',
                    'Access-Control-Allow-Headers': 'X-Requested-With,content-type'});
                response.write(addData(result));
                response.end();
            });
        } catch (ee){
            console.log(ee.message);
        }
    });
    req.on('error', function(e) {
        console.log("Got error: " + e.message);
        response.writeHead(404, {
            "Content-Type": applicationJson,
            "Access-Control-Allow-Origin":"*",
            'Access-Control-Allow-Methods': 'GET',
            'Access-Control-Allow-Headers': 'X-Requested-With,content-type'});
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
        console.log("res======== " + res);
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
                        result += JSON.stringify(docs[i].id);
                        result += ',\"n\":';
                        result += JSON.stringify(docs[i].n);
                        result += ',\"p\":';
                        result += docs[i].p;
//                        result += ',\"t\":';
//                        result += JSON.stringify(docs[i].t).substring(0,17).replace('T',' ')+'\"';
//                        result += ',\"d\":';
//                        result += JSON.stringify(docs[i].d);
//                        result += ',\"lp\":';
//                        result += JSON.stringify(docs[i].lp?docs[i].lp:null);
//                        result += ',\"sp\":';
//                        result += JSON.stringify(docs[i].sp?docs[i].sp:null);
                        result += ',\"pp\":';
                        result += JSON.stringify(docs[i].pp?docs[i].pp:null);
                        result += ',\"seq\":';
                        if(seqorderType=='discount'){
                            result += JSON.stringify(docs[i].sd);
                        } else if(seqorderType=='time'){
                            result += JSON.stringify(docs[i].st);
                        } else {
                            result += JSON.stringify(docs[i].ss);
                            result += ',\"sn\":';
                            result += JSON.stringify(docs[i].sn?docs[i].sn:0);
                        }
                        result += '}';
                    }

                    result += ']';
                } else {
                    result = "[]";
                }
                response.writeHead(200, {
                    "Content-Type": applicationJson,
                    "Access-Control-Allow-Origin":"*",
                    'Access-Control-Allow-Methods': 'GET',
                    'Access-Control-Allow-Headers': 'X-Requested-With,content-type'});
                response.write(addData(result));
                response.end();
            });
        } catch (ee){
            console.log(ee.message);
        }
    });
    req.on('error', function(e) {
        console.log("Got error: " + e.message);
        response.writeHead(404, {
            "Content-Type": applicationJson,
            "Access-Control-Allow-Origin":"*",
            'Access-Control-Allow-Methods': 'GET',
            'Access-Control-Allow-Headers': 'X-Requested-With,content-type'});
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
        } else {
            param += "&fq=ss%3A%5B*+"+(seqorderValue-1)+"%5D";
        }
    } else {
        //param += "&fq=st%3A%5B*+"+seqorderValue+"%5D";
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
                        result += JSON.stringify(docs[i].id);
                        result += ',\"n\":';
                        result += JSON.stringify(docs[i].n);
                        result += ',\"p\":';
                        result += docs[i].p;
//                        result += ',\"t\":';
//                        result += JSON.stringify(docs[i].t).substring(0,17).replace('T',' ')+'\"';
//                        result += ',\"d\":';
//                        result += JSON.stringify(docs[i].d);
//                        result += ',\"lp\":';
//                        result += JSON.stringify(docs[i].lp?docs[i].lp:null);
//                        result += ',\"sp\":';
//                        result += JSON.stringify(docs[i].sp?docs[i].sp:null);
                        result += ',\"pp\":';
                        result += JSON.stringify(docs[i].pp?docs[i].pp:null);
                        result += ',\"seq\":';
                        if(seqorderType=='discount'){
                            result += JSON.stringify(docs[i].sd);
                        } else if(seqorderType=='time'){
                            result += JSON.stringify(docs[i].st);
                        } else {
                            result += JSON.stringify(docs[i].ss);
                            result += ',\"sn\":';
                            result += JSON.stringify(docs[i].sn?docs[i].sn:0);
                        }
                        result += '}';
                    }

                    result += ']';
                } else {
                    result = "[]";
                }
                //console.log("result: " + result);
                response.writeHead(200, {
                    "Content-Type": applicationJson,
                    "Access-Control-Allow-Origin":"*",
                    'Access-Control-Allow-Methods': 'GET',
                    'Access-Control-Allow-Headers': 'X-Requested-With,content-type'});
                response.write(addData(result));
                response.end();
            });
        } catch (ee){
            console.log(ee.message);
        }
    });
    req.on('error', function(e) {
        console.log("Got error: " + e.message);
        response.writeHead(404, {
            "Content-Type": applicationJson,
            "Access-Control-Allow-Origin":"*",
            'Access-Control-Allow-Methods': 'GET',
            'Access-Control-Allow-Headers': 'X-Requested-With,content-type'});
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
    var id = query.id;
    if(id==null || id==''){
        response.writeHead(200, {
            "Content-Type": applicationJson,
            "Access-Control-Allow-Origin":"*",
            'Access-Control-Allow-Methods': 'GET',
            'Access-Control-Allow-Headers': 'X-Requested-With,content-type'});
        response.write('非法操作');
        response.end();
    }
    var key ='smile@products';
    client.hget(key,id, function(err, res) {
        console.log(res);
        var good = JSON.parse(res);
        var result = JSON.parse('{"id":null}');
        result.id = good.id;
        result.n = good.n;
        result.d = good.d;
        result.p = good.cover;
        result.lp = good.lp;
        result.pp = good.pp;
        result.sp = good.sp;
        result.bid = good.bid;
        result.be = good.be;
        result.cid = good.cid;
        result.ce = good.ce;
        result.ci = good.ci;
        result.c = good.c;
        result.zsn = good.zsn;
        result.sn = good.sn;

        response.writeHead(200, {
            "Content-Type": applicationJson,
            "Access-Control-Allow-Origin":"*",
            'Access-Control-Allow-Methods': 'GET',
            'Access-Control-Allow-Headers': 'X-Requested-With,content-type'});
//        response.write(addData(res));
        response.write(addData(JSON.stringify(result)));
        response.end();
    });
}

exports.index = index;
exports.recommendbrand = recommendbrand;
exports.category = category;
exports.recommendactivity = recommendactivity;
exports.brand = brand;
exports.brandmore = brandmore;
exports.sorttype = sorttype;
exports.sort = sort;
exports.rankingdiscount = rankingdiscount;
exports.rankingsales = rankingsales;
exports.search = search;
exports.searchmore = searchmore;
exports.goodsdetail = goodsdetail;

function addData(tmp){
    /*var res = JSON.parse('{"data":null}');
    res.data = tmp;
    return JSON.stringify(res);*/
    if(tmp==''){
        tmp = null;
    }
    return '{"data":'+tmp+'}';
}