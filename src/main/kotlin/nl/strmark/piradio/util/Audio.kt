package nl.strmark.piradio.util

import javax.sound.sampled.AudioSystem.getMixer
import javax.sound.sampled.AudioSystem.getMixerInfo
import javax.sound.sampled.CompoundControl
import javax.sound.sampled.Control
import javax.sound.sampled.FloatControl
import javax.sound.sampled.Line
import javax.sound.sampled.LineUnavailableException
import javax.sound.sampled.Mixer

object Audio {
    fun setOutputVolume(device: String, value: Float) {
        require(!(value < 0 || value > 1)) { "Volume can only be set to a value from 0 to 1. Given value is illegal: $value" }
        val line = getOutputLine(device) ?: throw RuntimeException("$device output port not found")
        val opened = open(line)
        try {
            val control = getVolumeControl(line) ?: throw RuntimeException("Volume control not found in $device port: $line")
            control.value = value
        } finally {
            if (opened) line.close()
        }
    }

    fun getOutputVolume(device: String): Float? {
        val line = getOutputLine(device) ?: return null
        val opened = open(line)
        return try {
            getVolumeControl(line)?.value ?: return null
        } finally {
            if (opened) line.close()
        }
    }

    // Device Master, PCM, Speaker or Headphone
    private fun getOutputLine(device: String): Line? {
        for (mixer in getMixers()) {
            for (line in getAvailableOutputLines(mixer)) {
                if (line != null && line.lineInfo.toString().contains(device)) return line
            }
        }
        return null
    }

    private fun getVolumeControl(line: Line): FloatControl? {
        if (!line.isOpen) throw RuntimeException("Line is closed: $line")
        return findControl(FloatControl.Type.VOLUME, *line.controls) as FloatControl?
    }

    private fun findControl(type: Control.Type, vararg controls: Control): Control? {
        for (control in controls) {
            when {
                control.type == type -> return control
                control is CompoundControl -> {
                    findControl(type, *control.memberControls).let { member ->
                        if (member != null) return member
                    }
                }
            }
        }
        return null
    }

    private fun getMixers(): List<Mixer> {
        mutableListOf<Mixer>().let { mixers ->
            for (info in getMixerInfo()) {
                mixers.add(getMixer(info))
            }
            return mixers
        }
    }

    private fun getAvailableOutputLines(mixer: Mixer): List<Line?> =
        getAvailableLines(mixer, mixer.targetLineInfo)

    private fun getAvailableLines(mixer: Mixer, lineInfos: Array<Line.Info>): List<Line?> {
        mutableListOf<Line?>().let { lines ->
            for (lineInfo in lineInfos) {
                getLineIfAvailable(mixer, lineInfo).let { line -> lines.add(line) }
            }
            return lines
        }
    }

    private fun getLineIfAvailable(mixer: Mixer, lineInfo: Line.Info?): Line? =
        try {
            mixer.getLine(lineInfo)
        } catch (ex: LineUnavailableException) {
            null
        }

    private fun open(line: Line): Boolean =
        when {
            line.isOpen -> false
            else -> try {
                line.open()
                true
            } catch (ex: LineUnavailableException) {
                false
            }
        }
}
