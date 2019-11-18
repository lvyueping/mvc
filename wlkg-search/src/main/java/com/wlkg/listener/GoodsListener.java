package com.wlkg.listener;

import com.wlkg.service.SearchService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GoodsListener {
    @Autowired
    private SearchService searchService;

    /**
     * 处理insert和update消息
     * @param id
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "wlkg.create.index.queue", durable = "true"),
            exchange = @Exchange(
                    value = "wlkg.item.exchange",
                    ignoreDeclarationExceptions = "true",
                    type = ExchangeTypes.TOPIC),
            key = {"item.insert", "item.update"}))
        public void listenerCreate(Long id) throws IOException{
            if(id==null){
                return;
            }
            searchService.createIndex(id);
        }
    /**
     * 处理delete消息
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "wlkg.delete.index.queue", durable = "true"),
            exchange = @Exchange(
                    value = "wlkg.item.exchange",
                    ignoreDeclarationExceptions = "true",
                    type = ExchangeTypes.TOPIC),
            key = "item.delete"))
        public void listenerDelete(Long id) {
            if(id==null){
                return;
            }
            searchService.deleteIndex(id);
        }

}
