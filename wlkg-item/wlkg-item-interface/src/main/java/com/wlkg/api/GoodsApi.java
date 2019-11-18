package com.wlkg.api;

import com.wlkg.common.vo.PageResult;
import com.wlkg.pojo.Sku;
import com.wlkg.pojo.Spu;
import com.wlkg.pojo.SpuDetail;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface GoodsApi {

    @GetMapping("/spu/page")
    public PageResult<Spu> select(@RequestParam(name = "page",defaultValue = "1") Integer page,
                                                  @RequestParam(name = "row",defaultValue = "5") Integer row,
                                                  @RequestParam(name = "key",required = false) String key,
                                                  @RequestParam(name = "saleable",required = false) Boolean saleable);

    @GetMapping("/spu/detail/{id}")
    public SpuDetail querySpuDetail(@PathVariable("id") Long id);

    @GetMapping("/sku/list")
    public List<Sku> querySkuBy(@RequestParam("id") Long id);

    @GetMapping("spu/{id}")
    public Spu querySpu(@PathVariable("id")Long id);

}
