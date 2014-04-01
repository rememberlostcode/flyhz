/**
 * Created by huoding on 14-2-18.
 */
var http = require("http");
var url = require("url");

function start(route, handle) {
    function onRequest(request, response) {
        try{
            var urlObj = url.parse(request.url,true,true);
            var headers = request.headers;
            //console.log("heades ->"+JSON.stringify(headers));
            console.log(JSON.stringify(urlObj));
            var pathname = urlObj.pathname.substring(12).split("/");

            var methodName = "";
            for(var i = 0;i<pathname.length;i++){
                methodName += pathname[i];
            }
            console.log(methodName);
            request.setEncoding("utf8");
            if(request.method.toLowerCase() == "get"){
                var query = urlObj.query;
                console.log("Request for " + pathname + " received.query = [" + JSON.stringify(query) + "]");
                route(handle, methodName,query,response);
            }else if(request.method.toLowerCase()== "post"){
                var postData = "";
                console.log("post");
                request.addListener("data",function(data){
                    postData += data;
                    //console.log("postData = " + postData);
                });
                request.addListener("end",function(){
                    //console.log("postData end = " + postData);
                    route(handle,pathname,postData,response);
                })
                console.log("contentType ->" + headers.content-type);
                if(headers.content-type == "multipart/form-data"){
                    console.log("upload");
                    var form = new formidable.IncomingForm();
                    form.parse(request, function(err,fields, files) {
                        //console.log('in if condition'+sys.inspect({fields: fields, files: files}));
                        fs.writeFile(files.upload.name, files.upload,'utf8', function (err) {
                            if (err) throw err;
                            console.log('It\'s saved!');
                        });
                    });
                }
            }
        } catch(e) {
            console.log(e.message);
        }
    }

    http.createServer(onRequest).listen(10000);
    console.log("Server has started.");
}

exports.start = start;
