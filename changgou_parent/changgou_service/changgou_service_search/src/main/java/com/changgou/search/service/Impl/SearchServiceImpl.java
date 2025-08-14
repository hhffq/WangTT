package com.changgou.search.service.Impl;

import com.alibaba.fastjson.JSON;
import com.changgou.pojo.SkuInfo;
import com.changgou.search.service.SearchService;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lht
 * @date 2025-08-10 14:13
 */
@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private ElasticsearchTemplate esTemplate;

    //设置每页查询条数据
    public final static Integer PAGE_SIZE = 20;

    @Override
    public Map search(Map<String, String> searchMap) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();

        //有条件才查询Es
        if (null != searchMap) {
            //组合条件对象
            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();


            // keywords里面的是 name 分类 可以是手机和 老花镜等

            //0:关键词
            if (!StringUtils.isEmpty(searchMap.get("keywords"))) {
                System.out.println("keywords:" + searchMap.get("keywords"));
                boolQuery.must(QueryBuilders.matchQuery("name", searchMap.get("keywords")).operator(Operator.AND));
            }


            // 1:: 条件查询 品牌
            if (!StringUtils.isEmpty(searchMap.get("brand"))) {
                System.out.println("keywords:" + searchMap.get("brand"));
                boolQuery.filter(QueryBuilders.matchQuery("brandName", searchMap.get("brand")).operator(Operator.AND));
            }


            //2::查询规格
            for (String key : searchMap.keySet()) {

                if (key.startsWith("spec_")) {
                    String value = searchMap.get(key).replace("%2B", "+");
                    boolQuery.filter(QueryBuilders.termQuery("specMap." + key.substring(5) + ".keyword" , value));
                }
            }


            //4. 原生搜索实现类
            NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
            nativeSearchQueryBuilder.withQuery(boolQuery);


            // 品牌桶聚合(分组查询)
            String skuBrand = "skuBrand";
            nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms(skuBrand).field("brandName.keyword"));


            // 规格桶聚合(分组查询)
            String skuSpec = "skuSpec";
            nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms(skuSpec).field("spec.keyword"));

            //10: 执行查询, 返回结果对象
            AggregatedPage<SkuInfo> aggregatedPage = esTemplate.queryForPage(nativeSearchQueryBuilder.build(), SkuInfo.class, new SearchResultMapper() {
                @Override
                public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {

                    List<T> list = new ArrayList<>();

                    SearchHits hits = searchResponse.getHits();
                    if (null != hits) {
                        for (SearchHit hit : hits) {
                            SkuInfo skuInfo = JSON.parseObject(hit.getSourceAsString(), SkuInfo.class);

                            list.add((T) skuInfo);
                        }
                    }
                    return new AggregatedPageImpl<T>(list, pageable, hits.getTotalHits(), searchResponse.getAggregations());
                }
            });

            //11. 总条数
            resultMap.put("total", aggregatedPage.getTotalElements());
            //12. 总页数
            resultMap.put("totalPages", aggregatedPage.getTotalPages());
            //13. 查询结果集合
            resultMap.put("rows", aggregatedPage.getContent());


            // 获取品牌聚合结果
            StringTerms brandTerms = (StringTerms) aggregatedPage.getAggregation(skuBrand);
            List<String> brandList = brandTerms.getBuckets().stream().map(bucket -> bucket.getKeyAsString()).collect(Collectors.toList());
            resultMap.put("brandList", brandList);

            // 获取规格聚合结果
            StringTerms specTerms = (StringTerms) aggregatedPage.getAggregation(skuSpec);
            List<String> specList = brandTerms.getBuckets().stream().map(bucket -> bucket.getKeyAsString()).collect(Collectors.toList());
            resultMap.put("specList", specList);

            return resultMap;
        }
        return null;
    }
}
