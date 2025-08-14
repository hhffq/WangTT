package com.changgou.goods.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.entity.util.IdWorker;
import com.changgou.goods.dao.BrandMapper;
import com.changgou.goods.dao.CategoryMapper;
import com.changgou.goods.dao.SkuMapper;
import com.changgou.goods.dao.SpuMapper;
import com.changgou.goods.pojo.*;
import com.changgou.goods.service.SpuService;
import com.changgou.system.dao.CategoryBrandMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class SpuServiceImpl implements SpuService {


    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private CategoryBrandMapper categoryBrandMapper;

    /**
     * 新增数据  sku+ spu列表
     *
     * @param goods
     */
    @Override
    public void addGoods(Goods goods) {
        // spu
        Spu spu = goods.getSpu();


        spu.setId(String.valueOf(idWorker.nextId()));
        spu.setIsDelete("0");
        spu.setIsMarketable("0");
        spu.setStatus("0");
        spu.setCreateTime(new Date());
        spuMapper.insertSelective(spu);

        // sku的列表
        List<Sku> skuList = goods.getSkuList();

        //保存sku集合数据到数据库
        saveSkuList(goods);
    }

    private void saveSkuList(Goods goods) {
        // spu
        Spu spu = goods.getSpu();

        // SELECT id, name, description FROM brand WHERE id = #{brandId};
        Brand brand = brandMapper.selectByPrimaryKey(spu.getBrandId());

        Category category = categoryMapper.selectByPrimaryKey(spu.getCategory3Id());

        // sku的列表
        List<Sku> skuList = goods.getSkuList();
        if (skuList != null) {
            for (Sku sku : skuList) {
                sku.setId(String.valueOf(idWorker.nextId()));

                if (sku.getSpec() == null || "".equals(sku.getSpec())) {
                    sku.setSpec("{}");
                }
                // 设置sku名称(商品名称 + 规格)
                String name = spu.getName();
                Map<String, String> map = JSON.parseObject(sku.getSpec(), Map.class);
                if (map != null && map.size() > 0) {
                    for (String value : map.values()) {
                        name += "" + value;
                    }
                }
                sku.setName(name);
                sku.setSpuId(spu.getId());
                sku.setCreateTime(new Date());
                sku.setUpdateTime(new Date());
                sku.setCategoryId(category.getId());
                sku.setCategoryName(category.getName());
                sku.setBrandName(category.getName());

                skuMapper.insertSelective(sku);//插入sku表数据
            }

        }

    }

    /**
     * 根据id查询sku和spu
     * ID 为spu的id
     */
    @Override
    public Goods selectById(String id) {
        if (id == null) {
            throw new RuntimeException("id为空");
        }
        Spu spu = spuMapper.selectByPrimaryKey(id);


        List<Sku> skuList = skuMapper.selectBySpuId(spu.getId());


        Goods goods = new Goods();
        goods.setSpu(spu);
        goods.setSkuList(skuList);


        return goods;
    }


    /**
     * 品牌与分类关联  将  分类ID 与   SPU的品牌ID 一起插入到  tb_category_brand 表中
     */
    @Override
    public void saveCategoryIdSpuId(Goods goods) {

        Spu spu = goods.getSpu();

        CategoryBrand categoryBrand = new CategoryBrand();
        Integer brandId = spu.getBrandId();
        Brand brand = brandMapper.selectByPrimaryKey(brandId);


        Integer categoryId = spu.getCategory3Id();


        categoryBrand.setCategoryId(categoryId);
        categoryBrand.setBrandId(brandId);


        Category category = categoryMapper.selectByPrimaryKey(categoryId);


        int count = categoryBrandMapper.selectCount(categoryBrand);
        if (count == 0) {
            categoryBrandMapper.insert(categoryBrand);
        }


        // 判断是否有这个品牌和分类的关系数据
        List<Sku> skuList = goods.getSkuList();


        if (skuList != null) {
            for (Sku sku : skuList) {

                if (sku.getSpec() == "" || sku.getSpec() == null) {
                    sku.setSpec("{}");
                }


                String name = sku.getName();
                Map<String, String> map = JSON.parseObject(sku.getSpec(), Map.class);
                if (map != null && map.size() > 0) {
                    for (String value : map.values()) {
                        name += "" + value;
                    }
                }
                sku.setId(String.valueOf(idWorker.nextId()));
                sku.setName(name);
                sku.setBrandName(brand.getName());
                sku.setSpuId(spu.getId());
                sku.setCategoryId(category.getId());
                sku.setSaleNum(0);
                sku.setCommentNum(0);

                sku.setCreateTime(new Date());
                sku.setUpdateTime(new Date());

                skuMapper.insertSelective(sku);
            }
        }
    }

    /**
     * 保存修改
     */
    @Override
    public void updateByIdSpuId(Goods goods) {

        //取出spu部分
        Spu spu = goods.getSpu();
        spuMapper.updateByPrimaryKey(spu);


        //删除原sku列表
        // delete   * from tb_sku where spuId = #{id}
        Example example = new Example(Sku.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("spuId", spu.getId());
        skuMapper.deleteByExample(example);


        saveSkuList(goods);
    }

    /**
     * 查询全部列表
     *
     * @return
     */
    @Override
    public List<Spu> findAll() {
        return spuMapper.selectAll();
    }

    /**
     * 根据ID查询
     *
     * @param id
     * @return
     */
//    @Override
//    public Spu findById(String id) {
//        return spuMapper.selectByPrimaryKey(id);
//    }


    /**
     * 增加
     *
     * @param spu
     */
    @Override
    public void add(Spu spu) {
        spuMapper.insert(spu);
    }


    /**
     * 修改
     *
     * @param spu
     */
    @Override
    public void update(Spu spu) {
        spuMapper.updateByPrimaryKey(spu);
    }

    /**
     * 删除
     *
     * @param id
     */
    @Override
    public void delete(String id) {
        spuMapper.deleteByPrimaryKey(id);
    }


    /**
     * 条件查询
     *
     * @param searchMap
     * @return
     */
    @Override
    public List<Spu> findList(Map<String, Object> searchMap) {
        Example example = createExample(searchMap);
        return spuMapper.selectByExample(example);
    }

    /**
     * 分页查询
     *
     * @param page
     * @param size
     * @return
     */
    @Override
    public Page<Spu> findPage(int page, int size) {
        PageHelper.startPage(page, size);
        return (Page<Spu>) spuMapper.selectAll();
    }

    /**
     * 条件+分页查询
     *
     * @param searchMap 查询条件
     * @param page      页码
     * @param size      页大小
     * @return 分页结果
     */
    @Override
    public Page<Spu> findPage(Map<String, Object> searchMap, int page, int size) {
        PageHelper.startPage(page, size);
        Example example = createExample(searchMap);
        return (Page<Spu>) spuMapper.selectByExample(example);
    }

    /**
     * 构建查询对象
     *
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap) {
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        if (searchMap != null) {
            // 主键
            if (searchMap.get("id") != null && !"".equals(searchMap.get("id"))) {
                criteria.andEqualTo("id", searchMap.get("id"));
            }
            // 货号
            if (searchMap.get("sn") != null && !"".equals(searchMap.get("sn"))) {
                criteria.andEqualTo("sn", searchMap.get("sn"));
            }
            // SPU名
            if (searchMap.get("name") != null && !"".equals(searchMap.get("name"))) {
                criteria.andLike("name", "%" + searchMap.get("name") + "%");
            }
            // 副标题
            if (searchMap.get("caption") != null && !"".equals(searchMap.get("caption"))) {
                criteria.andLike("caption", "%" + searchMap.get("caption") + "%");
            }
            // 图片
            if (searchMap.get("image") != null && !"".equals(searchMap.get("image"))) {
                criteria.andLike("image", "%" + searchMap.get("image") + "%");
            }
            // 图片列表
            if (searchMap.get("images") != null && !"".equals(searchMap.get("images"))) {
                criteria.andLike("images", "%" + searchMap.get("images") + "%");
            }
            // 售后服务
            if (searchMap.get("saleService") != null && !"".equals(searchMap.get("saleService"))) {
                criteria.andLike("saleService", "%" + searchMap.get("saleService") + "%");
            }
            // 介绍
            if (searchMap.get("introduction") != null && !"".equals(searchMap.get("introduction"))) {
                criteria.andLike("introduction", "%" + searchMap.get("introduction") + "%");
            }
            // 规格列表
            if (searchMap.get("specItems") != null && !"".equals(searchMap.get("specItems"))) {
                criteria.andLike("specItems", "%" + searchMap.get("specItems") + "%");
            }
            // 参数列表
            if (searchMap.get("paraItems") != null && !"".equals(searchMap.get("paraItems"))) {
                criteria.andLike("paraItems", "%" + searchMap.get("paraItems") + "%");
            }
            // 是否上架
            if (searchMap.get("isMarketable") != null && !"".equals(searchMap.get("isMarketable"))) {
                criteria.andEqualTo("isMarketable", searchMap.get("isMarketable"));
            }
            // 是否启用规格
            if (searchMap.get("isEnableSpec") != null && !"".equals(searchMap.get("isEnableSpec"))) {
                criteria.andEqualTo("isEnableSpec", searchMap.get("isEnableSpec"));
            }
            // 是否删除
            if (searchMap.get("isDelete") != null && !"".equals(searchMap.get("isDelete"))) {
                criteria.andEqualTo("isDelete", searchMap.get("isDelete"));
            }
            // 审核状态
            if (searchMap.get("status") != null && !"".equals(searchMap.get("status"))) {
                criteria.andEqualTo("status", searchMap.get("status"));
            }

            // 品牌ID
            if (searchMap.get("brandId") != null) {
                criteria.andEqualTo("brandId", searchMap.get("brandId"));
            }
            // 一级分类
            if (searchMap.get("category1Id") != null) {
                criteria.andEqualTo("category1Id", searchMap.get("category1Id"));
            }
            // 二级分类
            if (searchMap.get("category2Id") != null) {
                criteria.andEqualTo("category2Id", searchMap.get("category2Id"));
            }
            // 三级分类
            if (searchMap.get("category3Id") != null) {
                criteria.andEqualTo("category3Id", searchMap.get("category3Id"));
            }
            // 模板ID
            if (searchMap.get("templateId") != null) {
                criteria.andEqualTo("templateId", searchMap.get("templateId"));
            }
            // 运费模板id
            if (searchMap.get("freightId") != null) {
                criteria.andEqualTo("freightId", searchMap.get("freightId"));
            }
            // 销量
            if (searchMap.get("saleNum") != null) {
                criteria.andEqualTo("saleNum", searchMap.get("saleNum"));
            }
            // 评论数
            if (searchMap.get("commentNum") != null) {
                criteria.andEqualTo("commentNum", searchMap.get("commentNum"));
            }

        }
        return example;
    }

}
