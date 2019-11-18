package com.wlkg.client;


import com.wlkg.api.GoodsApi;
import com.wlkg.common.vo.PageResult;
import com.wlkg.pojo.Brand;
import com.wlkg.pojo.Sku;
import com.wlkg.pojo.SpuDetail;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@FeignClient(value = "item-service")
public interface GoodsClient extends GoodsApi {



}
