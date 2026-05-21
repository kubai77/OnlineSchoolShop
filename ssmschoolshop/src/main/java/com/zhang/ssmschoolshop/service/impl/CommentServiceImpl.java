package com.zhang.ssmschoolshop.service.impl;


import com.zhang.ssmschoolshop.dao.CommentMapper;
import com.zhang.ssmschoolshop.dao.UserMapper;
import com.zhang.ssmschoolshop.entity.Comment;
import com.zhang.ssmschoolshop.entity.CommentExample;
import com.zhang.ssmschoolshop.entity.User;
import com.zhang.ssmschoolshop.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("commentService")
public class CommentServiceImpl implements CommentService {

    @Autowired(required = false)
    private CommentMapper commentMapper;

    @Autowired(required = false)
    private UserMapper userMapper;

    @Override
    public void insertSelective(Comment comment){
        commentMapper.insertSelective(comment);
    }

    @Override
    public List<Comment> selectByExample(CommentExample commentExample) {
        return commentMapper.selectByExample(commentExample);
    }

    @Override
    public List<Comment> selectCommentsWithUser(Integer goodsId) {
        CommentExample commentExample = new CommentExample();
        commentExample.or().andGoodsidEqualTo(goodsId);
        List<Comment> commentList = commentMapper.selectByExample(commentExample);

        if (commentList.isEmpty()) {
            return commentList;
        }

        List<Integer> userIds = new ArrayList<>();
        for (Comment comment : commentList) {
            userIds.add(comment.getUserid());
        }

        List<User> userList = userMapper.selectByUserIds(userIds);
        Map<Integer, User> userMap = new HashMap<>();
        for (User user : userList) {
            userMap.put(user.getUserid(), user);
        }

        for (Comment comment : commentList) {
            User user = userMap.get(comment.getUserid());
            if (user != null) {
                comment.setUserName(user.getUsername());
            }
        }

        return commentList;
    }

}
