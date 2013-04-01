//var exec = require("child_process").exec;
//var solr_server_host = "211.149.175.138";
var solr_server_host = "10.22.22.40";
var solr_server_port = 8080;
var applicationJson = "application/json; charset=utf-8";
var UserAgent = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.154 Safari/537.36";
var http = require("http");
var redis = require("redis");
//var client = redis.createClient(6379,'211.149.175.138');
var client = redis.createClient(6379,'10.22.23.63');
client.on('error', function (err) {
    console.log('Error ' + err);
});


function list(query,response) {
    response.writeHead(200, {
        "Content-Type": applicationJson,
        "Access-Control-Allow-Origin":"*",
        'Access-Control-Allow-Methods': 'GET',
        'Access-Control-Allow-Headers': 'X-Requested-With,content-type'});
    response.write("测试接口");
    response.end();

}

exports.list = list;