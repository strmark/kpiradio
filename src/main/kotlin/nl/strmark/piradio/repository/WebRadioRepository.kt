package nl.strmark.piradio.repository

import nl.strmark.piradio.entity.WebRadio
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface WebRadioRepository : JpaRepository<WebRadio, Int>
