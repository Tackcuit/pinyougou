//控制层
app.controller('itemCatController', function ($scope, $controller, itemCatService) {

    $controller('baseController', {$scope: $scope});//继承

    //读取列表数据绑定到表单中  
    $scope.findAll = function () {
        itemCatService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        );
    }

    //分页
    $scope.findPage = function (page, rows) {
        itemCatService.findPage(page, rows).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    //查询实体
    $scope.findOne = function (id) {
        itemCatService.findOne(id).success(
            function (response) {
                $scope.entity = response;
                var test = $scope.type_response;
                for (var i = 0; i < test.length; i++) {
                    if (test[i]["id"] == response.typeId) {
                        $scope.entity.typeId = test[i];
                        break;
                    }
                }
            }
        );
    }

    //保存
    $scope.save = function () {
        var serviceObject;//服务层对象
        if ($scope.entity.id != null) {//如果有ID
            $scope.entity.typeId = $scope.entity.typeId["id"];
            serviceObject = itemCatService.update($scope.entity); //修改
        } else {
            $scope.entity.parentId = 0;
            if ($scope.entity_1 != null) {
                $scope.entity.parentId = $scope.entity_1.id;
            }
            if ($scope.entity_2 != null) {
                $scope.entity.parentId = $scope.entity_2.id;
            }
            $scope.entity.typeId = $scope.entity.typeId["id"];
            serviceObject = itemCatService.add($scope.entity);//增加
        }
        serviceObject.success(
            function (response) {
                if (response.success) {
                    //重新查询
                    $scope.entity.parentId = 0;
                    if ($scope.entity_1 != null) {
                        $scope.entity.parentId = $scope.entity_1.id;
                    }
                    if ($scope.entity_2 != null) {
                        $scope.entity.parentId = $scope.entity_2.id;
                    }
                    $scope.findByParentId($scope.entity.parentId);//重新加载
                } else {
                    alert(response.message);
                }
            }
        );
    }


    //批量删除
    $scope.dele = function () {
        //获取选中的复选框
        itemCatService.dele($scope.selectIds).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();//刷新列表
                    $scope.selectIds = [];
                }
            }
        );
    }

    $scope.searchEntity = {};//定义搜索对象

    //搜索
    $scope.search = function (page, rows) {
        itemCatService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    // 根据父ID查询分类
    $scope.findByParentId = function (parentId) {
        itemCatService.findByParentId(parentId).success(function (response) {
            $scope.list = response;
        });
    }

    // 定义一个变量记录当前是第几级分类
    $scope.grade = 1;

    $scope.setGrade = function (value) {
        $scope.grade = value;
    }

    $scope.selectList = function (p_entity) {

        if ($scope.grade == 1) {
            $scope.entity_1 = null;
            $scope.entity_2 = null;
        }
        if ($scope.grade == 2) {
            $scope.entity_1 = p_entity;
            $scope.entity_2 = null;
        }
        if ($scope.grade == 3) {
            $scope.entity_2 = p_entity;
        }

        $scope.findByParentId(p_entity.id);
    }

    $scope.type_response = null;

    $scope.type_template = {data: []};
    // 查询关联的品牌信息:
    $scope.findBrandList = function () {
        itemCatService.selectOptionList().success(function (response) {
            $scope.type_response = response;
            $scope.type_template = {data: response};
        });
    }
});	
