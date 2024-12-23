package com.shop.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.constant.ItemSellStatus;
import com.shop.entity.Item;
import com.shop.entity.QItem;
import jakarta.persistence.EntityManager;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;

import java.security.PublicKey;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log4j2
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    EntityManager em;

    @Test
    @DisplayName("Querydsl 조회 테스트")
    public void queryDslTest() {
//        this.createItemTest();
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QItem qItem = QItem.item;
        /*
            select * from item
            where itemSellStatus like "%SELL%" and(or)기본값
                itemDetail like "%테스트 상품 상세 설명%"
            order by price desc;
         */

        JPAQuery<Item> query = queryFactory.selectFrom(qItem)
                .where(qItem.itemSellStatus.eq(ItemSellStatus.SELL))
                .where(qItem.itemDetail.like("%" + "테스트 상품 상세 설명"+"%"))
                .orderBy(qItem.price.desc());

        List<Item> itemList = query.fetch();

        itemList.forEach(System.out::println);

        //가급적 사용 x
        long count = query.fetchCount();
        log.info("count : "+count);

        //사용 o
        int total = itemList.size();

        log.info("total : "+total);

        /*for(Item item : itemList) {
            log.info(item.toString());
        }*/
    }       //end queryDslTest()


    public void createItemList2(){
        for(int i=1;i<=5;i++){
            Item item = new Item();
            item.setItemNm("테스트 상품" + i);
            item.setPrice(10000 + i);
            item.setItemDetail("테스트 상품 상세 설명" + i);
            item.setItemSellStatus(ItemSellStatus.SELL);
            item.setStockNumber(100);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            itemRepository.save(item);
        }

        for(int i=6;i<=10;i++){
            Item item = new Item();
            item.setItemNm("테스트 상품" + i);
            item.setPrice(10000 + i);
            item.setItemDetail("테스트 상품 상세 설명" + i);
            item.setItemSellStatus(ItemSellStatus.SOLD_OUT);
            item.setStockNumber(0);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            itemRepository.save(item);
        }
    }

    @Test
    @DisplayName("상품 Querydsl 조회 테스트 2")
    public void queryDslTest2() {
        this.createItemList2();

        BooleanBuilder booleanbuilder = new BooleanBuilder();
        QItem item = QItem.item;

//        String itemDetail = "테스트 상품 상세 설명";
//        int price = 10003;
//        String itemSellStat = "SELL";
//
//        booleanbuilder.and(item.itemDetail.like("%" + itemDetail + "%"));
//        booleanbuilder.and(item.price.gt(price));
//
//        if(StringUtils.equals(itemSellStat, ItemSellStatus.SELL)){
//            booleanbuilder.and(item.itemSellStatus.eq(ItemSellStatus.SELL));
//        }

        Pageable pageable = PageRequest.of(0, 5);

        Page<Item> itemPagingResult = itemRepository.findAll(booleanbuilder, pageable);
        log.info("total elements : "+itemPagingResult.getTotalElements());

        List<Item> content = itemPagingResult.getContent();
        for(Item items : content){
            log.info(items.toString());
        }
    }

    @Test
    @DisplayName("상품 저장 테스트")
    public void createItemTest(){
        Item item = new Item();

        item.setItemNm("테스트 상품");
        item.setPrice(10000);
        item.setItemDetail("테스트 상품 상세 설명");
        item.setStockNumber(100);
        item.setRegTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());
        item.setItemSellStatus(ItemSellStatus.SELL);

        Item savedItem = itemRepository.save(item);

        System.out.println(savedItem);


    }

    @Test
    @DisplayName("레코드 삭제")
    public void deleteItemTest(){
        itemRepository.deleteById(1L);
    }

    @Test
    @DisplayName("레코드 조회")
    public void findItemTest(){
        Optional<Item> item = itemRepository.findById(2L);
        item.ifPresent(System.out::println);
    }

    @Test
    @DisplayName("상품 저장 테스트")
    public void createItemTest2(){
        for(int i=0; i<10; i++) {
            Item item = new Item();

            item.setItemNm("테스트 상품"+i);
            item.setPrice(10000*i);
            item.setItemDetail("테스트 상품 상세 설명"+i);
            item.setStockNumber(100*i);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            item.setItemSellStatus(ItemSellStatus.SELL);
            itemRepository.save(item);
        }
    }

    @Test
    @DisplayName("레코드 개수 조회")
    public void countItemTest(){
        long count = itemRepository.count();
        System.out.println("count : "+count);
    }

    @Test
    @DisplayName("전체 레코드 조회")
    public void selectItemTest(){
        List<Item> items = itemRepository.findAll();
        items.forEach( item -> log.info(item.toString()));
    }

    @Test
    @DisplayName("레코드 수정")
    public void updateItemTest(){
        Optional<Item> result = itemRepository.findById(1L);
        Item item = result.get();
        item.setItemNm("수정된 상품 이름");
        item.setPrice(9999);
        itemRepository.save(item);
    }

    @Test
    @DisplayName("상품명 조회(테스트 상품5)")
    public void selectByItemNmTest(){
        String name = "테스트 상품5";
        List<Item> itemNm = itemRepository.findItemByItemNm(name);

        itemNm.forEach( item -> log.info(item.toString()));
    }

    @Test
    @DisplayName("상품명 조회(와이드카드)")
    public void findItemByItemNmContainingTest(){
        String name="상품";
        List<Item> itemNm = itemRepository.findItemByItemNmContaining(name);

        itemNm.forEach( item -> log.info(item.toString()));
    }


    @Test
    @DisplayName("price 가격 이상 조회")
    public void findItemByPriceGreaterThanEqualTest(){

        List<Item> items = itemRepository.findItemByPriceGreaterThanEqual(10000);

        items.forEach( item -> log.info(item.toString()));
    }

    @Test
    @DisplayName("price 가격 초과 조회")
    public void findItemByPriceGreaterThanTest(){

        List<Item> items = itemRepository.findItemByPriceGreaterThan(10000);

        items.forEach( item -> log.info(item.toString()));
    }

    @Test
    @DisplayName("order by price desc")
    public void findItemByItemNmContainingOrderByPriceDescTest(){

        List<Item> items = itemRepository.findItemByItemNmContainingOrderByPriceDesc("상품");

        items.forEach( item -> log.info(item.toString()));
    }

    @Test
    @DisplayName("ItemDetail")
    public void findByItemDetailTest(){

        List<Item> items = itemRepository.findByItemDetail("상품");

        items.forEach( item -> log.info(item.toString()));
    }

    @Test
    @DisplayName("findByItemDetail2")
    public void findByItemDetailTest2(){
        List<Item> items = itemRepository.findByItemDetail2("상품");

        items.forEach( item -> log.info(item.toString()));
    }
}