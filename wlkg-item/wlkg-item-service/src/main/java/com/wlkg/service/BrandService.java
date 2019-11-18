package com.wlkg.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wlkg.common.enums.ExceptionEnums;
import com.wlkg.common.exception.WlkgException;
import com.wlkg.common.vo.PageResult;

import com.wlkg.mapper.BrandMapper;
import com.wlkg.pojo.Brand;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class BrandService {
    @Autowired
    private BrandMapper brandMapper;

    public PageResult<Brand> select( Integer row, Integer size, String sortBy, Boolean desc, String key){
        PageHelper.startPage(row,size);
        Example example = new Example(Brand.class);
        //过滤
        if(StringUtils.isNotBlank(key)){
            example.createCriteria().andLike("name","%"+key+"%").orEqualTo("letter",key);
        }
        //排序
        if(StringUtils.isNotBlank(sortBy)){
            String s = sortBy + (desc ? " DESC" : " ASC");
            example.setOrderByClause(s);
        }
        // 查询
        Page<Brand> pageInfo = (Page<Brand>) brandMapper.selectByExample(example);

        // 返回结果
        return new PageResult<>(pageInfo.getTotal(), pageInfo);
    }

    @Transactional
    public void insert(Brand brand, List<Long> cids) {
        this.brandMapper.insertSelective(brand);
        for(Long cid:cids){
            this.brandMapper.insertCategoryBrand(cid, brand.getId());
        }

    }
//修改
    public Brand update(Brand brand,List<Long> cids) {
        Brand brand1 = brandMapper.selectByPrimaryKey(brand.getId());
        if(brand1!=null){
            brandMapper.updateByPrimaryKeySelective(brand1);
            brandMapper.deleteBy(brand1.getId());
        }
        for(Long cid:cids){
            this.brandMapper.insertCategoryBrand(cid, brand1.getId());
        }
        return brand1;

    }

    public void delete(Long id) {
        int i = brandMapper.deleteByPrimaryKey(id);
    }
//通过分类查询品牌
    public List<Brand> selectBrand(Long cid) {
        List<Brand> brands = brandMapper.selectBrand(cid);
        if(CollectionUtils.isEmpty(brands)){
            return null;
        }
        return brands;
    }

    public Brand selectBrandById(Long id) {
        return this.brandMapper.selectByPrimaryKey(id);
    }
}
