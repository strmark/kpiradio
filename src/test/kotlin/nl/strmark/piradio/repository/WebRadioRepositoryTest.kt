package nl.strmark.piradio.repository

import nl.strmark.piradio.entity.WebRadio
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
class WebRadioRepositoryTest(@Autowired val webRadioRepository: WebRadioRepository) {

    @Test
    fun whenFindByIdThenReturnRadio() {
        val radio = WebRadio(
            id = 0,
            name = "test",
            url = "http://test",
            isDefault = false
        )
        val saved = webRadioRepository.save(radio)

        val found = webRadioRepository.findById(saved.id)
        assertThat(found.get()).isEqualTo(saved)
    }
}
