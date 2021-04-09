package nl.strmark.piradio.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "alarms")
data class Alarm (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null,

    @Column(length = 50, nullable = false, unique = false)
    var name: String? = null,

    var monday: Boolean = false,
    var tuesday: Boolean = false,
    var wednesday: Boolean = false,
    var thursday: Boolean = false,
    var friday: Boolean = false,
    var saturday: Boolean = false,
    var sunday: Boolean = false,
    var hour: Int = 0,
    var minute: Int = 0,
    var autoStopMinutes: Int = 0,
    var isActive: Boolean = false,
    var webradio: Int = 0
){}