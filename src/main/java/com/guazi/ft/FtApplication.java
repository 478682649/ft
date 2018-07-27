package com.guazi.ft;

import com.guazi.ft.common.CommonUtil;
import com.guazi.ft.common.DateUtil;
import com.guazi.ft.common.ExcelUtil;
import com.guazi.ft.common.SpringContextUtil;
import com.guazi.ft.config.profile.ProFile;
import com.guazi.ft.constant.PropertiesConstants;
import com.guazi.ft.exception.FtException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

/**
 * SpringBoot启动类
 *
 * @author shichunyang
 */
@SpringBootApplication
@EnableTransactionManagement
@RestController
@Slf4j
@EnableConfigurationProperties({PropertiesConstants.class})
@EnableScheduling
@EnableRetry
//@EnableEurekaClient
//@EnableDiscoveryClient
//@EnableFeignClients
//@EnableHystrix
public class FtApplication {

//    @Bean
//    public IRule iRule() {
//        return new RoundRobinRule();
//    }

    private final PropertiesConstants propertiesConstants;

    private final ProFile proFile;

    @Autowired
    public FtApplication(
            PropertiesConstants propertiesConstants,
            ProFile proFile
    ) {
        this.propertiesConstants = propertiesConstants;
        this.proFile = proFile;
    }

    @GetMapping("/")
    public String helloWorld() {
        log.info(proFile.proFile());
        return "<h1>Hello World!, 环境==>" + propertiesConstants.getConstant() + "</h1>";
    }

    //@Autowired
    @Qualifier("consignKafkaTemplate")
    private KafkaTemplate<String, String> kafkaTemplate;
    //@Value("${kafka.consign.topic}")
    private String topic;

    //@GetMapping("/kafka")
    public String kafka() {

        String key = DateUtil.getCurrentDateStr();
        String value = "最新工单id" + "_" + CommonUtil.getOrderNumber();
        kafkaTemplate.send(topic, key, value);
        return "success";
    }

    private ThreadLocal<Long> threadTime = new ThreadLocal<>();

    @Retryable(value = {Exception.class}, maxAttempts = 5, backoff = @Backoff(delay = 5000L, multiplier = 2, maxDelay = 86400000L))
    @GetMapping("/rpc")
    public String rpc() {
        if (threadTime.get() != null) {
            log.info("rpc test, time==>{}", System.currentTimeMillis() - threadTime.get());
        } else {
            threadTime.set(System.currentTimeMillis());
        }

        throw new FtException(500, "重试机制测试");
    }

    //@Autowired
    //@Qualifier("ftRabbitTemplate")
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/rabbit")
    public String rabbit() {
        Map<String, Object> map = new HashMap<>(16);
        map.put("username", "春阳");
        map.put("password", "success");
        rabbitTemplate.convertAndSend("ft.direct.exchange", "q1", map);
        return "success";
    }

    //@Autowired
    private JavaMailSender javaMailSender;

    @GetMapping("/mail")
    public String mail(HttpServletRequest request) throws Exception {
        MimeMessage mimeMailMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMailMessage, true);

        mimeMessageHelper.setTo("shichunyang@guazi.com");
        mimeMessageHelper.setFrom("903031015@qq.com");
        mimeMessageHelper.setSubject("一封测试邮件");

        String cid = "monster";
        mimeMessageHelper.setText("<h1>Hello World</h1> <img src='cid:" + cid + "' />", true);

        String sheetTitle = "人员信息";

        String[] columnChs = {"姓名", "年龄", "日期"};
        String[] columnEns = {"username", "age", "date"};

        Map<String, Object> scy = new HashMap<>(16);
        scy.put(columnEns[0], "史春阳");
        scy.put(columnEns[1], "28");
        scy.put(columnEns[2], new Date());

        List<Map<String, Object>> dataList = new ArrayList<>();
        dataList.add(scy);

        ByteArrayOutputStream excelOut = new ByteArrayOutputStream();
        ExcelUtil.createExcel(excelOut, sheetTitle, columnChs, columnEns, dataList);


        ByteArrayOutputStream jpegOut = new ByteArrayOutputStream();
        InputStream jpegIn = new FileInputStream("/Users/shichunyang/Downloads/monster.jpeg");
        IOUtils.copy(jpegIn, jpegOut);
        jpegIn.close();

        mimeMessageHelper.addInline(cid, new ByteArrayResource(jpegOut.toByteArray()), request.getServletContext().getMimeType("*.jpeg"));
        mimeMessageHelper.addAttachment("人员信息.xls", new ByteArrayResource(excelOut.toByteArray()), request.getServletContext().getMimeType("*.xls"));

        jpegOut.close();
        excelOut.close();

        javaMailSender.send(mimeMailMessage);
        return "success";
    }

    //@Autowired
    //private DiscoveryClient discoveryClient;

    @GetMapping("/discovery")
    public String discovery() {
        //List<String> services = discoveryClient.getServices();
        //System.out.println(JsonUtil.object2Json(services));
        //List<ServiceInstance> serviceInstances = discoveryClient.getInstances("business");
        //return JsonUtil.object2Json(serviceInstances);
        return "discovery";
    }

    /**
     * mvn clean package -Pstag -DskipTests=true
     * nohup java -jar ft-0.0.1-SNAPSHOT.jar > /root/temp.out 2>&1 &
     */
    public static void main(String[] args) {

        // 托管hystrix线程池
        //HystrixPlugins.getInstance().registerConcurrencyStrategy(new ThreadLocalHystrixConcurrencyStrategy());

        ApplicationContext applicationContext = SpringApplication.run(FtApplication.class, args);
        SpringContextUtil.setApplicationContext(applicationContext);

        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "20");
    }
}
