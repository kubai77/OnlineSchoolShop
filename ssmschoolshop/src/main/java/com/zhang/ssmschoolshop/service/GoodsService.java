package com.zhang.ssmschoolshop.service;


import com.zhang.ssmschoolshop.entity.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface GoodsService {
    public Integer addGoods(Goods goods);

    public void addImagePath(ImagePath imagePath);

    public List<Goods> selectByExample(GoodsExample example);

    public void deleteGoodsById(Integer goodsid);

    public void updateGoodsById(Goods goods);

    public List<ImagePath> findImagePath(Integer goodsid);

    /**
     * 批量查询多个商品的图片，返回 goodsId -> List<ImagePath> 映射
     */
    public Map<Integer, List<ImagePath>> findImagePathByGoodsIds(List<Integer> goodsIds);

    public Goods selectById(Integer goodsid);

    public List<Goods> selectByExampleLimit(GoodsExample digGoodsExample);

    public void addFavorite(Favorite favorite);

    public Favorite selectFavByKey(FavoriteKey favoriteKey);

    /**
     * 批量查询某用户对一批商品的收藏态，返回已收藏的 goodsId 集合
     */
    public Set<Integer> selectFavGoodsIdsByUserAndGoodsIds(Integer userId, List<Integer> goodsIds);

    public void deleteFavByKey(FavoriteKey favoriteKey);

    public List<Favorite> selectFavByExample(FavoriteExample favoriteExample);
}
