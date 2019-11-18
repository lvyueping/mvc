package com.wlkg.api;

import com.wlkg.pojo.Brand;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

public interface BrandApi {

    @GetMapping("/brand/{id}")
    @ResponseBody
    public Brand queryBrandById(@PathVariable("id")Long id);


}
