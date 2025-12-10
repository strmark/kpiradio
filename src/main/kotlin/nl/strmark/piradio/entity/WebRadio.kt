package nl.strmark.piradio.entity

import jakarta.persistence.*

@Entity
@Table(name = "webradios")
class WebRadio(

    @Id
    @GeneratedValue
    var id: Int?,

    @Column(length = 50, nullable = false)
    var name: String,

    @Column(length = 255, nullable = false)
    var url: String
)
