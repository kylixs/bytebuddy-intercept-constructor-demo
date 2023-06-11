package com.demo.biz;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gongdewei 2023/5/14
 */
public class DocService {

    private List<DocDO> cache = new ArrayList<>();

    public void add(DocDO DocDO) {
        this.cache.add(DocDO);
    }

    public DocDO getById(int id) {
        return cache.stream().filter(DocDO -> DocDO.getId() == id).findAny().orElse(null);
    }

    public List<DocDO> list() {
        return cache;
    }
}
