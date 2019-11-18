package com.wlkg.controller;


import com.wlkg.common.vo.PageResult;
import com.wlkg.pojo.Brand;
import com.wlkg.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class BrandController {
    @Autowired
    private BrandService brandService;

    @GetMapping("/brand/select")
    @ResponseBody
    public ResponseEntity<PageResult<Brand>> selectAll(@RequestParam(name = "page",defaultValue = "1") Integer row,//第几页
                                                       @RequestParam(name = "rows",defaultValue = "5")Integer size,//每页数据
                                                       @RequestParam(name = "sortBy",required = false)String sortBy,
                                                       @RequestParam(name = "desc",defaultValue = "true")Boolean desc,
                                                       @RequestParam(name = "key", required = false) String key){
        PageResult<Brand> select = brandService.select(row, size, sortBy, desc, key);
        return ResponseEntity.ok(select);
    }

    @ResponseBody
    @PostMapping("/brand")
    public ResponseEntity<Void> insertBrand(Brand brand,@RequestParam("cids") List<Long> cids){
        this.brandService.insert(brand,cids);

        return ResponseEntity.status(HttpStatus.CREATED).build();

    }
//修改
    @ResponseBody
    @PutMapping("/brand")
    public ResponseEntity<Brand> insert(Brand brand ,@RequestParam("cids") List<Long> cids){
        Brand brand1=this.brandService.update(brand,cids);
        return ResponseEntity.ok(brand1);

    }
//删除
    @ResponseBody
    @DeleteMapping("brand")
    public void delete( @RequestParam("id") Long id){
        brandService.delete(id);
    }

//
@ResponseBody
@GetMapping("/brand/cid/{cid}")
public ResponseEntity<List<Brand>> selectBrand(@PathVariable("cid") Long cid){
    List<Brand> brand = brandService.selectBrand(cid);
    return ResponseEntity.ok(brand);
}

//根据id查询品牌
    @GetMapping("/brand/{id}")
    @ResponseBody
    public ResponseEntity<Brand> queryBrandById(@PathVariable("id")Long id){
       Brand brand = brandService.selectBrandById(id);
       return ResponseEntity.ok(brand);
    }

}
