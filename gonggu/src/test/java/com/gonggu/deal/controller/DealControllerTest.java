package com.gonggu.deal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gonggu.deal.domain.*;
import com.gonggu.deal.repository.*;
import com.gonggu.deal.request.DealCreate;
import com.gonggu.deal.request.DealEdit;
import com.gonggu.deal.request.DealJoin;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DealControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private DealRepository dealRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DealMemberRepository dealMemberRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private DealImageRepository dealImageRepository;
    private User testUser;

    @BeforeEach
    void clean(){
        dealMemberRepository.deleteAll();
        dealRepository.deleteAll();
        userRepository.deleteAll();
        categoryRepository.deleteAll();
        dealImageRepository.deleteAll();
        testUser = User.builder()
                .nickname("테스트유저")
                .email("test@test.com")
                .password("password")
                .roles(Collections.singletonList("ROLE_USER")).build();
        userRepository.save(testUser);
    }

    @Test
    @DisplayName("게시글 가져오기")
    void getDeal() throws Exception{
        Category category = Category.builder()
                .name("카테고리").build();
        categoryRepository.save(category);

        LocalDateTime date = LocalDateTime.now();
//        List<Deal> deals = IntStream.range(0,20)
//                .mapToObj(i -> Deal.builder()
//                        .title("제목" +i)
//                        .category(category)
//                        .content("내용")
//                        .price(1000L)
//                        .unitPrice(200L)
//                        .totalCount(i)
//                        .url("url/")
//                        .expireTime(date)
//                        .quantity(10)
//                        .unitQuantity(2)
//                        .nowCount(i/2)
//                        .build()).collect(Collectors.toList());
//        dealRepository.saveAll(deals);
//
//        List<DealImage> images =  IntStream.range(0, 20)
//                .mapToObj(i -> DealImage.builder()
//                        .deal(deals.get(i))
//                        .build()).collect(Collectors.toList());
//        dealImageRepository.saveAll(images);

        mockMvc.perform(get("/deal")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }


    @Test
    @DisplayName("게시글 상세보기")
    void getDealDetail() throws Exception{
        Category category = Category.builder()
                .name("카테고리").build();
        categoryRepository.save(category);

        User user = User.builder()
                .nickname("유저")
                .email("user@test.com")
                .password("password").build();
        userRepository.save(user);

        LocalDateTime now = LocalDateTime.now();
        Deal deal = Deal.builder()
                .category(category)
                .title("제목")
                .content("내용")
                .price(1000L)
                .quantity(10)
                .unitQuantity(2)
                .unitPrice(200L)
                .totalCount(10)
                .url("url/")
                .expireTime(now.plusDays(3))
                .nowCount(2)
                .user(user)
                .build();
        dealRepository.save(deal);


        mockMvc.perform(get("/deal/{dealId}", deal.getId())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("게시글 작성")
    @WithMockUser
    void postDeal() throws Exception{
        User user = User.builder()
                .nickname("유저")
                .email("user@test.com")
                .password("password").build();
        userRepository.save(user);

        Category category = Category.builder()
                .name("카테고리").build();
        categoryRepository.save(category);

        LocalDateTime date = LocalDateTime.now();
        DealCreate dealCreate = DealCreate.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .price(10000L)
                .unitQuantity(5)
                .unit("단위")
                .nowCount(1)
                .totalCount(5)
                .categoryId(category.getId())
                .url("url 주소")
                .expireTime(date.plusDays(2))
                .build();

        mockMvc.perform(post("/deal")
                        .content(objectMapper.writeValueAsString(dealCreate))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

    }

    @Test
    @DisplayName("게시글 수정")
    void editDeal() throws Exception{
        User user = User.builder()
                .nickname("유저")
                .email("user@test.com")
                .password("password").build();
        userRepository.save(user);

        Category category = Category.builder()
                .name("카테고리").build();
        categoryRepository.save(category);

        LocalDateTime now = LocalDateTime.now();
        Deal deal = Deal.builder()
                .category(category)
                .title("제목")
                .content("내용")
                .price(1000L)
                .quantity(10)
                .unitQuantity(2)
                .unitPrice(200L)
                .totalCount(10)
                .url("url/")
                .expireTime(now.plusDays(3))
                .nowCount(2)
                .user(user)
                .build();
        dealRepository.save(deal);

        DealEdit dealEdit = DealEdit.builder()
                .content("내용변경")
                .build();
        mockMvc.perform(patch("/deal/{dealId}",deal.getId())
                        .content(objectMapper.writeValueAsString(dealEdit))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("게시글 삭제")
    void deleteDeal() throws Exception{
        Category category = Category.builder()
                .name("카테고리").build();
        categoryRepository.save(category);

        LocalDateTime now = LocalDateTime.now();
        Deal deal = Deal.builder()
                .category(category)
                .title("제목")
                .content("내용")
                .price(1000L)
                .quantity(10)
                .unitQuantity(2)
                .unitPrice(200L)
                .totalCount(10)
                .url("url/")
                .expireTime(now.plusDays(3))
                .nowCount(2)
                .user(testUser)
                .build();
        dealRepository.save(deal);


        mockMvc.perform(delete("/deal/{dealId}",deal.getId())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("구매 참가")
    @WithMockUser
    void requestJoin() throws Exception{
        List<User> users = IntStream.range(0,2)
                .mapToObj(i -> User.builder()
                        .nickname("이름" +i)
                        .email("email@test.com")
                        .password("password")
                        .build()).collect(Collectors.toList());
        userRepository.saveAll(users);

        Category category = Category.builder()
                .name("카테고리").build();
        categoryRepository.save(category);

        User user = User.builder()
                .nickname("유저")
                .email("test@test.com")
                .password("password").build();
        userRepository.save(user);

        LocalDateTime now = LocalDateTime.now();
        Deal deal = Deal.builder()
                .category(category)
                .title("제목")
                .content("내용")
                .price(1000L)
                .quantity(10)
                .unitQuantity(2)
                .unitPrice(200L)
                .totalCount(10)
                .url("url/")
                .expireTime(now.plusDays(3))
                .nowCount(2)
                .user(users.get(0))
                .build();
        dealRepository.save(deal);

        DealJoin dealJoin = DealJoin.builder()
                .quantity(5)
                .build();

        mockMvc.perform(post("/deal/{dealId}/enrollment", deal.getId())
                        .content(objectMapper.writeValueAsString(dealJoin))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        mockMvc.perform(get("/deal/{dealId}", deal.getId())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("구매 정보 수정")
    @WithUserDetails(value = "테스트유저", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void editJoin() throws Exception{
        Category category = Category.builder()
                .name("카테고리").build();
        categoryRepository.save(category);

        User user = User.builder()
                .nickname("유저")
                .email("user@test.com")
                .password("password").build();
        userRepository.save(user);

        LocalDateTime now = LocalDateTime.now();
        Deal deal = Deal.builder()
                .category(category)
                .title("제목")
                .content("내용")
                .price(1000L)
                .quantity(10)
                .unitQuantity(2)
                .unitPrice(200L)
                .totalCount(10)
                .url("url/")
                .expireTime(now.plusDays(3))
                .nowCount(2)
                .user(user)
                .build();
        dealRepository.save(deal);

        DealJoin dealJoin = DealJoin.builder()
                .quantity(5)
                .build();

        DealJoin dealJoin2 = DealJoin.builder()
                .quantity(2)
                .build();

        mockMvc.perform(post("/deal/{dealId}/enrollment", deal.getId())
                        .content(objectMapper.writeValueAsString(dealJoin))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(patch("/deal/{dealId}/enrollment", deal.getId())
                        .content(objectMapper.writeValueAsString(dealJoin2))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        mockMvc.perform(get("/deal/{dealId}", deal.getId())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("구매 취소")
    @WithUserDetails(value = "테스트유저", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void deleteJoin() throws Exception{
        Category category = Category.builder()
                .name("카테고리").build();
        categoryRepository.save(category);

        User user = User.builder()
                .nickname("유저")
                .email("user@test.com")
                .password("password").build();
        userRepository.save(user);

        LocalDateTime now = LocalDateTime.now();
        Deal deal = Deal.builder()
                .category(category)
                .title("제목")
                .content("내용")
                .price(1000L)
                .quantity(10)
                .unitQuantity(2)
                .unitPrice(200L)
                .totalCount(10)
                .url("url/")
                .expireTime(now.plusDays(3))
                .nowCount(2)
                .user(user)
                .build();
        dealRepository.save(deal);

        DealJoin dealJoin = DealJoin.builder()
                .quantity(5)
                .build();

        mockMvc.perform(post("/deal/{dealId}/enrollment", deal.getId())
                        .content(objectMapper.writeValueAsString(dealJoin))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/deal/{dealId}/enrollment", deal.getId())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        mockMvc.perform(get("/deal/{dealId}", deal.getId())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("구매자 명단")
    void getJoin() throws Exception{

    }

//    @Test
//    @DisplayName("판매 내역 조회")
//    void getMySellList() throws Exception{
//        User user = User.builder()
//                .nickname("유저").build();
//        userRepository.save(user);
//
//        Category category = Category.builder()
//                .name("카테고리").build();
//        categoryRepository.save(category);
//        LocalDateTime date = LocalDateTime.now();
//        List<Deal> deals = IntStream.range(0,5)
//                .mapToObj(i -> Deal.builder()
//                        .title("제목" +i)
//                        .category(category)
//                        .content("내용")
//                        .price(1000L)
//                        .unitPrice(200L)
//                        .totalCount(i)
//                        .url("url/")
//                        .expireTime(date.plusDays(i%4))
//                        .quantity(10)
//                        .unitQuantity(2)
//                        .nowCount(i)
//                        .user(user)
//                        .build()).collect(Collectors.toList());
//        dealRepository.saveAll(deals);
//
//        mockMvc.perform(get("/deal/sale/{userId}",user.getNickname())
//                        .contentType(APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andDo(print());
//    }
//
//
//    @Test
//    @DisplayName("구매 내역 조회")
//    void getMyJoinListTemp() throws Exception{
//
//        List<User> users = IntStream.range(0,5)
//                .mapToObj(i -> User.builder()
//                        .nickname("이름" +i)
//                        .build()).collect(Collectors.toList());
//        userRepository.saveAll(users);
//
//        Category category = Category.builder()
//                .name("카테고리").build();
//        categoryRepository.save(category);
//        LocalDateTime date = LocalDateTime.now();
//        List<Deal> deals = IntStream.range(0,5)
//                .mapToObj(i -> Deal.builder()
//                        .title("제목" +i)
//                        .category(category)
//                        .content("내용")
//                        .price(1000L)
//                        .unitPrice(200L)
//                        .totalCount(i)
//                        .url("url/")
//                        .expireTime(date.plusDays(i%4))
//                        .quantity(10)
//                        .unitQuantity(2)
//                        .nowCount(i)
//                        .user(users.get(i))
//                        .build()).collect(Collectors.toList());
//        dealRepository.saveAll(deals);
//
//        List<DealMember> dealMembers = IntStream.range(1,5)
//                .mapToObj(i -> DealMember.builder()
//                        .host(false)
//                        .deal(deals.get(i%5))
//                        .user(users.get(0))
//                        .quantity(i%5)
//                        .build()).collect(Collectors.toList());
//        dealMemberRepository.saveAll(dealMembers);
//
//        mockMvc.perform(get("/deal/sale/{userId}", users.get(0).getNickname())
//                        .contentType(APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andDo(print());
//    }
//
//    @Test
//    @DisplayName("게시글 가져오기")
//    void getDeal() throws Exception{
//        Category category = Category.builder()
//                .name("카테고리").build();
//        categoryRepository.save(category);
//
//        User user = User.builder()
//                .nickname("유저").build();
//        LocalDateTime date = LocalDateTime.now();
//
//        List<Deal> deals = IntStream.range(0,20)
//                .mapToObj(i -> Deal.builder()
//                        .title("제목" +i)
//                        .category(category)
//                        .content("내용")
//                        .price(1000L)
//                        .unitPrice(200L)
//                        .totalCount(i)
//                        .url("url/")
//                        .expireTime(date.plusDays(i%4))
//                        .quantity(10)
//                        .unitQuantity(2)
//                        .nowCount(i/2)
//                        .build()).collect(Collectors.toList());
//        dealRepository.saveAll(deals);
//
//        mockMvc.perform(get("/deal")
//                        .contentType(APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andDo(print());
//    }

}