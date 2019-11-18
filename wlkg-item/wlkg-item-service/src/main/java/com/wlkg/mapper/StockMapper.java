package com.wlkg.mapper;

import com.wlkg.pojo.Stock;

import tk.mybatis.mapper.additional.idlist.DeleteByIdListMapper;
import tk.mybatis.mapper.additional.insert.InsertListMapper;
import tk.mybatis.mapper.common.Mapper;


public interface StockMapper extends Mapper<Stock>, InsertListMapper<Stock>, DeleteByIdListMapper<Stock, Long> {
}
