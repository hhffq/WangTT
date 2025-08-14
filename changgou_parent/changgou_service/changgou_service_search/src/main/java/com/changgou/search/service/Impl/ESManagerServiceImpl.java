package com.changgou.search.service.Impl;

import com.alibaba.fastjson.JSON;
import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.pojo.SkuInfo;
import com.changgou.search.dao.ESManagerMapper;
import com.changgou.search.service.ESManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author lht
 * @date 2025-08-09 16:10
 */
@Service
public class ESManagerServiceImpl implements ESManagerService {


    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private ESManagerMapper esManagerMapper;

    @Autowired
    private SkuFeign skuFeign;

    /**
     * 创建索引库结构
     */
    @Override
    public void createIndexAndMapping() {
        // 创建索引
        elasticsearchTemplate.createIndex(SkuInfo.class);

        // 创建映射
        elasticsearchTemplate.putMapping(SkuInfo.class);
    }

    /**
     * 导入全部数据到ES索引库(导入全部Sku集合进入到索引库)
     */

    @Override
    public void importAll() {
        List<Sku> skuList = skuFeign.findSkuListBySpuId("all");

        if (skuList == null || skuList.size() <= 0) {
            throw new RuntimeException("当前没有数据被查询到，无法导入索引库");
        }

        // skuList 转换为 json
        String jsonSkuList = JSON.toJSONString(skuList);

        List<SkuInfo> skuInfoList = JSON.parseArray(jsonSkuList, SkuInfo.class);

        for (SkuInfo skuInfo : skuInfoList) {
            //将规格信息转换为map
            Map map = JSON.parseObject(skuInfo.getSpec(), Map.class);
            skuInfo.setSpecMap(map);
        }


        //导入索引库
        esManagerMapper.saveAll(skuInfoList);
    }

    /**
     * 根据spuid导入数据到ES索引库
     *
     * @param spuId 商品id
     */
    @Override
    public void importDataToESBySpuId(String spuId) {

        List<Sku> skuListBySpuId = skuFeign.findSkuListBySpuId(spuId);
        if (skuListBySpuId == null || skuListBySpuId.size() <= 0) {
            throw new RuntimeException("当前没有数据被查询到，无法导入索引库");
        }
        // skuList 转换为 json
        String jsonSkuList = JSON.toJSONString(skuListBySpuId);

        List<SkuInfo> skuInfoList = JSON.parseArray(jsonSkuList, SkuInfo.class);

        for (SkuInfo skuInfo : skuInfoList) {
            //将规格信息转换为map
            Map map = JSON.parseObject(skuInfo.getSpec(), Map.class);
            skuInfo.setSpecMap(map);
        }
        esManagerMapper.saveAll(skuInfoList);
    }

    /**
     * 根据spuid删除ES索引库
     *
     * @param spuId 商品id
     */
    @Override
    public void deleteBySpuId(String spuId) {
        List<Sku> skuList = skuFeign.findSkuListBySpuId(spuId);
        if (skuList == null || skuList.size() <= 0) {
            throw new RuntimeException("当前没有数据被查询到，无法导入索引库");
        }
        for (Sku sku : skuList) {
            esManagerMapper.deleteById(Long.valueOf(sku.getId()));
        }

    }
}
