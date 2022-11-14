package nl.strmark.piradio.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "alarms")
class Alarm(

    @Id
    @GeneratedValue
    var id: Int,

    @Column(length = 50, nullable = false, unique = false)
    var name: String,

    var monday: Boolean,
    var tuesday: Boolean,
    var wednesday: Boolean,
    var thursday: Boolean,
    var friday: Boolean,
    var saturday: Boolean,
    var sunday: Boolean,
    @Column(name = "hours")
    var hour: Int,
    @Column(name = "minutes")
    var minute: Int,
    var autoStopMinutes: Int,
    var isActive: Boolean,
    var webRadio: Int
)
