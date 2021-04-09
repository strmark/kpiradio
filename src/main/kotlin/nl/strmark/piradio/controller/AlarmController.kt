package nl.strmark.piradio.controller

import mu.KotlinLogging
import nl.strmark.piradio.entity.Alarm
import nl.strmark.piradio.entity.Webradio
import nl.strmark.piradio.exception.ResourceNotFoundException
import nl.strmark.piradio.job.AlarmJob
import nl.strmark.piradio.payload.ScheduleAlarmResponse
import nl.strmark.piradio.repository.AlarmRepository
import nl.strmark.piradio.repository.WebradioRepository
import org.quartz.CronScheduleBuilder
import org.quartz.JobBuilder
import org.quartz.JobDataMap
import org.quartz.JobDetail
import org.quartz.JobKey
import org.quartz.Scheduler
import org.quartz.SchedulerException
import org.quartz.Trigger
import org.quartz.TriggerBuilder
import org.springframework.http.ResponseEntity
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
    private val webradioRepository: WebradioRepository,
    private val scheduler: Scheduler
) {

    @GetMapping("/alarms")
    fun findAll() = alarmRepository.findAll()

    @PostMapping(path = ["/alarms"])
    fun saveAlarm(@RequestBody alarmData: Alarm?): Alarm? {
        return saveAlarm(alarmData, null)
    }

    @GetMapping(path = ["/alarms/{id}"])
    fun findById(@PathVariable(value = "id") alarmId: Int): Alarm? {
        return alarmRepository.findById(alarmId)
            .orElseThrow { ResourceNotFoundException(ALARM, "id", alarmId) }
    }

    @PutMapping(path = ["/alarms/{id}"])
    fun updateAlarm(
        @PathVariable(value = "id") alarmId: Int?,
        @RequestBody alarmDetails: Alarm?
    ): Alarm? {
        return when {
            alarmDetails != null -> {
                scheduleAlarm(
                    alarmDetails.webradio,
                    alarmDetails.isActive,
                    alarmDetails.autoStopMinutes,
                    getCronSchedule(alarmDetails)
                )
                saveAlarm(alarmDetails, alarmId)
            }
            else -> null
        }
    }

    @DeleteMapping(path = ["/alarms/{id}"])
    fun deleteAlarm(@PathVariable(value = "id") alarmId: Int): ResponseEntity<Long> {
        val alarm = alarmRepository.findById(alarmId)
            .orElseThrow { ResourceNotFoundException(ALARM, "id", alarmId) }
        try {
            val jobKey = JobKey("PIRADIO" + alarm?.webradio, ALARM_JOBS)

            when {
                scheduler.checkExists(jobKey) -> {
                    logger.info { "Delete schedule" }
                    scheduler.deleteJob(jobKey)
                }
            }
        } catch (ex: SchedulerException) {
            logger.error { "Error scheduling Alarm $ex" }
        }
        return when {
            alarm != null -> {
                alarmRepository.delete(alarm)
                ResponseEntity.ok().build()
            }
            else -> ResponseEntity.notFound().build()
        }

    }

    private fun scheduleAlarm(webradioId: Int, isActive: Boolean, autoStopMinutes: Int, cronSchedule: String) {
        try {
            val webradioOptional = webradioRepository.findById(webradioId)
            var webradio: Webradio? = null
            when {
                webradioOptional.isPresent -> {
                    webradio = webradioOptional.get()
                }
            }
            val jobkey = JobKey(PIRADIO + webradioId, ALARM_JOBS)
            when {
                scheduler.checkExists(jobkey) -> {
                    logger.info { "Already exists" }
                    scheduler.deleteJob(jobkey)
                }
            }
            when {
                isActive -> {
                    //JobDetail jobDetail = buildJobDetail(alarmDetails.getName()+'_'+alarmDetails.getWebradio()
                    val jobDetail = buildJobDetail(
                        PIRADIO + webradioId,
                        webradioId,
                        autoStopMinutes,
                        when {
                            webradio != null -> webradio.url
                            else -> "dummy"
                        }
                    )
                    val trigger = buildJobTrigger(jobDetail, cronSchedule, ZonedDateTime.now())
                    scheduler.scheduleJob(jobDetail, trigger)
                    ScheduleAlarmResponse(
                        true,
                        jobDetail.key.name, jobDetail.key.group, "Alarm Scheduled Successfully!"
                    )
                }
            }
        } catch (ex: SchedulerException) {
            logger.error { "Error scheduling Alarm $ex" }
            ScheduleAlarmResponse(
                false,
                "Error scheduling Alarm. Please try later!"
            )
        }
    }

    private fun getCronSchedule(alarmDetails: Alarm?): String {
        //0 45 6 ? * MON,TUE,WED,THU,FRI *
        val cronSchedule = ("0 "
                + (alarmDetails?.minute ?: 0) + " "
                + (alarmDetails?.hour ?: 0)
                + " ? * ")
        var cronDays = ""
        when {
            alarmDetails != null -> {
                cronDays = stringAppend(cronDays, alarmDetails.monday, "MON")
                cronDays = stringAppend(cronDays, alarmDetails.tuesday, "TUE")
                cronDays = stringAppend(cronDays, alarmDetails.wednesday, "WED")
                cronDays = stringAppend(cronDays, alarmDetails.thursday, "THU")
                cronDays = stringAppend(cronDays, alarmDetails.friday, "FRI")
                cronDays = stringAppend(cronDays, alarmDetails.saturday, "SAT")
                cronDays = stringAppend(cronDays, alarmDetails.sunday, "SUN")
            }
        }
        return "$cronSchedule$cronDays *"
    }

    private fun stringAppend(cronDays: String, isDay: Boolean, day: String): String {
        return when {
            isDay -> {
                when {
                    cronDays.isEmpty() -> day
                    else -> "$cronDays,$day"
                }
            }
            else -> ""
        }
    }

    private fun saveAlarm(alarmData: Alarm?, alarmId: Int?): Alarm? {
        var alarm = Alarm()
        when {
            alarmId != null -> {
                alarmRepository.findById(alarmId)
                    .orElseThrow { ResourceNotFoundException(ALARM, "id", alarmId) }.also {
                        when {
                            it != null -> {
                                alarm = it
                            }
                        }
                    }
            }
        }
        return when {
            alarmData != null -> {
                alarm.minute = alarmData.minute
                alarm.hour = alarmData.hour
                alarm.name = alarmData.name
                alarm.monday = alarmData.monday
                alarm.tuesday = alarmData.tuesday
                alarm.wednesday = alarmData.wednesday
                alarm.thursday = alarmData.thursday
                alarm.friday = alarmData.friday
                alarm.saturday = alarmData.saturday
                alarm.sunday = alarmData.sunday
                alarm.autoStopMinutes = alarmData.autoStopMinutes
                alarm.isActive = alarmData.isActive
                alarm.webradio = alarmData.webradio
                alarmRepository.save(alarm)
            }
            else -> null
        }
    }

    private fun buildJobDetail(alarmName: String, webradio: Int, autoStopMinutes: Int, url: String?): JobDetail {
        val jobDataMap = JobDataMap()
        jobDataMap["webradio"] = webradio
        jobDataMap["autoStopMinutes"] = autoStopMinutes
        jobDataMap["url"] = url
        return JobBuilder.newJob(AlarmJob::class.java)
            .withIdentity(alarmName, ALARM_JOBS)
            .withDescription("Alarm Job")
            .usingJobData(jobDataMap)
            .storeDurably(true)
            .build()
    }

    private fun buildJobTrigger(jobDetail: JobDetail, cronSchedule: String, startAt: ZonedDateTime): Trigger {
        return TriggerBuilder.newTrigger()
            .forJob(jobDetail)
            .withIdentity(jobDetail.key.name, "Alarm-triggers")
            .withDescription("Alarm Trigger")
            .startAt(Date.from(startAt.toInstant()))
            .withSchedule(CronScheduleBuilder.cronSchedule(cronSchedule))
            .build()
    }

    companion object {
        private val logger = KotlinLogging.logger {}
        private const val ALARM = "Alarm"
        private const val ALARM_JOBS = "Alarm-jobs"
        private const val PIRADIO = "Piradio_"
    }
}