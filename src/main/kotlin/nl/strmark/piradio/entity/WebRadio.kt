package nl.strmark.piradio.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "webradios")
class WebRadio(

    @Id
    @GeneratedValue
    var id: Int,

    @Column(length = 50, nullable = false)
    var name: String,

    @Column(length = 255, nullable = false)
    var url: String
)
