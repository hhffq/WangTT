package com.changgou.goods.controller;

import com.changgou.entity.PageResult;
import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import com.changgou.goods.pojo.Goods;
import com.changgou.goods.service.SpuService;
import com.changgou.goods.pojo.Spu;
import com.github.pagehelper.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.xml.ws.soap.Addressing;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/spu")
public class SpuController {


    @Autowired
    private SpuService spuService;

    /**
     * SPU与SKU列表的保存 新增数据
     * goods
     */
    @PostMapping("/addGoods")
    public Result add(@RequestBody Goods goods) {
        spuService.addGoods(goods);
        return new Result(true, StatusCode.OK, "添加成功");
    }

    /**
     * 根据id查询sku和spu
     */
    @GetMapping("/{id}")
    public Result<Goods> selectById(@PathVariable String id) {
        Goods goods = spuService.selectById(id);
        return new Result(true, StatusCode.OK, "查询成功", goods);
    }

    /**
     * 品牌与分类关联  将分类ID与SPU的品牌ID 一起插入到 tb_category_brand 表中
     */
    @PostMapping("/saveCategoryIdSpuId")
    public Result selectById(@RequestBody Goods goods) {
        spuService.saveCategoryIdSpuId(goods);
        return new Result(true, StatusCode.OK, "查询成功");
    }


    /**
     * 保存修改
     */
    @PostMapping("/update")
    public Result updateByIdSpuId(@RequestBody Goods goods) {
        spuService.updateByIdSpuId(goods);
        return new Result(true, StatusCode.OK, "修改成功");
    }

    /**
     * 查询全部数据
     * @return
     */
    @GetMapping
    public Result findAll() {
        List<Spu> spuList = spuService.findAll();
        return new Result(true, StatusCode.OK, "查询成功", spuList);
    }



    /***
     * 根据ID查询数据
     * @param id
     * @return
     */
//    @GetMapping("/{id}")
//    public Result findById(@PathVariable String id) {
//        Spu spu = spuService.findById(id);
//        return new Result(true, StatusCode.OK, "查询成功", spu);
//    }


    /***
     * 新增数据
     * @param spu
     * @return
     */
    @PostMapping
    public Result add(@RequestBody Spu spu) {
        spuService.add(spu);
        return new Result(true, StatusCode.OK, "添加成功");
    }


    /***
     * 修改数据
     * @param spu
     * @param id
     * @return
     */
    @PutMapping(value = "/{id}")
    public Result update(@RequestBody Spu spu, @PathVariable String id) {
        spu.setId(id);
        spuService.update(spu);
        return new Result(true, StatusCode.OK, "修改成功");
    }


    /***
     * 根据ID删除品牌数据
     * @param id
     * @return
     */
    @DeleteMapping(value = "/{id}")
    public Result delete(@PathVariable String id) {
        spuService.delete(id);
        return new Result(true, StatusCode.OK, "删除成功");
    }

    /***
     * 多条件搜索品牌数据
     * @param searchMap
     * @return
     */
    @GetMapping(value = "/search")
    public Result findList(@RequestParam Map searchMap) {
        List<Spu> list = spuService.findList(searchMap);
        return new Result(true, StatusCode.OK, "查询成功", list);
    }


    /***
     * 分页搜索实现
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    @GetMapping(value = "/search/{page}/{size}")
    public Result findPage(@RequestParam Map searchMap, @PathVariable int page, @PathVariable int size) {
        Page<Spu> pageList = spuService.findPage(searchMap, page, size);
        PageResult pageResult = new PageResult(pageList.getTotal(), pageList.getResult());
        return new Result(true, StatusCode.OK, "查询成功", pageResult);
    }


}
