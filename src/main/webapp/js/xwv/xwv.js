(function () {
    window.log = function (message) {
        console.log(message);
    };
    var UrlParams = [];
    var Params = document.location.search.substr(1).split('&');
    for (var param in Params) {
        var ParamPair = Params[param].split('=');
        UrlParams[ParamPair[0]] = decodeURI(ParamPair[1] || "");
    }
    window.getParam = function (name) {
        return UrlParams[name];
    };
})();
