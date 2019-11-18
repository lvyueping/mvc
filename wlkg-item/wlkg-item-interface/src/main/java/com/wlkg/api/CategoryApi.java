package com.wlkg.api;

import com.wlkg.pojo.Category;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping("/category")
public interface CategoryApi {
    @GetMapping("list/ids")
    public List<Category> queryCategoryById(@RequestParam("pid") List<Long> ids);
}
