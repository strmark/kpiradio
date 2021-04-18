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
    var hour: Int,
    var minute: Int,
    var autoStopMinutes: Int,
    var isActive: Boolean,
    var webradio: Int
)