package nl.strmark.piradio.repository

import nl.strmark.piradio.entity.Webradio
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
class WebradioRepositoryTest(@Autowired val webradioRepository: WebradioRepository) {

    @Test
    fun whenFindByIdThenReturnRadio() {
        val radio = Webradio(
            id = 0,
            name = "test",
            url = "http://test",
            isDefault = false
        )
        val saved = webradioRepository.save(radio)

        val found = webradioRepository.findById(saved.id)
        assertThat(found.get()).isEqualTo(saved)
    }
}
