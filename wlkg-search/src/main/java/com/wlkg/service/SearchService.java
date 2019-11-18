package com.wlkg.service;


import com.fasterxml.jackson.core.type.TypeReference;
import com.wlkg.client.BrandClient;
import com.wlkg.client.CategoryClient;
import com.wlkg.client.GoodsClient;
import com.wlkg.client.SpecificationClient;
import com.wlkg.common.enums.ExceptionEnums;
import com.wlkg.common.exception.WlkgException;
import com.wlkg.common.utils.JsonUtils;
import com.wlkg.common.vo.PageResult;
import com.wlkg.pojo.*;
import com.wlkg.repository.GoodsRepository;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SourceFilter;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchService {
    @Autowired
    private CategoryClient categoryClient;
    @Autowired
    private BrandClient brandClient;
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private SpecificationClient specificationClient;
    @Autowired
    private GoodsRepository goodsRepository;

    public Goods goodsBuild(Spu spu){

        //查询分类
        List<Category> categories = categoryClient.queryCategoryById(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
        if(CollectionUtils.isEmpty(categories)){
            throw new WlkgException(ExceptionEnums.NOT_IS_FOUND);
        }
        List<String> names = categories.stream().map(s -> s.getName()).collect(Collectors.toList());

        //查询品牌
        Brand brand = brandClient.queryBrandById(spu.getBrandId());
        if(brand==null){
            throw new WlkgException(ExceptionEnums.NOT_IS_FOUND);
        }
        //描述字段
        String all = spu.getSubTitle()+ StringUtils.join(names," ")+brand.getName();

        //查询价格
        List<Sku> skus = goodsClient.querySkuBy(spu.getId());
        if(CollectionUtils.isEmpty(skus)){
            throw new WlkgException(ExceptionEnums.NOT_IS_FOUND);
        }

        List<Long> price = new ArrayList();
        List<Map<String,Object>> skuList = new ArrayList<>();
        skus.forEach(sku -> {
            price.add(sku.getPrice());
            Map<String,Object> skuMap = new HashMap<>();
            skuMap.put("id",sku.getId());
            skuMap.put("title",sku.getTitle());
            skuMap.put("image",StringUtils.substringBefore(sku.getImages(),","));
            skuMap.put("price",sku.getPrice());
            skuList.add(skuMap);
        });

        //查询规格参数
        List<SpecParam> params = specificationClient.select(null, spu.getCid3(), true, null);
        //查询商品详情
        SpuDetail spuDetail = goodsClient.querySpuDetail(spu.getId());
        //获得通用参数规格
        Map<Long, String> genericSpec = JsonUtils.parseMap(spuDetail.getGenericSpec(), Long.class, String.class);
        //获得特殊参数规格
        Map<Long, List<String>> specialSpec = JsonUtils.nativeRead(spuDetail.getSpecialSpec(), new TypeReference<Map<Long, List<String>>>() {
        });
        HashMap<String, Object> list = new HashMap<>();
        for(SpecParam param:params){
            String key = param.getName();
            Object value = "";
            if(param.getGeneric()){
               value = genericSpec.get(param.getId());
               if(param.getNumeric()){
                   value = chooseSegment(value.toString(), param);
               }
            }else {
                value = specialSpec.get(param.getId());
            }
            value = (value==null?"其他":value);
            list.put(key,value);
        }
        Goods goods = new Goods();
        goods.setId(spu.getId());
        goods.setBrandId(spu.getBrandId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setCreateTime(spu.getCreateTime());
        goods.setSubTitle(spu.getSubTitle());
        goods.setAll(all);
        goods.setPrice(price);
        goods.setSkus(JsonUtils.serialize(skuList));
        goods.setSpecs(list);
        return goods;
    }

    //设置分段端信息
    private String chooseSegment(String value, SpecParam p) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if (segs.length == 2) {
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if (val >= begin && val < end) {
                if (segs.length == 1) {
                    result = segs[0] + p.getUnit() + "以上";
                } else if (begin == 0) {
                    result = segs[1] + p.getUnit() + "以下";
                } else {
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }



    public PageResult<Goods> search(SearchRequest request) {
        String key = request.getKey();
        if(StringUtils.isBlank(key)){
                return null;
        }
//构建查询条件
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //对key进行全文检索查询
        queryBuilder.withQuery(QueryBuilders.matchQuery("all",key).operator(Operator.AND));
        // 2、通过sourceFilter设置返回的结果字段,我们只需要id、skus、subTitle
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id","skus","subTitle"},null));
        // 3、分页
        // 准备分页参数
        Integer page = request.getPage();
        Integer size = request.getSize();
        queryBuilder.withPageable(PageRequest.of(page-1,size));
        // 4、查询，获取结果
        Page<Goods> pageInfo = goodsRepository.search(queryBuilder.build());
        System.out.println(pageInfo);
        //封装并返回结果
        List<Goods> content = pageInfo.getContent();
        long total = pageInfo.getTotalElements();
        long totalPages = pageInfo.getTotalPages();


        return new PageResult<>(total,totalPages,content);
    }


    /**
     * 创建和删除索引
     */
    public void createIndex(Long id){
        //查询spu
        Spu spu = goodsClient.querySpu(id);
        //构建goods
        Goods goods = goodsBuild(spu);
        //存入索引库
        goodsRepository.save(goods);
    }
    public void deleteIndex(Long id){
        goodsRepository.deleteById(id);
    }

}
