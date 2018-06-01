/**
 * @Title: CommitSetJPATest.java
 * @Prject: gitlabEventCenter
 * @Package: com.oscar.gitlabEventCenter
 * @author: Yukai  
 * @date: 2018年5月24日 下午2:20:02
 */
package com.oscar.gitlabEventCenter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import com.oscar.gitlabEventCenter.jpa.entity.CommitSet;
import com.oscar.gitlabEventCenter.jpa.entity.CommitSet.CommitSetBuilder;
import com.oscar.gitlabEventCenter.jpa.repository.CommitSetRepository;

/**
 * @ClassName: CommitSetJPATest
 * @Description: TODO
 * @author Yukai
 * @data 2018年5月24日 下午2:20:02
 */
@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class CommitSetJPATest {
    private static Logger logger = LoggerFactory.getLogger(CommitSetJPATest.class);
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private CommitSetRepository repository;
    
    @Before
    public void setUp() {
        CommitSetBuilder builder = new CommitSetBuilder(1234, "eevvvvveefsdfafdedafda");
        CommitSet commitSet = builder.author("yukai")
               .commitUrl("http://yukai.space")
               .message("test")
               .projectName("test")
               .projectUrl("http://yukai.space").build();
        this.entityManager.persist(commitSet);
        
        builder = new CommitSetBuilder(2345, "eeeeeefsdfafdedafda");
        commitSet = builder.author("yukai")
               .commitUrl("http://yukai.space")
               .message("test")
               .projectName("test")
               .projectUrl("http://yukai.space").build();
        this.entityManager.persist(commitSet);
    }
    
    @Test
    public void testQuery() {
        // fetch all CommitSet
        logger.info("CommitSet found with findAll():");
        logger.info("-------------------------------");
        for (CommitSet customer : repository.findAll()) {
            logger.info(customer.toString());
        }
        logger.info("");

        // fetch an individual CommitSet by ID
        repository.findById((long) 1234)
            .ifPresent(customer -> {
                logger.info("CommitSet found with findById(1234):");
                logger.info("--------------------------------");
                logger.info(customer.toString());
                logger.info("");
            });

        logger.info("");
    }
}
