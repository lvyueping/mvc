package com.wlkg.service;

import com.wlkg.common.enums.ExceptionEnums;
import com.wlkg.common.exception.WlkgException;
import com.wlkg.mapper.SpecGroupMapper;
import com.wlkg.pojo.SpecGroup;
import com.wlkg.pojo.SpecParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SpecificationService {
    @Autowired
    private SpecGroupMapper specGroupMapper;
    @Autowired
    private SpecParamService specParamService;

    public List<SpecGroup> querySpecGroups(Long cid) {
        SpecGroup specGroup = new SpecGroup();
        specGroup.setCid(cid);
        List<SpecGroup> select = specGroupMapper.select(specGroup);
        if(select==null){
            throw new WlkgException(ExceptionEnums.NOT_IS_FOUND);
        }
        return select;
    }

    public List<SpecGroup> querySpecsByCid(Long cid) {
        // 查询规格组
        List<SpecGroup> groups = this.querySpecGroups(cid);
        // 查询当前分类下的参数
        List<SpecParam> specParams = specParamService.query(null, cid, null, null);
        Map<Long, List<SpecParam>> map = new HashMap<>();

        for (SpecParam param : specParams){
            if(!map.containsKey(param.getGroupId())){
                //这个组id在map中不存在，新增一个list
                map.put(param.getGroupId(), new ArrayList<>());
            }

            map.get(param.getGroupId()).add(param);
        }
        //填充param到group
        for (SpecGroup specGroup: groups){
            if(map.get(specGroup.getId())!=null) {
                specGroup.setParams(map.get(specGroup.getId()));
            }
        }
        return groups;
    }



//修改
    public SpecGroup update(SpecGroup specGroup) {
        int i = specGroupMapper.updateByPrimaryKeySelective(specGroup);
        if(i>0){
            return specGroup;
        }else {
            return null;
        }
    }







}
