package cn.hm.core.service;

import cn.hm.core.pojo.seller.Seller;
import cn.hm.core.service.SellerService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

public class UserDetailServiceImpl implements UserDetailsService {

    private SellerService sellerService;

    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //设置这个用户所应该拥有的权限集合
        if (username != null && !"".equals(username.trim())) {
            List<GrantedAuthority> authList = new ArrayList<>();
            if (authList != null) {
                authList.add(new SimpleGrantedAuthority("ROLE_SELLER"));
                if (username != null) {
                    Seller seller = sellerService.findOne(username);
                    if (seller != null) {
                        return new User(username, seller.getPassword(), authList);
                    }
                }
            }
        }
        return null;
    }


}
