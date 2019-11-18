package com.wlkg.controller;

import com.wlkg.pojo.SpecGroup;
import com.wlkg.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class SpecificationController {
    @Autowired
    private SpecificationService specificationService;


    @GetMapping("/spec/groups/{cid}")
    public ResponseEntity<List<SpecGroup>> select(@PathVariable("cid") Long cid){
        List<SpecGroup> list= specificationService.querySpecsByCid(cid);
        return ResponseEntity.ok(list);
    }

    @PutMapping("/spec/group")
    public ResponseEntity<SpecGroup> update(@RequestBody SpecGroup specGroup){
        SpecGroup group = specificationService.update(specGroup);
        return ResponseEntity.ok(group);
    }



}
