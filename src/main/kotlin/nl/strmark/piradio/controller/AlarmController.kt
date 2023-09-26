package nl.strmark.piradio.controller

import mu.KotlinLogging
import nl.strmark.piradio.entity.Alarm
import nl.strmark.piradio.entity.WebRadio
import nl.strmark.piradio.exception.ResourceNotFoundException
import nl.strmark.piradio.job.AlarmJob
import nl.strmark.piradio.payload.ScheduleAlarmResponse
import nl.strmark.piradio.repository.AlarmRepository
import nl.strmark.piradio.repository.WebRadioRepository
import org.quartz.CronScheduleBuilder
import org.quartz.JobBuilder.newJob
import org.quartz.JobDataMap
import org.quartz.JobDetail
import org.quartz.JobKey
import org.quartz.Scheduler
import org.quartz.SchedulerException
import org.quartz.Trigger
import org.quartz.TriggerBuilder.newTrigger
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.notFound
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.time.ZonedDateTime
import java.util.Date

@CrossOrigin(origins = ["*"], allowedHeaders = ["*"])
@RestController
class AlarmController(
    private val alarmRepository: AlarmRepository,
    private val webRadioRepository: WebRadioRepository,
    private val scheduler: Scheduler
) {
    @GetMapping("/alarms")
    fun findAll(): MutableList<Alarm?> = alarmRepository.findAll()

    @PostMapping(path = ["/alarms"])
    fun saveAlarm(@RequestBody alarm: Alarm): Alarm? = alarmRepository.save(alarm)

    @GetMapping(path = ["/alarms/{id}"])
    fun findById(@PathVariable(value = "id") alarmId: Int): Alarm? =
        alarmRepository.findById(alarmId)
            .orElseThrow { ResourceNotFoundException(ALARM, "id", alarmId) }

    @PutMapping(path = ["/alarms/{id}"])
    fun updateAlarm(
        @PathVariable(value = "id") alarmId: Int,
        @RequestBody alarmDetails: Alarm
    ): Alarm? {
        assert(alarmDetails.id == alarmId)
        return alarmRepository.findById(alarmId)
            .orElseThrow { ResourceNotFoundException(ALARM, "id", alarmId) }
            ?.let { alarm ->
                scheduleAlarm(
                    alarmDetails.webRadio,
                    alarmDetails.isActive,
                    alarmDetails.autoStopMinutes,
                    getCronSchedule(alarmDetails)
                )
                saveAlarm(alarm, alarmDetails)
            }
    }

    @DeleteMapping(path = ["/alarms/{id}"])
    fun deleteAlarm(@PathVariable(value = "id") alarmId: Int): ResponseEntity<Long> =
        alarmRepository.findById(alarmId)
            .orElseThrow { ResourceNotFoundException(ALARM, "id", alarmId) }
            ?.let { alarm ->
                try {
                    JobKey("PI_RADIO" + alarm.webRadio, ALARM_JOBS).let { jobKey ->
                        if (scheduler.checkExists(jobKey)) {
                            logger.info { "Delete schedule" }
                            scheduler.deleteJob(jobKey)
                        }
                    }
                } catch (ex: SchedulerException) {
                    logger.error { "Error scheduling Alarm $ex" }
                }
                alarmRepository.delete(alarm)
                ok().build()
            } ?: notFound().build()

    private fun scheduleAlarm(webRadioId: Int, isActive: Boolean, autoStopMinutes: Int, cronSchedule: String) {
        try {
            val webRadioOptional = webRadioRepository.findById(webRadioId)
            var webRadio: WebRadio? = null
            if (webRadioOptional.isPresent) webRadio = webRadioOptional.get()
            JobKey(PI_RADIO + webRadioId, ALARM_JOBS).let { jobKey ->
                if (scheduler.checkExists(jobKey)) {
                    logger.info { "Already exists" }
                    scheduler.deleteJob(jobKey)
                }
            }
            if (isActive) {
                buildJobDetail(PI_RADIO + webRadioId, webRadioId, autoStopMinutes, webRadio?.url ?: "dummy").let { jobDetail ->
                    buildJobTrigger(jobDetail, cronSchedule, ZonedDateTime.now()).let { scheduler.scheduleJob(jobDetail, it) }
                    ScheduleAlarmResponse(true, jobDetail.key.name, jobDetail.key.group, "Alarm Scheduled Successfully!")
                }
            }
        } catch (ex: SchedulerException) {
            logger.error { "Error scheduling Alarm $ex" }
            ScheduleAlarmResponse(false, "Error scheduling Alarm. Please try later!")
        }
    }

    private fun saveAlarm(alarm: Alarm, alarmDetails: Alarm): Alarm {
        alarm.minute = alarmDetails.minute
        alarm.hour = alarmDetails.hour
        alarm.name = alarmDetails.name
        alarm.monday = alarmDetails.monday
        alarm.tuesday = alarmDetails.tuesday
        alarm.wednesday = alarmDetails.wednesday
        alarm.thursday = alarmDetails.thursday
        alarm.friday = alarmDetails.friday
        alarm.saturday = alarmDetails.saturday
        alarm.sunday = alarmDetails.sunday
        alarm.autoStopMinutes = alarmDetails.autoStopMinutes
        alarm.isActive = alarmDetails.isActive
        alarm.webRadio = alarmDetails.webRadio
        return alarmRepository.save(alarm)
    }

    private fun getCronSchedule(alarmDetails: Alarm?): String {
        // 0 45 6 ? * MON,TUE,WED,THU,FRI *
        val cronSchedule = "0 ${alarmDetails?.minute ?: 0} ${alarmDetails?.hour ?: 0} ? * "
        val cronDays: MutableList<String> = arrayListOf()

        alarmDetails?.let { alarm ->
            addToCronDays(alarm.monday, "MON", cronDays)
            addToCronDays(alarm.tuesday, "TUE", cronDays)
            addToCronDays(alarm.wednesday, "WED", cronDays)
            addToCronDays(alarm.thursday, "THU", cronDays)
            addToCronDays(alarm.friday, "FRI", cronDays)
            addToCronDays(alarm.saturday, "SAT", cronDays)
            addToCronDays(alarm.sunday, "SUN", cronDays)
        }

        logger.info("$cronSchedule${cronDays.joinToString(",")} *")
        return "$cronSchedule${cronDays.joinToString(",")} *"
    }

    private fun addToCronDays(alarm: Boolean, day: String, cronDays: MutableList<String>) {
        when {
            alarm -> cronDays.add(day)
        }
    }

    private fun buildJobDetail(alarmName: String, webRadio: Int, autoStopMinutes: Int, url: String?): JobDetail {
        val jobDataMap = JobDataMap()
        jobDataMap["webRadio"] = webRadio
        jobDataMap["autoStopMinutes"] = autoStopMinutes
        jobDataMap["url"] = url
        return newJob(AlarmJob::class.java)
            .withIdentity(alarmName, ALARM_JOBS)
            .withDescription("Alarm Job")
            .usingJobData(jobDataMap)
            .storeDurably(true)
            .build()
    }

    private fun buildJobTrigger(jobDetail: JobDetail, cronSchedule: String, startAt: ZonedDateTime): Trigger =
        newTrigger()
            .forJob(jobDetail)
            .withIdentity(jobDetail.key.name, "Alarm-triggers")
            .withDescription("Alarm Trigger")
            .startAt(Date.from(startAt.toInstant()))
            .withSchedule(CronScheduleBuilder.cronSchedule(cronSchedule))
            .build()

    companion object {
        private val logger = KotlinLogging.logger {}
        private const val ALARM = "Alarm"
        private const val ALARM_JOBS = "Alarm-jobs"
        private const val PI_RADIO = "PiRadio_"
    }
}
