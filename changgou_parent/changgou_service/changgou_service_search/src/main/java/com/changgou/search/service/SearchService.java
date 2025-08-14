package com.changgou.search.service;

import java.util.Map;

/**
 * @author lht
 * @date 2025-08-10 14:12
 */
public interface SearchService {

    /**
     * 全文检索
     * @param paramMap  查询参数
     * @return
     */
    public Map search(Map<String, String> paramMap) throws Exception;
}