package nl.strmark.piradio.util

import java.util.Arrays
import java.util.Objects
import java.util.stream.Collectors
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.CompoundControl
import javax.sound.sampled.Control
import javax.sound.sampled.FloatControl
import javax.sound.sampled.Line
import javax.sound.sampled.LineUnavailableException
import javax.sound.sampled.Mixer

object Audio {
    // HARDWARE_DESCRIPTION hw:0 or hw:1
    // HARDWARE_ITEM PCM, Master, Speaker or Headphone
    private const val HARDWARE_DESCRIPTION: String = "hw:0"
    private const val HARDWARE_ITEM: String = "Master"
    val speakerOutputVolume: Float?
        get() {
            val line = speakerOutputLine
            when {
                line != null -> {
                    val opened = open(line)
                    try {
                        val control = getVolumeControl(line)
                        when {
                            control != null -> {
                                return control.value
                            }
                        }
                    } finally {
                        when {
                            opened -> {
                                line.close()
                            }
                        }
                    }
                }
            }
            return null
        }

    fun setSpeakerOutputVolume(value: Float) {
        when {
            value < 0 || value > 1 -> {
                throw IllegalArgumentException(
                    "VolumeTransfer can only be set to a value from 0 to 1. Given value is illegal: $value"
                )
            }
            else -> {
                val line: Line? = speakerOutputLine
                when {
                    line != null -> {
                        val opened = open(line)
                        try {
                            val control = getVolumeControl(line)
                            when {
                                control != null -> control.value = value
                                else -> throw NullPointerException("VolumeTransfer control not found in speaker port: $line")
                            }
                        } finally {
                            when {
                                opened -> line.close()
                            }
                        }
                    }
                    else -> throw NullPointerException("Speaker output port not found")
                }
            }
        }
    }

    private val speakerOutputLine: Line?
        get() = mixers
            .stream()
            .filter { mixer: Mixer -> mixer.mixerInfo.name.contains(HARDWARE_DESCRIPTION) }
            .map { mixer: Mixer ->
                getAvailableOutputLines(mixer)
                    .stream()
                    .filter { line: Line? -> line?.lineInfo.toString().contains(HARDWARE_ITEM) }
                    .findFirst()
                    .orElse(null)
            }
            .findFirst()
            .orElse(null)

    private fun getVolumeControl(line: Line): FloatControl? {
        when {
            line.isOpen -> return findControl(FloatControl.Type.VOLUME, *line.controls) as FloatControl?
            else -> throw IllegalStateException("Line is closed: $line")
        }
    }

    private fun findControl(type: Control.Type, vararg controls: Control): Control? {
        return when {
            controls.isNotEmpty() -> {
                Arrays.stream(controls)
                    .map { control: Control -> getControl(type, control) }
                    .findFirst()
                    .orElse(null)
            }
            else -> null
        }
    }

    private fun getControl(type: Control.Type, control: Control): Control? {
        when {
            Objects.equals(control.type, type) -> return control
            control is CompoundControl -> {
                val member = findControl(type, *control.memberControls)
                when {
                    member != null -> return member
                }
            }
        }
        return null
    }

    val mixers: List<Mixer>
        get() {
            return Arrays.stream(AudioSystem.getMixerInfo())
                .map { info: Mixer.Info? -> AudioSystem.getMixer(info) }
                .collect(Collectors.toList())
        }

    fun getAvailableOutputLines(mixer: Mixer): List<Line?> {
        return getAvailableLines(mixer, mixer.targetLineInfo)
    }

    private fun getAvailableLines(mixer: Mixer, lineInfos: Array<Line.Info>): List<Line?> {
        return Arrays.stream(lineInfos)
            .map { lineInfo: Line.Info? -> getLineIfAvailable(mixer, lineInfo) }
            .filter { obj: Line? -> Objects.nonNull(obj) }
            .collect(Collectors.toList())
    }

    private fun getLineIfAvailable(mixer: Mixer, lineInfo: Line.Info?): Line? {
        return try {
            mixer.getLine(lineInfo)
        } catch (ex: LineUnavailableException) {
            null
        }
    }

    private fun open(line: Line): Boolean {
        return when {
            line.isOpen -> true
            else -> {
                try {
                    line.open()
                    true
                } catch (ex: LineUnavailableException) {
                    false
                }
            }
        }
    }

    private fun toString(line: Line?): String? {
        return when {
            line != null -> (line.lineInfo.toString())
            else -> null
        }
    }
}