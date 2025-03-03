package ru.yandex.practicum.filmorate.controller.review;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.FilmorateApplication;
import ru.yandex.practicum.filmorate.service.ReviewService;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = FilmorateApplication.class)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class DbReviewControllerTest extends ReviewControllerTest {

    @Autowired
    private ReviewService reviewService;

    @Override
    void init() {
    }

    @Test
    void contextLoads() {
        assertNotNull(reviewController);
        assertNotNull(reviewService);
    }
}