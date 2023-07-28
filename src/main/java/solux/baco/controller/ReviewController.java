package solux.baco.controller;


import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import solux.baco.domain.Member;
import solux.baco.domain.Review;
import solux.baco.service.MemberService;
import solux.baco.service.ReviewModel.ReviewDTO;
import solux.baco.service.ReviewModel.ReviewDetailDTO;
import solux.baco.service.ReviewService;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import solux.baco.service.ReviewModel.ReviewDTO;
import solux.baco.service.ReviewService;
import solux.baco.service.RouteService;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/Review")

public class ReviewController {

    private final ReviewService reviewService;


    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;

    }


    //@GetMapping("/test")
    public void dateTest() {
        /**
         //LocalDateTime 이용한 방법.
         LocalDateTime date = LocalDateTime.now();
         log.info("checklog: date = {}",date);
         */
    }


    //후기 저장(후기작성)
    @PostMapping("/save")
    @ResponseBody
    public ResponseEntity<String> saveReviewController(HttpSession session, @RequestBody ReviewDTO reviewData) { //@RequestBody : 요청바디와 데이터 매핑.
        try {
            //log.info("checklog: email:{}, reviewData:{}",email,reviewData);
            //예외처리
            /**정상적인 로직 가능한 경우에 실행되는 부분*/

            //1. ReviewDTO형태의 reviewData를 통해 startPlace,endPlace,content 추출.
            String startPlace = reviewData.getStartPlace();
            String endPlace = reviewData.getEndPlace();
            String content = reviewData.getContent();
            log.info("checklog: startPlace:{},endPlace:{},content:{}", startPlace, endPlace, content);
            //ReviewService 호출
            reviewService.saveReview(session, startPlace, endPlace, content);

            //db저장 성공 시
            return ResponseEntity.status(HttpStatus.CREATED).body("후기가 저장됐습니다.");


        } catch (Exception e) {
            //db저장 실패 시
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("후기 저장에 실패하였습니다.");

        }

    }



    //후기 게시글 상세 조회
    @GetMapping("/detail/{review_id}")
    public ReviewDetailDTO reviewDetailContriller (@PathVariable Long review_id){
        try {
            //예외처리 구현 예정
            return reviewService.reviewDetail(review_id);



        }catch (Exception e) {
            //예외처리 구현 예정
            return null;
        }

    }


}
