package ru.rusquant.messages.request.body;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 *   Author: Aleksey Kutergin <aleksey.v.kutergin@gmail.ru>
 *   Company: Rusquant
 */
@JsonAutoDetect
public class SecurityClassInfoRequestBody extends RequestBody
{
    private String classCode;

    public SecurityClassInfoRequestBody()
    {

    }

    public SecurityClassInfoRequestBody(String classCode)
    {
        this.classCode = classCode;
    }

    public String getClassCode()
    {
        return classCode;
    }

    public void setClassCode(String classCode)
    {
        this.classCode = classCode;
    }
}
