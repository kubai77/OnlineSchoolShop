package com.zhang.ssmschoolshop.service;


import com.zhang.ssmschoolshop.entity.User;
import com.zhang.ssmschoolshop.entity.UserExample;

import java.util.List;
import java.util.Map;

public interface UserService {
    public User selectByPrimaryKey(int userId);
    /*public User selectByPrimaryKeyAndPassword(int userId,String password);*/
    public List<User> selectByExample(UserExample userExample);

    /**
     * 批量查询用户，返回 userId -> User 映射
     */
    public Map<Integer, User> selectByPrimaryKeys(List<Integer> userIds);

    public void insertSelective(User user);

    public void deleteUserById(Integer userid);

    public void updateByPrimaryKeySelective(User user);

}
