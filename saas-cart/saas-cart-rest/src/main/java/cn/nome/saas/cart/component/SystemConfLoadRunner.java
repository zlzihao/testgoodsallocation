package cn.nome.saas.cart.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import cn.nome.saas.cart.service.SysConfService;

/**
 * @author chentaikuang
 */
@Order(value = 100)
@Component
public class SystemConfLoadRunner implements CommandLineRunner {

    @Autowired
    private SysConfService sysConfService;

    @Override
    public void run(String... args) throws Exception {
        sysConfService.initLoad();
    }
}