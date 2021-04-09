package nl.strmark.piradio.repository

import nl.strmark.piradio.entity.Webradio
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface WebradioRepository : JpaRepository<Webradio?, Int?>