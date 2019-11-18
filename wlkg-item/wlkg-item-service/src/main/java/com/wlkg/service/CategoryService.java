package com.wlkg.service;

import com.wlkg.common.enums.ExceptionEnums;
import com.wlkg.common.exception.WlkgException;

import com.wlkg.mapper.CategoryMapper;
import com.wlkg.pojo.Category;
import com.wlkg.pojo.SpecParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    public List<Category> selectCategory(Long pid) {
        Category c = new Category();
        c.setParentId(pid);
        List<Category> select = categoryMapper.select(c);
        if (CollectionUtils.isEmpty(select)) {
            throw new WlkgException(ExceptionEnums.CATEGORE_NOT_FOUND);
        }
        return select;
    }

    public ResponseEntity<String> delete(Long id) {
        int i = categoryMapper.deleteByPrimaryKey(id);
        if (i > 0) {
            return ResponseEntity.status(HttpStatus.OK).body("删除成功");
        } else {
            throw new WlkgException(ExceptionEnums.PRICE_CANNOT_BE_NULL);
        }
    }

    //添加
    public Category add(Category category) {
        int i = categoryMapper.insertSelective(category);
        if (i > 0) {
            return category;
        } else {
            throw new WlkgException(ExceptionEnums.CATEGORE_NOT_FOUND);
        }
    }

    //修改
    public Category update(Category category) {
        int i = categoryMapper.updateByPrimaryKeySelective(category);
        if (i > 0) {
            return category;
        } else {
            throw new WlkgException(ExceptionEnums.CATEGORE_NOT_FOUND);
        }
    }


    public List<Category> queryByBrandId(Long bid) {
        return this.categoryMapper.queryByBrandId(bid);
    }

    public List<String> queryNameById(List<Long> ids) {
        return this.categoryMapper.selectByIdList(ids).stream().map(Category::getName).collect(Collectors.toList());
    }

    //根据商品分类id，查询商品分类名称
    public List<Category> select(List<Long> ids) {
        List<Category> categories = categoryMapper.selectByIdList(ids);
        if(CollectionUtils.isEmpty(categories)){
            throw new WlkgException(ExceptionEnums.NOT_IS_FOUND);
        }
        return categories;
    }
}