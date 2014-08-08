/*
 * Copyright (c) 2014, Steven Van Impe
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *  1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
 *     following disclaimer in the documentation and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package svanimpe.reminders.json;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Calendar;
import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import svanimpe.reminders.domain.Location;
import svanimpe.reminders.domain.Reminder;

@Provider
@Consumes(MediaType.APPLICATION_JSON)
public class ReminderReader implements MessageBodyReader<Reminder>
{
    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
    {
        return Reminder.class.isAssignableFrom(type);
    }

    @Override
    public Reminder readFrom(Class<Reminder> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException
    {
        Reminder reminder = new Reminder();
        
        try {
            JsonObject jsonReminder = Json.createReader(entityStream).readObject();
            if (jsonReminder.containsKey("title")) {
                reminder.setTitle(jsonReminder.getString("title"));
            }
            if (jsonReminder.containsKey("date")) {
                Calendar date = Calendar.getInstance();
                date.setTimeInMillis(jsonReminder.getJsonNumber("date").longValue());
                reminder.setDate(date);
            }
            if (jsonReminder.containsKey("location")) {
                Location location = new Location();
                JsonObject jsonLocation = jsonReminder.getJsonObject("location");
                if (jsonLocation.containsKey("latitude")) {
                    location.setLatitude(jsonLocation.getJsonNumber("latitude").doubleValue());
                }
                if (jsonLocation.containsKey("longitude")) {
                    location.setLongitude(jsonLocation.getJsonNumber("longitude").doubleValue());
                }
                reminder.setLocation(location);
            }

        } catch (JsonException | ClassCastException ex) {
            // Invalid JSON or type mismatch.
            throw new BadRequestException("JSON");
        }
        
        return reminder;
    }
}
