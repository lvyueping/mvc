package com.wlkg.controller;


import com.wlkg.pojo.SpecParam;
import com.wlkg.service.SpecParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Controller
public class SpecParamController {
    @Autowired
    private SpecParamService specParamService;

    @GetMapping("/spec/params")
    public ResponseEntity<List<SpecParam>> select( @RequestParam(value="gid", required = false) Long gid,
                                                   @RequestParam(value="cid", required = false) Long cid,
                                                   @RequestParam(value="searching", required = false) Boolean searching,
                                                   @RequestParam(value="generic", required = false) Boolean generic){
        List<SpecParam> list = specParamService.query(gid,cid,searching,generic);
        return ResponseEntity.ok(list);
    }


}
