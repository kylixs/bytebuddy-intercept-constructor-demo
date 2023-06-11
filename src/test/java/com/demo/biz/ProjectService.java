package com.demo.biz;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gongdewei 2023/5/14
 */
public class ProjectService {

    private List<ProjectDO> cache = new ArrayList<>();

    public void addProject(ProjectDO projectDO) {
        this.cache.add(projectDO);
    }

    public ProjectDO getById(int id) {
        return cache.stream().filter(projectDO -> projectDO.getId() == id).findAny().orElse(null);
    }

    public List<ProjectDO> listProjects() {
        return cache;
    }
}
