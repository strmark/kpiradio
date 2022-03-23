package nl.strmark.piradio.job

import mu.KotlinLogging
import nl.strmark.piradio.controller.PlayerController
import org.quartz.JobExecutionContext
import org.springframework.scheduling.quartz.QuartzJobBean
import org.springframework.stereotype.Component
import org.springframework.web.context.support.SpringBeanAutowiringSupport

@Component
class AlarmJob(private val playerController: PlayerController) : QuartzJobBean() {
    companion object {
        private val logger = KotlinLogging.logger {}
    }

    override fun executeInternal(jobExecutionContext: JobExecutionContext) {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this)
        logger.info { "Executing Job with key ${jobExecutionContext.jobDetail.key}" }
        logger.info { "Start player" }
        val jobDataMap = jobExecutionContext.mergedJobDataMap
        playerController.startPlayer(jobDataMap.getString("url"), jobDataMap.getInt("autoStopMinutes"))
    }
}
