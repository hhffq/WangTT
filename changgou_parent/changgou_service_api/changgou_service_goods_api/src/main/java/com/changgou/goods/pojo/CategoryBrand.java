package com.changgou.goods.pojo;

import lombok.Data;

import javax.persistence.Table;

/**
 * @author lht
 * @date 2025-08-03 13:12
 */
@Data
@Table(name="tb_category_brand")
public class CategoryBrand {

    private Integer categoryId;

    private Integer brandId;

}
