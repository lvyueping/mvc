package com.wlkg.controller;


import com.wlkg.common.vo.PageResult;
import com.wlkg.pojo.Category;
import com.wlkg.pojo.Sku;
import com.wlkg.pojo.Spu;
import com.wlkg.pojo.SpuDetail;
import com.wlkg.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class GoodsController {
    @Autowired
    private GoodsService goodsService;


//增加商品
    @PostMapping("/goods")
    public ResponseEntity<Void> insert(@RequestBody Spu spu){
        goodsService.save(spu);
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    //分页查询商品信息
    @GetMapping("spu/page")
    public ResponseEntity<PageResult<Spu>> select(@RequestParam(name = "page",defaultValue = "1") Integer page,
                                                  @RequestParam(name = "row",defaultValue = "5") Integer row,
                                                  @RequestParam(name = "key",required = false) String key,
                                                  @RequestParam(name = "saleable",required = false) Boolean saleable){

        PageResult<Spu> list = goodsService.selectSpu(page,row,key,saleable);
        return ResponseEntity.ok(list);
    }

    //查询商品详情/spu/detail
    @GetMapping("/spu/detail/{id}")
    public ResponseEntity<SpuDetail> querySpuDetail(@PathVariable("id") Long id){
        SpuDetail spuDetail = goodsService.selectSpuDetail(id);
        return ResponseEntity.ok(spuDetail);
    }

    //查询Sku sku/list?id
    @GetMapping("/sku/list")
    public ResponseEntity<List<Sku>> querySkuBy(@RequestParam("id") Long id){
        List<Sku> skus = goodsService.selectSkusById(id);
        return ResponseEntity.ok(skus);
    }

    //修改商品
    @PutMapping("/goods")
    public ResponseEntity<Void> update(@RequestBody Spu spu){
        goodsService.update(spu);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    //根据spuId查询spu
    @GetMapping("spu/{id}")
    public ResponseEntity<Spu> querySpu(@PathVariable("id")Long id){
        Spu spu = goodsService.querySpuBySpuId(id);
        return ResponseEntity.ok(spu);
    }



}
