package cn.hm.core.controller;

import cn.hm.core.pojo.entity.Cart;
import cn.hm.core.pojo.entity.Result;
import cn.hm.core.service.CartService;
import cn.hm.core.utils.Constants;
import cn.hm.core.utils.CookieUtil;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Reference
    private CartService cartService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;


    @RequestMapping("/addGoodsToCartList")
    @CrossOrigin(origins = "http://localhost:8082", allowCredentials = "true")
    public Result addGoodsToCartList(Long itemId, Integer num) {
        try {
            //获取登陆名
            String userName = SecurityContextHolder.getContext().getAuthentication().getName();
            List<Cart> allCartList = findCartList();
            allCartList = cartService.addCart(allCartList, itemId, num);
            //如果没有登陆或者登陆过期则返回一个"anonymousUser"字符串
            if ("anonymousUser".equals(userName)) {
                //没登陆  添加到cookie
                //生成JSON
                String cartListStr = JSON.toJSONString(allCartList);
                //保存JSON到Cookie
                CookieUtil.setCookie(request, response, Constants.CART_COOKIE_NAME, cartListStr, 3600 * 24 * 30, "utf-8");
            } else {
                //登陆了
                cartService.saveCartListToRedis(userName, allCartList);
            }
            //返回成功
            return new Result(true, "保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "保存失败");
        }

    }


    @RequestMapping("/findCartList")
    public List<Cart> findCartList() {

        //获取登陆名
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        //因为怎么都要确认cookie中的购物信息,所以先获取
        String cartJson = CookieUtil.getCookieValue(request, Constants.CART_COOKIE_NAME,"utf-8");
        if (cartJson == null || "".equals(cartJson)) {
            cartJson = "[]";
        }
        //从cookie反序列化
        List<Cart> cartListFromCookie = JSON.parseArray(cartJson, Cart.class);
        //如果没有登陆或者登陆过期则返回一个"anonymousUser"字符串
        if ("anonymousUser".equals(userName)) {
            //没登陆
            return cartListFromCookie;
        } else {
            //登陆了
            List<Cart> cartListFromRedis = cartService.getCartListFromRedis(userName);
            List<Cart> allCart = cartService.copyAllCart(cartListFromRedis, cartListFromCookie);
            CookieUtil.deleteCookie(request, response, Constants.CART_COOKIE_NAME);
            return allCart;
        }

    }

}
