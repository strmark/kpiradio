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
    private val webRadioRepository: WebRadioRepository,
    private val scheduler: Scheduler
) {
    companion object {
        private val logger = KotlinLogging.logger {}
        private const val ALARM = "Alarm"
        private const val ALARM_JOBS = "Alarm-jobs"
        private const val PI_RADIO = "PiRadio_"
    }

    @GetMapping("/alarms")
    fun findAll(): MutableList<Alarm?> = alarmRepository.findAll()

    @PostMapping(path = ["/alarms"])
    fun saveAlarm(@RequestBody alarm: Alarm): Alarm? {
        return alarmRepository.save(alarm)
    }

    @GetMapping(path = ["/alarms/{id}"])
    fun findById(@PathVariable(value = "id") alarmId: Int): Alarm? {
        return alarmRepository.findById(alarmId)
            .orElseThrow { ResourceNotFoundException(ALARM, "id", alarmId) }
    }

    @PutMapping(path = ["/alarms/{id}"])
    fun updateAlarm(
        @PathVariable(value = "id") alarmId: Int,
        @RequestBody alarmDetails: Alarm
    ): Alarm? {
        assert(alarmDetails.id == alarmId)
        val alarm: Alarm? = alarmRepository.findById(alarmId)
            .orElseThrow { ResourceNotFoundException(ALARM, "id", alarmId) }

        return when {
            alarm != null -> {
                scheduleAlarm(
                    alarmDetails.webRadio,
                    alarmDetails.isActive,
                    alarmDetails.autoStopMinutes,
                    getCronSchedule(alarmDetails)
                )
                saveAlarm(alarm, alarmDetails)
            }
            else -> null
        }
    }

    @DeleteMapping(path = ["/alarms/{id}"])
    fun deleteAlarm(@PathVariable(value = "id") alarmId: Int): ResponseEntity<Long> {
        val alarm = alarmRepository.findById(alarmId)
            .orElseThrow { ResourceNotFoundException(ALARM, "id", alarmId) }
        try {
            val jobKey = JobKey("PI_RADIO" + alarm?.webRadio, ALARM_JOBS)

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

    private fun scheduleAlarm(webRadioId: Int, isActive: Boolean, autoStopMinutes: Int, cronSchedule: String) {
        try {
            val webRadioOptional = webRadioRepository.findById(webRadioId)
            var webRadio: WebRadio? = null
            when {
                webRadioOptional.isPresent -> {
                    webRadio = webRadioOptional.get()
                }
            }
            val jobKey = JobKey(PI_RADIO + webRadioId, ALARM_JOBS)
            when {
                scheduler.checkExists(jobKey) -> {
                    logger.info { "Already exists" }
                    scheduler.deleteJob(jobKey)
                }
            }
            when {
                isActive -> {
                    // JobDetail jobDetail = buildJobDetail(alarmDetails.getName()+'_'+alarmDetails.getWebRadio()
                    val jobDetail = buildJobDetail(
                        PI_RADIO + webRadioId,
                        webRadioId,
                        autoStopMinutes,
                        when {
                            webRadio != null -> webRadio.url
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
        val cronSchedule = (
            "0 " +
                (alarmDetails?.minute ?: 0) + " " +
                (alarmDetails?.hour ?: 0) +
                " ? * "
            )
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

    private fun buildJobDetail(alarmName: String, webRadio: Int, autoStopMinutes: Int, url: String?): JobDetail {
        val jobDataMap = JobDataMap()
        jobDataMap["webRadio"] = webRadio
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
}
