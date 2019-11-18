package com.wlkg.goodsPage.service;

import com.wlkg.goodsPage.client.BrandClient;
import com.wlkg.goodsPage.client.CategoryClient;
import com.wlkg.goodsPage.client.GoodsClient;
import com.wlkg.goodsPage.client.SpecificationClient;
import com.wlkg.goodsPage.utils.ThreadUtils;
import com.wlkg.pojo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PageService {
        @Autowired
        private GoodsClient goodsClient;

        @Autowired
        private CategoryClient categoryClient;

        @Autowired
        private BrandClient brandClient;

        @Autowired
        private SpecificationClient specClient;

        @Autowired
        private TemplateEngine templateEngine;

    @Value("${wlkg.thymeleaf.destPath}")
    private String destPath;// C:\nginx-1.12.2 - bak\html

        private static final Logger logger = LoggerFactory.getLogger(PageService.class);

        public Map<String, Object> loadModel(Long id) {
            // 模型数据
            Map<String, Object> modelMap = new HashMap<>();

            // 查询spu
            Spu spu = this.goodsClient.querySpu(id);
            // 查询spuDetail
            SpuDetail detail = spu.getSpuDetail();
            // 查询sku
            List<Sku> skus = spu.getSkus();

            // 准备品牌数据
            Brand brand = this.brandClient.queryBrandById(spu.getBrandId());

            // 准备商品分类
            List<Category> categories = categoryClient.queryCategoryById(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));

            //TODO 查询规格参数
            List<SpecGroup> specs = specClient.select(spu.getCid3());
            System.out.println(specs);
            // 装填模型数据
            modelMap.put("spu", spu);
            modelMap.put("title", spu.getTitle());
            modelMap.put("subTitle", spu.getSubTitle());
            modelMap.put("detail", detail);
            modelMap.put("skus", skus);
            modelMap.put("brand", brand);

            modelMap.put("categories", categories);
            modelMap.put("specs", specs);
            return modelMap;
        }
    /**
     * 创建Html页面
     */
    public void createHtml(Long spuId) throws Exception {
        //创建上下文
        Context context = new Context();
        //将数据加入到上下文中
        context.setVariables(loadModel(spuId));
        //创建输出流,关联到一个临时文件
        File file = new File(spuId+".html");
        //目标页面文件
        File dest = createPath(spuId);
        //备份原页面文件
        File bak = new File(spuId+"_bak.html");

        try (PrintWriter writer = new PrintWriter(file,"UTF-8");){
            templateEngine.process("item",context,writer);
            if(dest.exists()){
                dest.renameTo(bak);
            }
            FileCopyUtils.copy(file,dest);
            bak.delete();
        }catch (Exception e) {
            bak.renameTo(dest);
            throw new Exception(e);
        }finally {
            if(file.exists()){
                file.delete();
            }
        }
    }
    /**
     * 判断某个商品的页面是否存在
     * @param id
     * @return
     */
    public boolean exists(Long id){
        return this.createPath(id).exists();
    }

    private File createPath(Long id){
        if(id==null){
            return null;
        }
        File file = new File(destPath);
        if(!file.exists()){
            file.mkdirs();
        }
        return new File(file,id+".html");
    }
    /**
     * 异步创建html页面
     * @param id
     */
    public void syncCreateHtml(Long id){
        ThreadUtils.execute(() -> {
            try {
                createHtml(id);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 创建删除页面的方法
     * @param id
     */
    public void deleteHtml(Long id) {
        File file = new File(this.destPath, id + ".html");
        file.deleteOnExit();
    }


}


