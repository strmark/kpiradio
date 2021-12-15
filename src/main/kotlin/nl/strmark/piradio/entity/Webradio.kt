package nl.strmark.piradio.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "webradios")
class Webradio(

    @Id
    @GeneratedValue
    var id: Int,

    @Column(length = 50, nullable = false)
    var name: String,

    @Column(length = 255, nullable = false)
    var url: String,

    @Column(name = "is_default", nullable = false)
    var isDefault: Boolean
)
