package com.zhang.ssmschoolshop.service.impl;


import com.zhang.ssmschoolshop.dao.FavoriteMapper;
import com.zhang.ssmschoolshop.dao.GoodsMapper;
import com.zhang.ssmschoolshop.dao.ImagePathMapper;
import com.zhang.ssmschoolshop.entity.*;
import com.zhang.ssmschoolshop.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("goodsService")
public class GoodsServiceImpl implements GoodsService {

    @Autowired(required = false)
    GoodsMapper goodsMapper;

    @Autowired(required = false)
    ImagePathMapper imagePathMapper;

    @Autowired(required = false)
    FavoriteMapper favoriteMapper;

    @Override
    public Integer addGoods(Goods goods) {
        goodsMapper.insertSelective(goods);
        return goods.getGoodsid();
    }

    @Override
    public void addImagePath(ImagePath imagePath) {
        imagePathMapper.insertSelective(imagePath);
    }

    @Override
    public List<Goods> selectByExample(GoodsExample example) {
        return goodsMapper.selectByExampleWithBLOBs(example);
    }

    @Override
    public void deleteGoodsById(Integer goodsid) {

        goodsMapper.deleteByPrimaryKey(goodsid);
    }

    @Override
    public void updateGoodsById(Goods goods) {
        goodsMapper.updateByPrimaryKeySelective(goods);
    }

    @Override
    public List<ImagePath> findImagePath(Integer goodsid) {
        ImagePathExample imagePathExample = new ImagePathExample();
        imagePathExample.or().andGoodidEqualTo(goodsid);

        return imagePathMapper.selectByExample(imagePathExample);
    }

    @Override
    public Map<Integer, List<ImagePath>> findImagePathByGoodsIds(List<Integer> goodsIds) {
        if (goodsIds == null || goodsIds.isEmpty()) {
            return Collections.emptyMap();
        }
        ImagePathExample example = new ImagePathExample();
        example.or().andGoodidIn(goodsIds);
        List<ImagePath> allImages = imagePathMapper.selectByExample(example);

        Map<Integer, List<ImagePath>> result = new HashMap<Integer, List<ImagePath>>();
        for (ImagePath img : allImages) {
            Integer gid = img.getGoodid();
            List<ImagePath> list = result.get(gid);
            if (list == null) {
                list = new ArrayList<ImagePath>();
                result.put(gid, list);
            }
            list.add(img);
        }
        // 确保每个 goodsId 都有 entry（即使没有图片也返回空列表）
        for (Integer gid : goodsIds) {
            if (!result.containsKey(gid)) {
                result.put(gid, Collections.<ImagePath>emptyList());
            }
        }
        return result;
    }

    @Override
    public Goods selectById(Integer goodsid) {
        return goodsMapper.selectByPrimaryKey(goodsid);
    }

    @Override
    public List<Goods> selectByExampleLimit(GoodsExample digGoodsExample) {
        return goodsMapper.selectByExampleWithBLOBsLimit(digGoodsExample);
    }

    @Override
    public void addFavorite(Favorite favorite) {
        favoriteMapper.insertSelective(favorite);
    }

    @Override
    public Favorite selectFavByKey(FavoriteKey favoriteKey) {
        return favoriteMapper.selectByPrimaryKey(favoriteKey);
    }

    @Override
    public Set<Integer> selectFavGoodsIdsByUserAndGoodsIds(Integer userId, List<Integer> goodsIds) {
        if (userId == null || goodsIds == null || goodsIds.isEmpty()) {
            return Collections.emptySet();
        }
        FavoriteExample example = new FavoriteExample();
        example.or().andUseridEqualTo(userId).andGoodsidIn(goodsIds);
        List<Favorite> favList = favoriteMapper.selectByExample(example);

        Set<Integer> favGoodsIds = new HashSet<>();
        for (Favorite fav : favList) {
            favGoodsIds.add(fav.getGoodsid());
        }
        return favGoodsIds;
    }

    @Override
    public void deleteFavByKey(FavoriteKey favoriteKey) {
        favoriteMapper.deleteByPrimaryKey(favoriteKey);
    }

    @Override
    public List<Favorite> selectFavByExample(FavoriteExample favoriteExample) {
        return favoriteMapper.selectByExample(favoriteExample);
    }
}
