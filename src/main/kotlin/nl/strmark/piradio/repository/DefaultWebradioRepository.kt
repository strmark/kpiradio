package nl.strmark.piradio.repository

import nl.strmark.piradio.entity.DefaultWebRadio
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DefaultWebradioRepository : JpaRepository<DefaultWebRadio, Int>
