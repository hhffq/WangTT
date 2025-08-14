package com.changgou.goods.dao;

import com.changgou.goods.pojo.Sku;
import com.changgou.goods.pojo.Spu;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.web.bind.annotation.PathVariable;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SkuMapper extends Mapper<Sku> {
    @Select("SELECT  * FROM tb_sku where spu_id = #{id}")
//SELECT * from tb_sku where spu_id = '10000001516600'
    List<Sku> selectBySpuId(String id);
}
