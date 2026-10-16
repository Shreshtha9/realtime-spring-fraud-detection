package com.frauddetector.transaction.stream;

import com.frauddetector.transaction.model.AlertView;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class AlertStreamHub {

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    /** Called when a browser connects to /api/alerts/stream. */
    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter(0L);          // 0 = never time out
        emitters.add(emitter);
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError(e -> emitters.remove(emitter));

        try {
            // an immediate first message so the browser knows the connection is open
            emitter.send(SseEmitter.event().name("connected").data("ok"));
        } catch (IOException | IllegalStateException e) {
            emitters.remove(emitter);
        }
        return emitter;
    }

    /** Called for every new alert; sends it to every connected browser. */
    public void publish(AlertView alert) {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("alert")
                        .data(alert, MediaType.APPLICATION_JSON));
            } catch (IOException | IllegalStateException e) {
                emitters.remove(emitter);                 // browser has gone away
            }
        }
    }
}