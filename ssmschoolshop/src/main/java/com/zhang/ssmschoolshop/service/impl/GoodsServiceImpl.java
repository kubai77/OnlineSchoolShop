package com.zhang.ssmschoolshop.service.impl;


import com.zhang.ssmschoolshop.dao.FavoriteMapper;
import com.zhang.ssmschoolshop.dao.GoodsMapper;
import com.zhang.ssmschoolshop.dao.ImagePathMapper;
import com.zhang.ssmschoolshop.entity.*;
import com.zhang.ssmschoolshop.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    public List<Goods> enrichGoodsList(List<Goods> goodsList, Integer userid) {
        if (goodsList == null || goodsList.isEmpty()) {
            return goodsList;
        }

        List<Integer> goodsIdList = getGoodsIdList(goodsList);
        Map<Integer, List<ImagePath>> imagePathMap = getImagePathMap(goodsIdList);
        Set<Integer> favoriteGoodsIdSet = getFavoriteGoodsIdSet(userid, goodsIdList);

        for (Goods goods : goodsList) {
            List<ImagePath> imagePathList = imagePathMap.get(goods.getGoodsid());
            if (imagePathList == null) {
                imagePathList = new ArrayList<>();
            }
            goods.setImagePaths(imagePathList);
            goods.setFav(favoriteGoodsIdSet.contains(goods.getGoodsid()));
        }
        return goodsList;
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

    private List<Integer> getGoodsIdList(List<Goods> goodsList) {
        Set<Integer> goodsIdSet = new LinkedHashSet<Integer>();
        for (Goods goods : goodsList) {
            if (goods != null && goods.getGoodsid() != null) {
                goodsIdSet.add(goods.getGoodsid());
            }
        }
        return new ArrayList<Integer>(goodsIdSet);
    }

    private Map<Integer, List<ImagePath>> getImagePathMap(List<Integer> goodsIdList) {
        Map<Integer, List<ImagePath>> imagePathMap = new HashMap<Integer, List<ImagePath>>();
        if (goodsIdList.isEmpty()) {
            return imagePathMap;
        }

        ImagePathExample imagePathExample = new ImagePathExample();
        imagePathExample.or().andGoodidIn(goodsIdList);
        List<ImagePath> imagePathList = imagePathMapper.selectByExample(imagePathExample);

        for (ImagePath imagePath : imagePathList) {
            Integer goodsid = imagePath.getGoodid();
            List<ImagePath> currentImagePathList = imagePathMap.get(goodsid);
            if (currentImagePathList == null) {
                currentImagePathList = new ArrayList<ImagePath>();
                imagePathMap.put(goodsid, currentImagePathList);
            }
            currentImagePathList.add(imagePath);
        }
        return imagePathMap;
    }

    private Set<Integer> getFavoriteGoodsIdSet(Integer userid, List<Integer> goodsIdList) {
        Set<Integer> favoriteGoodsIdSet = new HashSet<Integer>();
        if (userid == null || goodsIdList.isEmpty()) {
            return favoriteGoodsIdSet;
        }

        FavoriteExample favoriteExample = new FavoriteExample();
        favoriteExample.or().andUseridEqualTo(userid).andGoodsidIn(goodsIdList);
        List<Favorite> favoriteList = favoriteMapper.selectByExample(favoriteExample);

        for (Favorite favorite : favoriteList) {
            favoriteGoodsIdSet.add(favorite.getGoodsid());
        }
        return favoriteGoodsIdSet;
    }
}
