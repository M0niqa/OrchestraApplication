package com.monika.worek.orchestra;

import com.monika.worek.orchestra.controller.common.RootController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class OrchestraApplicationTest {

    @Autowired
    private RootController controller;

    @Test
    void contextLoads() {
        assertThat(controller).isNotNull();
    }
}