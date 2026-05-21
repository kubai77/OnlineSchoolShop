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
    public void deleteFavByKey(FavoriteKey favoriteKey) {
        favoriteMapper.deleteByPrimaryKey(favoriteKey);
    }

    @Override
    public List<Favorite> selectFavByExample(FavoriteExample favoriteExample) {
        return favoriteMapper.selectByExample(favoriteExample);
    }

    @Override
    public List<Goods> enrichGoodsWithDetails(List<Goods> goodsList, Integer userId) {
        if (goodsList == null || goodsList.isEmpty()) {
            return goodsList;
        }

        List<Integer> goodIds = new ArrayList<>();
        for (Goods goods : goodsList) {
            goodIds.add(goods.getGoodsid());
        }

        List<ImagePath> allImagePaths = imagePathMapper.selectByGoodIds(goodIds);
        Map<Integer, List<ImagePath>> imageMap = new HashMap<>();
        for (ImagePath imagePath : allImagePaths) {
            imageMap.computeIfAbsent(imagePath.getGoodid(), k -> new ArrayList<>()).add(imagePath);
        }

        Set<Integer> favoriteGoodIds = new HashSet<>();
        if (userId != null) {
            List<Favorite> favoriteList = favoriteMapper.selectByUserAndGoodIds(userId, goodIds);
            for (Favorite favorite : favoriteList) {
                favoriteGoodIds.add(favorite.getGoodsid());
            }
        }

        for (Goods goods : goodsList) {
            goods.setImagePaths(imageMap.getOrDefault(goods.getGoodsid(), new ArrayList<>()));
            goods.setFav(userId != null && favoriteGoodIds.contains(goods.getGoodsid()));
        }

        return goodsList;
    }
}
