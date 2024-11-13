package com.hmdp.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.hmdp.entity.ShopType;
import com.hmdp.mapper.ShopTypeMapper;
import com.hmdp.service.IShopTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.utils.RedisConstants;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopTypeService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public List<ShopType> queryTypeList() {
        String key = RedisConstants.CACHE_TYPE_KEY;
        // 1.从redis中查询店铺类型
        String shopTypeJson = stringRedisTemplate.opsForValue().get(key);

        // 2.判断是否存在
        if (StrUtil.isNotBlank(shopTypeJson)) {
            // 3.存在，转化为list返回
            return JSONUtil.toList(shopTypeJson, ShopType.class);
        }

        // 4.不存在，查询数据库
        List<ShopType> typeList = this.query().orderByAsc("sort").list();
        // 5.不存在，返回空列表
        if (typeList.isEmpty()) {
            return new ArrayList<>();
        }

        // 6.存在，写入redis
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(typeList));

        // 7.返回
        return typeList;
    }
}
