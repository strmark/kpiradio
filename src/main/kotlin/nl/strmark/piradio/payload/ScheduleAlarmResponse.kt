package nl.strmark.piradio.payload

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
class ScheduleAlarmResponse {
    private var isSuccess: Boolean
    private var jobId: String? = null
    private var jobGroup: String? = null
    private var message: String

    constructor(success: Boolean, message: String) {
        isSuccess = success
        this.message = message
    }

    constructor(success: Boolean, jobId: String?, jobGroup: String?, message: String) {
        isSuccess = success
        this.jobId = jobId
        this.jobGroup = jobGroup
        this.message = message
    }
}