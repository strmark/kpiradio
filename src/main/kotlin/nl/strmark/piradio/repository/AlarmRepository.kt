package nl.strmark.piradio.repository

import nl.strmark.piradio.entity.Alarm
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AlarmRepository : JpaRepository<Alarm?, Int>
