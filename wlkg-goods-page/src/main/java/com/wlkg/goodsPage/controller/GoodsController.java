package com.wlkg.goodsPage.controller;


import com.wlkg.goodsPage.client.BrandClient;
import com.wlkg.goodsPage.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("item")
public class GoodsController {
    @Autowired
    private PageService pageService;


    /**
     * 跳转到商品详情页面
     */
    @GetMapping("{id}.html")
    public String toItemPage(Model model , @PathVariable("id")Long id){
        Map<String,Object> map = pageService.loadModel(id);
        model.addAllAttributes(map);
        // 判断是否需要生成新的页面
        if(!this.pageService.exists(id)){
            this.pageService.syncCreateHtml(id);
        }
        return "item";
    }

   /* @GetMapping("{id}.html")
    @ResponseBody
    public Map<String,Object> page(@PathVariable("id")Long id){
        Map<String,Object> map = pageService.loadModel(id);
        return map;
    }*/

}
