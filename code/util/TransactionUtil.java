package com.jdh.fuhsi.portal.util;

import com.jdh.log.LogTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.function.Consumer;

/**
 * @author lym
 * @date 2020/3/9 16:26
 */
@Component
public class TransactionUtil {

    @Autowired
    private PlatformTransactionManager transactionManager;

    public boolean transact(Consumer consumer){
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        // explicitly setting the transaction name is something that can only be done programmatically
        def.setName("NewTxManager");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus status = transactionManager.getTransaction(def);
        try {
            consumer.accept(null);

            transactionManager.commit(status);
            return true;
        }catch (Exception e){
            LogTools.warn("事务异常:{}",e);
            transactionManager.rollback(status);
            return false;
        }
    }
}
