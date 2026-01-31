package nl.strmark.piradio.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "defaultwebradio")
class DefaultWebRadio(
    @Id
    var id: Int,
    @Column(name = "web_radio_id")
    var webRadioId: Int?
)