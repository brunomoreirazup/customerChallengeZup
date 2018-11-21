package com.zup.model;

import org.springframework.data.domain.Page;

import java.util.HashMap;
import java.util.Map;

public class CustomPage {
    private Map<String,Object> _embedded;
    private Map<String,Integer> page;

    public CustomPage(Page<?> pages, String name){
        _embedded = new HashMap();
        page = new HashMap();

        _embedded.put(name, pages.getContent());

        page.put("size", pages.getSize());
        page.put("totalElements", pages.getNumberOfElements());
        page.put("totalPages", pages.getTotalPages());
        page.put("number",pages.getNumber());
    }

    public Map<String, Object> get_embedded() {
        return _embedded;
    }

    public void set_embedded(Map<String, Object> _embedded) {
        this._embedded = _embedded;
    }

    public Map<String, Integer> getPage() {
        return page;
    }

    public void setPage(Map<String, Integer> pages) {
        this.page = pages;
    }

}
