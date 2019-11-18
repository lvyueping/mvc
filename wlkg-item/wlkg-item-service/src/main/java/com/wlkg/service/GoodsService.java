package com.wlkg.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wlkg.common.enums.ExceptionEnums;
import com.wlkg.common.exception.WlkgException;
import com.wlkg.common.vo.PageResult;
import com.wlkg.mapper.*;
import com.wlkg.pojo.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoodsService {
    @Autowired
    private SpuMapper spuMapper;
    @Autowired
    private SpuDetailMapper spuDetailMapper;
    @Autowired
    private SkuMapper skuMapper;
    @Autowired
    private StockMapper stockMapper;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private BrandMapper brandMapper;
    @Autowired
    private AmqpTemplate amqpTemplate;

//增加商品
    @Transactional
    public void save(Spu spu) {
        // 保存spu
        spu.setSaleable(true);
        spu.setValid(true);
        spu.setCreateTime(new Date());
        spu.setLastUpdateTime(spu.getCreateTime());
        this.spuMapper.insert(spu);
        // 保存spu详情
        spu.getSpuDetail().setSpuId(spu.getId());
        this.spuDetailMapper.insert(spu.getSpuDetail());
        // 保存sku和库存信息
        saveSkuAndStock(spu.getSkus(), spu.getId());
        //发送消息
        this.sendMessage(spu.getId(),"insert");
    }
    private void saveSkuAndStock(List<Sku> skus, Long spuId) {
        List<Stock> stocks = new ArrayList<>();
        for (Sku sku : skus) {
            if (!sku.getEnable()) {
                continue;
            }
            // 保存sku
            sku.setSpuId(spuId);
            // 默认不参与任何促销
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            this.skuMapper.insert(sku);

            // 保存库存信息
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock().intValue());
            stocks.add(stock);
        }
    }


//
    public PageResult<Spu> selectSpu(Integer page, Integer row, String key, Boolean saleable) {
            PageHelper.startPage(page,Math.min(row,100));
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        if(saleable!=null){
            criteria.orEqualTo("saleable",saleable);
        }
        if(StringUtils.isNotBlank(key)){
           criteria.andLike("title","%"+key+"%");
        }
        example.setOrderByClause("last_update_time desc");
        List<Spu> list = spuMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(list)){
            throw new WlkgException(ExceptionEnums.NOT_IS_FOUND);
        }
        PageInfo<Spu> pageInfo = new PageInfo<Spu>(list);
        for(Spu spu : list){
            List<String> names = categoryService.queryNameById(
                    Arrays.asList(spu.getCid1(),spu.getCid2(),spu.getCid3())
            );
            spu.setCname(StringUtils.join(names,"/"));
            Brand brand = brandMapper.selectByPrimaryKey(spu.getBrandId());
            spu.setBname(brand.getName());
        }
        return new PageResult<>(pageInfo.getTotal(), list);
    }
//查询商品详情
    public SpuDetail selectSpuDetail(Long id) {
        SpuDetail spuDetail = spuDetailMapper.selectByPrimaryKey(id);
        if(spuDetail ==null){
            throw new WlkgException(ExceptionEnums.NOT_IS_FOUND);
        }
        return spuDetail;
    }
//查询Skus
    public List<Sku> selectSkusById(Long id) {
        System.out.println(id+"aaaaaaaaaa");
        Sku sku = new Sku();
        sku.setSpuId(id);
        List<Sku> skus = skuMapper.select(sku);

        if (CollectionUtils.isEmpty(skus)) {
            throw new WlkgException(ExceptionEnums.NOT_IS_FOUND);
        }

        //查询每个sku所关联的库存
        for (Sku s : skus) {
            System.out.println(s);
            Stock stock = stockMapper.selectByPrimaryKey(s.getId());
            if(stock!=null) {
                s.setStock(stock.getStock());
            }
        }

        return skus;
    }
//修改商品
    @Transactional
    public void update(Spu spu) {
        Sku sku = new Sku();
        sku.setSpuId(spu.getId());
        //查询Sku
        List<Sku> skus = skuMapper.select(sku);
        //删除sku和stock
        if(!CollectionUtils.isEmpty(skus)){
            skuMapper.delete(sku);
            List<Long> ids = skus.stream().map(s -> s.getId()).collect(Collectors.toList());
        }
        //修改spu
        spu.setValid(null);
        spu.setSaleable(null);
        spu.setCreateTime(null);
        spu.setLastUpdateTime(new Date());
        int i = spuMapper.updateByPrimaryKeySelective(spu);
        if(i==0){
            throw new WlkgException(ExceptionEnums.ERROR);
        }
        //修改detail
        int i1 = spuDetailMapper.updateByPrimaryKeySelective(spu.getSpuDetail());
        if(i1==0){
            throw new WlkgException(ExceptionEnums.ERROR);
        }
        //新增sku和stock
        saveSkuAndStock(spu.getSkus(), spu.getId());
        //发送消息
        this.sendMessage(spu.getId(),"update");
    }

    //根据spuId查询spu
    public Spu querySpuBySpuId(Long id) {
        Spu spu = spuMapper.selectByPrimaryKey(id);
        if(spu==null){
            throw new WlkgException(ExceptionEnums.NOT_IS_FOUND);
        }
        //查询sku
        spu.setSkus(selectSkusById(id));
        //查询detail
        spu.setSpuDetail(selectSpuDetail(id));
        return spu;
    }

    public void sendMessage(Long id,String type){
        try {
            amqpTemplate.convertAndSend("item" + type, id);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
