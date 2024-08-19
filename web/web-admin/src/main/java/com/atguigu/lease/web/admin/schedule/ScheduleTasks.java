package com.atguigu.lease.web.admin.schedule;

import com.atguigu.lease.model.entity.LeaseAgreement;
import com.atguigu.lease.model.enums.LeaseStatus;
import com.atguigu.lease.web.admin.service.LeaseAgreementService;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 定时任务
 */
@Component
public class ScheduleTasks {

//    @Scheduled(cron = "* * * * * *")
//    public void task(){
//        System.out.println(new Date());
//
//    }

    @Autowired
    private LeaseAgreementService leaseAgreementService;

    @Scheduled(cron = "0 0 0 * * *")
    public void checkLeaseStatus(){
        LambdaUpdateWrapper<LeaseAgreement> leaseAgreementLambdaUpdateWrapper = new LambdaUpdateWrapper<>();

        // 修改  当前到期，并且状态为已签约和退租待确认的租约信息
        leaseAgreementLambdaUpdateWrapper.le(LeaseAgreement::getLeaseEndDate, new Date());
        leaseAgreementLambdaUpdateWrapper.in(LeaseAgreement::getStatus, LeaseStatus.SIGNED, LeaseStatus.WITHDRAWING);
        leaseAgreementLambdaUpdateWrapper.set(LeaseAgreement::getStatus, LeaseStatus.EXPIRED);

        leaseAgreementService.update(leaseAgreementLambdaUpdateWrapper);
    }

}
