package nl.strmark.piradio.controller


import io.github.oshai.kotlinlogging.KotlinLogging
import nl.strmark.piradio.entity.Alarm
import nl.strmark.piradio.exception.ResourceNotFoundException
import nl.strmark.piradio.payload.ScheduleAlarmResponse
import nl.strmark.piradio.repository.AlarmRepository
import nl.strmark.piradio.repository.WebRadioRepository
import nl.strmark.piradio.util.VlcPlayer
import org.jobrunr.scheduling.JobScheduler
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.notFound
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.*

@CrossOrigin(origins = ["*"], allowedHeaders = ["*"])
@RequestMapping("/alarms")
@RestController
class AlarmController(
    private val alarmRepository: AlarmRepository,
    private val webRadioRepository: WebRadioRepository,
    private val jobScheduler: JobScheduler
) {

    @GetMapping
    fun findAll(): MutableList<Alarm> = alarmRepository.findAll()

    @PostMapping
    fun saveAlarm(@RequestBody alarm: Alarm) =
        alarmRepository.save(alarm).let { savedAlarm ->
            if (savedAlarm.isActive) {
                scheduleAlarm(
                    savedAlarm.id,
                    savedAlarm.webRadio,
                    true,
                    savedAlarm.autoStopMinutes,
                    getCronSchedule(savedAlarm)
                )
            }
        }

    @GetMapping(path = ["{id}"])
    fun findById(@PathVariable(value = "id") alarmId: Int) =
        alarmRepository.findById(alarmId)
            .orElseThrow { ResourceNotFoundException(ALARM, "id", alarmId) }

    @PutMapping(path = ["{id}"])
    fun updateAlarm(
        @PathVariable(value = "id") alarmId: Int,
        @RequestBody alarmDetails: Alarm
    ): Alarm? {
        assert(alarmDetails.id == alarmId)
        return alarmRepository.findById(alarmId)
            .orElseThrow { ResourceNotFoundException(ALARM, "id", alarmId) }
            ?.let { alarm -> scheduleAndSave(alarmId, alarmDetails, alarm) }
    }

    @DeleteMapping(path = ["{id}"])
    fun deleteAlarm(@PathVariable(value = "id") alarmId: Int): ResponseEntity<Long> =
        alarmRepository.findById(alarmId)
            .orElseThrow { ResourceNotFoundException(ALARM, "id", alarmId) }
            ?.let { alarm -> deleteAndResponseOK(alarmId, alarm) } ?: notFound().build()

    private fun deleteAndResponseOK(alarmId: Int, alarm: Alarm): ResponseEntity<Long> {
        jobScheduler.deleteRecurringJob(PI_RADIO + alarmId)
        alarmRepository.delete(alarm)
        return ok().build()
    }

    private fun scheduleAndSave(alarmId: Int, alarmDetails: Alarm, alarm: Alarm): Alarm {
        scheduleAlarm(
            alarmId,
            alarmDetails.webRadio,
            alarmDetails.isActive,
            alarmDetails.autoStopMinutes,
            getCronSchedule(alarmDetails)
        )
        return saveAlarm(alarm, alarmDetails)
    }

    private fun scheduleAlarm(
        alarmId: Int?,
        webRadioId: Int,
        isActive: Boolean,
        autoStopMinutes: Int,
        cronSchedule: String
    ) {
        webRadioRepository.findById(webRadioId).let { webRadioOptional ->
            jobScheduler.deleteRecurringJob(PI_RADIO + alarmId)
            if (isActive) {
                webRadioOptional.get().url.let { url ->
                    jobScheduler.scheduleRecurrently<VlcPlayer>(PI_RADIO + alarmId, cronSchedule) { vlcPlayer ->
                        vlcPlayer.open(url, autoStopMinutes.toLong())
                    }
                }
                ScheduleAlarmResponse(
                    true,
                    PI_RADIO + alarmId,
                    autoStopMinutes.toString(),
                    "Alarm Scheduled Successfully!"
                )
            }
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
        // 45 6 * * MON,TUE,WED,THU,FRI
        // minute (0-59 - hour (0 - 23)	- day of the month (1 - 31)	- month (1 - 12) -day of the week (0 - 6)
        val cronSchedule = "${alarmDetails?.minute ?: 0} ${alarmDetails?.hour ?: 0} * * "
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

        logger.info { "$cronSchedule${cronDays.joinToString(",")}" }
        return "$cronSchedule${cronDays.joinToString(",")}"
    }

    private fun addToCronDays(alarm: Boolean, day: String, cronDays: MutableList<String>) {
        when {
            alarm -> cronDays.add(day)
        }
    }

    companion object {
        private val logger = KotlinLogging.logger {}
        private const val ALARM = "Alarm"
        private const val PI_RADIO = "PiRadio_"
    }
}
