package com.wlkg.test;

import com.wlkg.SearchApplication;
import com.wlkg.client.GoodsClient;
import com.wlkg.common.vo.PageResult;
import com.wlkg.pojo.Goods;
import com.wlkg.pojo.Spu;
import com.wlkg.repository.GoodsRepository;
import com.wlkg.service.SearchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SearchApplication.class)
public class localTest {
    @Autowired
    private ElasticsearchTemplate template;
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private GoodsRepository repository;
    @Autowired
    private SearchService searchService;

    @Test
    public void createBulid(){
        template.createIndex(Goods.class);
        template.putMapping(Goods.class);
    }
    @Test
    public void loadData(){
        long begin = System.currentTimeMillis();
        int page = 1;
        int rows = 100;
        int size = 0;
        do {
            // 查询分页数据
            PageResult<Spu> result = goodsClient.select(page,rows,null,true);
            List<Spu> spus = result.getItems();
            size = spus.size();
            // 创建Goods集合
            List<Goods> goodsList = new ArrayList<>();
            // 遍历spu
            for (Spu spu : spus) {
                try {
                    Goods goods = this.searchService.goodsBuild(spu);
                    goodsList.add(goods);

                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
            this.repository.saveAll(goodsList);
            page++;
        } while (size == 100);
        long end = System.currentTimeMillis();
        System.out.println("耗时："+(end-begin)/1000);
    }

}
