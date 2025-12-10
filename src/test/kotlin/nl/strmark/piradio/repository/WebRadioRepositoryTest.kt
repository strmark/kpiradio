package nl.strmark.piradio.repository

import nl.strmark.piradio.entity.WebRadio
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest

@DataJpaTest
class WebRadioRepositoryTest(@Autowired val webRadioRepository: WebRadioRepository) {

    @Test
    fun whenFindByIdThenReturnWebRadio() {
        val radio = WebRadio(
            name = "test",
            url = "http://test",
            id = null
        )
        val saved = webRadioRepository.save(radio)

        val found = saved.id?.let { id -> webRadioRepository.findById(id)}
        assertThat(found?.get()).isEqualTo(saved)
    }
}
