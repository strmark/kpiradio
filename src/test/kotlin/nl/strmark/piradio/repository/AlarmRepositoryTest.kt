package nl.strmark.piradio.repository

import nl.strmark.piradio.entity.Alarm
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
class AlarmRepositoryTest(@Autowired val alarmRepository: AlarmRepository) {

    @Test
    fun whenFindByIdThenReturnRadio() {
        val alarm = Alarm(
            id = 999,
            name = "Alarmtest",
            monday = false,
            tuesday = false,
            wednesday = false,
            thursday = false,
            friday = false,
            saturday = false,
            sunday = false,
            hour = 23,
            minute = 59,
            autoStopMinutes = 0,
            isActive = false,
            webradio = 1
        )
        val saved = alarmRepository.save(alarm)

        val found = alarmRepository.findById(saved.id)
        Assertions.assertThat(found.get()).isEqualTo(saved)
    }
}