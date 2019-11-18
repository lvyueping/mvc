package com.wlkg.controller;


import com.wlkg.pojo.Category;
import com.wlkg.pojo.SpecParam;
import com.wlkg.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
//查询
    @GetMapping("/select")
    @ResponseBody
    public ResponseEntity<List<Category>> selectCategory(@RequestParam(name = "pid",defaultValue = "0") Long pid){
        List<Category> selectCategory = categoryService.selectCategory(pid);
        return ResponseEntity.ok(selectCategory);
    }
//删除
    @ResponseBody
    @DeleteMapping("/delete/{id}")
    public void delect(@PathVariable(name = "id") Long id){
        categoryService.delete(id);
    }
//添加
    @ResponseBody
    @PostMapping("/insert")
    public Category add(@RequestBody Category category){
        Category add = categoryService.add(category);
        return add;
    }
//修改
    @ResponseBody
    @PutMapping("/update")
    public Category update(@RequestBody Category category){
//        System.out.println(category.getName());
        Category update = categoryService.update(category);
        return update;
    }

    @ResponseBody
    @GetMapping("bid/{bid}")
    public ResponseEntity<List<Category>> queryByBrandId(@PathVariable("bid") Long bid) {
        List<Category> list = this.categoryService.queryByBrandId(bid);
        if (list == null || list.size() < 1) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(list);
    }

    //根据商品分类id，查询商品分类名称
    @ResponseBody
    @GetMapping("list/ids")
    public ResponseEntity<List<Category>> queryCategoryById(@RequestParam("pid") List<Long> ids){
        List<Category> category =  categoryService.select(ids);
        return ResponseEntity.ok(category);
    }

}
