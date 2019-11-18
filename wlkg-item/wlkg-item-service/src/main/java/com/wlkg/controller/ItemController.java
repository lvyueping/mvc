package com.wlkg.controller;

import com.wlkg.common.enums.ExceptionEnums;
import com.wlkg.common.exception.WlkgException;
import com.wlkg.pojo.Item;
import com.wlkg.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("item")
public class ItemController {
    @Autowired
    private ItemService itemService;
    @PostMapping
    @ResponseBody
    public ResponseEntity<Item> save(Item item){
        if(item.getPrice()==null){
            throw new WlkgException(ExceptionEnums.PRICE_CANNOT_BE_NULL);
        }
        Item item1 = itemService.saveItem(item);
        return ResponseEntity.status(HttpStatus.CREATED).body(item);
    }


}
