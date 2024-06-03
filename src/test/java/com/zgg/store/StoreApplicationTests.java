package com.zgg.store;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.SQLException;

@SpringBootTest
class StoreApplicationTests {

    @Autowired
    private DataSource dataSource;//自动装配
    @Value("${user.address.max-count}")
    private Integer maxCount;
    @Test
    void contextLoads() {
        System.out.println(maxCount);

    }
    @Test
    void getConnection() throws SQLException {
        System.out.println(dataSource.getConnection());
    }

}
