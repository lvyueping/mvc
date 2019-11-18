package com.wlkg.client;

import com.wlkg.SearchApplication;
import com.wlkg.pojo.Category;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SearchApplication.class)
public class CategoryClientTest {
    @Autowired
    private CategoryClient categoryClient;

    @Test
    public void test01(){
        List<Category> list = categoryClient.queryCategoryById(Arrays.asList(1L, 2L, 3L));
        list.forEach(e->System.out.println(e.getName()));
    }
}
