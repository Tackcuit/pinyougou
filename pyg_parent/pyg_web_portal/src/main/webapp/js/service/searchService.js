app.service('searchService', function ($http) {


    this.search = function (searchMap) {
        return $http.post('itemsearch/search.do', searchMap);
    }

    this.getUrl = function (id) {
        return $http.post('itemsearch/getUrl.do?id=' + id);
    }

});