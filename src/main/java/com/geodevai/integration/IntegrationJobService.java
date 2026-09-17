package com.geodevai.integration;

import com.geodevai.data.model.IntegrationSchedule;
import com.geodevai.data.repository.IntegrationRepository;
import com.geodevai.data.repository.IntegrationScheduleRepository;
import com.geodevai.integration.IntegrationDispatcher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class IntegrationJobService {

    private final IntegrationScheduleRepository scheduleRepository;
    private final IntegrationDispatcher dispatcher;
    private final IntegrationRepository integrationRepository;

    public IntegrationJobService(IntegrationScheduleRepository scheduleRepository,
                                  IntegrationDispatcher dispatcher,
                                  IntegrationRepository integrationRepository) {
        this.scheduleRepository = scheduleRepository;
        this.dispatcher = dispatcher;
        this.integrationRepository = integrationRepository;
    }

    @Scheduled(cron = "0 */5 * * * *")
    public void runScheduledIntegrations() {
        List<IntegrationSchedule> activeSchedules = scheduleRepository.findByActiveIsTrue();
        for (IntegrationSchedule schedule : activeSchedules) {
            try {
                if (isDue(schedule)) {
                    dispatcher.dispatch(schedule.getIntegration().getIntegrationId(), integrationRepository);
                    schedule.setLastRunTime(LocalDateTime.now());
                    schedule.setCurrentRetryCount(0);
                    schedule.setNextRunTime(calculateNextRunTime(schedule.getCronExpression()));
                    scheduleRepository.save(schedule);
                }
            } catch (Exception e) {
                schedule.setCurrentRetryCount(schedule.getCurrentRetryCount() + 1);
                if (schedule.getCurrentRetryCount() >= schedule.getMaxRetries()) {
                    schedule.setActive(false);
                }
                scheduleRepository.save(schedule);
            }
        }
    }

    private boolean isDue(IntegrationSchedule schedule) {
        if (schedule.getNextRunTime() == null) return true;
        return LocalDateTime.now().isAfter(schedule.getNextRunTime());
    }

    private LocalDateTime calculateNextRunTime(String cronExpression) {
        return LocalDateTime.now().plusMinutes(5);
    }
}
